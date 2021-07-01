<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl2="http://www.wfmc.org/2008/XPDL2.1" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:xpdExt="http://www.tibco.com/XPD/xpdExtension1.0.0" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>


  <xsl:template match="xpdl2:Activity/xpdl2:Implementation/xpdl2:SubFlow">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:callActivity drools:waitForCompletion="true" drools:independent="false">
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="calledElement">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <bpmn2:extensionElements>
        <xsl:apply-templates select="../../xpdExt:Audit/xpdExt:AuditEvent[@Type='Initiated']"/>
        <xsl:apply-templates select="../../xpdExt:Audit/xpdExt:AuditEvent[@Type='Completed']"/>
      </bpmn2:extensionElements>
      <xsl:for-each select="//*/xpdl2:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
      <xsl:for-each select="//*/xpdl2:Transition[@From=$origId]">
        <bpmn2:outgoing>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:outgoing>
      </xsl:for-each>
      <bpmn2:ioSpecification>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('InputOutputSpecification_', $nodeId)"/>
        </xsl:attribute>
        <!-- bpmn2:dataInput id="DataInput_1" itemSubjectRef="_String" name="inFirstName"/ -->
        <xsl:for-each select="xpdl2:DataMappings/xpdl2:DataMapping[@Direction='IN']">
          <bpmn2:dataInput itemSubjectRef="_String">
            <xsl:attribute name="id">
              <xsl:value-of select="concat('DataInput_',$nodeId, '_', @Formal)"/>
            </xsl:attribute>
            <xsl:attribute name="name">
              <xsl:value-of select="@Formal"/>
            </xsl:attribute>
          </bpmn2:dataInput>
        </xsl:for-each>
        <!-- bpmn2:dataOutput id="DataOutput_1" itemSubjectRef="_Integer" name="outInteger"/ -->
        <xsl:for-each select="xpdl2:DataMappings/xpdl2:DataMapping[@Direction='OUT']">
          <bpmn2:dataOutput itemSubjectRef="_String">
            <xsl:attribute name="id">
              <xsl:value-of select="concat('DataOutput_',$nodeId, '_', @Formal)"/>
            </xsl:attribute>
            <xsl:attribute name="name">
              <xsl:value-of select="@Formal"/>
            </xsl:attribute>
          </bpmn2:dataOutput>
        </xsl:for-each>
        <bpmn2:inputSet name="Input Set">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('_InputSet_',$nodeId, '_', @Formal)"/>
          </xsl:attribute>
          <!-- <bpmn2:dataInputRefs>DataInput_1</bpmn2:dataInputRefs> -->
          <xsl:for-each select="xpdl2:DataMappings/xpdl2:DataMapping[@Direction='IN']">
            <bpmn2:dataInputRefs>
              <xsl:value-of select="concat('DataInput_',$nodeId, '_', @Formal)"/>
            </bpmn2:dataInputRefs>
          </xsl:for-each>
        </bpmn2:inputSet>
        <bpmn2:outputSet name="Output Set">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('_OutputSet_',$nodeId, '_', @Formal)"/>
          </xsl:attribute>
          <!-- <bpmn2:dataOutputRefs>DataOutput_1</bpmn2:dataOutputRefs> -->
          <xsl:for-each select="xpdl2:DataMappings/xpdl2:DataMapping[@Direction='OUT']">
            <bpmn2:dataOutputRefs>
              <xsl:value-of select="concat('DataOutput_',$nodeId, '_', @Formal)"/>
            </bpmn2:dataOutputRefs>
          </xsl:for-each>
        </bpmn2:outputSet>
      </bpmn2:ioSpecification>
      <!-- <bpmn2:dataInputAssociation id="DataInputAssociation_1">
     <bpmn2:sourceRef>firstName</bpmn2:sourceRef>
<bpmn2:targetRef>DataInput_1</bpmn2:targetRef>
</bpmn2:dataInputAssociation>-->
      <xsl:for-each select="xpdl2:DataMappings/xpdl2:DataMapping[@Direction='IN']">
        <bpmn2:dataInputAssociation>
          <xsl:attribute name="id">
            <xsl:value-of select="concat('dataInputAssociation_',$nodeId, '_', @Formal)"/>
          </xsl:attribute>
          <bpmn2:sourceRef>
            <xsl:value-of select="@Formal"/>
          </bpmn2:sourceRef>
          <bpmn2:targetRef>
            <xsl:value-of select="concat('DataInput_',$nodeId, '_', @Formal)"/>
          </bpmn2:targetRef>
        </bpmn2:dataInputAssociation>
      </xsl:for-each>
      <!-- <bpmn2:dataOutputAssociation id="DataOutputAssociation_1">
     <bpmn2:sourceRef>DataOutput_1</bpmn2:sourceRef>
<bpmn2:targetRef>i</bpmn2:targetRef>
</bpmn2:dataOutputAssociation>-->
      <xsl:for-each select="xpdl2:DataMappings/xpdl2:DataMapping[@Direction='OUT']">
        <bpmn2:dataOutputAssociation>
          <xsl:attribute name="id">
            <xsl:value-of select="concat('dataOutputAssociation_',$nodeId, '_',  @Formal)"/>
          </xsl:attribute>
          <bpmn2:sourceRef>
            <xsl:value-of select="@Formal"/>
          </bpmn2:sourceRef>
          <bpmn2:targetRef>
            <xsl:value-of select="concat('DataOutput_',$nodeId, '_', @Formal)"/>
          </bpmn2:targetRef>
        </bpmn2:dataOutputAssociation>
      </xsl:for-each>
    </bpmn2:callActivity>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl2="http://www.wfmc.org/2009/XPDL2.2" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <!-- This template is used to populate the itemDefinition and message elements needed at the beginning of the bpmn2 process, mostly metadata -->
  <xsl:template match="xpdl2:WorkflowProcess/xpdl2:Activities/xpdl2:Activity/xpdl2:Event/xpdl2:IntermediateEvent[@Trigger='Message']">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:message itemRef="_String">
      <xsl:attribute name="id">
        <xsl:value-of select="concat('msgref_', $nodeId)"/>
      </xsl:attribute>
      <xsl:attribute name="itemRef">
        <xsl:value-of select="concat('msgtype_', $nodeId)"/>
      </xsl:attribute>
    </bpmn2:message>
    <bpmn2:itemDefinition >
      <xsl:attribute name="id">
        <xsl:value-of select="concat('msgtype_', $nodeId)"/>
      </xsl:attribute>
    </bpmn2:itemDefinition>
  </xsl:template>




  <!-- this template is used to transform the runtime intermediate Throw event -->
  <xsl:template match="xpdl2:Activity/xpdl2:Event/xpdl2:IntermediateEvent[@Trigger='Message']/xpdl2:TriggerResultMessage[@CatchThrow='THROW']">
    <xsl:variable name="origId" select="../../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:intermediateThrowEvent drools:selectable="true" drools:boundaryca="" color:background-color="#f5deb3" color:border-color="#a0522d" color:color="#000000">
        <xsl:attribute name="id">
          <xsl:value-of select="$nodeId"/>
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="$nodeName"/>
        </xsl:attribute>
      <xsl:for-each select="//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
      <xsl:for-each select="//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId]">
        <bpmn2:outgoing>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:outgoing>
      </xsl:for-each>
      <bpmn2:messageEventDefinition>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('message_',$nodeId)"/>
        </xsl:attribute>
        <xsl:attribute name="messageRef">
          <xsl:value-of select="concat('msgref_',$nodeId)"/>
        </xsl:attribute>
        <xsl:attribute name="drools:msgref">
          <xsl:value-of select="concat('msgref_',$nodeId)"/>
        </xsl:attribute>
      </bpmn2:messageEventDefinition>
    </bpmn2:intermediateThrowEvent>
  </xsl:template>
</xsl:stylesheet>

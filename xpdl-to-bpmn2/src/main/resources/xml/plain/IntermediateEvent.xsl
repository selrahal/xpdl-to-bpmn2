<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <!-- This template is used to populate the itemDefinition and message elements needed at the beginning of the bpmn2 process, mostly metadata -->
  <xsl:template match="xpdl:WorkflowProcess/xpdl:Activities/xpdl:Activity/xpdl:Event/xpdl:IntermediateEvent[@Trigger='Message']/xpdl:TriggerResultMessage">
    <xsl:variable name="origId" select="../../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:message itemRef="_String">
      <xsl:attribute name="id">
        <xsl:value-of select="concat('msgref_', $nodeId)"/>
      </xsl:attribute>
    </bpmn2:message>
  </xsl:template>




  <!-- this template is used to transform the runtime intermediate event -->
  <xsl:template match="xpdl:Activity/xpdl:Event/xpdl:IntermediateEvent[@Trigger='Message']">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:intermediateCatchEvent drools:selectable="true" drools:boundaryca="" color:background-color="#f5deb3" color:border-color="#a0522d" color:color="#000000">
        <xsl:attribute name="id">
          <xsl:value-of select="$nodeId"/>
        </xsl:attribute>
      <xsl:for-each select="//xpdl:WorkflowProcesses/xpdl:WorkflowProcess/xpdl:Transitions/xpdl:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
      <xsl:for-each select="//xpdl:WorkflowProcesses/xpdl:WorkflowProcess/xpdl:Transitions/xpdl:Transition[@From=$origId]">
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
    </bpmn2:intermediateCatchEvent>
  </xsl:template>
</xsl:stylesheet>

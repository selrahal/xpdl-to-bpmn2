<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl2="http://www.wfmc.org/2008/XPDL2.1" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:xpdExt="http://www.tibco.com/XPD/xpdExtension1.0.0" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <!-- Plain End Event -->
  <xsl:template match="xpdl2:Event/xpdl2:EndEvent">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:endEvent drools:bgcolor="#ff6347" drools:selectable="true">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <bpmn2:extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue>
            <xsl:value-of select="../../@xpdExt:DisplayName"/>
          </drools:metaValue>
        </drools:metaData>
      </bpmn2:extensionElements>
      <xsl:for-each select="//*/xpdl2:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
    </bpmn2:endEvent>
  </xsl:template>


  <!-- Terminate End Event -->
  <xsl:template match="xpdl2:Event/xpdl2:EndEvent[@Result='Terminate']">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:endEvent drools:bgcolor="#ff6347" drools:selectable="true">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <bpmn2:extensionElements>
        <drools:metaData name="elementname">
          <drools:metaValue>
            <xsl:value-of select="../../@xpdExt:DisplayName"/>
          </drools:metaValue>
        </drools:metaData>
      </bpmn2:extensionElements>
      <xsl:for-each select="//*/xpdl2:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
      <bpmn2:terminateEventDefinition>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('terminate_',$nodeId)"/>
        </xsl:attribute>
      </bpmn2:terminateEventDefinition>
    </bpmn2:endEvent>
  </xsl:template>

  <!-- Signal End Event -->
  <xsl:template match="xpdl2:Event/xpdl2:EndEvent[@Result='Signal']">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:endEvent drools:bgcolor="#ff6347" drools:selectable="true">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:for-each select="//*/xpdl2:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
      <bpmn2:signalEventDefinition>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('SignalEventDefinition_',$nodeId)"/>
        </xsl:attribute>
        <xsl:attribute name="signalRef">
          <xsl:value-of select="xpdl2:TriggerResultSignal/@Name"/>
        </xsl:attribute>
      </bpmn2:signalEventDefinition>
    </bpmn2:endEvent>
  </xsl:template>
</xsl:stylesheet>

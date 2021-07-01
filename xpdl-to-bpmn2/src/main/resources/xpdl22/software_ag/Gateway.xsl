<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl2="http://www.wfmc.org/2009/XPDL2.2" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <!-- Exclusive Gateway -->
  <xsl:template match="xpdl2:Activity/xpdl2:Route[@GatewayType='Exclusive']">
    <xsl:variable name="origId" select="../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <xsl:variable name="outgoingCount" select="count(//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId])"/>
    <bpmn2:exclusiveGateway drools:bgcolor="#f0e68c" drools:selectable="true" drools:bordercolor="#a67f00" drools:dg="">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="gatewayDirection">
        <xsl:choose>
          <xsl:when test="$outgoingCount &gt; 1">Diverging</xsl:when>
          <xsl:otherwise>Converging</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:if test="//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId]/xpdl2:Condition[@Type='OTHERWISE']">
        <xsl:attribute name="default">
          <xsl:value-of select="translate(//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId]/xpdl2:Condition[@Type='OTHERWISE']/../@Id,':','_')"/>
        </xsl:attribute>
      </xsl:if>
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
    </bpmn2:exclusiveGateway>
  </xsl:template>
  <!-- Parallel Gateway -->
  <xsl:template match="xpdl2:Activity/xpdl2:Route[@GatewayType='Parallel']">
    <xsl:variable name="origId" select="../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <xsl:variable name="outgoingCount" select="count(//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId])"/>
    <bpmn2:parallelGateway drools:bgcolor="#f0e68c" drools:selectable="true" drools:bordercolor="#a67f00" drools:dg="">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="gatewayDirection">
        <xsl:choose>
          <xsl:when test="$outgoingCount &gt; 1">Diverging</xsl:when>
          <xsl:otherwise>Converging</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:if test="//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId]/xpdl2:Condition[@Type='OTHERWISE']">
        <xsl:attribute name="default">
          <xsl:value-of select="translate(//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition[@From=$origId]/xpdl2:Condition[@Type='OTHERWISE']/../@Id,':','_')"/>
        </xsl:attribute>
      </xsl:if>
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
    </bpmn2:parallelGateway>
  </xsl:template>
</xsl:stylesheet>

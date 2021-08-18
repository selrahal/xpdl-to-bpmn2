<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2"
	xmlns:xpdl2="http://www.wfmc.org/2008/XPDL2.1"
	xmlns:drools="http://www.jboss.org/drools"
	xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL"
	xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color"
	xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
	xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
	xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
	xmlns:xpdExt="http://www.tibco.com/XPD/xpdExtension1.0.0" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue drools:script"/>
  <!-- Includes -->
  <xsl:include href="Nodes.xsl"/>
  <xsl:include href="SequenceFlow.xsl"/>
  <xsl:include href="BPMNShape.xsl"/>
  <xsl:include href="BPMNEdge.xsl"/>
  <xsl:include href="NoTasks.xsl"/>
  <xsl:include href="Tasks.xsl"/>
  <xsl:include href="UserTask.xsl"/>
  <xsl:include href="IntermediateEvent.xsl"/>
  <xsl:include href="Timer.xsl"/>
  <xsl:include href="Signal.xsl"/>
  <xsl:include href="Pool.xsl"/>
  <xsl:include href="OnEntry.xsl"/>
  <xsl:include href="OnExit.xsl"/>
  <!-- xpdl2:Package is the root element -->
  <xsl:template match="/">
    <xsl:apply-templates select="xpdl2:Package"/>
  </xsl:template>
  <!-- Transform xpdl2:Package to bpmn2:definitions -->
  <xsl:template match="/xpdl2:Package">
    <bpmn2:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.omg.org/bpmn20" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:bpsim="http://www.bpsim.org/schemas/1.0" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:drools="http://www.jboss.org/drools" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd" targetNamespace="http://www.omg.org/bpmn20" typeLanguage="http://www.java.com/javaTypes">
      <bpmn2:itemDefinition id="_String" isCollection="false" structureRef="String"/>
      <bpmn2:itemDefinition id="_Integer" isCollection="false" structureRef="Integer"/>
      <bpmn2:itemDefinition id="_Float" isCollection="false" structureRef="Float"/>
      <bpmn2:itemDefinition id="_Boolean" isCollection="false" structureRef="Boolean"/>
      <bpmn2:itemDefinition id="_Object" isCollection="false" structureRef="Object"/>
      <xsl:apply-templates select="xpdl2:WorkflowProcesses"/>
    </bpmn2:definitions>
  </xsl:template>
  <xsl:template match="xpdl2:WorkflowProcesses">
    <xsl:apply-templates select="xpdl2:WorkflowProcess/xpdl2:Activities/xpdl2:Activity/xpdl2:Event/xpdl2:IntermediateEvent[@Trigger='Link']"/>
    <xsl:apply-templates select="xpdl2:WorkflowProcess"/>
  </xsl:template>
  <xsl:template match="xpdl2:WorkflowProcess">
    <xsl:variable name="origId" select="@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:process drools:packageName="org.jbpm" drools:version="1.0" isExecutable="true">
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <!-- Pools -->
      <xsl:apply-templates select="../../xpdl2:Pools/xpdl2:Pool[@Process=$origId]"/>
      <!-- Activities -->
      <xsl:apply-templates select="xpdl2:Activities"/>
      <!-- Transitions -->
      <xsl:apply-templates select="xpdl2:Transitions"/>
    </bpmn2:process>
    <bpmndi:BPMNDiagram>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('BPMNDiagram_',$nodeId)"/>
      </xsl:attribute>
        <bpmndi:BPMNPlane>
          <xsl:attribute name="id">
            <xsl:value-of select="concat('BPMNPlane_',$nodeId)"/>
          </xsl:attribute>
          <xsl:attribute name="bpmnElement">
            <xsl:value-of select="$nodeId"/>
          </xsl:attribute>
          <xsl:comment> Shapes </xsl:comment>
          <xsl:apply-templates select="//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess[@Id=$origId]/xpdl2:Activities/xpdl2:Activity/xpdl2:NodeGraphicsInfos/xpdl2:NodeGraphicsInfo/xpdl2:Coordinates"/>
          <xsl:apply-templates select="//xpdl2:Pools/xpdl2:Pool[@Process=$origId]/xpdl2:Lanes/xpdl2:Lane/xpdl2:NodeGraphicsInfos/xpdl2:NodeGraphicsInfo"/>
          <xsl:comment> Edges </xsl:comment>
          <xsl:apply-templates select="//xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess[@Id=$origId]/xpdl2:Transitions/xpdl2:Transition/xpdl2:ConnectorGraphicsInfos"/>
        </bpmndi:BPMNPlane>
    </bpmndi:BPMNDiagram>
  </xsl:template>
  <xsl:template match="xpdl2:Activities">
    <xsl:comment>Start Event</xsl:comment>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:StartEvent"/>
    <xsl:comment>End Events</xsl:comment>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:EndEvent[@Result='None']"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:EndEvent[@Result='Terminate']"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:EndEvent[@Result='Signal']"/>
    <xsl:comment>Gateways</xsl:comment>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Route"/>
    <xsl:comment>Tasks</xsl:comment>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Implementation/xpdl2:No"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Implementation/xpdl2:Task/xpdl2:TaskUser"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Implementation/xpdl2:Task/xpdl2:TaskScript"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Implementation/xpdl2:Task/xpdl2:TaskService"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Implementation/xpdl2:SubFlow"/>
    <xsl:comment>Messages</xsl:comment>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:IntermediateEvent[@Trigger='Link']/xpdl2:TriggerResultLink[@CatchThrow='CATCH']"/>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:IntermediateEvent[@Trigger='Link']/xpdl2:TriggerResultLink[@CatchThrow='THROW']"/>
    <xsl:comment>Timers</xsl:comment>
    <xsl:apply-templates select="xpdl2:Activity/xpdl2:Event/xpdl2:IntermediateEvent[@Trigger='Timer']"/>
  </xsl:template>
  <xsl:template match="xpdl2:Transitions">
    <xsl:comment>Sequence Flows</xsl:comment>
    <xsl:apply-templates select="xpdl2:Transition"/>
  </xsl:template>
</xsl:stylesheet>

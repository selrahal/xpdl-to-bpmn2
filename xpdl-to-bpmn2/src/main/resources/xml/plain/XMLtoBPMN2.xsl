<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:xpdExt="http://www.tibco.com/XPD/xpdExtension1.0.0" version="1.0">
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
  <!-- xpdl:Package is the root element -->
  <xsl:template match="/">
      <bpmn2:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.omg.org/bpmn20" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:bpsim="http://www.bpsim.org/schemas/1.0" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:drools="http://www.jboss.org/drools" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd" targetNamespace="http://www.omg.org/bpmn20" typeLanguage="http://www.java.com/javaTypes">
      <bpmn2:itemDefinition id="_String" isCollection="false" structureRef="String"/>
      <bpmn2:itemDefinition id="_Integer" isCollection="false" structureRef="Integer"/>
      <bpmn2:itemDefinition id="_Float" isCollection="false" structureRef="Float"/>
      <bpmn2:itemDefinition id="_Boolean" isCollection="false" structureRef="Boolean"/>
      <bpmn2:itemDefinition id="_Object" isCollection="false" structureRef="Object"/>
       <xsl:apply-templates select="workflow"/>
    </bpmn2:definitions>
  </xsl:template>

  <xsl:template match="workflow">
    <xsl:apply-templates select="xpdl:WorkflowProcess/xpdl:Activities/xpdl:Activity/xpdl:Event/xpdl:IntermediateEvent[@Trigger='Link']/xpdl:TriggerResultLink"/>
    <xsl:apply-templates select="rootTask"/>
  </xsl:template>
  <xsl:template match="rootTask">
    <xsl:variable name="origId" select="../@name"/>
    <xsl:variable name="nodeId" select="translate($origId,'/','_')"/>
    <xsl:variable name="origName" select="../@name"/>
    <xsl:variable name="nodeName" select="translate($origName,'/','_')"/>
    <bpmn2:process drools:packageName="org.jbpm" drools:version="1.0" isExecutable="true">
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <!-- Pools -->
      <xsl:apply-templates select="../../xpdl:Pools/xpdl:Pool[@Process=$origId]"/>
      <!-- Activities -->
      <xsl:apply-templates select="task"/>
      <xsl:apply-templates select="parallelGroup" />
      <!-- Transitions -->
      <xsl:apply-templates select="xpdl:Transitions"/>
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
          <xsl:apply-templates select="//xpdl:WorkflowProcesses/xpdl:WorkflowProcess[@Id=$origId]/xpdl:Activities/xpdl:Activity/xpdl:NodeGraphicsInfos/xpdl:NodeGraphicsInfo/xpdl:Coordinates"/>
          <xsl:apply-templates select="//xpdl:Pools/xpdl:Pool[@Process=$origId]/xpdl:Lanes/xpdl:Lane/xpdl:NodeGraphicsInfos/xpdl:NodeGraphicsInfo"/>
          <xsl:comment> Edges </xsl:comment>
          <xsl:apply-templates select="//xpdl:WorkflowProcesses/xpdl:WorkflowProcess[@Id=$origId]/xpdl:Transitions/xpdl:Transition/xpdl:ConnectorGraphicsInfos"/>
        </bpmndi:BPMNPlane>
    </bpmndi:BPMNDiagram>
  </xsl:template>
  <xsl:template match="asd">
    <xsl:comment>Start Event</xsl:comment>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Event/xpdl:StartEvent"/>
    <xsl:comment>End Events</xsl:comment>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Event/xpdl:EndEvent[@Result='None']"/>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Event/xpdl:EndEvent[@Result='Terminate']"/>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Event/xpdl:EndEvent[@Result='Signal']"/>
    <xsl:comment>Gateways</xsl:comment>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Route"/>
    <xsl:comment>Tasks</xsl:comment>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Implementation/xpdl:No"/>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Implementation/xpdl:Task/xpdl:TaskUser"/>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Implementation/xpdl:Task/xpdl:TaskApplication"/>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Extensions/albpm:ALBPMExtensions/albpm:FeatureSet/albpm:StringFeature[@value='CONNECTOR']"/>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Implementation/xpdl:SubFlow"/>
    <xsl:comment>Messages</xsl:comment>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Event/xpdl:IntermediateEvent[@Trigger='Message']"/>
    <xsl:comment>Timers</xsl:comment>
    <xsl:apply-templates select="xpdl:Activity/xpdl:Event/xpdl:IntermediateEvent[@Trigger='Timer']"/>
  </xsl:template>
  <xsl:template match="xpdl:Transitions">
    <xsl:apply-templates select="xpdl:Transition"/>
  </xsl:template>
</xsl:stylesheet>

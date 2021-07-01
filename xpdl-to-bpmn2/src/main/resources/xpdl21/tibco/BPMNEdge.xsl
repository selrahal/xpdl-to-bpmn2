<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl2="http://www.wfmc.org/2008/XPDL2.1" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="xpdl2:WorkflowProcesses/xpdl2:WorkflowProcess/xpdl2:Transitions/xpdl2:Transition/xpdl2:ConnectorGraphicsInfos">
    <xsl:variable name="origId" select="../@Id"/>
    <xsl:variable name="nodeId" select="translate(../@Id,':','_')"/>
    <xsl:variable name="origTargetId" select="../@To"/>
    <xsl:variable name="targetNodeId" select="translate(../@To,':','_')"/>
    <xsl:variable name="origSourceId" select="../@From"/>
    <xsl:variable name="sourceNodeId" select="translate(../@From,':','_')"/>
    <xsl:variable name="fromX" select="//*/xpdl2:Activity[@Id=$origSourceId]/xpdl2:NodeGraphicsInfos/xpdl2:NodeGraphicsInfo/xpdl2:Coordinates/@XCoordinate"/>
    <xsl:variable name="fromY" select="//*/xpdl2:Activity[@Id=$origSourceId]/xpdl2:NodeGraphicsInfos/xpdl2:NodeGraphicsInfo/xpdl2:Coordinates/@YCoordinate"/>
    <xsl:variable name="toX" select="//*/xpdl2:Activity[@Id=$origTargetId]/xpdl2:NodeGraphicsInfos/xpdl2:NodeGraphicsInfo/xpdl2:Coordinates/@XCoordinate"/>
    <xsl:variable name="toY" select="//*/xpdl2:Activity[@Id=$origTargetId]/xpdl2:NodeGraphicsInfos/xpdl2:NodeGraphicsInfo/xpdl2:Coordinates/@YCoordinate"/>
    <bpmndi:BPMNEdge>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('BPMNEdge_',$nodeId)"/>
      </xsl:attribute>
      <xsl:attribute name="bpmnElement">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="sourceElement">
        <xsl:value-of select="concat('BPMNShape_',$sourceNodeId)"/>
      </xsl:attribute>
      <xsl:attribute name="targetElement">
        <xsl:value-of select="concat('BPMNShape_',$targetNodeId)"/>
      </xsl:attribute>
      <!-- start -->
      <di:waypoint xsi:type="dc:Point">
        <xsl:attribute name="x">
          <xsl:value-of select="$fromX"/>
        </xsl:attribute>
        <xsl:attribute name="y">
          <xsl:value-of select="$fromY"/>
        </xsl:attribute>
      </di:waypoint>
      <!-- end -->
      <di:waypoint xsi:type="dc:Point">
        <xsl:attribute name="x">
          <xsl:value-of select="$toX"/>
        </xsl:attribute>
        <xsl:attribute name="y">
          <xsl:value-of select="$toY"/>
        </xsl:attribute>
      </di:waypoint>
    </bpmndi:BPMNEdge>
  </xsl:template>
</xsl:stylesheet>

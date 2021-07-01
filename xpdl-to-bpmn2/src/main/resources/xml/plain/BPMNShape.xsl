<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="asd">
    <xsl:variable name="nodeId" select="message/@text"/>
    <bpmndi:BPMNShape>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('BPMNShape_',$nodeId)"/>
      </xsl:attribute>
      <xsl:attribute name="bpmnElement">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <dc:Bounds height="30.0" width="30.0">
        <xsl:attribute name="x">
          <xsl:value-of select="@XCoordinate"/>
        </xsl:attribute>
        <xsl:attribute name="y">
          <xsl:value-of select="@YCoordinate"/>
        </xsl:attribute>
      </dc:Bounds>
    </bpmndi:BPMNShape>
  </xsl:template>
  
  <xsl:template match="xpdl:Pools/xpdl:Pool/xpdl:Lanes/xpdl:Lane/xpdl:NodeGraphicsInfos/xpdl:NodeGraphicsInfo">
    <xsl:variable name="origLaneId" select="../../@Id"/>
    <xsl:variable name="laneId" select="translate($origLaneId,':','_')"/>
    <xsl:variable name="origProcessId" select="../../../../@Process"/>
    <xsl:variable name="processId" select="translate($origProcessId,':','_')"/>
    <bpmndi:BPMNShape isHorizontal="true">
      <xsl:attribute name="id">
        <xsl:value-of select="concat('BPMNShape_',$laneId)"/>
      </xsl:attribute>
      <xsl:attribute name="bpmnElement">
        <xsl:value-of select="$laneId"/>
      </xsl:attribute>
      <dc:Bounds x="0.0" y="0.0" width="300.0">
        <xsl:attribute name="height">
          <xsl:value-of select="@Height"/>
        </xsl:attribute>
      </dc:Bounds>
    </bpmndi:BPMNShape>
  </xsl:template>
</xsl:stylesheet>

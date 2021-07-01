<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>

  <xsl:template match="xpdl:Pool">
    <xsl:variable name="origId" select="@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <xsl:variable name="origProcessId" select="@Process"/>
    <xsl:variable name="processId" select="translate($origProcessId,':','_')"/>
    <bpmn2:laneSet>
        <xsl:attribute name="id">
            <xsl:value-of select="$nodeId"/>
        </xsl:attribute>
        <xsl:attribute name="name">
            <xsl:value-of select="$nodeName"/>
        </xsl:attribute>
        <xsl:for-each select="xpdl:Lanes/xpdl:Lane">
            <xsl:variable name="origLaneId" select="@Id"/>
            <xsl:variable name="laneId" select="translate($origLaneId,':','_')"/>
            <xsl:variable name="origLaneName" select="@Name"/>
            <xsl:variable name="laneName" select="translate($origLaneName,':','_')"/>
            <bpmn2:lane>
                <xsl:attribute name="id">
                    <xsl:value-of select="$laneId"/>
                </xsl:attribute>
                <xsl:attribute name="name">
                    <xsl:value-of select="$laneName"/>
                </xsl:attribute>
                <xsl:for-each select="//*/xpdl:WorkflowProcess[@Id=$origProcessId]/xpdl:Activities/xpdl:Activity/xpdl:NodeGraphicsInfos/xpdl:NodeGraphicsInfo[@LaneId=$origLaneId]">
                    <bpmn2:flowNodeRef>
                        <xsl:value-of select="translate(../../@Id,':','_')"/>
                    </bpmn2:flowNodeRef>
                </xsl:for-each>
            </bpmn2:lane>
        </xsl:for-each>
    </bpmn2:laneSet>
  </xsl:template>
</xsl:stylesheet>
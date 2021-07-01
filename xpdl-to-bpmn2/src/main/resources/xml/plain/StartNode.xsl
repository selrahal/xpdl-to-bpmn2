<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="xpdl:Event/xpdl:StartEvent">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:startEvent drools:bgcolor="#9acd32" drools:selectable="true">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:for-each select="//*/xpdl:Transition[@From=$origId]">
        <bpmn2:outgoing>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:outgoing>
      </xsl:for-each>
    </bpmn2:startEvent>
  </xsl:template>
</xsl:stylesheet>

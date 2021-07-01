<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl2="http://www.wfmc.org/2008/XPDL2.1" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="xpdl2:Activity/xpdl2:Implementation/xpdl2:Task[not(*)]">
    <bpmn2:scriptTask drools:selectable="true" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000" scriptFormat="http://www.javascript.com/javascript">
      <xsl:variable name="origId" select="../../@Id"/>
      <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
      <xsl:variable name="origName" select="../../@Name"/>
      <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="drools:taskName">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <bpmn2:incoming>
        <xsl:value-of select="translate(//*/xpdl2:Transition[@To=$origId]/@Id, ':','_')"/>
      </bpmn2:incoming>
      <bpmn2:outgoing>
        <xsl:value-of select="translate(//*/xpdl2:Transition[@From=$origId]/@Id, ':','_')"/>
      </bpmn2:outgoing>
      <bpmn2:script>
        <xsl:value-of select="./xpdl2:Script"/>
      </bpmn2:script>
    </bpmn2:scriptTask>
  </xsl:template>
</xsl:stylesheet>

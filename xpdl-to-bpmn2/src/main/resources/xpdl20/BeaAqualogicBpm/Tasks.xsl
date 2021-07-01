<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"  xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="xpdl:Activity/xpdl:Implementation/xpdl:Task/xpdl:TaskApplication">
    <bpmn2:scriptTask drools:selectable="true" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000" scriptFormat="http://www.javascript.com/javascript">
      <xsl:variable name="origId" select="../../../@Id"/>
      <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
      <xsl:variable name="origName" select="../../../@Name"/>
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
        <xsl:value-of select="translate(//*/xpdl:Transition[@To=$origId]/@Id, ':','_')"/>
      </bpmn2:incoming>
      <bpmn2:outgoing>
        <xsl:value-of select="translate(//*/xpdl:Transition[@From=$origId]/@Id, ':','_')"/>
      </bpmn2:outgoing>
      <bpmn2:script>
        <xsl:value-of select="./xpdl:Script"/>
      </bpmn2:script>
    </bpmn2:scriptTask>
  </xsl:template>

  <xsl:template match="xpdl:Activity/xpdl:Extensions/albpm:ALBPMExtensions/albpm:FeatureSet/albpm:StringFeature[@value='CONNECTOR']">
    <bpmn2:scriptTask drools:selectable="true" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000" scriptFormat="http://www.javascript.com/javascript">
      <xsl:variable name="origId" select="../../../../@Id"/>
      <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
      <xsl:variable name="origName" select="../../../../@Name"/>
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
        <xsl:value-of select="translate(//*/xpdl:Transition[@To=$origId]/@Id, ':','_')"/>
      </bpmn2:incoming>
      <bpmn2:outgoing>
        <xsl:value-of select="translate(//*/xpdl:Transition[@From=$origId]/@Id, ':','_')"/>
      </bpmn2:outgoing>
    </bpmn2:scriptTask>
  </xsl:template>


</xsl:stylesheet>

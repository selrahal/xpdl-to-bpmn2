<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <!-- BPMN: corresponds to a task with unspecified TaskType -->
  <xsl:template match="xpdl:Activity/xpdl:Implementation/xpdl:No">
    <xsl:variable name="origId" select="../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:comment>Below task was created from xpdl:No task type </xsl:comment>
    <bpmn2:scriptTask name="No-op" drools:selectable="true" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000" scriptFormat="http://www.javascript.com/javascript">
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

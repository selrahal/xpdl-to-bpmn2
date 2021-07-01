<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="xpdl:Transitions/xpdl:Transition">
    <xsl:variable name="origId" select="@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origTargetId" select="@To"/>
    <xsl:variable name="targetNodeId" select="translate(@To,':','_')"/>
    <xsl:variable name="origSourceId" select="@From"/>
    <xsl:variable name="sourceNodeId" select="translate(@From,':','_')"/>
    <bpmn2:sequenceFlow>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:attribute name="sourceRef">
        <xsl:value-of select="$sourceNodeId"/>
      </xsl:attribute>
      <xsl:attribute name="targetRef">
        <xsl:value-of select="$targetNodeId"/>
      </xsl:attribute>
      <xsl:if test="./xpdl:Condition/@Type='CONDITION'">
        <bpmn2:conditionExpression xsi:type="bpmn2:tFormalExpression" language="http://www.javascript.com/javascript">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('FormalExpression_', $nodeId)"/>
          </xsl:attribute>
          <xsl:value-of select="./xpdl:Condition"/>
        </bpmn2:conditionExpression>
      </xsl:if>
    </bpmn2:sequenceFlow>
  </xsl:template>
</xsl:stylesheet>

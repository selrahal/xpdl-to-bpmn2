<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <!-- Includes -->
  <xsl:include href="StartNode.xsl"/>
  <xsl:include href="EndNode.xsl"/>
  <xsl:include href="SubProcessNode.xsl"/>
  <xsl:include href="Gateway.xsl"/>
  <xsl:include href="Tasks.xsl"/>
</xsl:stylesheet>

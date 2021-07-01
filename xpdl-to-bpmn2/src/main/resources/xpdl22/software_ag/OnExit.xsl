<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl2="http://www.wfmc.org/2009/XPDL2.2" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:xpdExt="http://www.tibco.com/XPD/xpdExtension1.0.0" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:script"/>
  <xsl:template match="xpdExt:Audit/xpdExt:AuditEvent[@Type='Completed']">
    <drools:onExit-script scriptFormat="http://www.javascript.com/javascript">
      <drools:script>
        <xsl:value-of select="./xpdExt:Information"/>
      </drools:script>
    </drools:onExit-script>
  </xsl:template>
</xsl:stylesheet>

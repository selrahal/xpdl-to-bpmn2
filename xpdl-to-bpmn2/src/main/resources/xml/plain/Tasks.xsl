<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"  xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
	<xsl:template match="task">
		<xsl:variable name="texists" select="./task" />
		<xsl:if test="$texists">
			<bpmn2:adHocSubProcess>
				<xsl:attribute name="name">
        			<xsl:value-of select="./@name" />
      			</xsl:attribute>
				<xsl:attribute name="id">
       				 <xsl:value-of
					select="concat('subprocess-',./@name)" />
     			 </xsl:attribute>
				<xsl:apply-templates select="task" />
			</bpmn2:adHocSubProcess>
		</xsl:if>
		<xsl:if test="not($texists)">
			<bpmn2:scriptTask>
				<xsl:attribute name="name">
        			<xsl:value-of select="./message/@text" />
      			</xsl:attribute>
				<xsl:attribute name="drools:taskName">
       				<xsl:value-of select="@name" />
      			</xsl:attribute>
				<xsl:attribute name="id">
        			<xsl:value-of select="@name" />
      			</xsl:attribute>
			</bpmn2:scriptTask>
		</xsl:if>

		<xsl:apply-templates select="parallelGroup" />
	</xsl:template>
  
  <xsl:template match="parallelGroup">
   <bpmn2:adHocSubProcess >
         <xsl:attribute name="name">
        <xsl:value-of select="concat('Parallel Group ',../@name)"/>
      </xsl:attribute>
       <xsl:attribute name="id">
        <xsl:value-of select="concat('subprocess-pg-',../@name)"/>
      </xsl:attribute>
     <xsl:apply-templates select="parallelItem"/>
    </bpmn2:adHocSubProcess>
  </xsl:template>
  
  <xsl:template match="parallelItem">
        <xsl:apply-templates select="task"/>
  </xsl:template>

  <xsl:template match="xpdl:Activity/xpdl:Extensions/albpm:ALBPMExtensions/albpm:FeatureSet/albpm:StringFeature[@value='CONNECTOR']">
<!--     <bpmn2:scriptTask drools:selectable="true" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000" scriptFormat="http://www.javascript.com/javascript">
 -->      
     <bpmn2:scriptTask >
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

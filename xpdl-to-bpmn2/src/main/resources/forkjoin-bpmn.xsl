<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jpdl="urn:jbpm.org:jpdl-3.2"
  xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:java="http://xml.apache.org/xslt/java"
  extension-element-prefixes="java">

  <!-- Import the pieces of jPDL we need. -->
  <xsl:import href="transition-bpmn.xsl" />

  <xsl:template match="jpdl:fork">
    <parallelGateway>
      <xsl:attribute name="name">
		<xsl:value-of select="@name" />
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="java:org.jbpm.migration.xml.IdGeneratorUtil.translate(@name)" />
      </xsl:attribute>
      <xsl:attribute name="gatewayDirection">
       	<xsl:text>Diverging</xsl:text>
      </xsl:attribute>
      

      <xsl:if test="jpdl:description">
        <xsl:apply-templates select="jpdl:description" />
      </xsl:if>
    </parallelGateway>

    <xsl:apply-templates select="jpdl:transition" />
  </xsl:template>

  <xsl:template match="jpdl:join">
    <parallelGateway>
      <xsl:attribute name="name">
		<xsl:value-of select="@name" />
	  </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="java:org.jbpm.migration.xml.IdGeneratorUtil.translate(@name)" />
      </xsl:attribute>
      <xsl:attribute name="gatewayDirection">
       	<xsl:text>Converging</xsl:text>
      </xsl:attribute>
      
      <xsl:if test="jpdl:description">
        <xsl:apply-templates select="jpdl:description" />
      </xsl:if>      
    </parallelGateway>

    <xsl:apply-templates />
  </xsl:template>

  <!-- Removes description element from the transformation. -->
  <xsl:template match="jpdl:description" />
</xsl:stylesheet>

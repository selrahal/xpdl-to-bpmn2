<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:albpm="http://www.albpm.com/2007/albpm.XPDL2" xmlns:xpdl2="http://www.wfmc.org/2008/XPDL2.1" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:color="http://www.omg.org/spec/BPMN/non-normative/color" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:xpdExt="http://www.tibco.com/XPD/xpdExtension1.0.0" version="1.0">
  <xsl:output method="xml" indent="yes" cdata-section-elements="drools:metaValue"/>
  <xsl:template match="xpdl2:Activity/xpdl2:Implementation/xpdl2:Task/xpdl2:TaskUser">
    <xsl:variable name="origId" select="../../../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <xsl:variable name="origName" select="../../../@Name"/>
    <xsl:variable name="nodeName" select="translate($origName,':','_')"/>
    <bpmn2:userTask drools:selectable="true" drools:scriptFormat="http://www.java.com/java" color:background-color="#fafad2" color:border-color="#000000" color:color="#000000">
      <xsl:attribute name="name">
        <xsl:value-of select="$nodeName"/>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$nodeId"/>
      </xsl:attribute>
      <xsl:apply-templates select="../../../xpdExt:AssociatedParameters"/>
      <xsl:apply-templates select="../../../xpdl2:Performers/xpdl2:Performer"/>
      <bpmn2:extensionElements>
        <xsl:apply-templates select="../../xpdExt:Audit/xpdExt:AuditEvent[@Type='Initiated']"/>
        <xsl:apply-templates select="../../xpdExt:Audit/xpdExt:AuditEvent[@Type='Completed']"/>
      </bpmn2:extensionElements>
      <xsl:for-each select="//*/xpdl2:Transition[@To=$origId]">
        <bpmn2:incoming>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:incoming>
      </xsl:for-each>
      <xsl:for-each select="//*/xpdl2:Transition[@From=$origId]">
        <bpmn2:outgoing>
          <xsl:value-of select="translate(./@Id,':','_')"/>
        </bpmn2:outgoing>
      </xsl:for-each>
    </bpmn2:userTask>
  </xsl:template>

<!--  until we get https://issues.redhat.com/browse/KOGITO-5731
  <xsl:template match="xpdExt:AssociatedParameters">
    <xsl:variable name="origId" select="../@Id"/>
    <xsl:variable name="nodeId" select="translate($origId,':','_')"/>
    <bpmn2:ioSpecification>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('InputOutputSpecification_', $nodeId)"/>
      </xsl:attribute>
      <bpmn2:dataInput itemSubjectRef="_String" name="TaskName">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_TaskName')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_Integer" name="Priority">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Priority')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_String" name="Comment">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Comment')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_String" name="Description">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Description')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_String" name="GroupId">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_GroupId')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_Boolean" name="Skippable">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Skippable')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_String" name="Content">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Content')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_String" name="Locale">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Locale')"/>
        </xsl:attribute>
      </bpmn2:dataInput>
      <bpmn2:dataInput itemSubjectRef="_String" name="CreatedBy">
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_CreatedBy')"/>
        </xsl:attribute>
      </bpmn2:dataInput>

      <xsl:for-each select="xpdExt:AssociatedParameter[@Mode='IN'] | xpdExt:AssociatedParameter[@Mode='INOUT']">
        <bpmn2:dataInput itemSubjectRef="_String">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('DataInput_', $nodeId, '_', @FormalParam)"/>
          </xsl:attribute>
          <xsl:attribute name="name">
            <xsl:value-of select="@FormalParam"/>
          </xsl:attribute>
        </bpmn2:dataInput>
      </xsl:for-each>
      <xsl:for-each select="xpdExt:AssociatedParameter[@Mode='OUT'] | xpdExt:AssociatedParameter[@Mode='INOUT']">
        <bpmn2:dataOutput itemSubjectRef="_String">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('DataOutput_', $nodeId, '_', @FormalParam)"/>
          </xsl:attribute>
          <xsl:attribute name="name">
            <xsl:value-of select="@FormalParam"/>
          </xsl:attribute>
        </bpmn2:dataOutput>
      </xsl:for-each>
      <bpmn2:inputSet>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('_InputSet_', $nodeId)"/>
        </xsl:attribute>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_TaskName')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_Priority')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_Comment')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_Description')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_GroupId')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_Skippable')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_Content')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_Locale')"/></bpmn2:dataInputRefs>
        <bpmn2:dataInputRefs><xsl:value-of select="concat('TaskInput_', $nodeId, '_CreatedBy')"/></bpmn2:dataInputRefs>
        <xsl:for-each select="xpdExt:AssociatedParameter[@Mode='IN'] | xpdExt:AssociatedParameter[@Mode='INOUT']">
          <bpmn2:dataInputRefs>
              <xsl:value-of select="concat('DataInput_', $nodeId, '_', @FormalParam)"/>
          </bpmn2:dataInputRefs>
        </xsl:for-each>
      </bpmn2:inputSet>
      <bpmn2:outputSet>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('_OutputSet_', $nodeId)"/>
        </xsl:attribute>
        <xsl:for-each select="xpdExt:AssociatedParameter[@Mode='OUT'] | xpdExt:AssociatedParameter[@Mode='INOUT']">
          <bpmn2:dataOutputRefs>
            <xsl:value-of select="concat('DataOutput_', $nodeId, '_', @FormalParam)"/>
          </bpmn2:dataOutputRefs>
        </xsl:for-each>
      </bpmn2:outputSet>
    </bpmn2:ioSpecification>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_TaskName')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_TaskName')"/></bpmn2:targetRef>
      <bpmn2:assignment>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskAssignment_', $nodeId, '_TaskName')"/>
        </xsl:attribute>
        <bpmn2:from xsi:type="bpmn2:tFormalExpression" >
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_From_', $nodeId, '_TaskName')"/>
          </xsl:attribute>
          Task Name
        </bpmn2:from>
        <bpmn2:to xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_To_', $nodeId, '_TaskName')"/>
          </xsl:attribute>
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_TaskName')"/>
        </bpmn2:to>
      </bpmn2:assignment>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_Priority')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_Priority')"/></bpmn2:targetRef>
      <bpmn2:assignment>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskAssignment_', $nodeId, '_Priority')"/>
        </xsl:attribute>
        <bpmn2:from xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_From_', $nodeId, '_Priority')"/>
          </xsl:attribute>
          1
        </bpmn2:from>
        <bpmn2:to xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_To_', $nodeId, '_Priority')"/>
          </xsl:attribute>
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Priority')"/>
        </bpmn2:to>
      </bpmn2:assignment>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_Comment')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_Comment')"/></bpmn2:targetRef>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_Description')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_Description')"/></bpmn2:targetRef>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_GroupId')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_GroupId')"/></bpmn2:targetRef>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_Skippable')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_Skippable')"/></bpmn2:targetRef>
      <bpmn2:assignment>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskAssignment_', $nodeId, '_Skippable')"/>
        </xsl:attribute>
        <bpmn2:from xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_From_', $nodeId, '_Skippable')"/>
          </xsl:attribute>
          true
        </bpmn2:from>
        <bpmn2:to xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_To_', $nodeId, '_Skippable')"/>
          </xsl:attribute>
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Skippable')"/>
        </bpmn2:to>
      </bpmn2:assignment>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_Content')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_Content')"/></bpmn2:targetRef>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_Locale')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_Locale')"/></bpmn2:targetRef>
      <bpmn2:assignment>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('TaskAssignment_', $nodeId, '_Locale')"/>
        </xsl:attribute>
        <bpmn2:from xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_From_', $nodeId, '_Locale')"/>
          </xsl:attribute>
          en-US
        </bpmn2:from>
        <bpmn2:to xsi:type="bpmn2:tFormalExpression">
          <xsl:attribute name="id">
            <xsl:value-of select="concat('TaskExpression_To_', $nodeId, '_Locale')"/>
          </xsl:attribute>
          <xsl:value-of select="concat('TaskInput_', $nodeId, '_Locale')"/>
        </bpmn2:to>
      </bpmn2:assignment>
    </bpmn2:dataInputAssociation>
    <bpmn2:dataInputAssociation>
      <xsl:attribute name="id">
        <xsl:value-of select="concat('TaskInputAssociation_', $nodeId, '_CreatedBy')"/>
      </xsl:attribute>
      <bpmn2:targetRef><xsl:value-of select="concat('TaskInput_', $nodeId, '_CreatedBy')"/></bpmn2:targetRef>
    </bpmn2:dataInputAssociation>
    <xsl:for-each select="xpdExt:AssociatedParameter[@Mode='IN'] | xpdExt:AssociatedParameter[@Mode='INOUT']">
      <bpmn2:dataInputAssociation>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('DataInputAssociation_', $nodeId,  '_', @FormalParam)"/>
        </xsl:attribute>
        <bpmn2:sourceRef>
          <xsl:value-of select="@FormalParam"/>
        </bpmn2:sourceRef>
        <bpmn2:targetRef>
          <xsl:value-of select="concat('DataInput_', $nodeId, '_', @FormalParam)"/>
        </bpmn2:targetRef>
      </bpmn2:dataInputAssociation>
    </xsl:for-each>
    <xsl:for-each select="xpdExt:AssociatedParameter[@Mode='OUT'] | xpdExt:AssociatedParameter[@Mode='INOUT']">
      <bpmn2:dataOutputAssociation>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('DataOutputAssociation_', $nodeId, '_', @FormalParam)"/>
        </xsl:attribute>
        <bpmn2:sourceRef>
          <xsl:value-of select="@FormalParam"/>
        </bpmn2:sourceRef>
        <bpmn2:targetRef>
          <xsl:value-of select="concat('DataOutput_', $nodeId, '_', @FormalParam)"/>
        </bpmn2:targetRef>
      </bpmn2:dataOutputAssociation>
    </xsl:for-each>
  </xsl:template>
-->
  <xsl:template match="xpdl2:Performers/xpdl2:Performer">
    <xsl:variable name="origPerformer" select="text()"/>
    <xsl:variable name="performer" select="translate($origPerformer,':','_')"/>
    <bpmn2:potentialOwner name="Potential Owner" id="PotentialOwner">
      <bpmn2:resourceAssignmentExpression id="ResourceAssignmentExpression">
        <bpmn2:formalExpression id="FormalExpression_Owner">
          <xsl:value-of select="//xpdl2:Participants/xpdl2:Participant[@Id=$origPerformer]/@Name"/>
        </bpmn2:formalExpression>
      </bpmn2:resourceAssignmentExpression>
    </bpmn2:potentialOwner>
  </xsl:template>

</xsl:stylesheet>




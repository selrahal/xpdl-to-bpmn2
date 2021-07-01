package org.jbpm.migration.processor.xpdl;

/**
 * Processor to migrate Software AG XPDL 2.2 files to BPMN2
 */
public class SoftwareAG22Processor extends AbstractXSLTBasedProcessor {
	@Override
	protected String getXSLTLocation() {
		return "xpdl22/software_ag/XPDLtoBPMN2.xsl";
	}
}
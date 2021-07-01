package org.jbpm.migration.processor.xpdl;

/**
 * Processor to migrate TIBCO XPDL 2.1 files to BPMN2
 */
public class TibcoXPDL21Processor extends AbstractXSLTBasedProcessor {
	@Override
	protected String getXSLTLocation() {
		return "xpdl21/tibco/XPDLtoBPMN2.xsl";
	}
}

package org.jbpm.migration.processor.xpdl;

/**
 * Processor to migrate TIBCO XPDL 2.1 files to BPMN2
 */
public class JPDL32Processor extends AbstractXSLTBasedProcessor {
	@Override
	protected String getXSLTLocation() {
		return "jpdl3-bpmn2.xsl";
	}
}

package org.jbpm.migration.processor.xpdl;

/**
 * Processor to migrate BlueworksLive XPDL 2.1 files to BPMN2
 */
public class BlueworksLiveXPDL21 extends AbstractXSLTBasedProcessor {
	@Override
	protected String getXSLTLocation() {
		return "xpdl21/BlueworksLive/XPDLtoBPMN2.xsl";
	}
}

package org.jbpm.migration.processor.xpdl;

/**
 * Processor to migrate Bea AquaLogic XPDL 2.0 files to BPMN2
 */
public class BeaAquaLogicBpm20Processor extends AbstractXSLTBasedProcessor {
	@Override
	protected String getXSLTLocation() {
		return "xpdl20/BeaAqualogicBpm/XPDLtoBPMN2.xsl";
	}
}

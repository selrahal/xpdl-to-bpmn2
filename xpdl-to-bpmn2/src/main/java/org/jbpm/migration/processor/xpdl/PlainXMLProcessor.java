package org.jbpm.migration.processor.xpdl;

import org.jbpm.migration.processor.dom.ForceLayoutShapesProcessor;

/**
 * Processor to migrate "Plain" XML files to BPMN2
 */
public class PlainXMLProcessor extends AbstractXSLTBasedProcessor {
	public PlainXMLProcessor() {
		super();
	}
	@Override
	protected String getXSLTLocation() {
		return "xml/plain/XMLtoBPMN2.xsl";
	}
}

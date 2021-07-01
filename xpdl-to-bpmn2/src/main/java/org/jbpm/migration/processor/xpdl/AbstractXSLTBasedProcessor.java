package org.jbpm.migration.processor.xpdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.jbpm.migration.processor.MigrationProcessor;
import org.jbpm.migration.processor.dom.ApplyXSLTransformationProcessor;
import org.jbpm.migration.processor.dom.DomProcessor;
import org.jbpm.migration.processor.dom.EnsureShapesExistProcessor;
import org.jbpm.migration.processor.dom.GenerateBPMNEdgeProcessor;
import org.jbpm.migration.processor.dom.GenerateProcessVariablesProcessor;
import org.jbpm.migration.processor.dom.GenerateSequenceFlowProcessor;
import org.jbpm.migration.processor.dom.GenerateStartEndEventsProcessor;
import org.jbpm.migration.processor.dom.GenerateUniqueIoSpecificationIdProcessor;
import org.jbpm.migration.processor.dom.GenerateUniqueUserTaskIdProcessor;
import org.jbpm.migration.processor.dom.MultiSourceToDivergingGatewayProcessor;
import org.jbpm.migration.processor.dom.MultiTargetToConvergingGatewayProcessor;
import org.jbpm.migration.processor.dom.OverlappingPoolProcessor;
import org.jbpm.migration.processor.dom.ResizePoolProcessor;
import org.jbpm.migration.processor.dom.SanityCheckProcessor;
import org.jbpm.migration.processor.dom.SignalsProcessor;
import org.jbpm.migration.processor.dom.SizeShapesProcessor;
import org.jbpm.migration.processor.stream.SplitProcessesProcessor;
import org.jbpm.migration.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Processor to migrate XML files to BPMN2, starting with an XSLT transformation then a series
 * of DomProcessors
 */
public abstract class AbstractXSLTBasedProcessor extends MigrationProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractXSLTBasedProcessor.class);
	protected abstract String getXSLTLocation();
	private final ApplyXSLTransformationProcessor applyXSLTransformationProcessor;
	private final SplitProcessesProcessor splitProcessesProcessor;
	protected final List<DomProcessor> processors;
	
	public AbstractXSLTBasedProcessor() {
		this.applyXSLTransformationProcessor = new ApplyXSLTransformationProcessor(getXSLTLocation());
		this.splitProcessesProcessor = new SplitProcessesProcessor();
		this.processors = new ArrayList<>(Arrays.asList(
				new GenerateUniqueUserTaskIdProcessor(),
				new GenerateStartEndEventsProcessor(),
				new GenerateSequenceFlowProcessor(),
				new GenerateProcessVariablesProcessor(),
				new SignalsProcessor(),
				new GenerateUniqueIoSpecificationIdProcessor(),
				new MultiTargetToConvergingGatewayProcessor(),
				new MultiSourceToDivergingGatewayProcessor(),
				new EnsureShapesExistProcessor(),
				new SizeShapesProcessor(),
				new ResizePoolProcessor(),
				new OverlappingPoolProcessor(),
				new GenerateBPMNEdgeProcessor(),
				new SanityCheckProcessor()
				));
	}
	
	public Stream<Document> process(Document doc) {
		Stream<Document> stream = Stream.of(doc)
		.map(applyXSLTransformationProcessor::process)
		.flatMap(splitProcessesProcessor::process);
		for (DomProcessor processor : this.processors) {
			stream = stream
			  .map(processor::process)
			  .map(this::debugLog);
		}
		return stream;
	}
	
	private Document debugLog(Document bpmn) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(XmlUtils.toString(bpmn));
		}
		return bpmn;
	}
}

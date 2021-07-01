package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.joox.JOOX;
import org.w3c.dom.Element;

/**
 * Generates missing sequenceflow by guessing from xml element order.
 * 
 *
 */
public class GenerateSequenceFlowProcessor extends SubprocessDomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateSequenceFlowProcessor.class);

	public void process(Element processOrSubprocess) {

		Queue<Element> nI = new LinkedList<Element>();
		Queue<Element> nO = new LinkedList<Element>();

		List<Element> sequenceFlows = $(processOrSubprocess).children("sequenceFlow").get();
		
		// start node needs one outgoing flow startEvent
		List<Element> startEvents = $(processOrSubprocess).children("startEvent").get();
		if (!hasOutgoingFlow(startEvents.get(0), sequenceFlows)) {
			LOG.debug(" No outgoing ref from startevent");
			nO.add(startEvents.get(0));
		}

			for (Element element : $(processOrSubprocess).children(JOOX.or(JOOX.tag("scriptTask"),JOOX.tag( "userTask"),JOOX.tag( "task"),JOOX.tag( "serviceTask"),JOOX.tag( "callActivity"), JOOX.tag("adHocSubProcess"))).get()) {
				if (!hasOutgoingFlow(element, sequenceFlows)) {
					LOG.debug(" No outgoing ref from " + element.getAttribute("id"));
					nO.add(element);
				}

				if (!hasIncomingFlow(element, sequenceFlows)) {
					LOG.debug(" No incoming ref from " + element.getAttribute("id"));
					nI.add(element);
				}

			}

		// end node needs one incoming flow
		List<Element> endEvents = $(processOrSubprocess).children("endEvent").get();
		if (!hasIncomingFlow(endEvents.get(0), sequenceFlows)) {
			LOG.debug(" No incoming ref for endevent");
			nI.add(endEvents.get(0));
		}
		
		
		// Add missing sequence flows

		while (!nO.isEmpty()) {
			Element needsOutgoing = nO.poll();
			if (!nI.isEmpty()) {
				Element needsIncoming = nI.poll();
				LOG.info(" Generating sequence flow " + needsOutgoing.getAttribute("id") + " -> " + needsIncoming.getAttribute("id"));
				String sequenceFlowRef =  needsOutgoing.getAttribute("id") + "_" + needsIncoming.getAttribute("id");
				$(processOrSubprocess).append($("bpmn2:sequenceFlow")
						.attr("id", sequenceFlowRef)
						.attr("targetRef",needsIncoming.getAttribute("id"))
						.attr("sourceRef",needsOutgoing.getAttribute("id")));
				$(needsOutgoing).append($("bpmn2:outgoing").text(sequenceFlowRef));
				$(needsIncoming).append($("bpmn2:incoming").text(sequenceFlowRef));
			} else {
				LOG.error(needsOutgoing.getAttribute("id") + " needs outgoing, no nodes left");
			}
		}

		if (!nI.isEmpty()) {
			while (!nI.isEmpty()) {
				Element needsIncoming = nI.poll();
				LOG.error(needsIncoming.getAttribute("id") + " needs incoming, no nodes left");
			}
		}

	}
	
	private boolean hasOutgoingFlow(Element target, List<Element> sequenceFlows) {
		return hasFlow("sourceRef", target, sequenceFlows);
	}
	
	private boolean hasIncomingFlow(Element target, List<Element> sequenceFlows) {
		return hasFlow("targetRef", target, sequenceFlows);
	}
	
	private boolean hasFlow(String direction, Element target, List<Element> sequenceFlows) {
		if (target.getAttribute("id") == null) return false;
		
		boolean foundOutgoingFlow = false;
		
		for (Element flow : sequenceFlows) {
			String sourceRef = flow.getAttribute(direction);
			if (target.getAttribute("id").equals(sourceRef)) {
				foundOutgoingFlow = true;
				break;
			}
		}
		
		return foundOutgoingFlow;
	}

}

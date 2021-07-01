package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * Generates missing sequenceflow by guessing from xml element order.
 * 
 *
 */
public class GenerateStartEndEventsProcessor extends SubprocessDomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateStartEndEventsProcessor.class);

	public void process(Element processOrSubprocess) {
		// need exactly one start event
		List<Element> startEvents = $(processOrSubprocess).children("startEvent").get();
		String processRef = processOrSubprocess.getAttribute("id");
		if (startEvents.isEmpty()) {
			LOG.info("Generating start event for " + processRef);
			$(processOrSubprocess).prepend($("bpmn2:startEvent").attr("id", "startevent_" + processRef));
		} else if (startEvents.size() > 1) {
			LOG.error("More than one start event, no good");
		}
		
		// need exactly one end event
		List<Element> endEvents = $(processOrSubprocess).children("endEvent").get();
		if (endEvents.isEmpty()) {
			LOG.info("Generating end event for "+ processRef);
			$(processOrSubprocess).append($("bpmn2:endEvent").attr("id", "endevent_" + processRef));
		} else if (endEvents.size() > 1) {
			LOG.error("More than one end event, no good");
		}
	}

}

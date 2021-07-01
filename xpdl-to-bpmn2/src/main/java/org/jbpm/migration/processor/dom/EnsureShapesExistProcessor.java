package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.xml.XmlUtils;
import org.joox.JOOX;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Sets the size of the standard elements; pads certain elements from the GPD to 
 * the BPMN2.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class EnsureShapesExistProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(EnsureShapesExistProcessor.class);
	
	private Map<String, Double> nodeSizes = new HashMap<String, Double>();
	
    public EnsureShapesExistProcessor() {
    	this.setSize("startEvent", 30.0)
    	.setSize("endEvent", 28.0)
    	.setSize("exclusiveGateway", 40.0)
    	.setSize("parallelGateway", 40.0)
    	.setSize("intermediateCatchEvent", 40.0)
    	.setSize("intermediateThrowEvent", 40.0)
    	.setSize("scriptTask", 80.0)
    	.setSize("userTask", 80.0)
    	.setSize("task", 80.0)
    	.setSize("serviceTask", 80.0)
    	.setSize("adHocSubProcess", 240.0)
    	.setSize("callActivity", 80.0);
    }
    
	public EnsureShapesExistProcessor setSize(String nodeName, Double size) {
		nodeSizes.put(nodeName, size);
		return this;
	}
	
	@Override
	public Document process(Document bpmn) {
		//If there is not a BPMNEdge for this flowId, create it
		double y = 0.0;
		LinkedList<OffsetElement> toCheck = new LinkedList<>();
		for (Element element : $(bpmn).find("process").children().filter(JOOX.not(JOOX.tag("sequenceFlow"))).get()) {
			OffsetElement toAdd = new OffsetElement();
			toAdd.element = element;
			toAdd.offset = 0.0;
			toCheck.add(toAdd);
		}
		//elementsToCheck.addAll( $(bpmn).find("adHocSubProcess").children().filter(JOOX.not(JOOX.tag("sequenceFlow"))).get());
		
		while (!toCheck.isEmpty()) {
			OffsetElement oe = toCheck.poll();
			if (oe.element.getTagName().equals("bpmn2:adHocSubProcess")) {
				LOG.info("Found subprocess " + oe.element.getAttribute("id"));
				List<OffsetElement> oes = new ArrayList<>();
				for (Element element : $(oe.element).children().filter(JOOX.not(JOOX.tag("sequenceFlow"))).get()) {
					if (!Strings.isNullOrEmpty(element.getAttribute("id"))) {
						LOG.info("-- adding " + element.getAttribute("id"));
						OffsetElement toAdd = new OffsetElement();
						toAdd.element = element;
						toAdd.offset = oe.offset + 20.0;
						oes.add(toAdd);
					} else {
						LOG.error("!!!!empty id element found in subprocess:" + element.getTextContent());
					}
				}
				toCheck.addAll(0, oes);
			} else {
				LOG.info("Not subprocess " + oe.element.getAttribute("id") + " tag:" + oe.element.getTagName());
			}
			
			String elementRef = $(oe.element).attr("id");
			if (elementRef != null && findBpmnShapeName(bpmn, elementRef) == null) {
				LOG.info("Creating BPMNShape for: " + elementRef + " x=" + oe.offset + ", y=" + y);
				addBpmnShape(bpmn, elementRef,oe.offset, y);
				y += 100.0;
			}
		}
		
		return bpmn;
	}
	private String findBpmnShapeName(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).first().attr("id");
		if(StringUtils.isNoneBlank(bpmnShapeId)) {
			return bpmnShapeId;
		}
		
		return null;
	}
	
	private void addBpmnShape(Document document, String nodeId, double x,double y) {
		$(document).find("BPMNPlane").first().append(
				$("bpmndi:BPMNShape")
					.attr("id", "BPMNShape_".concat(nodeId))
					.attr("bpmnElement", nodeId)
					.attr("isHorizontal", "true")
					.append($("dc:Bounds")
							.attr("x", Double.toString(x))
							.attr("y", Double.toString(y))
					)
				);
	}
	
	private class OffsetElement {
		double offset;
		Element element;
	}
	
}

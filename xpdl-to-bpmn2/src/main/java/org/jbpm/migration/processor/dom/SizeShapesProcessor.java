package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sets the size of the standard elements; pads certain elements from the GPD to 
 * the BPMN2.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class SizeShapesProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(SizeShapesProcessor.class);
	
	private Map<String, Double> nodeWidths = new HashMap<String, Double>();
	private Map<String, Double> nodeHeights = new HashMap<String, Double>();

	
    public SizeShapesProcessor() {
    	this.setSize("startEvent", 30.0)
    	.setSize("endEvent", 28.0)
    	.setSize("exclusiveGateway", 40.0)
    	.setSize("parallelGateway", 40.0)
    	.setSize("intermediateCatchEvent", 40.0)
    	.setSize("intermediateThrowEvent", 40.0)
    	.setSize("scriptTask", 200.0, 50.0)
    	.setSize("userTask", 80.0)
    	.setSize("task", 80.0)
    	.setSize("serviceTask", 80.0)
    	.setSize("callActivity", 80.0);
    }
    
	public SizeShapesProcessor setSize(String nodeName, Double size) {
		this.setSize(nodeName, size, size);
		return this;
	}
    
	public SizeShapesProcessor setSize(String nodeName, Double width, Double height) {
		nodeWidths.put(nodeName, width);
		nodeHeights.put(nodeName, height);
		return this;
	}
	
	@Override
	public Document process(Document bpmn) {
		
		for (Entry<String, Double> entry : nodeWidths.entrySet()) {
			for(Element sequenceFlow : $(bpmn).find(entry.getKey()).get()) {
				//find the BPMNShape with the source and targets
				String id = $(sequenceFlow).attr("id");
				LOG.info("Setting BPMN shape " + id + " width to " + entry.getValue());
				setBpmnShapeWidth(bpmn, id, entry.getValue());
			}
		}
		
		for (Entry<String, Double> entry : nodeHeights.entrySet()) {
			for(Element sequenceFlow : $(bpmn).find(entry.getKey()).get()) {
				//find the BPMNShape with the source and targets
				String id = $(sequenceFlow).attr("id");
				LOG.info("Setting BPMN shape " + id + " height to " + entry.getValue());
				setBpmnShapeHeight(bpmn, id, entry.getValue());
			}
		}
		
		//calculate height for adHocSubProcess
		for(Element subprocess : $(bpmn).find("adHocSubProcess").get()) {
			double xBound = 0.0;
			double yBound = 0.0;
			for (Element child : $(subprocess).children().get()) {
				Element shape = this.findBpmnShapeName(bpmn, child.getAttribute("id"));
				if (shape == null) {
					LOG.error("id=" + child.getAttribute("id"));
				} else {
					try {
					double x = Double.parseDouble($(shape).child("Bounds").attr("x"));
					double y = Double.parseDouble($(shape).child("Bounds").attr("y"));
					double width = Double.parseDouble($(shape).child("Bounds").attr("width"));
					double height = Double.parseDouble($(shape).child("Bounds").attr("height"));
					if (x + width > xBound)
						xBound = 10.0 + x + width;
					if (y + height > yBound)
						yBound = 10.0 + y + height;
					} catch (NullPointerException npe) {
						
					}
				}
			}
			
			//find the BPMNShape with the source and targets
			String id = $(subprocess).attr("id");
			Element shape = this.findBpmnShapeName(bpmn, id);
			double x = Double.parseDouble($(shape).child("Bounds").attr("x"));
			double y = Double.parseDouble($(shape).child("Bounds").attr("y"));
			double newWidth = xBound - x;
			double newHeight = yBound - y;
			LOG.info("Setting BPMN shape " + id + " height to " + newHeight + " , width to " + newWidth);
			setBpmnShapeHeight(bpmn, id, newHeight);
			setBpmnShapeWidth(bpmn, id, newWidth);
		}
		
		return bpmn;
	}
	
	private void setBpmnShapeWidth(Document bpmn, String sourceRef, Double width) {
		$(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("width", width.toString());
	}
	private void setBpmnShapeHeight(Document bpmn, String sourceRef, Double height) {
		$(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).child("Bounds").attr("height", height.toString());
	}
	private Element findBpmnShapeName(Document bpmn, String sourceRef) {
		return $(bpmn).find("BPMNShape").filter(attr("bpmnElement", sourceRef)).first().get(0);
	}
	
}

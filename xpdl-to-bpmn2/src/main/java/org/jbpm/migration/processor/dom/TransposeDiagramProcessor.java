package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Flips diagram based on location of the start node.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class TransposeDiagramProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(TransposeDiagramProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		boolean transposeLeft = false;
		boolean transposeUp = false;
		
		//check where the start is
		Double startX = findStartX(bpmn);
		Double startY = findStartY(bpmn);
		
		
		Double midX = findMidX(bpmn);
		Double midY = findMidY(bpmn);
		
		if(startX > midX) {
			transposeLeft = true;
		}
		if(startY > midY) {
			transposeUp = true;
		}
		repositionToMid(bpmn, midX, midY);
		if(transposeLeft) {
			transposeHorizantal(bpmn);
		}
		if(transposeUp) {
			transposeVertical(bpmn);
		}
		
		return bpmn;
	}
	
	public void transposeHorizantal(Document bpmn) {
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double x = Double.parseDouble($(shape).child("Bounds").attr("x"));
			x = x * (-1);
			$(shape).child("Bounds").attr("x", x.toString());
		}
	}

	public void transposeVertical(Document bpmn) {
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double y = Double.parseDouble($(shape).child("Bounds").attr("y"));
			y = y * (-1);
			$(shape).child("Bounds").attr("y", y.toString());
		}
	}
	
	public void repositionToMid(Document bpmn, Double midX, Double midY) {
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double x = Double.parseDouble($(shape).child("Bounds").attr("x"));
			Double y = Double.parseDouble($(shape).child("Bounds").attr("y"));
			
			x = x - midX;
			y = y - midY;
			$(shape).child("Bounds").attr("x", x.toString());
			$(shape).child("Bounds").attr("y", y.toString());
		}
	}
	
	private Double findStartY(Document bpmn) {
		String startEventName = $(bpmn).find("startEvent").first().attr("id");
		for(Element shape : $(bpmn).find("BPMNShape").filter(attr("bpmnElement", startEventName))) {
			Double val = Double.parseDouble($(shape).child("Bounds").attr("y"));
			return val;
		}
		
		return null;
	}
	private Double findStartX(Document bpmn) {
		String startEventName = $(bpmn).find("startEvent").first().attr("id");
		for(Element shape : $(bpmn).find("BPMNShape").filter(attr("bpmnElement", startEventName))) {
			Double val = Double.parseDouble($(shape).child("Bounds").attr("x"));
			return val;
		}
		
		return null;
	}
	
	private Double findMinX(Document bpmn) {
		Double val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double currentVal = Double.parseDouble($(shape).child("Bounds").attr("x"));
			
			if(val == null || val < currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	private Double findMinY(Document bpmn) {
		Double val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double currentVal = Double.parseDouble($(shape).child("Bounds").attr("y"));
			
			if(val == null || val > currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	private Double findMaxX(Document bpmn) {
		Double val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double currentVal = Double.parseDouble($(shape).child("Bounds").attr("x"));
			
			if(val == null || val > currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	private Double findMaxY(Document bpmn) {
		Double val = null;
		for(Element shape : $(bpmn).find("BPMNShape").get()) {
			Double currentVal = Double.parseDouble($(shape).child("Bounds").attr("y"));
			
			if(val == null || val < currentVal) {
				val = currentVal;
			}
		}
		
		return val;
	}
	
	
	private Double findMidX(Document bpmn) {
		Double max = findMaxX(bpmn);
		Double min = findMinX(bpmn);
		
		Double mid = (min+max)/2;
		return mid;
	}
	private Double findMidY(Document bpmn) {
		Double max = findMaxY(bpmn);
		Double min = findMinY(bpmn);
		
		Double mid = (min+max)/2;
		return mid;
	}

}

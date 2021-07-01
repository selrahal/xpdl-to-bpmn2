package org.jbpm.migration.processor.dom;

import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;

import java.util.List;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

/**
 * Ensures no pools are overlapping
 * 
 *
 */
public class OverlappingPoolProcessor implements DomProcessor {
	
	private static final Logger LOG = Logger.getLogger(OverlappingPoolProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		double lowestBlankY = 0;
		double largestWidth = Double.MIN_VALUE;
		
		//Should not be parrallelized, we are using the lowestBlankY to keep track of the position
		List<Match> lanes = $(bpmn).find("lane").each();
		
		for (Match lane : lanes) {
			Match bpmnShape = $(bpmn).find("BPMNShape").filter(attr("bpmnElement",lane.attr("id")));
			double oldX = Double.parseDouble(bpmnShape.child().attr("x") == null ? "0":bpmnShape.child().attr("x"));
			double oldY = Double.parseDouble(bpmnShape.child().attr("y") == null ? "0":bpmnShape.child().attr("y"));
			double deltaX = 0 - oldX;
			double deltaY = lowestBlankY - oldY;
			LOG.debug("Moving lane " + lane.attr("id") + " by " + deltaX + "," + deltaY);
			moveElements(bpmn, lane.attr("id"), deltaX, deltaY);
			lowestBlankY += Double.parseDouble(bpmnShape.child().attr("height") == null ? "0":bpmnShape.child().attr("height"));
			
			Double currentWidth = Double.parseDouble(bpmnShape.child().attr("width") == null ? "0":bpmnShape.child().attr("width"));
			if (largestWidth < currentWidth) {
				largestWidth = currentWidth;
			}
		}
		
		//Set the width of every pool to the max width we have seen so far
		for (Match lane : lanes) {
			Match bpmnShape = $(bpmn).find("BPMNShape").filter(attr("bpmnElement",lane.attr("id")));
			bpmnShape.child().attr("width",Double.toString(largestWidth));
		}
		
		//Reset the sequence flows
		List<Match> edges = $(bpmn).find("BPMNEdge").each();
		for (Match edge : edges) {
			Match sourceElement = $(bpmn).find("BPMNShape").filter(attr("id",edge.attr("sourceElement")));
			Match targetElement = $(bpmn).find("BPMNShape").filter(attr("id",edge.attr("targetElement")));
			
			
			edge.child(0).attr("x", sourceElement.child().attr("x"));
			edge.child(0).attr("y", sourceElement.child().attr("y"));
			
			edge.child(1).attr("x", targetElement.child().attr("x"));
			edge.child(1).attr("y", targetElement.child().attr("y"));
		}
		
		
		return bpmn;
	}
	
	private void moveElements(Document bpmn, String laneId, double deltaX, double deltaY) {
		
		//first move the actual swim lane
		Match bpmnShape = $(bpmn).find("BPMNShape").filter(attr("bpmnElement",laneId));
		double oldX = Double.parseDouble(bpmnShape.child().attr("x") == null ? "0":bpmnShape.child().attr("x"));
		double oldY = Double.parseDouble(bpmnShape.child().attr("y") == null ? "0":bpmnShape.child().attr("y"));
		
		bpmnShape.child().attr("x", Double.toString(oldX + deltaX));
		bpmnShape.child().attr("y", Double.toString(oldY + deltaY));
		
		//Find all of the FlowNodeRef's for the give laneId
		List<Match> flowNodeRefs = $(bpmn).find("lane").filter(attr("id",laneId)).children().each();
		LOG.debug("Moving " + flowNodeRefs.size() + " flowNodeRefs for " + laneId);
		for (Match flowNodeRef : flowNodeRefs) {
			this.moveElement(bpmn, flowNodeRef.text(), deltaX, deltaY);
		}
	}
	
	private void moveElement(Document bpmn, String elementId, double deltaX, double deltaY) {
		Match bpmnShape  = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", elementId));
		
		//Update X
		double oldX = Double.parseDouble(bpmnShape.child().attr("x") == null ? "0":bpmnShape.child().attr("x"));
		double newX = oldX + deltaX;
		bpmnShape.child().attr("x", Double.toString(newX));
		
		//Update Y
		double oldY = Double.parseDouble(bpmnShape.child().attr("y") == null ? "0":bpmnShape.child().attr("y"));
		double newY = oldY + deltaY;
		bpmnShape.child().attr("y", Double.toString(newY));
		
		LOG.debug("Moved shape for bpmnElement=" + elementId + " from " + oldX + "," + oldY + " to " + newX + "," + newY);
	}

}

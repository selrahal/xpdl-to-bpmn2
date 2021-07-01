package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Attempts to resize and order pools to a decent size and orientation
 * Assumes only the Shape's height has been filled out
 * 
 * @author abaxter@redhat.com
 *
 */
public class ResizePoolProcessor implements DomProcessor {
	
	private static final Logger LOG = Logger.getLogger(ResizePoolProcessor.class);
	
	private static final double margin = 60.0f;
	
	@Override
	public Document process(Document bpmn) {
		
		for(Element e: $(bpmn).find("process").children("laneSet").children("lane")) {
			Match lane = $(e);
			Match flowNodes = lane.children();
			if(flowNodes.size() > 0) {
				String laneId = lane.attr("id");
				String laneName = lane.attr("name");
				Match laneShape = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", laneId)).child("Bounds");
				double laneX, laneY, laneX2, laneY2, laneW, laneH;
				laneX = 0.0f;
				laneY = 0.0f;
				laneX2 = 0.0f;
				laneY2 = 0.0f;
				boolean firstElement = true;
				// Determine the expected size of the swimlane based off of its nodes.
				for(Element node : flowNodes) {
					String nodeId = node.getTextContent();
					Match shape = $(bpmn).find("BPMNShape").filter(attr("bpmnElement", nodeId)).child("Bounds");
					double nodeX = Double.parseDouble(shape.attr("x") == null ? "0":shape.attr("x"));
					double nodeY = Double.parseDouble(shape.attr("y") == null ? "0":shape.attr("y"));
					double nodeW = Double.parseDouble(shape.attr("width") == null ? "0":shape.attr("width"));
					double nodeH = Double.parseDouble(shape.attr("height") == null ? "0":shape.attr("height"));
					double newX2 = nodeX + nodeW;
					double newY2 = nodeY + nodeH;
					if(firstElement) {
						firstElement = false;
						laneX = nodeX;
						laneY = nodeY;
						laneX2 = newX2;
						laneY2 = newY2;
					} else {
						if(nodeX < laneX) {
							laneX = nodeX;
						}
						if(nodeY < laneY) {
							laneY = nodeY;
						}
						if(newX2 > laneX2) {
							laneX2 = newX2;
						}
						if(newY2 > laneY2) {
							laneY2 = newY2;
						}
					}
				}
				laneX -= margin;
				laneY -= margin;
				if(laneX < 0.0f) {
					laneX = 0.0f;
				}
				if(laneY < 0.0f) {
					laneY = 0.0f;
				}
				laneW = laneX2 - laneX + margin;
				laneH = laneY2 - laneY + margin;
				LOG.debug(String.format("%s updated to rect: x: %f, y: %f, w: %f, h: %f", laneId, laneX, laneY, laneW, laneH));
				laneShape.attr("x", Double.toString(laneX));
				laneShape.attr("y", Double.toString(laneY));
				laneShape.attr("width", Double.toString(laneW));
				laneShape.attr("height", Double.toString(laneH));
			}
			
		}
		return bpmn;
	}

}

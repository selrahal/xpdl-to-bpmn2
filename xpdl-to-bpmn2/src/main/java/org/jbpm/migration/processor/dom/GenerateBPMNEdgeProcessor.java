package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generates missing BPMNEdges from sequenceFlow elements.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class GenerateBPMNEdgeProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateBPMNEdgeProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		
		for(Element sequenceFlow : $(bpmn).find("sequenceFlow").get()) {
			//find the BPMNShape with the source and targets
			String flowId = $(sequenceFlow).attr("id");
			
			String sourceRef = $(sequenceFlow).attr("sourceRef");
			String sourceBpmnShapeName = findBpmnShapeName(bpmn, sourceRef);
			
			
			
			String targetRef = $(sequenceFlow).attr("targetRef");
			String targetBpmnShapeName = findBpmnShapeName(bpmn, targetRef);
			
			
			String bpmnEdgeName = "BPMNEdge_"+flowId;
			
			//If there is not a BPMNEdge for this flowId, create it
			if(findBpmnEdgeName(bpmn, flowId) == null) {
				LOG.info("Creating BPMNEdge for: "+flowId+": source["+sourceBpmnShapeName+"] target["+targetBpmnShapeName+"]");
				addBpmnEdge(bpmn, bpmnEdgeName, flowId, sourceBpmnShapeName, targetBpmnShapeName);
			}
			else {
				LOG.debug("Found BPMNEdge for: "+flowId+", " + findBpmnEdgeName(bpmn, flowId));
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
	
	private String findBpmnEdgeName(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNEdge").filter(attr("bpmnElement", sourceRef)).first().attr("id");
		if(StringUtils.isNoneBlank(bpmnShapeId)) {
			return bpmnShapeId;
		}
		
		return null;
	}

	private Double findBpmnShapeX(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("x");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Double.parseDouble(bpmnShapeId);
		}
		LOG.error("Could not find x for " + sourceRef + " default to 50.0");
		return 50.0;
	}
	
	private Double findBpmnShapeY(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("y");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Double.parseDouble(bpmnShapeId);
		}
		LOG.error("Could not find y for " + sourceRef + " default to 50.0");
		return 50.0;
	}
	
	private Double findBpmnShapeWidth(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("width");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Double.parseDouble(bpmnShapeId);
		}
		LOG.error("Could not find width for " + sourceRef + " default to 50.0");
		return 50.0;
	}
	
	private Double findBpmnShapeHeight(Document bpmn, String sourceRef) {
		String bpmnShapeId = $(bpmn).find("BPMNShape").filter(attr("id", sourceRef)).child("Bounds").attr("height");
		if(StringUtils.isNotBlank(bpmnShapeId)) {
			return Double.parseDouble(bpmnShapeId);
		}
		LOG.error("Could not find height for " + sourceRef + " default to 50.0");
		return 50.0;
	}

	private void addBpmnEdge(Document document, String bpmnEdgeId, String sequenceFlowId, String bpmnSourceShape, String bpmnTargetShape) {
		Double sourceX = findBpmnShapeX(document, bpmnSourceShape);
		Double sourceY = findBpmnShapeY(document, bpmnSourceShape);
		Double sourceHeight = findBpmnShapeHeight(document, bpmnSourceShape);
		Double sourceWidth = findBpmnShapeWidth(document, bpmnSourceShape);
		Double sourceWaypointX = sourceX + Math.round(sourceWidth/2);
		Double sourceWaypointY = sourceY + sourceHeight;
		
		
		Double targetX = findBpmnShapeX(document, bpmnTargetShape);
		Double targetY = findBpmnShapeY(document, bpmnTargetShape);
		Double targetHeight = findBpmnShapeHeight(document, bpmnTargetShape);
		Double targetWidth = findBpmnShapeWidth(document, bpmnTargetShape);
		Double targetWaypointX = targetX + Math.round(targetWidth/2);
		Double targetWaypointY = targetY;
		
		
		LOG.debug("X: "+sourceWaypointX+" Y: "+sourceWaypointY);
		
		Match sourceWayPoint = $("di:waypoint").attr("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
		sourceWayPoint.attr("x", sourceWaypointX+"").attr("y", sourceWaypointY+"");
		sourceWayPoint.attr("xsi:type", "dc:Point");
		
		Match targetWayPoint = $("di:waypoint").attr("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
		targetWayPoint.attr("x", targetWaypointX+"").attr("y", targetWaypointY+"");
		targetWayPoint.attr("xsi:type", "dc:Point");
		
		//<bpmn2:property id="processVar1" itemSubjectRef="ItemDefinition_1" name="processVar1"/>
		$(document).find("BPMNPlane").first().append(
				$("bpmndi:BPMNEdge").attr("id", bpmnEdgeId)
					.attr("bpmnElement", sequenceFlowId)
					.attr("sourceElement", bpmnSourceShape)
					.attr("targetElement", bpmnTargetShape)
					.append(sourceWayPoint).append(targetWayPoint));
	}
	
}

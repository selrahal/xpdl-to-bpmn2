package org.jbpm.migration.processor.dom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

/**
 * Checks for multiple targetRef's pointing to BPMN nodes that aren't converging gateways.
 * The processor then introduces the converging gateway to the workflow to align with BPMN 2.
 * 
 * @author bradsdavis@gmail.com
 * @author abaxter@redhat.com
 *
 */
public class MultiTargetToConvergingGatewayProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(MultiTargetToConvergingGatewayProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		Set<String> target = new HashSet<String>();
    	Set<String> multipleCheck = new HashSet<String>();
    	for(Element e : $(bpmn).xpath("//*[@targetRef]").get()) {
    		String targetRef = $(e).attr("targetRef");
    		
    		if(!target.add(targetRef)) {
    			multipleCheck.add(targetRef);
    		}
    	}
    	
    	for(String m : multipleCheck) {
    		//determine the tags for each of the targets
    		for(Element e : $(bpmn).xpath("//*[@id]").filter(attr("id", m)).get()) {
    			if(shouldProcess(e)) {
    				String gateway = m+"-ExclusiveConvergingGateway";
    				String sequenceFlow = gateway+"-To-"+m;
    				LOG.info("Must Introduce: Converging Gateway ["+gateway+"] " + " for " + m + " matching " + $(e).toString());
    				LOG.info("Must Introduce: Sequence Flow ["+sequenceFlow+"] :: from["+gateway+"] to["+m+"]");
    				
    				addConvergingGateway(bpmn, m, gateway, sequenceFlow);
    				updateTargetRef(bpmn, m, gateway);
    				updateLane(bpmn, m, gateway);
    				addSequenceFlow(bpmn, sequenceFlow, gateway, m);
    				
    				generateBpmnShapeLocation(bpmn, m, gateway);
    			}
    		}
    	}
    	return bpmn;
	}
	
	private void generateBpmnShapeLocation(Document document, String originalId, String gatewayName) {
		Match bpmnShapeOriginal = $(document).find("BPMNShape").filter(attr("bpmnElement", originalId)).child();
		
		if(bpmnShapeOriginal != null && bpmnShapeOriginal.isNotEmpty()) {
			//deep copy
			bpmnShapeOriginal = $($(bpmnShapeOriginal).toString());
			
			String yVal = $(bpmnShapeOriginal).attr("y");
			if(StringUtils.isNotBlank(yVal)) {
				try {
					Double y = Double.parseDouble(yVal);
					y = y - 40;
					bpmnShapeOriginal.attr("y", y.toString());
				}
				catch(Exception e) {
					LOG.error("Exception adding BPMN Shape Location for Gateway: "+gatewayName, e);
				}
			}
			
			$(document).namespace("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI")
			.namespace("di", "http://www.omg.org/spec/DD/20100524/DI")
			.namespace("dc", "http://www.omg.org/spec/DD/20100524/DC")
			.find("BPMNPlane").first().append(
				$("bpmndi:BPMNShape").attr("id", "BPMNShape_"+gatewayName).attr("bpmnElement", gatewayName).append($(bpmnShapeOriginal)));
		}
		else {
			LOG.info("Didn't create BPMNShape for: "+gatewayName);
		}
	}
	
	
	private boolean shouldProcess(Element tag) {
		String tagName = $(tag).tag();
		String direction = $(tag).attr("gatewayDirection");
		
		if(StringUtils.equals(tagName, "exclusiveGateway") || StringUtils.equals(tagName, "parallelGateway")) {
			if(StringUtils.equals(direction, "Converging")) {
				return false;
			}
		}
		return true;
	}
	
	private void updateLane(Document document, String targetRef, String newTargetRef) {
		$(document).find("process").children("laneSet").children("lane").children()
			.matchText(targetRef).matchText(newTargetRef, false).parent()
			.append($("bpmn2:flowNodeRef").text(newTargetRef));
	}
	
	private void addConvergingGateway(Document document, String nodeId, String gatewayName, String sequenceFlowId) {
		//<complexGateway id="ProfileOK-Gateway" name="ProfileOK-Gateway" gatewayDirection="Converging" ></complexGateway>
		Match process = $(document).find("process").first();
		process.append(
				$("bpmn2:exclusiveGateway").attr("id", gatewayName)
					.attr("name", gatewayName)
					.attr("gatewayDirection", "Converging")
						.append($("bpmn2:outgoing").text(sequenceFlowId)));
		Match node = process.child(attr("id", nodeId));
		Match gateway = process.child(attr("id", gatewayName));
		node.children("incoming").remove();
		process.children("sequenceFlow").filter(attr("targetRef", nodeId)).each().forEach(
				flow -> gateway.append($("bpmn2:incoming").text(flow.id()))
		);
		node.append($("bpmn2:incoming").text(sequenceFlowId));
	}
	
	
	private void addSequenceFlow(Document document, String sequenceFlowName, String sourceRef, String targetRef) {
		//<sequenceFlow id="ProfileOK-Gateway-ProfileOK" sourceRef="ProfileOK-Gateway" targetRef="ProfileOK" />
		LOG.info("Adding sequence flow id=" + sequenceFlowName + ", sourceRef=" + sourceRef + ", targetRef=" + targetRef);
		$(document).find("process").first().append(
				$("bpmn2:sequenceFlow").attr("id", sequenceFlowName)
					.attr("sourceRef", sourceRef)
					.attr("targetRef", targetRef));
	}
	
	
	private void updateTargetRef(Document document, String targetRef, String newTargetRef) {
		for(Element e : $(document).xpath("//*[@targetRef]").filter(attr("targetRef", targetRef)).get()) {
			$(e).attr("targetRef", newTargetRef);
		}
	}
	

}

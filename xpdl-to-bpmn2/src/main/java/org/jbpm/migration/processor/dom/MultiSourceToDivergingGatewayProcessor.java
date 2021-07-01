package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * Checks for multiple sourceRef's pointing to BPMN nodes that aren't diverging gateways.
 * The processor then introduces the diverging gateway to the workflow to align with BPMN 2.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class MultiSourceToDivergingGatewayProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(MultiSourceToDivergingGatewayProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		Set<String> target = new HashSet<String>();
    	Set<String> multipleCheck = new HashSet<String>();
    	for(Element e : $(bpmn).xpath("//*[@sourceRef]").get()) {
    		String targetRef = $(e).attr("sourceRef");
    		
    		if(!target.add(targetRef)) {
    			multipleCheck.add(targetRef);
    		}
    	}
    	
    	for(String m : multipleCheck) {
    		//determine the tags for each of the targets
    		for(Element e : $(bpmn).xpath("//*[@id]").filter(attr("id", m)).get()) {
    			if(shouldProcess(e)) {
    				String gateway = m+"-ExclusiveDivergingGateway";
    				String sequenceFlow = m+"-To-"+gateway;
    				
    				addDivergingGateway(bpmn, m, gateway, sequenceFlow);
    				updateSourceRef(bpmn, m, gateway);
    				addSequenceFlow(bpmn, sequenceFlow, m, gateway);
    				updateLane(bpmn, m, gateway);
    				
    				//setup the location of the generated gateway
    				generateBpmnShapeLocation(bpmn, m, gateway);
    				
    			}
    		}
    	}
    	return bpmn;
	}
	
	private boolean shouldProcess(Element tag) {
		String tagName = $(tag).tag();
		String direction = $(tag).attr("gatewayDirection");
		
		if(StringUtils.equals(tagName, "exclusiveGateway") || StringUtils.equals(tagName, "parallelGateway")) {
			if(StringUtils.equals(direction, "Diverging")) {
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
	
	private void generateBpmnShapeLocation(Document document, String originalId, String gatewayName) {
		Match bpmnShapeOriginal = $(document).find("BPMNShape").filter(attr("bpmnElement", originalId)).child();
		
		if(bpmnShapeOriginal != null && bpmnShapeOriginal.isNotEmpty()) {
			//deep copy
			String result = $(bpmnShapeOriginal).toString();
			LOG.info("originalId=" + originalId + " gatewayName=" + gatewayName);
			LOG.info(result);
			bpmnShapeOriginal = $(result);
			
			String yVal = $(bpmnShapeOriginal).attr("y");
			if(StringUtils.isNotBlank(yVal)) {
				try {
					Double y = Double.parseDouble(yVal);
					y = y + 40;
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
	
	
	private void addDivergingGateway(Document document, String nodeId, String gatewayName, String sequenceFlowId) {
		LOG.debug("Must Introduce: Diverging Gateway ["+gatewayName+"]");
		Match process = $(document).find("process").first();
		//<exclusiveGateway id="ProfileOK-Gateway" name="ProfileOK-Gateway" gatewayDirection="Converging" ></exclusiveGateway>
		process.append(
				$("bpmn2:exclusiveGateway").attr("id", gatewayName)
					.attr("name", gatewayName)
					.attr("gatewayDirection", "Diverging")
						.append($("bpmn2:incoming").text(sequenceFlowId)));
		Match node = process.child(attr("id", nodeId));
		Match gateway = process.child(attr("id", gatewayName));
		node.children("outgoing").remove();
		process.children("sequenceFlow").filter(attr("sourceRef", nodeId)).each().forEach(
				flow -> gateway.append($("bpmn2:outgoing").text(flow.id()))
		);
		node.append($("bpmn2:outgoing").text(sequenceFlowId));
	}
	
	
	private void addSequenceFlow(Document document, String sequenceFlowName, String sourceRef, String targetRef) {
		LOG.debug("Must Introduce: Sequence Flow ["+sequenceFlowName+"] :: from["+sourceRef+"] to["+targetRef+"]");
		
		
		//<sequenceFlow id="ProfileOK-Gateway-ProfileOK" sourceRef="ProfileOK-Gateway" targetRef="ProfileOK" />
		$(document).find("process").first().append(
				$("bpmn2:sequenceFlow").attr("id", sequenceFlowName)
					.attr("sourceRef", sourceRef)
					.attr("targetRef", targetRef));
	}
	
	
	private void updateSourceRef(Document document, String ref, String newRef) {
		for(Element e : $(document).xpath("//*[@sourceRef]").filter(attr("sourceRef", ref)).get()) {
			$(e).attr("sourceRef", newRef);
		}
	}
	

}

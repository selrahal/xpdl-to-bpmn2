package org.jbpm.migration.processor.dom;

import static org.jbpm.migration.util.CountedMapUtil.toCountedMapFromIds;
import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;
import static org.joox.JOOX.matchText;
import static org.joox.JOOX.not;
import static org.joox.JOOX.or;
import static org.joox.JOOX.tag;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.migration.events.NodeClassification;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Created by abaxter on 5/4/16.
 */
public class SanityCheckProcessor implements DomProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SanityCheckProcessor.class);


    /*
     * - Ensure that there is only one process
     * - For ever lane:
     *      - ensure each node exists in the process
     * - For ever process node:
     *      - if it has a to or from, ensure that those sequenceFlows exist
     * - For every sequenceFlow:
     *      - ensure the to/from nodes exist
     *      - If the from node is in a lane, the to node should be in it, too, and vice-versa
     * - Ensure that there is only one BPMNDiagram, BPMNPlane
     * - For every shape:
     *      - ensure that its corresponding process element exists
     *          - if it's not a process node list, check to see if it's a lane
 *          - ensure it has a shape with a bounds
     * - For every edge:
     *      - ensure that its bpmnElement exists
     *      - ensure that its source and target elements exist
     */
    @Override
    public Document process(Document document) {
        final Match bpmn = $(document);

        final Match processes = bpmn.find("process");
        if(processes.isEmpty() || processes.size() > 1) {
            LOG.error("Expected 1 process, found: {}", processes.ids());
        }
        final Match diagrams = bpmn.find("BPMNDiagram");
        if(diagrams.isEmpty() || diagrams.size() > 1) {
            LOG.error("Expected 1 diagram, found: {}", diagrams.ids());
        }

        final Match process = processes.first();
        final String processId = process.id();
        final Match processNodes = process.find(not(or(tag("sequenceFlow"), tag("property"),tag("laneSet"))));
        final Match sequenceFlows = process.find("sequenceFlow");
        final Match bpmnDiagram = diagrams.first();
        final Match bpmnPlanes = bpmnDiagram.children("BPMNPlane");

        if(bpmnPlanes.isEmpty() || bpmnPlanes.size() > 1) {
            LOG.error("Process {}: Expected 1 plane, found: {}", processId, bpmnPlanes.ids());
        }
        final Match bpmnShapes = bpmnPlanes.children("BPMNShape");
        final Match bpmnEdges = bpmnPlanes.children("BPMNEdge");

        // Check lanes
        final Match lanes = process.children("laneSet").children("lane");
        for(Match lane : lanes.each()) {
            String laneId = lane.id();
            Match flowNodes = lane.children("flowNodeRef");
            if(flowNodes.isEmpty()) {
                LOG.error("Process {}: lane {} is empty", processId, laneId);
                continue;
            }
            for(Match flowNode : flowNodes.each()) {
                String nodeId = flowNode.text();
                if(processNodes.filter(attr("id", nodeId)).isEmpty()) {
                    LOG.error("Process {}: lane {} expects process node {}. It does not exist.", processId, laneId, nodeId);
                }
            }

            //Check if lane has no shape

            if(bpmnShapes.filter(attr("bpmnElement", laneId)).isEmpty()) {
                LOG.info("{}", bpmnShapes.ids());
                LOG.error("Process {}: lane {} should have a shape. It does not exist.", processId, laneId);
            }

        }

        // Check process nodes
        for(Match node : processNodes.each()) {
            String nodeId = node.id();
            String nodeTag = node.tag();
            // Check if incoming flows exist
            Match incomingFlows = node.children("incoming");
            for(Match incoming : incomingFlows.each()) {
                String expectedFlowId = incoming.text();
                if(sequenceFlows.filter(attr("id", expectedFlowId)).isEmpty()) {
                    LOG.error("Process {}: {} {} expects incoming sequenceFlow {}. It does not exist.", processId, nodeTag, nodeId, expectedFlowId);
                }
            }
            // Check if outgoing flows exist
            Match outgoingFlows = node.children("outgoing");
            for( Match outgoing : outgoingFlows.each()) {
                String expectedFlowId = outgoing.text();
                if(sequenceFlows.filter(attr("id", expectedFlowId)).isEmpty()) {
                    LOG.error("Process {}: {} {} expects outgoing sequenceFlow {}. It does not exist.", processId, nodeTag, nodeId, expectedFlowId);
                }
            }
            String gatewayDirection = node.attr("gatewayDirection");
            if(StringUtils.equals("Converging", gatewayDirection)) {
                if(outgoingFlows.size() != 1) {
                    LOG.error("Process {}: {} {} is Converging and expects only 1 outgoing sequenceFlow. Found {}.", processId, nodeTag, nodeId, outgoingFlows.size());
                }
            } else if (StringUtils.equals("Diverging", gatewayDirection)) {
                if(incomingFlows.size() != 1) {
                    LOG.error("Process {}: {} {} is Diverging and expects only 1 incoming sequenceFlow. Found {}.", processId, nodeTag, nodeId, incomingFlows.size());
                }
            } else if (gatewayDirection == null ) {
                if(outgoingFlows.size() != 1 && NodeClassification.needsOutgoing(nodeTag)) {
                    LOG.error("Process {}: {} {} expects 1 outgoing sequenceFlow. Found {}.", processId, nodeTag, nodeId, outgoingFlows.size());
                }
                if(incomingFlows.size() != 1 && NodeClassification.needsIncoming(nodeTag)) {
                    LOG.error("Process {}: {} {} expects 1 incoming sequenceFlow. Found {}.", processId, nodeTag, nodeId, incomingFlows.size());
                }
            }
            // Check if in multiple lanes
            Match matchingLanes = lanes.children("flowNodeRef").filter(matchText(nodeId));
            if(matchingLanes.size() > 1) {
                LOG.error("Process {}: {} {} expected in multiple lanes {}", processId, nodeTag, nodeId, matchingLanes.ids());
            }

            // Check to make sure the nodes have shapes
            if(bpmnShapes.filter(attr("bpmnElement", nodeId)).isEmpty()) {
                LOG.error("Process {}: {} {} should have a shape. It does not exist.", processId, nodeTag, nodeId);
            }
        }

        // Check sequence flows
        for(Match flow : sequenceFlows.each()) {
            String flowId = flow.id();
            String sourceId = flow.attr("sourceRef");
            Match sourceNode = processNodes.filter(attr("id", sourceId)).first();
            String targetId = flow.attr("targetRef");
            Match targetNode = processNodes.filter(attr("id", targetId)).first();

            if(sourceNode.children("outgoing").filter(matchText(flowId)).isEmpty()) {
                LOG.error("Process {}: sequenceFlow {} expects sourceRef {} to reference it as outgoing. It does not.", processId, flowId, sourceId);
            }
            if(targetNode.children("incoming").filter(matchText(flowId)).isEmpty()) {
                LOG.error("Process {}: sequenceFlow {} expects targetRef {} to reference it as incoming. It does not.", processId, flowId, targetId);
            }

            if(bpmnEdges.filter(attr("bpmnElement", flowId)).isEmpty()) {
                LOG.error("Process {}: sequenceFlow {} should have an edge. It does not exist.", processId, flowId);
            }
        }

        toCountedMapFromIds(bpmnShapes.each()).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .forEach( e -> {
                    String id = e.getKey();
                    Integer count = e.getValue();
                    LOG.error("Process {}: Multiple BPMNShapes sharing the same id {} with count {}", processId, id, count);
                });

        for(Match shape : bpmnShapes.each()) {
            String shapeId = shape.id();
            String bpmnNodeId = shape.attr("bpmnElement");

            if(processNodes.filter(attr("id", bpmnNodeId)).isEmpty() && lanes.filter(attr("id", bpmnNodeId)).isEmpty()) {
                LOG.error("Process {}: BPMNShape {} expects to match a process node or lane with id {}. It does not", processId, shapeId, bpmnNodeId);
            }
            if(shape.children("Bounds").isEmpty()) {
                LOG.error("Process {}: BPMNShape {} has no bounds", processId, shapeId);
            }


            if(bpmnEdges.filter(or(attr("sourceElement",shapeId), attr("targetElement",shapeId))).isEmpty() &&
                    lanes.filter(attr("id", bpmnNodeId)).isEmpty()) {
                LOG.error("Process {}: BPMNShape {} has no edges attached to it.", processId, shapeId);
            }
        }

        toCountedMapFromIds(bpmnEdges.each()).entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .forEach( e -> {
                    String id = e.getKey();
                    Integer count = e.getValue();
                    LOG.error("Process {}: Multiple BPMNEdges sharing the same id {} with count {}", processId, id, count);
                });

        for(Match edge: bpmnEdges.each()) {
            String edgeId = edge.id();
            String flowId = edge.attr("bpmnElement");
            String sourceId = edge.attr("sourceElement");
            String targetId = edge.attr("targetElement");

            if(sequenceFlows.filter(attr("id", flowId)).isEmpty()) {
                LOG.error("Process {}: BPMNEdge {} has no corresponding flow {}.", processId, edgeId, flowId);
            }

            if(bpmnShapes.filter(attr("id", sourceId)).isEmpty()) {
                LOG.error("Process {}: BPMNEdge {} references source shape {}. It does not exist.", processId, edgeId, sourceId);
            }
            if(bpmnShapes.filter(attr("id", targetId)).isEmpty()) {
                LOG.error("Process {}: BPMNEdge {} references target shape {}. It does not exist.", processId, edgeId, targetId);
            }

            if(edge.children("waypoint").isEmpty()) {
                LOG.error("Process {}: BPMNEdge {} has no waypoints.", processId, edgeId);
            }
        }

        return document;
    }
}

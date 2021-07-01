package org.jbpm.migration.layout;

import java.util.List;
import java.util.Map;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class MultiLevelLayoutUtil {
	private static final double COEFFICIENT = 2.5;
	private static final Logger LOG = LoggerFactory.getLogger(MultiLevelLayoutUtil.class);

	public static void layout(Document bpmn) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();

        //See if graph is well imported
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        Map<String, Node> shapeIds = BpmnShapeUtil.getShapeIds(graphModel, bpmn);
		
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        
        //Create three nodes
        List<Edge> edges = BpmnShapeUtil.getEdges(graphModel, bpmn, shapeIds);
        for(Node n : shapeIds.values()) {
        	directedGraph.addNode(n);
        }
        for(Edge e : edges) {
        	directedGraph.addEdge(e);
        }
        

        
        //Run the YifanHu layout algorithm
        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
        layout.setGraphModel(graphModel);
        layout.initAlgo();
        layout.resetPropertiesValues();
        layout.setOptimalDistance(10f);
        layout.setStepRatio(.99f);
        layout.setBarnesHutTheta(1.0f);

        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
           layout.goAlgo();
        }
        layout.endAlgo();
        

        Float minX = null;
        Float minY = null;
        for(Node n : graphModel.getGraph().getNodes()) {
        	LOG.info("x=" + n.x() + ",y=" + n.y());
        	float dx = 0;
        	float dy = 0;
        	Float x = (-1)*n.x() + (-1)*dx;
        	Float y = (-1)*n.y() + (-1)*dy;
        	
        	if(minX == null || minX > x) {
        		minX = x;
        	}
        	if(minY == null || minY > y) {
        		minY = y;
        	}
        }
        for (Node n : graphModel.getGraph().getNodes()) {
        	Float x = (-1)*n.x();
        	Float y = (-1)*n.y();
        	float dx = 0;
        	float dy = 0;
        	
        	x = x+dx;
        	y = y+dy;
        	
        	x = x + + (-1)*minX;
        	y = y + ((-1)*minY);
        	
        	Integer xFinal = (int)Math.round(x * COEFFICIENT);
        	Integer yFinal = (int)Math.round(y * COEFFICIENT);
        	
        	BpmnShapeUtil.updateXY(bpmn, n.getLabel(), xFinal, yFinal);
        }
        
	}
}

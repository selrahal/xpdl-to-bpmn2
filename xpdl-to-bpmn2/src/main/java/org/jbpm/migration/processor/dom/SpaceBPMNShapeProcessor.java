package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Spaces out the shapes of the diagram.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class SpaceBPMNShapeProcessor implements DomProcessor {

	private final double X_SPACE_FACTOR = 1.5;
	private final double Y_SPACE_FACTOR = 1.5;
	private static final Logger LOG = Logger.getLogger(SpaceBPMNShapeProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		double minX = -1;
		double minY = -1;
		
		//Multiple the {x,y} coordinate of all shaped by {X_SPACE_FACTOR,Y_SPACE_FACTOR} 
		for(Element shape : $(bpmn).find("BPMNShape")) {
			Element bounds = $(shape).child("Bounds").first().get(0);
			String x = $(bounds).attr("x");
			String y = $(bounds).attr("y");
			
			if(StringUtils.isNotBlank(x)&&StringUtils.isNotBlank(y)) {
				Double xVal = Double.parseDouble(x);
				Double yVal = Double.parseDouble(y);

				xVal = (double)Math.round(xVal * X_SPACE_FACTOR);
				yVal = (double)Math.round(yVal * Y_SPACE_FACTOR);

				if(minX == -1 || xVal < minX) {
					minX = xVal;
				}
				if(minY == -1 || yVal < minY) {
					minY = yVal;
				}
				
				$(bounds).attr("x", xVal.toString());
				$(bounds).attr("y", yVal.toString());
			}
		}
		
		//Move the diagram to be close to the top left portion of the canvas
		if(minX>0&&minY>0) {
			for(Element shape : $(bpmn).find("BPMNShape")) {
				Element bounds = $(shape).child("Bounds").first().get(0);
				String x = $(bounds).attr("x");
				String y = $(bounds).attr("y");
				
				if(StringUtils.isNotBlank(x)&&StringUtils.isNotBlank(y)) {
					Double xVal = Double.parseDouble(x);
					Double yVal = Double.parseDouble(y);
	
					xVal = xVal - minX + 80;
					yVal = yVal - minY + 80;
					
					$(bounds).attr("x", xVal.toString());
					$(bounds).attr("y", yVal.toString());
				}
			}
		}
		
		return bpmn;
	}


	
}

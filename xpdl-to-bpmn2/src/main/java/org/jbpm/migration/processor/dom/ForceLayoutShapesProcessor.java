package org.jbpm.migration.processor.dom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.layout.MultiLevelLayoutUtil;
import org.w3c.dom.Document;

/**
 * Leverages the YifanHu algorithm to automatically lay out the BPMN workflows
 * 
 * @author bradsdavis@redhat.com
 *
 */
public class ForceLayoutShapesProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(ForceLayoutShapesProcessor.class);
	
	@Override
	public Document process(Document bpmn) {
		String prop = System.getProperty("auto-layout");
		if(StringUtils.equals("true", prop)) {
			MultiLevelLayoutUtil.layout(bpmn);
		}
		return bpmn;
	}
	
}

package org.jbpm.migration.processor.dom;

import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import static org.jbpm.migration.util.CountedMapUtil.setUniqueIds;
import static org.joox.JOOX.$;

/**
 * Makes potentialOwner, input/outputSets, resourceAssignmentExpression, and formalExpression ids globally unique
 * IDs are generated ONLY for values that need it
 * 
 * @author bradsdavis@gmail.com
 * @author abaxter@redhat.com
 *
 */
public class GenerateUniqueUserTaskIdProcessor implements DomProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(GenerateUniqueUserTaskIdProcessor.class);
	
	@Override
	public Document process(Document document) {
		Match bpmn = $(document); 
		setUniqueIds(LOG, "scriptTask", bpmn.find("scriptTask"));
		setUniqueIds(LOG, "adHocSubProcess", bpmn.find("adHocSubProcess"));
		setUniqueIds(LOG, "potentialOwner", bpmn.find("potentialOwner"));
		setUniqueIds(LOG, "resourceAssignmentExpression", bpmn.find("potentialOwner").children("resourceAssignmentExpression"));
		setUniqueIds(LOG, "formalExpression", bpmn.find("potentialOwner").children("resourceAssignmentExpression").children("formalExpression"));
    	return document;
	}

}

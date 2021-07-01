package org.jbpm.migration.processor.dom;

import org.w3c.dom.Document;

public interface DomProcessor {
	Document process(Document bpmn);
}

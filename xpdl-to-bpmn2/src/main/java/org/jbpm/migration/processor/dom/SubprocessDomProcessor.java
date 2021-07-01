package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;

import java.util.List;

import org.joox.JOOX;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SubprocessDomProcessor implements DomProcessor{
	
	@Override
	public Document process(Document bpmn) {
		List<Element> processOrSubprocesses = $(bpmn).find(JOOX.or(JOOX.tag("process"), JOOX.matchTag("adHocSubProcess"))).get();
		for (Element processOrSubprocess: processOrSubprocesses) {
			this.process(processOrSubprocess);
		}

		return bpmn;
	}
	
	public abstract void process(Element processOrSubprocess);
}

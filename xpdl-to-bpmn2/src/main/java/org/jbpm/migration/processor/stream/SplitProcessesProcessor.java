package org.jbpm.migration.processor.stream;

import static org.joox.JOOX.*;

import java.io.StringWriter;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.jbpm.migration.xml.XmlUtils;
import org.joox.Match;
import org.w3c.dom.Document;

/**
 * Splits a Document into multiple documents to support BPMN limitation of one process per file
 * 
 */
public class SplitProcessesProcessor {

	private static final Logger LOG = Logger.getLogger(SplitProcessesProcessor.class);
	
	public Stream<Document> process(Document document) {
		TransformerFactory tfactory = TransformerFactory.newInstance();
		try {
			final Transformer tx = tfactory.newTransformer();

        DOMSource source = new DOMSource(document);

        return StreamSupport.stream($(document).find("process").spliterator(), true)
                // Clone a new BPMN file per process
                .map(process -> {
                    String processId = process.getAttribute("id");
                    String processName = process.getAttribute("name");
                    StringWriter stringWriter = new StringWriter();
                    StreamResult result = new StreamResult(stringWriter);
                    try {
                        tx.transform(source,result);
                    } catch (TransformerException e) {
                       LOG.error(e);
                    }
                    Document clonedDocument = XmlUtils.parseString(stringWriter.toString());
                    LOG.info("Cloned process: " + processId + "-" + processName);
                    return ImmutablePair.of(processId,clonedDocument);
                })
                // Remove unnecessary elements
                .map(docPair -> {
                    String processId = docPair.getLeft();
                    Match bpmn = $(docPair.getRight());
                    bpmn.find("process").filter(not(attr("id", processId))).remove();
                    Match bpmnDiagrams = bpmn.find("BPMNDiagram");
                    bpmnDiagrams.children("BPMNPlane").filter(not(attr("bpmnElement", processId))).parent().remove();
                    return docPair;
                })
                .map(ImmutablePair::getRight);
		} catch (TransformerConfigurationException e1) {
			throw new IllegalArgumentException("Could not create transformer", e1);
		}
	}


	
}

package org.jbpm.migration.processor.dom;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.xml.XmlUtils;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Apply XSL transformation to the Document
 */
public class ApplyXSLTransformationProcessor implements DomProcessor{

	private static final Logger LOG = Logger.getLogger(ApplyXSLTransformationProcessor.class);
	private InputStream xslInputStream;
	private String relativeDirectory;
	
	public ApplyXSLTransformationProcessor(String xslFileName) {
		File xslFile = new File(xslFileName);
		relativeDirectory = xslFileName.substring(0, xslFileName.lastIndexOf("/") + 1);
		if (StringUtils.isNotEmpty(this.relativeDirectory)) {
			LOG.debug("Using " + relativeDirectory + " for relative directory");
		}
		try {
			this.xslInputStream = new FileInputStream(xslFile);
		} catch (FileNotFoundException e) {
			LOG.warn("XSL File " + xslFileName + " not found, falling back to classpath");
			this.xslInputStream = ApplyXSLTransformationProcessor.class.getResourceAsStream("/" + xslFileName);
		}
	}
	
	public Document process(Document bpmn) {
		DOMSource bpmnSource = new DOMSource(bpmn);
		Source xsltSource = new StreamSource(xslInputStream);

		// Create a string writer
	    StringWriter stringWriter = new StringWriter();

	    // Create the result stream for the transform
	    StreamResult result = new StreamResult(stringWriter);

	    // Apply the transformation
		XmlUtils.transform(relativeDirectory, bpmnSource, xsltSource, result);
		
		// Return a Document created from the transformed result
		String resultS = stringWriter.toString();
		LOG.info(XmlUtils.toString(XmlUtils.parseString(resultS)));
		return XmlUtils.parseString(resultS);
	}
	
}

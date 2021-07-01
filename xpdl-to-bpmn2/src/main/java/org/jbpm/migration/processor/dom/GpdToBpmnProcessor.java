package org.jbpm.migration.processor.dom;

import static org.joox.JOOX.$;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * If a GPD is provided, it will migrate to BPMN 2 compliant XML via the XSLT.
 * This will then merge the transformed GPD into the BPMN 2 result.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class GpdToBpmnProcessor implements DomProcessor {
	private static final Logger LOG = Logger.getLogger(GpdToBpmnProcessor.class);
	
	private static final String DEFAULT_GPD_XSLT_SHEET = "gpd-bpmn2.xsl";
	
	final Source gpdXsltSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_GPD_XSLT_SHEET));
	
	private final File gpdFile;
	
	public GpdToBpmnProcessor(File gpdFile) {
		this.gpdFile = gpdFile;
	}
	
	@Override
	public Document process(Document bpmn) {
		if(gpdFile != null) {
	        Document gpd;
				try {
					gpd = $(gpdFile).document();
					$(gpd).transform(gpdXsltSource);
					
					//find the name of the process, and enhance the GPD name
					String processId = $(bpmn).find("process").first().attr("id");
					$(gpd).find("BPMNPlane").attr("bpmnElement", processId);
					
					//append the GPD result to definitions
					$(bpmn).append($(gpd));
				} catch (Exception e) {
					LOG.error("Exception processing gpd.xml", e);
				}
	    	}
		return bpmn;
	}

}

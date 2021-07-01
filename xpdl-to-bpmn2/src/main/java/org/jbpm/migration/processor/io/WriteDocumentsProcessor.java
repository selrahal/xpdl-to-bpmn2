package org.jbpm.migration.processor.io;

import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.joox.JOOX.$;

import java.io.File;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.jbpm.migration.xml.XmlUtils;
import org.joox.Match;
import org.w3c.dom.Document;


/**
 * Writes the document to a file 
 *
 */
public class WriteDocumentsProcessor {

	private static final Logger LOG = Logger.getLogger(WriteDocumentsProcessor.class);
	
	private Path originalFileName;
	private Path outputDirectory;
	
	public WriteDocumentsProcessor(Path originalFile, Path outputDir) {
		this.originalFileName = originalFile;
		this.outputDirectory = outputDir;
	}

	
	public File process(Document bpmn) {
		Match process = $(bpmn).find("process").first();
        String processId = process.id();
        String processName = process.attr("name");
        String outputFileName = processName + "_";
        if (!processId.equals(processName)) {
        	outputFileName = outputFileName.concat(processId + "_" );
        }
         outputFileName = outputFileName + removeExtension(originalFileName.toString()) + ".bpmn2";
        if (!outputDirectory.toFile().exists()) {
        	LOG.debug("Creating directory for output at " + outputDirectory.getFileName());
        	outputDirectory.toFile().mkdir();
        }
        return XmlUtils.writeFile(bpmn, outputDirectory.resolve(outputFileName).toFile());
	}
	
	
	
}

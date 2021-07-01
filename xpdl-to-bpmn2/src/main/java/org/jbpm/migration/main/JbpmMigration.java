/**
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.jbpm.migration.main;

import static org.joox.JOOX.$;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jbpm.migration.processor.MigrationProcessor;
import org.jbpm.migration.processor.MigrationProcessorFactory;
import org.jbpm.migration.xml.XmlUtils;
import org.w3c.dom.Document;

/**
 * @author Eric D. Schabell
 * @author Maurice de Chateau
 * @author Brad Davis - bradsdavis@gmail.com
 */
public final class JbpmMigration {
    // Default XSLT sheet.
	private static final Logger LOG = Logger.getLogger(JbpmMigration.class);
    
    /** Private constructor to prevent instantiation. */
    private JbpmMigration() {
    }

    /**
     * Accept two or three command line arguments: - the name of an XML file (required) - the name of an XSLT stylesheet (optional, default is XPDL 2.1) - the
     * name of the file the result of the transformation is to be written to.
     */
    public static void main(final String[] args) throws TransformerException {
        if (args.length == 2) {
            transform(new File(args[0]), new File(args[1]));
        } else {
            System.err.println("Usage:");
            System.err.println("  java " + JbpmMigration.class.getName() + " xpdlFile outputDirectoryName");
            System.exit(1);
        }
    }
    
    /** 
     * Transforms XPDL to BPMN 
     * 
     * @param xpdlFile
     * @param bpmnOutputDir
     */
	public static void transform(final File xpdlFile, final File bpmnOutputDir) {

		Path outputDir = FileSystems.getDefault().getPath(bpmnOutputDir.getAbsolutePath());
		Path xpdlFileName = FileSystems.getDefault().getPath(xpdlFile.getAbsolutePath()).getFileName();

		Document bpmnDocument = XmlUtils.parseFile(xpdlFile);
		
		MigrationProcessor migrationProcessor = MigrationProcessorFactory.creatProcessor(bpmnDocument);
		Stream<Document> bpmnDocuments = migrationProcessor.process(bpmnDocument);
		List<File> bpmnFiles = migrationProcessor.write(bpmnDocuments, xpdlFileName, outputDir);


		LOG.info("Completed: " + bpmnFiles.size());

	}
  
    
    /** 
     * Transforms XPDL to BPMN 
     * 
     * @param xpdlFile
     * @param bpmnOutputDir
     */
	public static String transform(final String xpdl) {

		Document bpmnDocument = XmlUtils.parseString(xpdl);
		
		MigrationProcessor migrationProcessor = MigrationProcessorFactory.creatProcessor(bpmnDocument);
		Stream<Document> bpmnDocuments = migrationProcessor.process(bpmnDocument);
		Optional<Document> result = bpmnDocuments.findFirst();
		
		return result.isPresent() ? XmlUtils.toString(result.get()) : "";

	}
}

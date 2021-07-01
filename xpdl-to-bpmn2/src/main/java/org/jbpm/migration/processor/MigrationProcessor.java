package org.jbpm.migration.processor;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.migration.processor.io.WriteDocumentsProcessor;
import org.w3c.dom.Document;

public abstract class MigrationProcessor {
	public abstract Stream<Document> process(Document input);
	
	
	public List<File> write(Stream<Document> document, Path inputFile, Path outputDirectory) {
		WriteDocumentsProcessor writeDocumentsProcessor = new WriteDocumentsProcessor(inputFile, outputDirectory);
		return document.map(writeDocumentsProcessor::process).collect(Collectors.toList());
	}
}

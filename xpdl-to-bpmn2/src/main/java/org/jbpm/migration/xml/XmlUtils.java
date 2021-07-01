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
package org.jbpm.migration.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.apache.xalan.trace.PrintTraceListener;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.jbpm.migration.util.ErrorCollector;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Convenience class for working with XML documents.
 * 
 * @author Eric D. Schabell
 * @author Maurice de Chateau
 */
public final class XmlUtils {
    static final Logger LOGGER = Logger.getLogger(XmlUtils.class);
	private static final String EMPTY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";


    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    static {
        FACTORY.setNamespaceAware(true);
    }

    /** Private constructor to prevent instantiation. */
    private XmlUtils() {
    }

    /**
     * Create an empty XML structure.
     * 
     * @return An empty DOM tree.
     */
    public static Document createEmptyDocument() {
        Document output = null;
        try {
            final DocumentBuilder db = FACTORY.newDocumentBuilder();
            db.setErrorHandler(new ParserErrorHandler());
            output = db.newDocument();
        } catch (final Exception ex) {
            LOGGER.error("Problem creating empty XML document.", ex);
        }

        return output;
    }

    /**
     * Parse a <code>File</code> containing an XML structure into a DOM tree.
     * 
     * @param input
     *            The input XML file.
     * @return The corresponding DOM tree, or <code>null</code> if the input could not be parsed successfully.
     */
    public static Document parseFile(final File input) {
        Document output = null;
        try {
            final DocumentBuilder db = FACTORY.newDocumentBuilder();
            db.setErrorHandler(new ParserErrorHandler());
            output = db.parse(input);
        } catch (final Exception ex) {
            String msg = "Problem parsing the input XML file";
            if (ex instanceof SAXParseException) {
                msg += " at line #" + ((SAXParseException) ex).getLineNumber();
            }
            LOGGER.error(msg, ex);
        }

        return output;
    }

    /**
     * Parse a <code>String</code> containing an XML structure into a DOM tree.
     * 
     * @param input
     *            The input XML <code>String</code>.
     * @return The corresponding DOM tree, or <code>null</code> if the input could not be parsed successfully.
     */
    public static Document parseString(final String input) {
        Document output = null;
        try {
            final DocumentBuilder db = FACTORY.newDocumentBuilder();
            db.setErrorHandler(new ParserErrorHandler());
            ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
            output = db.parse(bais);
        } catch (final Exception ex) {
            String msg = "Problem parsing the input XML string";
            if (EMPTY_XML.equals(input)) {
            	LOGGER.error("Empty XML result, transformation probably didn't match any elements", ex);
            	return null;
            }
            if (ex instanceof SAXParseException) {
                msg += " at line #" + ((SAXParseException) ex).getLineNumber();
            }
            LOGGER.error(msg, ex);
        }

        return output;
    }

    /**
     * Write an XML document (formatted) to a given <code>File</code>.
     * 
     * @param input
     *            The input XML document.
     * @param output
     *            The intended <code>File</code>.
     */
    public static File writeFile(final Document input, final File output) {
        final StreamResult result = new StreamResult(new StringWriter());

        format(new DOMSource(input), result);

        FileWriter fw = null;
        try {
        	fw = new FileWriter(output);
            fw.write(result.getWriter().toString());
        } catch (final IOException ioEx) {
            LOGGER.error("Problem writing XML to file.", ioEx);
        } finally {
        	if (fw != null ) {
				try {
					fw.close();
				} catch (IOException e) {
					LOGGER.error("Problem closing the file.", e);
				}
        	}
        }
        return output;
    }
    
    public static String toString(final Document input) {
        final StreamResult result = new StreamResult(new StringWriter());

        format(new DOMSource(input), result);

        return result.getWriter().toString();
    }

    /**
     * Validate an XML document against an XML Schema definition.
     * 
     * @param input
     *            The input XML document.
     * @param schemas
     *            The XML Schema(s) against which the document must be validated.
     * @return Whether the validation was successful.
     */
    public static boolean validate(final Source input, final Source[] schemas) {
        boolean isValid = true;
        try {
            final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new SchemaLSResourceResolver());
            final Validator val = factory.newSchema(schemas).newValidator();
            final ParserErrorHandler eh = new ParserErrorHandler();
            val.setErrorHandler(eh);
            val.validate(input);
            if (eh.didErrorOccur()) {
                isValid = false;
                eh.logErrors(LOGGER);
            }
        } catch (final Exception ex) {
            LOGGER.error("Problem validating the given process definition.", ex);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Transform an XML document according to an XSL style sheet.
     * 
     * @param input
     *            The input XML {@link Source}.
     * @param sheet
     *            The XSL style sheet according to which the document must be transformed.
     * @param output
     *            The {@link Result} in which the transformed XML is to be stored.
     */
    public static void transform(final String relativeDirectory, final Source input, final Source sheet, final Result output) {
        try {

            // Transform.
            createTransformer(relativeDirectory, sheet).transform(input, output);

        } catch (final Exception ex) {
            LOGGER.error("Problem transforming XML file.", ex);
        }
    }
    

    /**
-     * Format an XML {@link Source} to a pretty-printable {@link StreamResult}.
-     * 
-     * @param input
-     *            The (unformatted) input XML {@link Source}.
-     * @return The formatted {@link StreamResult}.
-     */
    public static void format(final Source input, final Result output) {
        try {
            // Use an identity transformation to write the source to the result.
            final Transformer transformer = createTransformer(null, null);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(input, output);
        } catch (final Exception ex) {
            LOGGER.error("Problem formatting DOM representation.", ex);
        }
    }

    /**
     * Create a {@link Transformer} from the given sheet.
     * 
     * @param xsltSource
     *            The sheet to be used for the transformation, or <code>null</code> if an identity transformator is needed.
     * @return The created {@link Transformer}
     * @throws Exception
     *             If the creation or instrumentation of the {@link Transformer} runs into trouble.
     */
    private static Transformer createTransformer(final String relativeDirectory, final Source xsltSource) throws Exception {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        if (xsltSource != null) {
            // Create a resolver for imported sheets (assumption: available from classpath or the root of the jar).
            final URIResolver resolver = new URIResolver() {
                @Override
                public Source resolve(final String href, final String base) throws TransformerException {
                	String fileLocation = relativeDirectory + href;
                	InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileLocation);
                	if (inputStream != null) {
                		LOGGER.debug("Resolved import " + fileLocation );
                	} else {
                		LOGGER.debug("Could not resolve import " + fileLocation + " base=" + base);
                	}
                    return new StreamSource(inputStream);
                }
            };
            transformerFactory.setURIResolver(resolver);

            // Transformer using the given sheet.
            transformer = transformerFactory.newTransformer(xsltSource);
            transformer.setURIResolver(resolver);
        } else {
            // Transformer without a sheet, i.e. for "identity transform" (e.g. formatting).
            transformer = transformerFactory.newTransformer();
        }

        if (LOGGER.isTraceEnabled()) {
            instrumentTransformer(transformer);
        }

        return transformer;
    }

    /**
     * Adds a little verbosity to a constructed <code>Transformer</code>.
     * 
     * @param transformer
     *            The <code>Transformer</code> which may produce some output.
     * @throws Exception
     *             If setting/adding a listener gives a problem.
     */
    private static void instrumentTransformer(Transformer transformer) throws Exception {
        transformer.setErrorListener(new TransformerErrorListener());

        if (transformer instanceof TransformerHandler) {
            transformer = ((TransformerHandler) transformer).getTransformer();
        }
        if (transformer instanceof TransformerImpl) {
            final TraceManager tm = ((TransformerImpl) transformer).getTraceManager();
            final PrintTraceListener ptl = new PrintTraceListener(new PrintWriter(System.out));
            // Print information as each node is 'executed' in the stylesheet.
            ptl.m_traceElements = true;
            // Print information after each result-tree generation event.
            ptl.m_traceGeneration = true;
            // Print information after each selection event.
            ptl.m_traceSelection = true;
            // Print information whenever a template is invoked.
            ptl.m_traceTemplates = true;
            // Print information whenever an extension call is made.
            ptl.m_traceExtension = true;
            tm.addTraceListener(ptl);
        }
    }

    private static class ParserErrorHandler extends ErrorCollector<SAXParseException> implements ErrorHandler {
    }

    private static class TransformerErrorListener extends ErrorCollector<TransformerException> implements ErrorListener {
    }
    
    /**
     * Custom resource resolver used to load imported and included XSD documents
     * from classpath resources located at the resource root.
     */
    private static class SchemaLSResourceResolver implements LSResourceResolver {

        @Override
        public LSInput resolveResource(String type, String namespaceURI,
                                       String publicId, String systemId, String baseURI) {
            
            return new LSInputImpl(publicId, systemId, baseURI, 
                                   Thread.currentThread().getContextClassLoader().getResourceAsStream(systemId));
        }
        
    }
    
    /**
     * Application-specific LSInput implementation required for loading schema documents
     * from classpath.
     * 
     * It implements only those methods required (returning the resource as InputStream)
     * and assumes that resource (XSD schema) is encoded using UTF-8.
     */
    private static class LSInputImpl implements LSInput {

        public static final String ENCODING = "UTF-8";

        private String publicId;
        private String systemId;
        private String baseURI;
        private boolean certifiedText;

        private final InputStream inputStream;
        
        public LSInputImpl(String publicId, String systemId,
                           String baseURI, InputStream inputStream) {
            
            this.publicId = publicId;
            this.systemId = systemId;
            this.baseURI = baseURI;
            this.certifiedText = false;

            this.inputStream = inputStream;
        }
        
        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public InputStream getByteStream() {
            return this.inputStream;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public void setStringData(String stringData) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getSystemId() {
            return this.systemId;
        }

        @Override
        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        @Override
        public String getPublicId() {
            return this.publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        @Override
        public String getBaseURI() {
            return this.baseURI;
        }

        @Override
        public void setBaseURI(String baseURI) {
            this.baseURI = baseURI;
        }

        @Override
        public String getEncoding() {
            return ENCODING;
        }

        @Override
        public void setEncoding(String encoding) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean getCertifiedText() {
            return this.certifiedText;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            this.certifiedText = certifiedText;
        }
    }
}

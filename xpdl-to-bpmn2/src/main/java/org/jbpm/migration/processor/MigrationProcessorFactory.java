package org.jbpm.migration.processor;

import static org.joox.JOOX.$;

import org.jbpm.migration.processor.xpdl.BeaAquaLogicBpm20Processor;
import org.jbpm.migration.processor.xpdl.BlueworksLiveXPDL21;
import org.jbpm.migration.processor.xpdl.JPDL32Processor;
import org.jbpm.migration.processor.xpdl.PlainXMLProcessor;
import org.jbpm.migration.processor.xpdl.SoftwareAG22Processor;
import org.jbpm.migration.processor.xpdl.TibcoXPDL21Processor;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


public class MigrationProcessorFactory {
	private static final Logger LOG = LoggerFactory.getLogger(MigrationProcessorFactory.class);
	private static final String VENDOR_TIBCO = "TIBCO";
	private static final String VENDOR_SOFTWARE_AG = "Software AG";
	private static final String VENDOR_BEA_AQUALOGIC_BPM = "BEA Aqualogic BPM";
	private static final String VENDOR_BLUEWORKS_LIVE = "BlueworksLive";
	
	private static final String VERSION_2_0 = "2.0";
	private static final String VERSION_2_1 = "2.1";
	private static final String VERSION_2_2 = "2.2";
	
	public static MigrationProcessor creatProcessor(String vendor, String version) {
		if (vendor == null) {
			LOG.warn("No vendor supplied, defaulting to plain processor");
			return new PlainXMLProcessor();
		}
		if (version == null) throw new IllegalArgumentException("Version required");
		String vendorTrimmed = vendor.trim();
		String versionTrimmed = version.trim();
		if (VENDOR_TIBCO.equals(vendorTrimmed) && VERSION_2_1.equals(versionTrimmed)) {
			return new TibcoXPDL21Processor();
		} else if (VENDOR_BEA_AQUALOGIC_BPM.equals(vendorTrimmed) && VERSION_2_0.equals(versionTrimmed)) {
			return new BeaAquaLogicBpm20Processor();
		} else if (VENDOR_SOFTWARE_AG.equals(vendorTrimmed) && VERSION_2_2.equals(versionTrimmed)) {
			return new SoftwareAG22Processor();
		} else if (VENDOR_BLUEWORKS_LIVE.equals(vendorTrimmed) && VERSION_2_1.equals(versionTrimmed)) {
			return new BlueworksLiveXPDL21();
		}else {
			throw new IllegalArgumentException("Does not support " + vendor + ":" + version + ", consider contributing!");
		}
		
		
	}
	
	public static MigrationProcessor creatProcessor(Document document) {
		String namespace = $(document).attr("xmlns");
		LOG.info("namespace:" + namespace);
		if ("urn:jbpm.org:jpdl-3.2".equals(namespace)) {
			return new JPDL32Processor();
		}
		
		String version = $(document).find("PackageHeader").child("XPDLVersion").text();
		String vendor = $(document).find("PackageHeader").child("Vendor").text();
		return MigrationProcessorFactory.creatProcessor(vendor, version);
		
	}
}

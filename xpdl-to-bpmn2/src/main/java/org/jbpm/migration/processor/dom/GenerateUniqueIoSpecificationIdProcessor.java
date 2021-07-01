package org.jbpm.migration.processor.dom;

import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jbpm.migration.util.CountedMapUtil.setUniqueIds;
import static org.jbpm.migration.util.CountedMapUtil.toCountedMapFromIds;
import static org.joox.JOOX.$;
import static org.joox.JOOX.ids;
import static org.joox.JOOX.matchText;
import static org.joox.JOOX.or;
import static org.joox.JOOX.tag;

/**
 * Makes ioSpecification, input/outputSets, dataInput/dataOutputAssociation, and dataInput/dataOutput ids globally unique
 * UUIDs are generated ONLY for values that need it
 * 
 * @author bradsdavis@gmail.com
 * @author abaxter@redhat.com
 *
 */
public class GenerateUniqueIoSpecificationIdProcessor implements DomProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(GenerateUniqueIoSpecificationIdProcessor.class);
	
	@Override
	public Document process(Document document) {
		Match bpmn = $(document);
		Match ioSpecifications = bpmn.find("ioSpecification");
		setUniqueIds(LOG, "ioSpecification", ioSpecifications);
		// re-search ioSpecification
		ioSpecifications = bpmn.find("ioSpecification");
		setUniqueIds(LOG, "inputSet or outputSet", ioSpecifications.children(or(tag("inputSet"), tag("outputSet"))));
		setUniqueIds(LOG, "dataInputAssociation out dataOutputAssociation", ioSpecifications.parent().children(or(tag("dataInputAssociation"), tag("dataOutputAssociation"))));

		Match dataInputs = ioSpecifications.children("dataInput");
		toCountedMapFromIds(dataInputs.each()).entrySet().stream()
				.filter(e -> e.getValue() > 1)
				.forEach( e -> {
					String id = e.getKey();
					Integer count = e.getValue();
					AtomicInteger idCount = new AtomicInteger(0);
					LOG.debug("Multiple dataInputs sharing the same id {}: {}", id, count);
					dataInputs.filter(ids(id)).forEach(
							m -> {
								String newId = id + "_" + idCount.addAndGet(1);
								Match dataInput = $(m);
								dataInput.attr("id", newId);
								Match ioSpecification = dataInput.parent();
								Match inputRefs = ioSpecification.children("inputSet").children("dataInputRefs")
										.filter(matchText(id));
								if(inputRefs.size() > 1) {
									LOG.error("Multiple dataInputRefs with the same id {}. Skipping.", id);
								} else {
									inputRefs.text(newId);
								}
								Match targetRefs = ioSpecification.parent()
										.children("dataInputAssociation").children("targetRef")
										.filter(matchText(id));
								if(targetRefs.size() > 1) {
									LOG.error("Multiple targetRef with the same id {}. Skipping.", id);
								} else {
									targetRefs.text(newId);
								}
							}
					);
				});

		Match dataOutputs = ioSpecifications.children("dataOutput");
		toCountedMapFromIds(dataOutputs.each()).entrySet().stream()
				.filter(e -> e.getValue() > 1)
				.forEach( e -> {
					String id = e.getKey();
					Integer count = e.getValue();
					AtomicInteger idCount = new AtomicInteger(0);
					LOG.debug("Multiple dataOutputs sharing the same id {}: {}", id, count);
					dataOutputs.filter(ids(id)).forEach(
							m -> {
								String newId = id + "_" + idCount.addAndGet(1);
								Match dataOutput = $(m);
								dataOutput.attr("id", newId);
								Match ioSpecification = dataOutput.parent();
								Match outputRefs = ioSpecification.children("outputSet").children("dataOutputRefs")
										.filter(matchText(id));
								if(outputRefs.size() > 1) {
									LOG.error("Multiple dataInputRefs with the same id {}. Skipping.", id);
								} else {
									outputRefs.text(newId);
								}
								Match targetRefs = ioSpecification.parent()
										.children("dataOutputAssociation").children("targetRef")
										.filter(matchText(id));
								if(targetRefs.size() > 1) {
									LOG.error("Multiple targetRefs with the same id {}. Skipping.", id);
								} else {
									targetRefs.text(newId);
								}
							}
					);
				});

    	return document;
	}

}

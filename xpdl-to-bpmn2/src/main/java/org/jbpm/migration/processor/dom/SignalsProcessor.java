package org.jbpm.migration.processor.dom;

import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jbpm.migration.util.CountedMapUtil.setUniqueIds;
import static org.joox.JOOX.$;

/**
 * Scans for bpmn2:signalEventDefinition entries without corresponding bpmn2:signal entries
 * @author abaxter@redhat.com
 */
public class SignalsProcessor implements DomProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SignalsProcessor.class);

    @Override
    public Document process(Document document) {
        Match bpmn = $(document);
        Match signalDefinitions = bpmn.find("signalEventDefinition");
        setUniqueIds(LOG, "signalEventDefinition", signalDefinitions);
        Set<String> signalRefs = signalDefinitions.each().stream()
                .map(s -> s.attr("signalRef"))
                .collect(Collectors.toSet());

        List<String> signals = bpmn.children("signal").ids();
        signalRefs.stream()
                .filter(s -> !signals.contains(s))
                .forEach( s ->  bpmn.find("itemDefinition").last().after($("bpmn2:signal").attr("id", s).attr("name", s))
                );

        return document;
    }
}

package org.jbpm.migration.events;

import com.google.common.collect.ImmutableSet;

public class NodeClassification {
    public static final ImmutableSet<String> nodesWithoutIncoming = ImmutableSet.of("startEvent", "intermediateCatchEvent");
    public static final ImmutableSet<String> nodesWithoutOutgoing = ImmutableSet.of("endEvent");
    
    public static boolean needsOutgoing(String type) {
    	return !nodesWithoutOutgoing.contains(type);
    }
    
    public static boolean needsIncoming(String type) {
    	return !nodesWithoutIncoming.contains(type);
    }
}

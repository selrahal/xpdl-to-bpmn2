package org.jbpm.migration.processor.dom;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;

import java.util.Set;
import java.util.stream.Collectors;

import static org.joox.JOOX.$;
import static org.joox.JOOX.or;
import static org.joox.JOOX.tag;

/**
 * Generates process variable mapping.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class GenerateProcessVariablesProcessor implements DomProcessor {

	private static final Logger LOG = Logger.getLogger(GenerateProcessVariablesProcessor.class);
	
	@Override
	public Document process(Document document) {
		Match bpmn = $(document);

		ImmutableMap<String, String> structureDefinitions = createDefitionMap(bpmn);

		if(!structureDefinitions.keySet().contains("String")) {
			addVariableType(bpmn, "String");
			structureDefinitions = createDefitionMap(bpmn);
		}

		Set<String> variableNames = bpmn.find(or(tag("dataInputAssociation"), tag("dataOutputAssociation")))
				.children("sourceRef").each().stream()
				.map(dataIO -> dataIO.text())
				.collect(Collectors.toSet());

		for(String variableName : variableNames) {
    		if (!processVariablePresent(bpmn, variableName)) {
    			LOG.debug("Must Introduce: Process Variable ["+variableName+"] ");
    			addProcessVariable(bpmn, variableName, "String", structureDefinitions);
    		} else {
    			LOG.debug("Found process variable " + variableName);
    		}
    	}
    
    	return document;
	}
	
	private boolean processVariablePresent(Match bpmn, String variableName) {
		boolean found = false;
		for (Match prop : bpmn.find("process").find("property").each()){
			if (prop.attr("id").equals(variableName)) {
				found = true;
				break;
			}
		}
		
		return found;
	}

	private void addVariableType(Match bpmn, String variableType) {
		String itemTypeId = generateVariableTypeId(variableType);

		//add item type
		//<bpmn2:itemDefinition id="ItemDefinition_3" isCollection="false" structureRef="Example1"/>
		bpmn.find("process").first().parent().prepend(
				$("bpmn2:itemDefinition").attr("id", itemTypeId)
					.attr("isCollection", "false")
					.attr("structureRef", variableType));
	}
	private void addProcessVariable(Match bpmn, String variableName, String variableType, ImmutableMap<String, String> structureDefinitions) {
		
		
		//<bpmn2:property id="processVar1" itemSubjectRef="ItemDefinition_1" name="processVar1"/>
		bpmn.find("process").first().prepend(
				$("bpmn2:property").attr("id", variableName)
					.attr("name", variableName)
					.attr("itemSubjectRef", structureDefinitions.get(variableType)));
	}

	private String generateVariableTypeId(String variableType) {
		String typeId = "_"+variableType;
		return typeId;
	}

	//<id, structureRef>
	private ImmutableMap<String, String> createDefitionMap(Match bpmn) {
		ImmutableMap.Builder<String, String> tItemDefitions = ImmutableMap.builder();
		for(Match itemDef : bpmn.children("itemDefinition").each()) {
			if (itemDef.attr("structureRef") != null)
	tItemDefitions.put(itemDef.attr("structureRef"), itemDef.attr("id"));
		}
		return tItemDefitions.build();
	}
}

package org.knime.gateway.codegen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.codegen.CodegenModelFactory;
import io.swagger.codegen.CodegenModelType;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.SupportingFile;
import io.swagger.codegen.languages.AbstractJavaCodegen;
import io.swagger.models.Operation;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

/**
 * Service and entity-class/interface generation for the KNIME gateway based on
 * a swagger/openapi spec.
 * 
 * @author Martin Horn, University of Konstanz
 */
public class GatewayCodegen extends AbstractJavaCodegen {

	static Logger LOGGER = LoggerFactory.getLogger(GatewayCodegen.class);

	private final Map<String, String> m_tagDescriptions = new HashMap<String, String>();

	private String m_apiTemplateFile;
	private String m_modelNamePattern;
	private String m_apiNamePattern;
	private String m_modelPropertyNamePattern;
	private String m_modelPropertyPackage;

	@Override
	public void processOpts() {
		super.processOpts();
		apiTemplateFiles.clear();
		m_apiTemplateFile = getPropertyAsString("apiTemplateFile").orElse(null);
		if (m_apiTemplateFile != null) {
			apiTemplateFiles.put(m_apiTemplateFile, ".java");
		}
		modelTemplateFiles.clear();
		if (additionalProperties().get("modelTemplateFile") != null) {
			modelTemplateFiles.put(additionalProperties().get("modelTemplateFile").toString(), ".java");
		}
		apiTestTemplateFiles.clear();
		modelTestTemplateFiles.clear();
		apiDocTemplateFiles.clear();
		modelDocTemplateFiles.clear();

		// property options
		m_modelNamePattern = getPropertyAsString("modelNamePattern").orElse(null);
		m_apiNamePattern = getPropertyAsString("apiNamePattern").orElse(null);
		m_modelPropertyNamePattern = getPropertyAsString("modelPropertyNamePattern").orElse(null);
		m_modelPropertyPackage = getPropertyAsString("modelPropertyPackage").orElse(null);

		// supporting files
		getPropertyAsList("supportingFiles").ifPresent(l -> l.stream().forEach(sf -> {
			Map<String, Object> sfmap = (Map<String, Object>) sf;
			final String folder = (sourceFolder + '/' + sfmap.get("package").toString()).replace(".", "/");
			supportingFiles.add(new SupportingFile(sfmap.get("templateFile").toString(), folder,
					sfmap.get("destinationFileName").toString()));
			sfmap.keySet().forEach(k -> {
				additionalProperties.put(sfmap.get("templateFile").toString() + "#package",
						sfmap.get("package").toString());
			});

		}));
	}

	private Optional<String> getPropertyAsString(String propName) {
		return Optional.ofNullable(additionalProperties().get(propName)).map(o -> o.toString());
	}

	private Optional<List<Object>> getPropertyAsList(String propName) {
		return Optional.ofNullable(additionalProperties.get(propName)).map(o -> List.class.cast(o));
	}

	@Override
	public String getName() {
		return additionalProperties().get("codegenName").toString();
	}

	public CodegenType getTag() {
		return CodegenType.OTHER;
	}

	public String getHelp() {
		return "TODO";
	}

	@Override
	public Map<String, Object> postProcessModels(Map<String, Object> objs) {
		// Remove imports ApiModel and ApiModelProperty
		List<Map<String, String>> imports = (List<Map<String, String>>) objs.get("imports");
		Pattern pattern = Pattern.compile(".*(ApiModel|ApiModelProperty)");
		for (Iterator<Map<String, String>> itr = imports.iterator(); itr.hasNext();) {
			String _import = itr.next().get("import");
			if (pattern.matcher(_import).matches()) {
				itr.remove();
			}
		}
		return super.postProcessModels(objs);
	}

	@Override
	public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co,
			Map<String, List<CodegenOperation>> operations) {
		// makes the tag descriptions available to the mustache templates
		// (e.g. to be used in the javadoc of the service interfaces)
		// TODO is there a better way?
		String tagDesc = null;
		for (int i = 0; i < co.tags.size(); i++) {
			if (co.tags.get(i).getName().equals(tag)) {
				tagDesc = co.tags.get(i).getDescription();
				break;
			}
		}
		if (tagDesc != null) {
			m_tagDescriptions.put(tag.toLowerCase(), tagDesc);
		}
		super.addOperationToGroup(tag, resourcePath, operation, co, operations);
	}

	@Override
	public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
		// TODO is there a better way
		String tagDesc = (String) m_tagDescriptions
				.get(((Map<String, Object>) objs.get("operations")).get("pathPrefix"));
		if ((tagDesc != null)) {
			objs.put("tagDescription", tagDesc);
		}
		return super.postProcessOperations(objs);
	}

	@Override
	public String toApiName(String name) {
		// make original name available to templates
		// these form the basis of the service names, e.g. WorkflowService,
		// NodeService
		if (additionalProperties().get("tags") == null) {
			additionalProperties().put("tags", new HashSet<String>());
		} else {
			Set<String> tags = (Set<String>) additionalProperties().get("tags");
			tags.add(name);
		}
		if (m_apiNamePattern != null) {
			return m_apiNamePattern.replace("##name##", camelize(name));
		} else {
			return name;
		}
	}

	@Override
	public String toModelName(String name) {
		if (m_modelNamePattern != null) {
			return m_modelNamePattern.replace("##name##", name);
		} else {
			return name;
		}
	}

	@Override
	public CodegenProperty fromProperty(String name, Property p) {
		// enables properties to have another name then the property they are
		// part of
		// e.g. DefaultNodeEnt.getNodeMessage() returns NodeMessageEnt instead
		// of DefaultNodeMessageEnt
		if (m_modelPropertyNamePattern != null && p instanceof RefProperty) {
			CodegenProperty property = CodegenModelFactory.newInstance(CodegenModelType.PROPERTY);
			property.name = toVarName(name);
			property.baseName = name;
			property.nameInCamelCase = camelize(property.name, false);
			property.description = escapeText(p.getDescription());
			property.unescapedDescription = p.getDescription();
			property.title = p.getTitle();
			property.getter = toGetter(name);
			property.setter = toSetter(name);
			property.baseType = property.datatype = property.datatypeWithEnum = m_modelPropertyNamePattern
					.replace("##name##", ((RefProperty) p).getSimpleRef());
			importMapping.put(property.datatype, m_modelPropertyPackage + "." + property.datatype);
			return property;
		} else {
			return super.fromProperty(name, p);
		}
	}

	@Override
	protected void updatePropertyForMap(CodegenProperty property, CodegenProperty innerProperty) {
		if (m_modelPropertyNamePattern != null) {
			// TODO hacky but works!
			super.updatePropertyForMap(property, innerProperty);
			property.datatype = property.datatypeWithEnum = "java.util.Map<String, " + innerProperty.datatype + ">";
		} else {
			super.updatePropertyForMap(property, innerProperty);
		}
	}

	@Override
	protected void updatePropertyForArray(CodegenProperty property, CodegenProperty innerProperty) {
		if (m_modelPropertyNamePattern != null) {
			// TODO hacky but works!
			super.updatePropertyForArray(property, innerProperty);
			property.datatype = property.datatypeWithEnum = "java.util.List<" + innerProperty.datatype + ">";
		} else {
			super.updatePropertyForArray(property, innerProperty);
		}
	}
}

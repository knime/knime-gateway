/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.json.webui.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt.DefaultWorkflowCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultExpandCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultInsertNodeCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateLinkedComponentsCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultPortCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateProjectMetadataCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultRemovePortCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultCutCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultTranslateCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultPartBasedCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultReorderWorkflowAnnotationsCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultCopyCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddBendpointCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddWorkflowAnnotationCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultCollapseCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultReplaceNodeCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddPortCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateComponentMetadataCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultPasteCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateComponentLinkInformationCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddNodeCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultTransformMetanodePortsBarCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateNodeLabelCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateWorkflowAnnotationCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultConnectCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultTransformWorkflowAnnotationCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultDeleteCommandEnt;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "kind",
    visible = true,
    defaultImpl = DefaultWorkflowCommandEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowCommandEnt.class, name="WorkflowCommand")
,
  @Type(value = DefaultTranslateCommandEnt.class, name = "translate")
,
  @Type(value = DefaultDeleteCommandEnt.class, name = "delete")
,
  @Type(value = DefaultConnectCommandEnt.class, name = "connect")
,
  @Type(value = DefaultAddNodeCommandEnt.class, name = "add_node")
,
  @Type(value = DefaultReplaceNodeCommandEnt.class, name = "replace_node")
,
  @Type(value = DefaultInsertNodeCommandEnt.class, name = "insert_node")
,
  @Type(value = DefaultUpdateComponentOrMetanodeNameCommandEnt.class, name = "update_component_or_metanode_name")
,
  @Type(value = DefaultUpdateNodeLabelCommandEnt.class, name = "update_node_label")
,
  @Type(value = DefaultCollapseCommandEnt.class, name = "collapse")
,
  @Type(value = DefaultExpandCommandEnt.class, name = "expand")
,
  @Type(value = DefaultAddPortCommandEnt.class, name = "add_port")
,
  @Type(value = DefaultRemovePortCommandEnt.class, name = "remove_port")
,
  @Type(value = DefaultCopyCommandEnt.class, name = "copy")
,
  @Type(value = DefaultCutCommandEnt.class, name = "cut")
,
  @Type(value = DefaultPasteCommandEnt.class, name = "paste")
,
  @Type(value = DefaultTransformWorkflowAnnotationCommandEnt.class, name = "transform_workflow_annotation")
,
  @Type(value = DefaultUpdateWorkflowAnnotationCommandEnt.class, name = "update_workflow_annotation")
,
  @Type(value = DefaultReorderWorkflowAnnotationsCommandEnt.class, name = "reorder_workflow_annotations")
,
  @Type(value = DefaultAddWorkflowAnnotationCommandEnt.class, name = "add_workflow_annotation")
,
  @Type(value = DefaultUpdateProjectMetadataCommandEnt.class, name = "update_project_metadata")
,
  @Type(value = DefaultUpdateComponentMetadataCommandEnt.class, name = "update_component_metadata")
,
  @Type(value = DefaultAddBendpointCommandEnt.class, name = "add_bendpoint")
,
  @Type(value = DefaultUpdateComponentLinkInformationCommandEnt.class, name = "update_component_link_information")
,
  @Type(value = DefaultTransformMetanodePortsBarCommandEnt.class, name = "transform_metanode_ports_bar")
,
  @Type(value = DefaultUpdateLinkedComponentsCommandEnt.class, name = "update_linked_components")
})
@JsonDeserialize(builder=DefaultWorkflowCommandEntBuilder.class)
@JsonSerialize(as=WorkflowCommandEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface WorkflowCommandEntMixIn extends WorkflowCommandEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("kind")
    public KindEnum getKind();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "kind",
    visible = true,
    defaultImpl = DefaultWorkflowCommandEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWorkflowCommandEnt.class, name="WorkflowCommand")
,
  @Type(value = DefaultTranslateCommandEnt.class, name = "translate")
,
  @Type(value = DefaultDeleteCommandEnt.class, name = "delete")
,
  @Type(value = DefaultConnectCommandEnt.class, name = "connect")
,
  @Type(value = DefaultAddNodeCommandEnt.class, name = "add_node")
,
  @Type(value = DefaultReplaceNodeCommandEnt.class, name = "replace_node")
,
  @Type(value = DefaultInsertNodeCommandEnt.class, name = "insert_node")
,
  @Type(value = DefaultUpdateComponentOrMetanodeNameCommandEnt.class, name = "update_component_or_metanode_name")
,
  @Type(value = DefaultUpdateNodeLabelCommandEnt.class, name = "update_node_label")
,
  @Type(value = DefaultCollapseCommandEnt.class, name = "collapse")
,
  @Type(value = DefaultExpandCommandEnt.class, name = "expand")
,
  @Type(value = DefaultAddPortCommandEnt.class, name = "add_port")
,
  @Type(value = DefaultRemovePortCommandEnt.class, name = "remove_port")
,
  @Type(value = DefaultCopyCommandEnt.class, name = "copy")
,
  @Type(value = DefaultCutCommandEnt.class, name = "cut")
,
  @Type(value = DefaultPasteCommandEnt.class, name = "paste")
,
  @Type(value = DefaultTransformWorkflowAnnotationCommandEnt.class, name = "transform_workflow_annotation")
,
  @Type(value = DefaultUpdateWorkflowAnnotationCommandEnt.class, name = "update_workflow_annotation")
,
  @Type(value = DefaultReorderWorkflowAnnotationsCommandEnt.class, name = "reorder_workflow_annotations")
,
  @Type(value = DefaultAddWorkflowAnnotationCommandEnt.class, name = "add_workflow_annotation")
,
  @Type(value = DefaultUpdateProjectMetadataCommandEnt.class, name = "update_project_metadata")
,
  @Type(value = DefaultUpdateComponentMetadataCommandEnt.class, name = "update_component_metadata")
,
  @Type(value = DefaultAddBendpointCommandEnt.class, name = "add_bendpoint")
,
  @Type(value = DefaultUpdateComponentLinkInformationCommandEnt.class, name = "update_component_link_information")
,
  @Type(value = DefaultTransformMetanodePortsBarCommandEnt.class, name = "transform_metanode_ports_bar")
,
  @Type(value = DefaultUpdateLinkedComponentsCommandEnt.class, name = "update_linked_components")
})
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WorkflowCommandEntMixInBuilder extends WorkflowCommandEntBuilder {
    
        @Override
        public WorkflowCommandEntMixIn build();
    
        @Override
        @JsonProperty("kind")
        public WorkflowCommandEntMixInBuilder setKind(final KindEnum kind);
        
    }


}


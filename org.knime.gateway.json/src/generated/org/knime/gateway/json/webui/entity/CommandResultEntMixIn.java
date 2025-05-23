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

import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultCommandResultEnt.DefaultCommandResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCommandResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultExpandResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultConvertContainerResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddAnnotationResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddComponentPlaceholderResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultUpdateLinkedComponentsResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultPasteResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddPortResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultCopyResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultCollapseResultEnt;
import org.knime.gateway.impl.webui.entity.DefaultAddNodeResultEnt;

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
    defaultImpl = DefaultCommandResultEnt.class)
@JsonSubTypes({
    @Type(value = DefaultCommandResultEnt.class, name="CommandResult")
,
  @Type(value = DefaultCollapseResultEnt.class, name = "collapse_result")
,
  @Type(value = DefaultExpandResultEnt.class, name = "expand_result")
,
  @Type(value = DefaultConvertContainerResultEnt.class, name = "convert_container_result")
,
  @Type(value = DefaultCopyResultEnt.class, name = "copy_result")
,
  @Type(value = DefaultPasteResultEnt.class, name = "paste_result")
,
  @Type(value = DefaultAddNodeResultEnt.class, name = "add_node_result")
,
  @Type(value = DefaultAddPortResultEnt.class, name = "add_port_result")
,
  @Type(value = DefaultAddAnnotationResultEnt.class, name = "add_annotation_result")
,
  @Type(value = DefaultUpdateLinkedComponentsResultEnt.class, name = "update_linked_components_result")
,
  @Type(value = DefaultAddComponentPlaceholderResultEnt.class, name = "add_component_placeholder_result")
})
@JsonDeserialize(builder=DefaultCommandResultEntBuilder.class)
@JsonSerialize(as=CommandResultEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface CommandResultEntMixIn extends CommandResultEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("snapshotId")
    public String getSnapshotId();
    
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
    defaultImpl = DefaultCommandResultEnt.class)
@JsonSubTypes({
    @Type(value = DefaultCommandResultEnt.class, name="CommandResult")
,
  @Type(value = DefaultCollapseResultEnt.class, name = "collapse_result")
,
  @Type(value = DefaultExpandResultEnt.class, name = "expand_result")
,
  @Type(value = DefaultConvertContainerResultEnt.class, name = "convert_container_result")
,
  @Type(value = DefaultCopyResultEnt.class, name = "copy_result")
,
  @Type(value = DefaultPasteResultEnt.class, name = "paste_result")
,
  @Type(value = DefaultAddNodeResultEnt.class, name = "add_node_result")
,
  @Type(value = DefaultAddPortResultEnt.class, name = "add_port_result")
,
  @Type(value = DefaultAddAnnotationResultEnt.class, name = "add_annotation_result")
,
  @Type(value = DefaultUpdateLinkedComponentsResultEnt.class, name = "update_linked_components_result")
,
  @Type(value = DefaultAddComponentPlaceholderResultEnt.class, name = "add_component_placeholder_result")
})
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface CommandResultEntMixInBuilder extends CommandResultEntBuilder {
    
        @Override
        public CommandResultEntMixIn build();
    
        @Override
        @JsonProperty("snapshotId")
        public CommandResultEntMixInBuilder setSnapshotId(final String snapshotId);
        
        @Override
        @JsonProperty("kind")
        public CommandResultEntMixInBuilder setKind(final KindEnum kind);
        
    }


}


/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * ---------------------------------------------------------------------
 */

<<<<<<<< HEAD:org.knime.gateway.json/src/generated/org/knime/gateway/json/webui/entity/AlignNodesCommandEntMixIn.java
import org.knime.gateway.json.webui.entity.WorkflowCommandEntMixIn;
========
package org.knime.gateway.impl.project;
>>>>>>>> 0d96930e5 (NXT-135: Add ability to dispose of a given workflow wfm, and to invoke the same loader when it is requested again):org.knime.gateway.impl/src/eclipse/org/knime/gateway/impl/project/WorkflowManagerLoader.java

import java.util.function.Function;
import java.util.function.Supplier;

<<<<<<<< HEAD:org.knime.gateway.json/src/generated/org/knime/gateway/json/webui/entity/AlignNodesCommandEntMixIn.java
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.knime.gateway.api.webui.entity.AlignNodesCommandEnt;
import org.knime.gateway.impl.webui.entity.DefaultAlignNodesCommandEnt.DefaultAlignNodesCommandEntBuilder;
========
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.util.VersionId;
>>>>>>>> 0d96930e5 (NXT-135: Add ability to dispose of a given workflow wfm, and to invoke the same loader when it is requested again):org.knime.gateway.impl/src/eclipse/org/knime/gateway/impl/project/WorkflowManagerLoader.java

/**
 * Defines how a {@link WorkflowManager} instance is loaded.
 * <p>
 * "Loading" here means loading it from file representation on disk to provide an initialized and usable
 * {@link WorkflowManager} instance. However, this method may also include fetching the files from a remote location.
 */
<<<<<<<< HEAD:org.knime.gateway.json/src/generated/org/knime/gateway/json/webui/entity/AlignNodesCommandEntMixIn.java

@JsonDeserialize(builder=DefaultAlignNodesCommandEntBuilder.class)
@JsonSerialize(as=AlignNodesCommandEnt.class)
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public interface AlignNodesCommandEntMixIn extends AlignNodesCommandEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("kind")
    public KindEnum getKind();
    
    @Override
    @JsonProperty("nodeIds")
    public java.util.List<org.knime.gateway.api.entity.NodeIDEnt> getNodeIds();
    
    @Override
    @JsonProperty("direction")
    public DirectionEnum getDirection();
    
========
public interface WorkflowManagerLoader extends Function<VersionId, WorkflowManager> {
>>>>>>>> 0d96930e5 (NXT-135: Add ability to dispose of a given workflow wfm, and to invoke the same loader when it is requested again):org.knime.gateway.impl/src/eclipse/org/knime/gateway/impl/project/WorkflowManagerLoader.java

    /**
     * Obtain a loader instance that provides only the current state version.
     * 
     * @param currentStateLoader A supplier of the current state wfm
     * @return A loader instance that provides only the current state version
     */
<<<<<<<< HEAD:org.knime.gateway.json/src/generated/org/knime/gateway/json/webui/entity/AlignNodesCommandEntMixIn.java

    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface AlignNodesCommandEntMixInBuilder extends AlignNodesCommandEntBuilder {
    
        @Override
        public AlignNodesCommandEntMixIn build();
    
        @Override
        @JsonProperty("kind")
        public AlignNodesCommandEntMixInBuilder setKind(final KindEnum kind);
        
        @Override
        @JsonProperty("nodeIds")
        public AlignNodesCommandEntMixInBuilder setNodeIds(final java.util.List<org.knime.gateway.api.entity.NodeIDEnt> nodeIds);
        
        @Override
        @JsonProperty("direction")
        public AlignNodesCommandEntMixInBuilder setDirection(final DirectionEnum direction);
        
========
    static WorkflowManagerLoader providingOnlyCurrentState(final Supplier<WorkflowManager> currentStateLoader) {
        return version -> {
            if (!(version instanceof VersionId.CurrentState)) {
                throw new IllegalArgumentException("VersionId.Fixed is not supported");
            }
            return currentStateLoader.get();
        };
>>>>>>>> 0d96930e5 (NXT-135: Add ability to dispose of a given workflow wfm, and to invoke the same loader when it is requested again):org.knime.gateway.impl/src/eclipse/org/knime/gateway/impl/project/WorkflowManagerLoader.java
    }

    /**
     * Load the workflow manager instance
     * 
     * @param version the version to load
     * @return the loaded instance, <code>null</code> if loading failed.
     */
    WorkflowManager apply(final VersionId version);

}

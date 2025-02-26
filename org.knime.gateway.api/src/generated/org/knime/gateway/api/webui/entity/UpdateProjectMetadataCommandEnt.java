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
package org.knime.gateway.api.webui.entity;

import org.knime.gateway.api.webui.entity.EditableMetadataEnt;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Update the metadata of a workflow project.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface UpdateProjectMetadataCommandEnt extends GatewayEntity, WorkflowCommandEnt, EditableMetadataEnt {



  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (UpdateProjectMetadataCommandEnt)other;
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("description", Pair.create(getDescription(), e.getDescription()));
      valueConsumer.accept("metadataType", Pair.create(getMetadataType(), e.getMetadataType()));
      valueConsumer.accept("tags", Pair.create(getTags(), e.getTags()));
      valueConsumer.accept("links", Pair.create(getLinks(), e.getLinks()));
  }

    /**
     * The builder for the entity.
     */
    public interface UpdateProjectMetadataCommandEntBuilder extends GatewayEntityBuilder<UpdateProjectMetadataCommandEnt> {

        /**
         * The kind of command which directly maps to a specific &#39;implementation&#39;.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        UpdateProjectMetadataCommandEntBuilder setKind(KindEnum kind);
        
        /**
   		 * Set description
         * 
         * @param description the property value,  
         * @return this entity builder for chaining
         */
        UpdateProjectMetadataCommandEntBuilder setDescription(TypedTextEnt description);
        
        /**
         * Descriminator for different types of metadata.
         * 
         * @param metadataType the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        UpdateProjectMetadataCommandEntBuilder setMetadataType(MetadataTypeEnum metadataType);
        
        /**
         * A collection of tags the user chose to describe the workflow
         * 
         * @param tags the property value,  
         * @return this entity builder for chaining
         */
        UpdateProjectMetadataCommandEntBuilder setTags(java.util.List<String> tags);
        
        /**
         * A collection of URLs attached to the workflow
         * 
         * @param links the property value,  
         * @return this entity builder for chaining
         */
        UpdateProjectMetadataCommandEntBuilder setLinks(java.util.List<LinkEnt> links);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        UpdateProjectMetadataCommandEnt build();
    
    }

}

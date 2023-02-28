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

import org.knime.gateway.api.webui.entity.JobManagerEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Information about the node execution. Might not be present if no special node execution info is available. If given, usually only one of the following properties is set, either the icon, the &#39;streamable&#39;-flag, or the job-manager.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeExecutionInfoEnt extends GatewayEntity {


  /**
   * Get jobManager
   * @return jobManager 
   **/
  public JobManagerEnt getJobManager();

  /**
   * This properties is only given if this node is part of a workflow (i.e. a component&#39;s workflow) that is in streaming mode. If true, this node can process the data in streamed manner, if false, it can&#39;t.
   * @return streamable 
   **/
  public Boolean isStreamable();

  /**
   * A custom (decorator) icon set by its node executor (or the node executor of the parent workflow). Not present if the custom executor doesn&#39;t define a special icon or the &#39;streamable&#39; property is given. The icon is encoded in a data-url.
   * @return icon 
   **/
  public String getIcon();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NodeExecutionInfoEnt)other;
      valueConsumer.accept("jobManager", Pair.create(getJobManager(), e.getJobManager()));
      valueConsumer.accept("streamable", Pair.create(isStreamable(), e.isStreamable()));
      valueConsumer.accept("icon", Pair.create(getIcon(), e.getIcon()));
  }

    /**
     * The builder for the entity.
     */
    public interface NodeExecutionInfoEntBuilder extends GatewayEntityBuilder<NodeExecutionInfoEnt> {

        /**
   		 * Set jobManager
         * 
         * @param jobManager the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionInfoEntBuilder setJobManager(JobManagerEnt jobManager);
        
        /**
         * This properties is only given if this node is part of a workflow (i.e. a component&#39;s workflow) that is in streaming mode. If true, this node can process the data in streamed manner, if false, it can&#39;t.
         * 
         * @param streamable the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionInfoEntBuilder setStreamable(Boolean streamable);
        
        /**
         * A custom (decorator) icon set by its node executor (or the node executor of the parent workflow). Not present if the custom executor doesn&#39;t define a special icon or the &#39;streamable&#39; property is given. The icon is encoded in a data-url.
         * 
         * @param icon the property value,  
         * @return this entity builder for chaining
         */
        NodeExecutionInfoEntBuilder setIcon(String icon);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeExecutionInfoEnt build();
    
    }

}

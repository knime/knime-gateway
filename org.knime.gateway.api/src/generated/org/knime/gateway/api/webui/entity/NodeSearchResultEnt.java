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

import org.knime.gateway.api.webui.entity.NodeTemplateEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents the result of a node/component search in the node repository.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeSearchResultEnt extends GatewayEntity {


  /**
   * The found nodes. If a non-empty search query has been given for the search, the nodes are sorted by their &#39;search score&#39; (i.e. how well it &#39;fits&#39; the query). If there is no search query but, e.g., only a list of tags (which is then more a &#39;node filter result&#39;), the nodes are sorted by their pre-defined weight (the weight might be, e.g., the nodes general popularity).
   * @return nodes , never <code>null</code>
   **/
  public java.util.List<NodeTemplateEnt> getNodes();

  /**
   * The total number of found nodes (depending on the actual search query). Not all nodes might be included because of used offsets or limits (pagination) used for the search.
   * @return totalNumNodes , never <code>null</code>
   **/
  public Integer getTotalNumNodes();

  /**
   * The total number of founds nodes (depending on the actual search query) in the complementary partitions to the one currently selected
   * @return totalNonPartitionNodes , never <code>null</code>
   **/
  public Integer getTotalNonPartitionNodes();

  /**
   * The union of the tags of all the nodes in the search result (i.e. also including the nodes that might not be explicitly listed as part of this search result instance). The tags are sorted by their frequency of how many nodes nodes (in the search result) carry that particular tag.
   * @return tags , never <code>null</code>
   **/
  public java.util.List<String> getTags();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NodeSearchResultEnt)other;
      valueConsumer.accept("nodes", Pair.create(getNodes(), e.getNodes()));
      valueConsumer.accept("totalNumNodes", Pair.create(getTotalNumNodes(), e.getTotalNumNodes()));
      valueConsumer.accept("totalNonPartitionNodes", Pair.create(getTotalNonPartitionNodes(), e.getTotalNonPartitionNodes()));
      valueConsumer.accept("tags", Pair.create(getTags(), e.getTags()));
  }

    /**
     * The builder for the entity.
     */
    public interface NodeSearchResultEntBuilder extends GatewayEntityBuilder<NodeSearchResultEnt> {

        /**
         * The found nodes. If a non-empty search query has been given for the search, the nodes are sorted by their &#39;search score&#39; (i.e. how well it &#39;fits&#39; the query). If there is no search query but, e.g., only a list of tags (which is then more a &#39;node filter result&#39;), the nodes are sorted by their pre-defined weight (the weight might be, e.g., the nodes general popularity).
         * 
         * @param nodes the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeSearchResultEntBuilder setNodes(java.util.List<NodeTemplateEnt> nodes);
        
        /**
         * The total number of found nodes (depending on the actual search query). Not all nodes might be included because of used offsets or limits (pagination) used for the search.
         * 
         * @param totalNumNodes the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeSearchResultEntBuilder setTotalNumNodes(Integer totalNumNodes);
        
        /**
         * The total number of founds nodes (depending on the actual search query) in the complementary partitions to the one currently selected
         * 
         * @param totalNonPartitionNodes the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeSearchResultEntBuilder setTotalNonPartitionNodes(Integer totalNonPartitionNodes);
        
        /**
         * The union of the tags of all the nodes in the search result (i.e. also including the nodes that might not be explicitly listed as part of this search result instance). The tags are sorted by their frequency of how many nodes nodes (in the search result) carry that particular tag.
         * 
         * @param tags the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodeSearchResultEntBuilder setTags(java.util.List<String> tags);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeSearchResultEnt build();
    
    }

}

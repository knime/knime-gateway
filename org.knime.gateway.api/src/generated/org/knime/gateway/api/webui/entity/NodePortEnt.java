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

import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A single port of a node.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodePortEnt extends GatewayEntity, NodePortTemplateEnt {


  /**
   * For native nodes, this provides additional information if the port carries data (i.e. if the respective node is executed and the port is active). For components, the port description is taken from the component&#39;s description, if provided by the user.
   * @return info 
   **/
  public String getInfo();

  /**
   * The index starting at 0.
   * @return index , never <code>null</code>
   **/
  public Integer getIndex();

  /**
   * Get connectedVia
   * @return connectedVia , never <code>null</code>
   **/
  public java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> getConnectedVia();

  /**
   * Get inactive
   * @return inactive 
   **/
  public Boolean isInactive();

  /**
   * A port content version which allows one to detect port content changes (port data and spec). Will be absent if there is no data, i.e. no port content at all or if it&#39;s an input port. Will also be absent if there is no &#39;interaction info&#39; supposed to be included.
   * @return portContentVersion 
   **/
  public Integer getPortContentVersion();

  /**
   * The port group this port belongs to. Only available for native nodes.
   * @return portGroupId 
   **/
  public String getPortGroupId();

  /**
   * Whether this port can be removed. Only available if &#39;interaction info&#39; is supposed to be included.
   * @return canRemove 
   **/
  public Boolean isCanRemove();

  /**
   * Flag for the magical ports on the outside of Components with reporting activated, absent if &#x60;false&#x60;.
   * @return isComponentReportPort 
   **/
  public Boolean isComponentReportPort();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (NodePortEnt)other;
      valueConsumer.accept("name", Pair.create(getName(), e.getName()));
      valueConsumer.accept("typeId", Pair.create(getTypeId(), e.getTypeId()));
      valueConsumer.accept("optional", Pair.create(isOptional(), e.isOptional()));
      valueConsumer.accept("info", Pair.create(getInfo(), e.getInfo()));
      valueConsumer.accept("index", Pair.create(getIndex(), e.getIndex()));
      valueConsumer.accept("connectedVia", Pair.create(getConnectedVia(), e.getConnectedVia()));
      valueConsumer.accept("inactive", Pair.create(isInactive(), e.isInactive()));
      valueConsumer.accept("portContentVersion", Pair.create(getPortContentVersion(), e.getPortContentVersion()));
      valueConsumer.accept("portGroupId", Pair.create(getPortGroupId(), e.getPortGroupId()));
      valueConsumer.accept("canRemove", Pair.create(isCanRemove(), e.isCanRemove()));
      valueConsumer.accept("isComponentReportPort", Pair.create(isComponentReportPort(), e.isComponentReportPort()));
  }

    /**
     * The builder for the entity.
     */
    public interface NodePortEntBuilder extends GatewayEntityBuilder<NodePortEnt> {

        /**
         * A descriptive name for the port. For native nodes, this name is taken from the node description. For components, the port name is taken from the component&#39;s description, if provided by the user.
         * 
         * @param name the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setName(String name);
        
        /**
         * A unique port type id helping to infer the corresponding &#39;PortType&#39;
         * 
         * @param typeId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setTypeId(String typeId);
        
        /**
         * Whether it&#39;s a optional port or not.
         * 
         * @param optional the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setOptional(Boolean optional);
        
        /**
         * For native nodes, this provides additional information if the port carries data (i.e. if the respective node is executed and the port is active). For components, the port description is taken from the component&#39;s description, if provided by the user.
         * 
         * @param info the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setInfo(String info);
        
        /**
         * The index starting at 0.
         * 
         * @param index the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setIndex(Integer index);
        
        /**
   		 * Set connectedVia
         * 
         * @param connectedVia the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setConnectedVia(java.util.List<org.knime.gateway.api.entity.ConnectionIDEnt> connectedVia);
        
        /**
   		 * Set inactive
         * 
         * @param inactive the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setInactive(Boolean inactive);
        
        /**
         * A port content version which allows one to detect port content changes (port data and spec). Will be absent if there is no data, i.e. no port content at all or if it&#39;s an input port. Will also be absent if there is no &#39;interaction info&#39; supposed to be included.
         * 
         * @param portContentVersion the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setPortContentVersion(Integer portContentVersion);
        
        /**
         * The port group this port belongs to. Only available for native nodes.
         * 
         * @param portGroupId the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setPortGroupId(String portGroupId);
        
        /**
         * Whether this port can be removed. Only available if &#39;interaction info&#39; is supposed to be included.
         * 
         * @param canRemove the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setCanRemove(Boolean canRemove);
        
        /**
         * Flag for the magical ports on the outside of Components with reporting activated, absent if &#x60;false&#x60;.
         * 
         * @param isComponentReportPort the property value,  
         * @return this entity builder for chaining
         */
        NodePortEntBuilder setIsComponentReportPort(Boolean isComponentReportPort);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodePortEnt build();
    
    }

}

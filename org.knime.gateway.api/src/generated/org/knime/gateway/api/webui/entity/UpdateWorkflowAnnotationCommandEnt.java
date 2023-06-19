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

import org.knime.gateway.api.webui.entity.WorkflowAnnotationCommandEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Updates the text and/or the border color of a workflow annotation. Either one can be &#39;null&#39;,  but never both of them.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface UpdateWorkflowAnnotationCommandEnt extends GatewayEntity, WorkflowAnnotationCommandEnt {


  /**
   * The new formatted text to update the annotation with
   * @return text 
   **/
  public String getText();

  /**
   * The new border color as a hex string (rgb)
   * @return borderColor 
   **/
  public String getBorderColor();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (UpdateWorkflowAnnotationCommandEnt)other;
      valueConsumer.accept("kind", Pair.create(getKind(), e.getKind()));
      valueConsumer.accept("annotationId", Pair.create(getAnnotationId(), e.getAnnotationId()));
      valueConsumer.accept("text", Pair.create(getText(), e.getText()));
      valueConsumer.accept("borderColor", Pair.create(getBorderColor(), e.getBorderColor()));
  }

    /**
     * The builder for the entity.
     */
    public interface UpdateWorkflowAnnotationCommandEntBuilder extends GatewayEntityBuilder<UpdateWorkflowAnnotationCommandEnt> {

        /**
         * The kind of command which directly maps to a specific &#39;implementation&#39;.
         * 
         * @param kind the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        UpdateWorkflowAnnotationCommandEntBuilder setKind(KindEnum kind);
        
        /**
         * The ID of the annotation to manipulate
         * 
         * @param annotationId the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        UpdateWorkflowAnnotationCommandEntBuilder setAnnotationId(org.knime.gateway.api.entity.AnnotationIDEnt annotationId);
        
        /**
         * The new formatted text to update the annotation with
         * 
         * @param text the property value,  
         * @return this entity builder for chaining
         */
        UpdateWorkflowAnnotationCommandEntBuilder setText(String text);
        
        /**
         * The new border color as a hex string (rgb)
         * 
         * @param borderColor the property value,  
         * @return this entity builder for chaining
         */
        UpdateWorkflowAnnotationCommandEntBuilder setBorderColor(String borderColor);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        UpdateWorkflowAnnotationCommandEnt build();
    
    }

}

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

import org.knime.gateway.api.webui.entity.KaiWelcomeMessagesEnt;

import java.util.function.BiConsumer;

import org.knime.core.util.Pair;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * KaiUiStringsEnt
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface KaiUiStringsEnt extends GatewayEntity {


  /**
   * The disclaimer users have to accept before they can chat with K-AI.
   * @return disclaimer , never <code>null</code>
   **/
  public String getDisclaimer();

  /**
   * Get welcomeMessages
   * @return welcomeMessages , never <code>null</code>
   **/
  public KaiWelcomeMessagesEnt getWelcomeMessages();


  @Override
  default void forEachPropertyValue(final GatewayEntity other,
      final BiConsumer<String, Pair<Object, Object>> valueConsumer) {
      var e = (KaiUiStringsEnt)other;
      valueConsumer.accept("disclaimer", Pair.create(getDisclaimer(), e.getDisclaimer()));
      valueConsumer.accept("welcomeMessages", Pair.create(getWelcomeMessages(), e.getWelcomeMessages()));
  }

    /**
     * The builder for the entity.
     */
    public interface KaiUiStringsEntBuilder extends GatewayEntityBuilder<KaiUiStringsEnt> {

        /**
         * The disclaimer users have to accept before they can chat with K-AI.
         * 
         * @param disclaimer the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiUiStringsEntBuilder setDisclaimer(String disclaimer);
        
        /**
   		 * Set welcomeMessages
         * 
         * @param welcomeMessages the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        KaiUiStringsEntBuilder setWelcomeMessages(KaiWelcomeMessagesEnt welcomeMessages);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        KaiUiStringsEnt build();
    
    }

}

/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.json;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knime.gateway.json.entity.util.ListEntities;

/**
 * Utility class for json rpc stuff.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonUtil {

    private JsonUtil() {
        //utility class
    }

    /**
     * Adds entity and entity builder mixin classes to the passed mapper in order to add jackson-annotations to the
     * respective entity (and entity builder) interface for de-/serialization.
     *
     * @param mapper the object mapper to add the mixins to
     */
    public static final void addMixIns(final ObjectMapper mapper) {
        List<Class<?>> entityClasses = ListEntities.listEntityClasses();
        List<Class<?>> entityBuilderClasses = ListEntities.listEntityBuilderClasses();
        List<Class<?>> entityMixInClasses = com.knime.gateway.json.entity.util.ListEntities.listEntityClasses();
        List<Class<?>> entityBuilderMixInClasses =
            com.knime.gateway.json.entity.util.ListEntities.listEntityBuilderClasses();

        for (int i = 0; i < entityClasses.size(); i++) {
            mapper.addMixIn(entityClasses.get(i), entityMixInClasses.get(i));
            mapper.addMixIn(entityBuilderClasses.get(i), entityBuilderMixInClasses.get(i));
        }
    }
}

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
package com.knime.gateway.json.testing.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.knime.gateway.json.util.ObjectMapperUtil;
import com.knime.gateway.v0.entity.XYEnt;
import com.knime.gateway.v0.entity.impl.DefaultXYEnt.DefaultXYEntBuilder;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DeSerializationTest {

    /**
     * Deserialization with a unknown property is expected to work for backward compatibility.
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testDeserializationWithUnknownProperty() throws JsonProcessingException {
        XYEnt entity = new DefaultXYEntBuilder().setX(10).setY(50).build();
        ObjectMapper mapper = ObjectMapperUtil.getInstance().getObjectMapper();
        ObjectNode jsonNode = mapper.valueToTree(entity).deepCopy();
        jsonNode.put("testProp", "test123");

        XYEnt newEntity = mapper.treeToValue(jsonNode, entity.getClass());
        assertThat("Unexpected problem serializing entity from json with unknown property", newEntity, is(entity));
    }

}

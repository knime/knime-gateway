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
package com.knime.gateway.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests {@link ConnectionIDEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ConnectionIDEntTest {

    /**
     * Tests 'toString' and create from string via constructor.
     */
    @Test
    public void testToAndFromString() {
        //to
        assertThat(new ConnectionIDEnt(new NodeIDEnt(4,3), 4).toString(), is("root:4:3_4"));

        //from
        assertThat(new ConnectionIDEnt("root_3"), is(new ConnectionIDEnt(NodeIDEnt.getRootID(), 3)));
    }

}

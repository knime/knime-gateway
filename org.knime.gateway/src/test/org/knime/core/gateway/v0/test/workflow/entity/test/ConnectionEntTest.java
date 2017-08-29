/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *
 */
package org.knime.core.gateway.v0.test.workflow.entity.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.knime.gateway.entities.EntityBuilderManager;
import java.util.List;
import org.knime.gateway.v0.workflow.entity.ConnectionEnt;
import org.knime.gateway.v0.workflow.entity.XYEnt;
import org.knime.gateway.v0.workflow.entity.builder.ConnectionEntBuilder;
import org.knime.gateway.v0.workflow.entity.builder.XYEntBuilder;

/**
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class ConnectionEntTest {

    private static Random RAND = new Random();

    @Test
    public void test() {
        List<Object> valueList = createValueList();
        ConnectionEnt ent = createEnt(valueList);
        testEnt(ent, valueList);
    }

    public static ConnectionEnt createEnt(final List<Object> valueList) {
        ConnectionEntBuilder builder = EntityBuilderManager.builder(ConnectionEntBuilder.class);
		builder.setDest((String) valueList.get(0));
		builder.setDestPort((int) valueList.get(1));
		builder.setSource((String) valueList.get(2));
		builder.setSourcePort((int) valueList.get(3));
		builder.setIsDeleteable((boolean) valueList.get(4));
		builder.setIsFlowVariablePortConnection((boolean) valueList.get(5));
		List<XYEnt> list6 = new ArrayList<>();
		List<Object> subList6 = (List<Object>) valueList.get(6);
		for(int i = 0; i < subList6.size(); i++) {
			list6.add(XYEntTest.createEnt((List<Object>) subList6.get(i)));
		}
		builder.setBendPoints(list6);
		builder.setType((String) valueList.get(7));
        return builder.build();
    }

    public static void testEnt(final ConnectionEnt ent, final List<Object> valueList) {
		assertEquals(ent.getDest(), (String) valueList.get(0));
		assertEquals(ent.getDestPort(), (int) valueList.get(1));
		assertEquals(ent.getSource(), (String) valueList.get(2));
		assertEquals(ent.getSourcePort(), (int) valueList.get(3));
		assertEquals(ent.getIsDeleteable(), (boolean) valueList.get(4));
		assertEquals(ent.getIsFlowVariablePortConnection(), (boolean) valueList.get(5));
		List<Object> subValueList6 = (List<Object>) valueList.get(6);
		List<XYEnt> subList6 =  ent.getBendPoints();
		for(int i = 0; i < subList6.size(); i++) {
			XYEntTest.testEnt(subList6.get(i), (List<Object>) subValueList6.get(i));
		}
		assertEquals(ent.getType(), (String) valueList.get(7));
    }

    public static List<Object> createValueList() {
        List<Object> valueList = new ArrayList<Object>();
 		valueList.add("lzCuG");

 		valueList.add(-1154715079);

 		valueList.add("OuJNz");

 		valueList.add(-1157023572);

 		valueList.add(true);

 		valueList.add(true);

 		List<List<Object>> subList7 = new ArrayList<>();
		subList7.add(XYEntTest.createValueList());
		subList7.add(XYEntTest.createValueList());
		subList7.add(XYEntTest.createValueList());
		subList7.add(XYEntTest.createValueList());
		subList7.add(XYEntTest.createValueList());
 		valueList.add(subList7);

 		valueList.add("0qjmL");

        return valueList;
    }

}

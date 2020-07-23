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
package org.knime.gateway.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.knime.gateway.api.util.EntityTranslateUtil.translateDataCellEnt;

import java.util.Arrays;

import org.junit.Test;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.MissingCell;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.gateway.api.entity.DataCellEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt;
import org.knime.gateway.api.util.EntityBuilderUtil;
import org.knime.gateway.api.util.EntityTranslateUtil;
import org.knime.gateway.impl.entity.DefaultDataCellEnt.DefaultDataCellEntBuilder;

/**
 * Tests {@link EntityTranslateUtil}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityTranslateUtilTest {

	@Test
	public void testTranslateDataCellEntMissing() {
		DataCellEnt ent = new DefaultDataCellEntBuilder().setMissing(true)
				.setValueAsString("missing#!").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not a missing cell", translated, is(new MissingCell("missing#!")));
	}

	@Test
	public void testTranslateDataCellEntProblem() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setProblem(true)
				.setMissing(true) //'problem' precedes 'missing'
				.setValueAsString("problem#!").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not an error cell", translated.toString(), is("problem#!"));
	}

	@Test
	public void testTranslateDataCellUnknownType() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType("unknown_type").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not an error cell", translated.toString(), containsString("Cannot deserialize cell of type"));
	}

	@Test
	public void testTranslateDataCellListCell() {
		ListCell listCell = CollectionCellFactory
				.createListCell(Arrays.asList(new IntCell(5), new StringCell("string")));
		DataCellEnt ent = EntityBuilderUtil.buildDataCellEnt(listCell, null);
		DataCell translated = translateDataCellEnt(ent, DataType.getType(ListCell.class, IntCell.TYPE));
		assertThat("not the expected cell", translated, is(listCell));
	}

	@Test
	public void testTranslateDataCellIntCellFail() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType(IntCell.class.getCanonicalName())
				.setValueAsString("5")
				.setBinary(true).build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("error cell expected", translated.toString(), containsString("Problem deserializing cell"));
	}

	@Test
	public void testTranslateDataCellDoubleCell() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType(DoubleCell.class.getCanonicalName())
				.setValueAsString("5.6").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not the expected double cell", translated, is(new DoubleCell(5.6)));
	}

	@Test
	public void testTranslateDataCellIntCell() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType(IntCell.class.getCanonicalName())
				.setValueAsString("5").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not the expected int cell", translated, is(new IntCell(5)));
	}

	@Test
	public void testTranslateDataCellStringCell() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType(StringCell.class.getCanonicalName())
				.setValueAsString("string").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not the expected string cell", translated, is(new StringCell("string")));
	}

	@Test
	public void testTranslateDataCellLongCell() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType(LongCell.class.getCanonicalName())
				.setValueAsString("5").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not the expected long cell", translated, is(new LongCell(5)));
	}

	@Test
	public void testTranslateDataCellBooleanCell() {
		DataCellEnt ent = new DefaultDataCellEntBuilder()
				.setType(BooleanCell.class.getCanonicalName())
				.setValueAsString("false").build();
		DataCell translated = translateDataCellEnt(ent, null);
		assertThat("not the expected boolean cell", translated, is(BooleanCellFactory.create(false)));
	}

	@Test
	public void testTranslateNodeUIInfoEnt() {
		NodeUIInformation uiinfo = NodeUIInformation.builder()
				.setNodeLocation(50, 30, 10, 8)
				.setIsDropLocation(false).build();
		NodeUIInfoEnt ent = EntityBuilderUtil.buildNodeUIInfoEnt(uiinfo);
		NodeUIInformation translated = EntityTranslateUtil.translateNodeUIInfoEnt(ent);
		assertThat("not the expected ui info", translated.getBounds(), is(uiinfo.getBounds()));
		assertThat("not the expected ui info", translated.isDropLocation(), is(uiinfo.isDropLocation()));
		assertThat("not the expected ui info", translated.hasAbsoluteCoordinates(), is(true));
	}

}

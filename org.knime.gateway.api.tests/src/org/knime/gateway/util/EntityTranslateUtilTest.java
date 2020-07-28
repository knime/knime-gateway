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

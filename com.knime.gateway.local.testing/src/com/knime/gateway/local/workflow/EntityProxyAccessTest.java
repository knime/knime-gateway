/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 * History
 *   May 25, 2020 (hornm): created
 */
package com.knime.gateway.local.workflow;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static com.knime.gateway.util.EntityBuilderUtil.buildPortObjectSpecEnt;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;

import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.PortObjectSpecEnt.PortObjectSpecEntBuilder;
import com.knime.gateway.entity.PortTypeEnt.PortTypeEntBuilder;
import com.knime.gateway.local.workflow.EntityProxyNodeOutPort.ProblemPortObjectSpec;

/**
 * Tests methods in {@link EntityProxyAccess}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityProxyAccessTest {

    /**
     * Tests {@link EntityProxyAccess#createPortObjectSpecsFromEntity(List)}.
     */
    @Test
    public void testCreatePortObjectSpecsFromEntityList() {
        PortObjectSpecEnt inactivePort =
            buildPortObjectSpecEnt(BufferedDataTable.TYPE, InactiveBranchPortObjectSpec.INSTANCE);
        PortObjectSpecEnt flowvarPort =
            buildPortObjectSpecEnt(FlowVariablePortObject.TYPE, FlowVariablePortObjectSpec.INSTANCE);
        PortObjectSpecEnt tablespecPort = buildPortObjectSpecEnt(BufferedDataTable.TYPE, new DataTableSpec("test"));
        PortObjectSpecEnt abstractSimplePort =
            buildPortObjectSpecEnt(ImagePortObject.TYPE, new ImagePortObjectSpec(IntCell.TYPE));
        PortObjectSpecEnt dummyPort = buildPortObjectSpecEnt(DummyPortObject.TYPE, new DummyPortObjectSpec());
        PortObjectSpecEnt dummySimplePort =
            buildPortObjectSpecEnt(DummySimplePortObject.TYPE, new DummySimplePortObjectSpec());
        PortObjectSpecEnt problemPort = builder(PortObjectSpecEntBuilder.class).setProblem(true)
            .setClassName(DataTableSpec.class.getCanonicalName())
            .setPortType(builder(PortTypeEntBuilder.class)
                .setPortObjectClassName(BufferedDataTable.class.getCanonicalName())
                .setOptional(false).build())
            .setRepresentation("problem").setInactive(false).build();
        List<PortObjectSpecEnt> portSpecEntList = Arrays.asList(inactivePort, flowvarPort, tablespecPort,
            abstractSimplePort, dummyPort, dummySimplePort, problemPort);

        // method under test
        PortObjectSpec[] specs = EntityProxyAccess.createPortObjectSpecsFromEntity(portSpecEntList);
        assertThat("wrong spec class", specs[0], instanceOf(InactiveBranchPortObjectSpec.class));
        assertThat("wrong spec class", specs[1], instanceOf(FlowVariablePortObjectSpec.class));
        assertThat("wrong spec class", specs[2], instanceOf(DataTableSpec.class));
        assertThat("wrong spec class", specs[3], instanceOf(ImagePortObjectSpec.class));
        assertThat("wrong spec class", specs[4], instanceOf(DummyPortObjectSpec.class));
        assertThat("wrong spec class", specs[5], instanceOf(DummySimplePortObjectSpec.class));
        assertThat("wrong spec class", specs[6], instanceOf(ProblemPortObjectSpec.class));
    }

}

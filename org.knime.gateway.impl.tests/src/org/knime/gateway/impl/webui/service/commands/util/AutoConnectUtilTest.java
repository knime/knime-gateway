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
 *   Oct 5, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.util.Pair;

@SuppressWarnings({ //
    "javadoc", //
    "java:S1602", // curly braces around single-expression lambdas
    "java:S1192" // repeated string literals
})
public class AutoConnectUtilTest {

    private static final int STEP = 100;

    private static final Geometry.Bounds ORIGIN = new Geometry.Bounds(0, 0, 1, 1);

    private static final Geometry.Delta X_STEP = Geometry.Delta.of(STEP, 0);

    private PrimitiveIterator.OfInt m_intGenerator;

    private static <T extends Connectable.Destination> List<Connectable.DestinationPort<T>>
        mockDestinationPorts(final T mock, final int numPorts) {
        return mockDestinationPorts(mock, repeat(PortObject.TYPE, numPorts).toList());
    }

    private static <T extends Connectable.Destination> List<Connectable.DestinationPort<T>>
        mockDestinationPorts(final T mock, final List<PortType> types) {
        var ports =
            enumerate(types).map(p -> spy(new Connectable.DestinationPort<>(mock, p.index(), p.element()))).toList();
        ports.forEach(p -> {
            // not connected by default
            doReturn(Optional.empty()).when(p).getIncomingConnection();
        });
        doReturn(ports).when(mock).getDestinationPorts();
        return ports;
    }

    private static <T extends Connectable.Source> List<Connectable.SourcePort<T>> mockSourcePorts(final T mock,
        final int numPorts) {
        return mockSourcePorts(mock, IntStream.range(0, numPorts).mapToObj(i -> PortObject.TYPE).toList());
    }

    private static <T extends Connectable.Source> List<Connectable.SourcePort<T>> mockSourcePorts(final T mock,
        final List<PortType> types) {
        var ports = enumerate(types).map(p -> spy(new Connectable.SourcePort<>(mock, p.index(), p.element()))).toList();
        ports.forEach(p -> {
            // not connected by default
            doReturn(Set.of()).when(p).getOutgoingConnections();
        });
        doReturn(ports).when(mock).getSourcePorts();
        return ports;
    }

    private static <T> Stream<T> repeat(T obj, int times) {
        return IntStream.range(0, times).mapToObj(i -> obj);
    }

    private static <T> Stream<Enumerated<T>> enumerate(final List<T> list) {
        return IntStream.range(0, list.size()).mapToObj(i -> new Enumerated<>(i, list.get(i)));
    }

    private static void assertPlanned(final String message, final Connectable.SourcePort<?> sourcePort,
        final Connectable.DestinationPort<?> destinationPort, final List<AutoConnectUtil.PlannedConnection> plan) {
        var isPlanned = plan.stream()
            .anyMatch(pc -> pc.sourcePort().equals(sourcePort) && pc.destinationPort().equals(destinationPort));
        assertThat(message, isPlanned);

    }

    private static void assertPlanned(final Connectable.SourcePort<?> sourcePort,
        final Connectable.DestinationPort<?> destinationPort, final List<AutoConnectUtil.PlannedConnection> plan) {
        assertPlanned("Ports %s and %s should be planned to be connected".formatted(sourcePort, destinationPort),
            sourcePort, destinationPort, plan);
    }

    @Before
    public void initIntGenerator() {
        m_intGenerator = IntStream.iterate(0, i -> i + 1).iterator();
    }

    private <T extends Connectable> T mockConnectable(final Class<T> clazz, final Geometry.Bounds bounds) {
        var mock = mock(clazz);
        when(mock.getNodeId()).thenReturn(new NodeID(m_intGenerator.next()));
        when(mock.getBounds()).thenReturn(bounds);
        return mock;
    }

    /**
     * Most basic case: Node [A] has one source port with type 1, node [B] has one destination part with type 1. There
     * is no existing connection between [A] and [B]. [A] is strictly north-west of [B]. We expect a connection to be
     * planned between [A] and [B]. <code>
         * setup:          [A]>      <[B]
         * expected plan : [A]>~~~~~<[B]
     * </code>
     *
     */
    @Test
    public void singleConnectionIsCreated() {
        // use general port object type since port object compatibility is not what we're testing here
        var portObjectType = PortObject.TYPE;

        var sourceNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MIN_VALUE);
        var sourcePort = mockSourcePorts(sourceNode, List.of(portObjectType)).get(0);
        mockDestinationPorts(sourceNode, List.of());

        var destinationNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MAX_VALUE);
        mockSourcePorts(destinationNode, List.of());
        var destinationPort = mockDestinationPorts(destinationNode, List.of(portObjectType)).get(0);

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));

        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        var plannedConnection = resultingPlan.get(0);
        assertThat("The correct connection should be planned", plannedConnection.sourcePort().equals(sourcePort)
            && plannedConnection.destinationPort().equals(destinationPort));
    }

    /**
     * Test an example input with nodes having none or several input/output ports and a meaningful spatial arrangement.
     * Setup: <code>
     * [A]>         <[C]>       <[D]
     *              <
     * [B]>
     * </code>
     */
    @Test
    public void multiplePortsAreConnected() {
        var a = mockConnectable(Connectable.NodeData.class, ORIGIN);
        mockDestinationPorts(a, 0);
        mockSourcePorts(a, 1);

        var b = mockConnectable(Connectable.NodeData.class, ORIGIN.translate(Geometry.Delta.of(0, STEP)));
        mockDestinationPorts(a, 0);
        mockSourcePorts(b, 1);

        var c = mockConnectable(Connectable.NodeData.class, ORIGIN.translate(X_STEP));
        mockDestinationPorts(c, 2);
        mockSourcePorts(c, 1);

        var d = mockConnectable(Connectable.NodeData.class, ORIGIN.translate(Geometry.Delta.of(STEP + STEP, 0)));
        mockDestinationPorts(d, 1);
        mockSourcePorts(d, 0);

        var plan = AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(a, b, c, d)));

        assertPlanned(a.getSourcePorts().get(0), c.getDestinationPorts().get(0), plan);
        assertPlanned(b.getSourcePorts().get(0), c.getDestinationPorts().get(1), plan);
        assertPlanned(c.getSourcePorts().get(0), d.getDestinationPorts().get(0), plan);
    }

    /**
     * Test an example input with a meaningful spatial arrangement. <code>
     * <[A]>     <[B]>
     *                         <[D]>
     *               <[C]>
     * </code>
     */
    @Test
    public void testSpatialArrangementExample() {
        var a = mockConnectable(Connectable.NodeData.class, ORIGIN);
        mockDestinationPorts(a, 1);
        mockSourcePorts(a, 1);

        var b = mockConnectable(Connectable.NodeData.class, a.getBounds().translate(Geometry.Delta.of(STEP, -10)));
        mockDestinationPorts(b, 1);
        mockSourcePorts(b, 1);

        var c = mockConnectable(Connectable.NodeData.class,
            a.getBounds().translate(Geometry.Delta.of(STEP + 10, 2 * STEP)));
        mockDestinationPorts(c, 1);
        mockSourcePorts(c, 1);

        var d = mockConnectable(Connectable.NodeData.class, b.getBounds().translate(Geometry.Delta.of(STEP, STEP)));
        mockDestinationPorts(d, 1);
        mockSourcePorts(d, 1);

        var plan = AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(a, b, c, d)));

        assertPlanned(a.getSourcePorts().get(0), b.getDestinationPorts().get(0), plan);
        assertPlanned(b.getSourcePorts().get(0), c.getDestinationPorts().get(0), plan);
        assertPlanned(c.getSourcePorts().get(0), d.getDestinationPorts().get(0), plan);
    }

    /**
     * Even though port types are compatible, do not connect because spatial arrangement is invalid <code>
     *     <[A]     [B]>
     * </code>
     */
    @Test
    public void noConnectionPlannedBetweenSpatiallyIncompatible() {
        var a = mockConnectable(Connectable.NodeData.class, ORIGIN);
        mockDestinationPorts(a, 1);
        mockSourcePorts(a, 0);

        var b = mockConnectable(Connectable.NodeData.class, a.getBounds().translate(X_STEP));
        mockDestinationPorts(b, 0);
        mockSourcePorts(b, 1);

        var plan = AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(a, b)));

        assertThat("Connection is not planned", plan.isEmpty());
    }

    /**
     * <code>
     *     [A]>     [B]
     * </code>
     */
    @Test
    public void noConnectionIfNoDestinationPort() {
        var a = mockConnectable(Connectable.NodeData.class, ORIGIN);
        mockDestinationPorts(a, 0);
        mockSourcePorts(a, 1);

        var b = mockConnectable(Connectable.NodeData.class, a.getBounds().translate(X_STEP));
        mockDestinationPorts(b, 0);
        mockSourcePorts(b, 0);

        var plan = AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(a, b)));

        assertThat("Connection is not planned", plan.isEmpty());
    }

    /**
     * <code>
     *     [A]     <[B]
     * </code>
     */
    @Test
    public void noConnectionIfNoSourcePort() {
        var a = mockConnectable(Connectable.NodeData.class, ORIGIN);
        mockDestinationPorts(a, 0);
        mockSourcePorts(a, 0);

        var b = mockConnectable(Connectable.NodeData.class, a.getBounds().translate(X_STEP));
        mockDestinationPorts(b, 1);
        mockSourcePorts(b, 0);

        var plan = AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(a, b)));

        assertThat("Connection is not planned", plan.isEmpty());
    }

    /**
     * <code>
     *     [A]1>    <2[B]
     * </code>
     */
    @Test
    public void noConnectionPlannedBetweenIncompatiblePortTypes() {
        var incompatiblePortObjectTypes = new Pair<>(FlowVariablePortObject.TYPE, ImagePortObject.TYPE);

        var sourceNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MIN_VALUE);
        mockSourcePorts(sourceNode, List.of(incompatiblePortObjectTypes.getFirst()));
        mockDestinationPorts(sourceNode, List.of());

        var destinationNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MAX_VALUE);
        mockSourcePorts(destinationNode, List.of());
        mockDestinationPorts(destinationNode, List.of(incompatiblePortObjectTypes.getSecond()));

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));

        assertThat("No connections are planned", resultingPlan.isEmpty());
    }

    /**
     * Assert that visible flow variable ports are connected by the "standard mode", i.e. not explicitly considering
     * hidden flow variable ports. <code>
     *     [ ]f>       <f[ ]
     * </code>
     */
    @Test
    public void visibleToVisibleIsConnectedInDataPorts() {
        var sourceNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MIN_VALUE);
        var sourcePort = mockSourcePorts(sourceNode, List.of(FlowVariablePortObject.TYPE)).get(0);
        mockDestinationPorts(sourceNode, List.of());

        var destinationNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MAX_VALUE);
        mockSourcePorts(destinationNode, List.of());
        var destinationPort = mockDestinationPorts(destinationNode, List.of(FlowVariablePortObject.TYPE)).get(0);

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));

        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned(sourcePort, destinationPort, resultingPlan);
    }

    /**
     * Assert that visible flow variable ports are connected by "flow variable mode", i.e., when considering hidden
     * flow variable ports. <code>
     *     [ ]f>       <f[ ]
     * </code>
     */
    @Test
    public void visibleToVisibleIsConnectedInFlowPorts() {
        var sourceNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MIN_VALUE);
        var sourcePort = mockSourcePorts(sourceNode, List.of(FlowVariablePortObject.TYPE)).get(0);
        mockDestinationPorts(sourceNode, List.of());

        var destinationNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MAX_VALUE);
        mockSourcePorts(destinationNode, List.of());
        var destinationPort = mockDestinationPorts(destinationNode, List.of(FlowVariablePortObject.TYPE)).get(0);

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));

        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned(sourcePort, destinationPort, resultingPlan);
    }

    /**
     * Assert that hidden flow variable ports (<code>fh></code>) are connected by the "standard mode", but only if no
     * visible ones (<code>f></code>) are present. <code>
     *     [ ]fh>   <fh[ ]
     * </code>
     */
    @Test
    public void hiddenToHiddenIsPlannedOnlyWhenNoExplicitPresent() {
        var sourceNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MIN_VALUE);
        var sourcePorts =
            List.of(spy(new Connectable.FlowSourcePort(sourceNode, 0, FlowVariablePortObject.TYPE, false)));
        sourcePorts.forEach(p -> doReturn(Set.of()).when(p).getOutgoingConnections());
        doReturn(sourcePorts).when(sourceNode).getSourcePorts();
        var destinationNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MAX_VALUE);
        var destinationPorts =
            List.of(spy(new Connectable.FlowDestinationPort(destinationNode, 0, FlowVariablePortObject.TYPE, true)));
        destinationPorts.forEach(p -> doReturn(Optional.empty()).when(p).getIncomingConnection());
        doReturn(destinationPorts).when(destinationNode).getDestinationPorts();

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));
        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned("hidden flow variable ports are connected", sourcePorts.get(0), destinationPorts.get(0),
            resultingPlan);
    }

    /**
     * Assert that visible-to-hidden connections are planned <code>
     *        [ ]fh>
     *           f >         <fh[ ]
     * </code>
     */
    @Test
    public void visibleToHiddenIsPlanned() {
        var sourceNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MIN_VALUE);
        var sourcePorts =
            List.of(spy(new Connectable.FlowSourcePort(sourceNode, 0, FlowVariablePortObject.TYPE, false)),
                spy(new Connectable.FlowSourcePort(sourceNode, 1, FlowVariablePortObject.TYPE, true)));
        sourcePorts.forEach(p -> doReturn(Set.of()).when(p).getOutgoingConnections());
        doReturn(sourcePorts).when(sourceNode).getSourcePorts();
        var destinationNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MAX_VALUE);
        var destinationPorts =
            List.of(spy(new Connectable.FlowDestinationPort(destinationNode, 0, FlowVariablePortObject.TYPE, true)));
        destinationPorts.forEach(p -> doReturn(Optional.empty()).when(p).getIncomingConnection());
        doReturn(destinationPorts).when(destinationNode).getDestinationPorts();

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));
        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned("Source for hidden flow variable target is visible port if present", sourcePorts.get(1),
            destinationPorts.get(0), resultingPlan);
    }

    /**
     * <code>
     *      [ ]fh>      <f[ ]
     * </code>
     */
    @Test
    public void hiddenToVisibleIsPlanned() {
        var sourceNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MIN_VALUE);
        var sourcePorts =
            List.of(spy(new Connectable.FlowSourcePort(sourceNode, 0, FlowVariablePortObject.TYPE, false)));
        sourcePorts.forEach(p -> doReturn(Set.of()).when(p).getOutgoingConnections());
        doReturn(sourcePorts).when(sourceNode).getSourcePorts();
        var destinationNode = mockConnectable(Connectable.NodeFlow.class, Geometry.Bounds.MAX_VALUE);
        var destinationPorts =
            List.of(spy(new Connectable.FlowDestinationPort(destinationNode, 0, FlowVariablePortObject.TYPE, false)));
        destinationPorts.forEach(p -> doReturn(Optional.empty()).when(p).getIncomingConnection());
        doReturn(destinationPorts).when(destinationNode).getDestinationPorts();

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(sourceNode, destinationNode)));
        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned("Source for hidden flow variable target is visible port if present", sourcePorts.get(0),
            destinationPorts.get(0), resultingPlan);

    }

    /**
     * <code>
     * |                        |
     * | >    <[A]>   <[B]>   < |
     * |                        |
     * </code>
     */
    @Test
    public void bothInOutPortsBarAreConnected() {
        var inPortsBar = mockConnectable(Connectable.InPortsBarData.class, Geometry.Bounds.MIN_VALUE);
        mockSourcePorts(inPortsBar, 1);

        var a = mockConnectable(Connectable.NodeData.class, ORIGIN);
        mockDestinationPorts(a, 1);
        mockSourcePorts(a, 1);

        var b = mockConnectable(Connectable.NodeData.class, a.getBounds().translate(X_STEP));
        mockDestinationPorts(b, 1);
        mockSourcePorts(b, 1);

        var outPortsBar = mockConnectable(Connectable.OutPortsBarData.class, Geometry.Bounds.MAX_VALUE);
        mockDestinationPorts(outPortsBar, 1);

        var plan = AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(inPortsBar, a, b, outPortsBar)));

        assertPlanned(inPortsBar.getSourcePorts().get(0), a.getDestinationPorts().get(0), plan);
        assertPlanned(a.getSourcePorts().get(0), b.getDestinationPorts().get(0), plan);
        assertPlanned(b.getSourcePorts().get(0), outPortsBar.getDestinationPorts().get(0), plan);
    }

    /**
     * <code>
     *     |
     *     | >      <[ ]
     *     |
     * </code>
     */
    @Test
    public void inPortsBarIsConnected() {
        var portObjectType = PortObject.TYPE;
        var inPortsBar = mockConnectable(Connectable.InPortsBarData.class, Geometry.Bounds.MIN_VALUE);
        var sourcePort = mockSourcePorts(inPortsBar, List.of(portObjectType)).get(0);

        var destinationNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MAX_VALUE);
        var destinationPort = mockDestinationPorts(destinationNode, List.of(portObjectType)).get(0);
        mockSourcePorts(destinationNode, Collections.emptyList());

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(inPortsBar, destinationNode)));

        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned(sourcePort, destinationPort, resultingPlan);
    }

    /**
     * <code>
     *                |
     *     [ ]>     < |
     *                |
     * </code>
     */
    @Test
    public void outPortsBarIsConnected() {
        var portObjectType = PortObject.TYPE;

        var sourceNode = mockConnectable(Connectable.NodeData.class, Geometry.Bounds.MIN_VALUE);
        var sourcePort = mockSourcePorts(sourceNode, List.of(portObjectType)).get(0);
        mockDestinationPorts(sourceNode, Collections.emptyList());

        var outPortsBar = mockConnectable(Connectable.OutPortsBar.class, Geometry.Bounds.MAX_VALUE);
        var destinationPort = mockDestinationPorts(outPortsBar, List.of(portObjectType)).get(0);

        var resultingPlan =
            AutoConnectUtil.plan(new AutoConnectUtil.OrderedConnectables(Set.of(outPortsBar, sourceNode)));

        assertThat("Only one connection should be planned", resultingPlan.size() == 1);
        assertPlanned(sourcePort, destinationPort, resultingPlan);
    }

    record Enumerated<T>(int index, T element) {

    }

}

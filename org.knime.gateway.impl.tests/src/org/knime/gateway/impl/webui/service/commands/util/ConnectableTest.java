package org.knime.gateway.impl.webui.service.commands.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.extension.NodeFactoryProvider;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.Pair;
import org.knime.testing.util.WorkflowManagerUtil;

@SuppressWarnings("javadoc")
public class ConnectableTest {

    private static final int[] EXAMPLE_BOUNDS = new int[]{1, 2, 3, 4};

    private static final NodeFactory<? extends NodeModel> SORTER;

    static {
        try {
            SORTER = NodeFactoryProvider.getInstance() //
                .getNodeFactory("org.knime.base.node.preproc.sorter.SorterNodeFactory").get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WorkflowManager m_wfm;

    private static void setBounds(final NodeContainer nc, final int[] bounds) {
        nc.setUIInformation(
            NodeUIInformation.builder().setNodeLocation(bounds[0], bounds[1], bounds[2], bounds[3]).build());
    }

    private static void assertBounds(final int[] bounds, final Connectable connectable) {
        assertThat("Bounds not null", connectable.getBounds() != null);
        assertThat("Bounds valid", //
            connectable.getBounds().leftTop().x() == bounds[0] //
                && connectable.getBounds().leftTop().y() == bounds[1] //
                && connectable.getBounds().width() == bounds[2] //
                && connectable.getBounds().height() == bounds[3] //
        );
    }

    private static void assertDestinationPorts(final List<PortType> expectedPorts, final Connectable.Destination destination) {
        assertThat("Input ports match", zip( //
            expectedPorts, //
            destination.getDestinationPorts() //
        ).allMatch(pair -> pair.getFirst().equals(pair.getSecond().type())) //
        );
    }

    private static void assertSourcePorts(final List<PortType> expectedPorts, final Connectable.Source destination) {
        assertThat("Input ports match", zip( //
            expectedPorts, //
            destination.getSourcePorts() //
        ).allMatch(pair -> pair.getFirst().equals(pair.getSecond().type())) //
        );
    }

    private static <A, B> Stream<Pair<A, B>> zip(final List<A> a, final List<B> b) {
        assertThat("Lists of different length", a.size(), is(b.size()));
        return IntStream.range(0, Math.min(a.size(), b.size())).mapToObj(i -> Pair.create(a.get(i), b.get(i)));
    }

    @Before
    public void initWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    @Test
    public void testNodeDataForNativeNodes() throws Exception {
        var nativeNode = WorkflowManagerUtil.createAndAddNode(m_wfm, SORTER);
        var nodeData = new Connectable.NodeData(nativeNode.getID(), m_wfm);
        setBounds(nativeNode, EXAMPLE_BOUNDS);
        assertEquals(nativeNode.getID(), nodeData.getNodeId());
        assertDestinationPorts(List.of(BufferedDataTable.TYPE), nodeData);
        assertSourcePorts(List.of(BufferedDataTable.TYPE), nodeData);
        assertBounds(EXAMPLE_BOUNDS, nodeData);
    }

    @Test
    public void testNodeFlowForNativeNodes() throws Exception {
        var nativeNode = WorkflowManagerUtil.createAndAddNode(m_wfm, SORTER);
        var nodeData = new Connectable.NodeFlow(nativeNode.getID(), m_wfm);
        setBounds(nativeNode, EXAMPLE_BOUNDS);
        assertEquals(nativeNode.getID(), nodeData.getNodeId());
        assertDestinationPorts(List.of(FlowVariablePortObject.TYPE), nodeData);
        assertSourcePorts(List.of(FlowVariablePortObject.TYPE), nodeData);
        assertBounds(EXAMPLE_BOUNDS, nodeData);
    }

    @Test
    public void testNodeDataForComponent() {
        var inPorts = List.of(PortObject.TYPE, FlowVariablePortObject.TYPE, FlowVariablePortObject.TYPE);
        var outPorts = List.of(PortObject.TYPE, PortObject.TYPE, FlowVariablePortObject.TYPE);
        var metanode = m_wfm.createAndAddSubWorkflow(inPorts.toArray(new PortType[0]),
            outPorts.toArray(new PortType[0]), "metanode/component");
        var componentId = m_wfm.convertMetaNodeToSubNode(metanode.getID()).getConvertedNodeID();
        var component = m_wfm.getNodeContainer(componentId);
        setBounds(component, EXAMPLE_BOUNDS);
        var nodeData = new Connectable.NodeData(component.getID(), m_wfm);
        assertEquals(component.getID(), nodeData.getNodeId());
        assertDestinationPorts(inPorts, nodeData);
        assertSourcePorts(outPorts, nodeData);
        assertBounds(EXAMPLE_BOUNDS, nodeData);
    }

    @Test
    public void testNodeFlowForComponent() {
        var inPorts = List.of(PortObject.TYPE, FlowVariablePortObject.TYPE, FlowVariablePortObject.TYPE);
        var outPorts = List.of(PortObject.TYPE, PortObject.TYPE, FlowVariablePortObject.TYPE);
        var metanode = m_wfm.createAndAddSubWorkflow(inPorts.toArray(new PortType[0]),
            outPorts.toArray(new PortType[0]), "metanode");
        var componentId = m_wfm.convertMetaNodeToSubNode(metanode.getID()).getConvertedNodeID();
        var component = m_wfm.getNodeContainer(componentId);
        setBounds(component, EXAMPLE_BOUNDS);
        var nodeData = new Connectable.NodeFlow(component.getID(), m_wfm);
        assertEquals(metanode.getID(), nodeData.getNodeId());
        var expectedDestinationPorts =
            inPorts.stream().filter(FlowVariablePortObject.TYPE::equals).collect(Collectors.toList());
        expectedDestinationPorts.add(FlowVariablePortObject.TYPE); // implicit / hidden port
        assertDestinationPorts(expectedDestinationPorts, nodeData);
        var expectedSourcePorts =
            outPorts.stream().filter(FlowVariablePortObject.TYPE::equals).collect(Collectors.toList());
        expectedSourcePorts.add(FlowVariablePortObject.TYPE); // implicit / hidden port
        assertSourcePorts(expectedSourcePorts, nodeData);
        assertBounds(EXAMPLE_BOUNDS, nodeData);
    }

    @Test
    public void testNodeDataForMetanode() {
        var inPorts = List.of(PortObject.TYPE, FlowVariablePortObject.TYPE);
        var outPorts = List.of(PortObject.TYPE, PortObject.TYPE, FlowVariablePortObject.TYPE);
        var metanode = m_wfm.createAndAddSubWorkflow(inPorts.toArray(new PortType[0]),
            outPorts.toArray(new PortType[0]), "metanode");
        setBounds(metanode, EXAMPLE_BOUNDS);
        var nodeData = new Connectable.NodeData(metanode.getID(), m_wfm);
        assertEquals(metanode.getID(), nodeData.getNodeId());
        assertDestinationPorts(inPorts, nodeData);
        assertSourcePorts(outPorts, nodeData);
        assertBounds(EXAMPLE_BOUNDS, nodeData);
    }

    @Test
    public void testNodeFlowForMetanode() {
        var inPorts = List.of(PortObject.TYPE, FlowVariablePortObject.TYPE, FlowVariablePortObject.TYPE);
        var outPorts = List.of(PortObject.TYPE, PortObject.TYPE, FlowVariablePortObject.TYPE);
        var metanode = m_wfm.createAndAddSubWorkflow(inPorts.toArray(new PortType[0]),
            outPorts.toArray(new PortType[0]), "metanode");
        setBounds(metanode, EXAMPLE_BOUNDS);
        var nodeData = new Connectable.NodeFlow(metanode.getID(), m_wfm);
        assertEquals(metanode.getID(), nodeData.getNodeId());
        var expectedDestinationPorts = inPorts.stream().filter(FlowVariablePortObject.TYPE::equals).toList();
        assertDestinationPorts(expectedDestinationPorts, nodeData);
        var expectedSourcePorts = outPorts.stream().filter(FlowVariablePortObject.TYPE::equals).toList();
        assertSourcePorts(expectedSourcePorts, nodeData);
        assertBounds(EXAMPLE_BOUNDS, nodeData);
    }

    @Test
    public void testInOutPortsBarData() {
        var inPorts = List.of(PortObject.TYPE, FlowVariablePortObject.TYPE);
        var outPorts = List.of(PortObject.TYPE, PortObject.TYPE, FlowVariablePortObject.TYPE);
        var metanode = m_wfm.createAndAddSubWorkflow(inPorts.toArray(new PortType[0]),
                outPorts.toArray(new PortType[0]), "metanode");
        var inPortsBar = new Connectable.InPortsBarData(metanode);
        var outPortsBar = new Connectable.OutPortsBarData(metanode);
        assertEquals(metanode.getID(), inPortsBar.getNodeId());
        assertDestinationPorts(outPorts, outPortsBar);
        assertSourcePorts(inPorts, inPortsBar);
    }

    @Test
    public void testInOutPortsBarFlow() {
        var inPorts = List.of(PortObject.TYPE, FlowVariablePortObject.TYPE);
        var outPorts = List.of(PortObject.TYPE, PortObject.TYPE, FlowVariablePortObject.TYPE);
        var metanode = m_wfm.createAndAddSubWorkflow(inPorts.toArray(new PortType[0]),
                outPorts.toArray(new PortType[0]), "metanode");
        var inPortsBar = new Connectable.InPortsBarFlow(metanode);
        var outPortsBar = new Connectable.OutPortsBarFlow(metanode);
        assertEquals(metanode.getID(), inPortsBar.getNodeId());
        var expectedDestinationPorts = inPorts.stream().filter(FlowVariablePortObject.TYPE::equals).toList();
        assertDestinationPorts(expectedDestinationPorts, outPortsBar);
        var expectedSourcePorts = outPorts.stream().filter(FlowVariablePortObject.TYPE::equals).toList();
        assertSourcePorts(expectedSourcePorts, inPortsBar);
    }

}

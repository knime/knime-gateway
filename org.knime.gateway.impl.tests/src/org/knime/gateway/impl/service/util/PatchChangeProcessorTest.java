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
 *   Apr 22, 2021 (hornm): created
 */
package org.knime.gateway.impl.service.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt.ComponentNodeDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.EditableMetadataEnt.MetadataTypeEnum;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
import org.knime.gateway.api.webui.entity.PortGroupEnt.PortGroupEntBuilder;
import org.knime.gateway.api.webui.entity.TypedTextEnt.ContentTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.mockito.Mockito;

/**
 * Tests logic in {@link PatchChangeProcessor}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class PatchChangeProcessorTest {

    /**
     * Test for bug https://knime-com.atlassian.net/browse/NXT-573. If nodes have been added where one id is the
     * 'prefix/subset' of the other id (e.g. 1 and 11), one node-add patch operation went missing.
     */
    @Test
    public void testPatchesForNewNodesNXT_573() {
        NativeNodeEntBuilder nodeBuilder = builder(NativeNodeEntBuilder.class).setInPorts(Collections.emptyList())
            .setOutPorts(Collections.emptyList()).setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build())
            .setKind(KindEnum.NODE).setTemplateId("templateId");
        NodeEnt node1 = nodeBuilder.setId(new NodeIDEnt(1)).build();
        NodeEnt node2 = nodeBuilder.setId(new NodeIDEnt(11)).build();
        WorkflowEntBuilder workflowBuilder = builder(WorkflowEntBuilder.class)//
            .setInfo(builder(WorkflowInfoEntBuilder.class)//
                .setName("wf-name")//
                .setContainerType(ContainerTypeEnum.PROJECT)//
                .setContainerId(new NodeIDEnt(0))//
                .build());
        WorkflowEnt workflow1 = workflowBuilder.setNodes(Collections.emptyMap()).setDirty(false).build();
        WorkflowEnt workflow2 =
            workflowBuilder.setNodes(Map.of("root:1", node1, "root:11", node2)).setDirty(true).build();

        PatchCreator<Object> patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);

        verify(patchCreator, Mockito.times(2)).added(any(), any());
        verify(patchCreator, Mockito.times(1)).added(eq("/nodes/root:1"), any());
        verify(patchCreator, Mockito.times(1)).added(eq("/nodes/root:11"), any());
    }

    /**
     * Tests that element removals from arrays are correctly translated into respective 'remove'-patch operations with
     * the correct index. Because there is a little peculiarity to consider: In order to, e.g., remove all elements from
     * an array, the patch must look like this (i.e. the patch-ops depend on each other and their order is important):
     *
     * <pre>
     * { "op": "remove", "path":"/0" },
     * { "op": "remove", "path":"/0" },
     * { "op": "remove", "path":"/0" }
     * </pre>
     *
     * (instead of {@code ... "path":"/0" ... "path":"/1" ... "path":"/2"})
     *
     * For more details, see, e.g., https://github.com/json-patch/json-patch-tests/issues/26
     */
    @Test
    public void testPatchRemovingMultipleArrayElements() {
        var workflowBuilder = builder(WorkflowEntBuilder.class)//
            .setInfo(builder(WorkflowInfoEntBuilder.class)//
                .setName("wf-name")//
                .setContainerType(ContainerTypeEnum.PROJECT)//
                .setContainerId(new NodeIDEnt(0))//
                .build())//
            .setDirty(false);

        var workflowAnnoBuilder = builder(WorkflowAnnotationEntBuilder.class)//
            .setTextAlign(TextAlignEnum.CENTER)//
            .setBounds(builder(BoundsEntBuilder.class).setX(0).setY(0).setWidth(0).setHeight(0).build())//
            .setId(new AnnotationIDEnt("root:1_1"))//
            .setBorderColor("test")//
            .setStyleRanges(Collections.emptyList())//
            .setBorderWidth(0);
        var anno1 = workflowAnnoBuilder.setText(EntityUtil.toTypedTextEnt("anno1", ContentTypeEnum.PLAIN)).build();
        var anno2 = workflowAnnoBuilder.setText(EntityUtil.toTypedTextEnt("anno2", ContentTypeEnum.PLAIN)).build();
        var anno3 = workflowAnnoBuilder.setText(EntityUtil.toTypedTextEnt("anno3", ContentTypeEnum.PLAIN)).build();

        WorkflowEnt workflow1 = workflowBuilder.setWorkflowAnnotations(List.of(anno1, anno2, anno3)).build();
        // remove all wf annotations
        WorkflowEnt workflow2 = workflowBuilder.setWorkflowAnnotations(Collections.emptyList()).build();
        var patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);
        verify(patchCreator, Mockito.times(3)).removed("/workflowAnnotations/0");

        // remove only the first two workflow annotations
        workflow2 = workflowBuilder.setWorkflowAnnotations(List.of(anno3)).build();
        patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);
        verify(patchCreator, Mockito.times(2)).removed("/workflowAnnotations/1");
        verify(patchCreator).replaced("/workflowAnnotations/0/text/value", "anno3");
    }

    /**
     * Test if `onListChange()` method can also handle {@link ElementValueChange} changes
     */
    @Test
    public void testPatchReplacingListElements() {
        NativeNodeEntBuilder nodeBuilder = builder(NativeNodeEntBuilder.class).setInPorts(Collections.emptyList())
                .setOutPorts(Collections.emptyList()).setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build())
                .setKind(KindEnum.NODE).setTemplateId("templateId");
        PortGroupEntBuilder portGroupBuilder = builder(PortGroupEntBuilder.class).setSupportedPortTypeIds(List.of("portTypeId"));
        NodeEnt node1 = nodeBuilder//
                .setId(new NodeIDEnt(1))//
                .setPortGroups(Map.of("groupName", portGroupBuilder.setInputRange(List.of(1, 2)).build()))//
                .build();
        NodeEnt node2 = nodeBuilder//
                .setId(new NodeIDEnt(1))//
                .setPortGroups(Map.of("groupName", portGroupBuilder.setInputRange(List.of(2, 4)).build()))//
                .build();
        WorkflowEntBuilder workflowBuilder = builder(WorkflowEntBuilder.class)//
            .setInfo(builder(WorkflowInfoEntBuilder.class)//
                .setName("wf-name")//
                .setContainerType(ContainerTypeEnum.PROJECT)//
                .setContainerId(new NodeIDEnt(0))//
                .build())
            .setDirty(true);
       WorkflowEnt workflow1 = workflowBuilder.setNodes(Map.of("root:1", node1)).build();
       WorkflowEnt workflow2 = workflowBuilder.setNodes(Map.of("root:1", node2)).build();

       PatchCreator<Object> patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);

       verify(patchCreator).replaced("/nodes/root:1/portGroups/groupName/inputRange/0", 2);
       verify(patchCreator).replaced("/nodes/root:1/portGroups/groupName/inputRange/1", 4);
    }

    /**
     * Tests that a patch operation is created that adds a completely new list if an element is added to a list which
     * was previously {@code null} - NXT-1490.
     */
    @Test
    public void testPatchNewList() {
        WorkflowEntBuilder workflowBuilder = builder(WorkflowEntBuilder.class)//
            .setInfo(builder(WorkflowInfoEntBuilder.class)//
                .setName("wf-name")//
                .setContainerType(ContainerTypeEnum.PROJECT)//
                .setContainerId(new NodeIDEnt(0))//
                .build())
            .setDirty(true) //
            .setNodes(Map.of());

        var workflow1 = workflowBuilder.setMetadata(builder(ComponentNodeDescriptionEntBuilder.class)
            .setName("blub").setMetadataType(MetadataTypeEnum.COMPONENT).build()).build();
        var option = builder(NodeDialogOptionGroupEntBuilder.class).setSectionName("test").build();
        var workflow2 = workflowBuilder
            .setMetadata(builder(ComponentNodeDescriptionEntBuilder.class).setName("blub").setOptions(List.of(option))
                .setMetadataType(MetadataTypeEnum.COMPONENT).build())
            .build();

        var patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);
        verify(patchCreator).added("/componentMetadata/options", List.of(option));
    }

    /**
     * Tests that settings a value to {@code null} results in a respective 'removed'-patch.
     */
    @Test
    public void testSetValueToNull() {
        var builder = builder(LinkEntBuilder.class).setUrl("url");
        var obj2 = builder.build();
        var obj1 = builder.setText("text").build();
        var patchCreator = createDiffAndPatchCreatorMock(obj1, obj2);
        verify(patchCreator).removed("/text");
    }

    private static PatchCreator<Object> createDiffAndPatchCreatorMock(final GatewayEntity ent1,
        final GatewayEntity ent2) {
        PatchCreator<Object> patchCreator = Mockito.mock(PatchCreator.class);
        EntityDiff.compare(ent1, ent2, patchCreator);
        return patchCreator;
    }

}

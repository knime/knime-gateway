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

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.junit.Test;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt.TextAlignEnum;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.KindEnum;
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
        WorkflowEntBuilder workflowBuilder =
            builder(WorkflowEntBuilder.class).setInfo(builder(WorkflowInfoEntBuilder.class).setName("wf-name")
                .setContainerType(ContainerTypeEnum.PROJECT).build());
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
        var workflowBuilder = builder(WorkflowEntBuilder.class).setInfo(builder(WorkflowInfoEntBuilder.class)
            .setName("wf-name").setContainerType(ContainerTypeEnum.PROJECT).build()).setDirty(false);

        var workflowAnnoBuilder = builder(WorkflowAnnotationEntBuilder.class).setTextAlign(TextAlignEnum.CENTER)
            .setBounds(builder(BoundsEntBuilder.class).build()).setId(new AnnotationIDEnt("root:1_1"))
            .setBorderColor("test").setStyleRanges(Collections.emptyList()).setBorderWidth(0);
        var anno1 = workflowAnnoBuilder.setText("anno1").build();
        var anno2 = workflowAnnoBuilder.setText("anno2").build();
        var anno3 = workflowAnnoBuilder.setText("anno3").build();

        WorkflowEnt workflow1 = workflowBuilder.setWorkflowAnnotations(List.of(anno1, anno2, anno3)).build();
        // remove all wf annotations
        WorkflowEnt workflow2 = workflowBuilder.setWorkflowAnnotations(Collections.emptyList()).build();
        var patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);
        verify(patchCreator, Mockito.times(3)).removed("/workflowAnnotations/0");

        // remove only the first two workflow annotations
        workflow2 = workflowBuilder.setWorkflowAnnotations(List.of(anno3)).build();
        patchCreator = createDiffAndPatchCreatorMock(workflow1, workflow2);
        verify(patchCreator, Mockito.times(2)).removed("/workflowAnnotations/1");
        verify(patchCreator).replaced("/workflowAnnotations/0/text", "anno3");
    }

    private static PatchCreator<Object> createDiffAndPatchCreatorMock(final WorkflowEnt workflow1, final WorkflowEnt workflow2) {
        Javers javers = JaversBuilder.javers().registerValue(NodeIDEnt.class).registerValue(ConnectionIDEnt.class)
            .registerValue(AnnotationIDEnt.class).withNewObjectsSnapshot(false).build();
        Diff diff = javers.compare(workflow1, workflow2);

        PatchCreator<Object> patchCreator = Mockito.mock(PatchCreator.class);
        PatchChangeProcessor<Object> changeProcessor = new PatchChangeProcessor<>(patchCreator, "foo");
        javers.processChangeList(diff.getChanges(), changeProcessor);
        return patchCreator;
    }

}

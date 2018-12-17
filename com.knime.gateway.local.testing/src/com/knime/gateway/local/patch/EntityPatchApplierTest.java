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
package com.knime.gateway.local.patch;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.json.util.ObjectMapperUtil;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt.NodeMessageEntBuilder;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.PatchEnt.PatchEntBuilder;
import com.knime.gateway.v0.entity.PatchOpEnt.OpEnum;
import com.knime.gateway.v0.entity.PatchOpEnt.PatchOpEntBuilder;
import com.knime.gateway.v0.entity.WorkflowEnt;

/**
 * Tests the {@link EntityPatchApplier}-implementation that is provided by {@link EntityPatchApplierManager}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityPatchApplierTest {

    /**
     * Tests general sanity of a {@link EntityPatchApplier}, i.e. whether its application leads to the desired results.
     * Includes some operations (add, remove, replace) on different levels.
     *
     * @throws Exception
     */
    @Test
    public void testApplyPatchToWorkflowEnt() throws Exception {
        WorkflowEnt workflow = readEnt("files/workflowent.json", WorkflowEnt.class);
        PatchEnt patch = readEnt("files/patchent.json", PatchEnt.class);
        WorkflowEnt patchedWorkflow = EntityPatchApplierManager.getPatchApplier().applyPatch(workflow, patch);
        WorkflowEnt modifiedWorkflow = readEnt("files/workflowent_modified.json", WorkflowEnt.class);
        assertThat("applying patch failed - result after patch differs", modifiedWorkflow, is(patchedWorkflow));
    }

    private static final <E extends GatewayEntity> E readEnt(final String path, final Class<E> entityClass)
        throws JsonParseException, JsonMappingException, IOException {
        Bundle myself = FrameworkUtil.getBundle(EntityPatchApplierTest.class);
        IPath p = new Path(path);
        URL url = FileLocator.find(myself, p, null);
        if (url == null) {
            throw new FileNotFoundException("Path " + path + " does not exist in bundle " + myself.getSymbolicName());
        }
        return ObjectMapperUtil.getInstance().getObjectMapper().readValue(url, entityClass);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Makes sure that an exception is thrown when the patch type id differs from the type id the patch is applied on.
     */
    @Test
    public void testFailWhenPatchTargetTypeIDDiffers() {
        PatchEnt patch = builder(PatchEntBuilder.class).setSnapshotID(UUID.randomUUID())
            .setOps(
                Arrays.asList(builder(PatchOpEntBuilder.class).setFrom("adf").setPath("xyz").setOp(OpEnum.ADD).build()))
            .setTargetTypeID("patchTargetTypeID").build();
        NodeMessageEnt ent = builder(NodeMessageEntBuilder.class).setMessage("xyz").setType("type").build();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The patch's target type id doesn't match the entity type id");
        EntityPatchApplierManager.getPatchApplier().applyPatch(ent, patch);
    }

    /**
     * Makes sure that an exception is thrown when the patch type id differs from the type id the empty(!) patch is
     * applied on.
     */
    @Test
    public void testFailWhenPatchTargetTypeIDDiffersAndPatchIsEmpty() {
        PatchEnt patch = builder(PatchEntBuilder.class).setSnapshotID(UUID.randomUUID())
            .setTargetTypeID("patchTargetTypeID").build();
        NodeMessageEnt ent = builder(NodeMessageEntBuilder.class).setMessage("xyz").setType("type").build();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The patch's target type id doesn't match the entity type id");
        EntityPatchApplierManager.getPatchApplier().applyPatch(ent, patch);
    }

}

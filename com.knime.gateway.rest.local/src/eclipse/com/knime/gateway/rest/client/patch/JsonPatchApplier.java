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
package com.knime.gateway.rest.client.patch;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.json.util.ObjectMapperUtil;
import com.knime.gateway.local.patch.EntityPatchApplier;

/**
 * Entity patch applier implementation that uses {@link JsonPatch} to apply the {@link PatchEnt}. It assumes that
 * {@link PatchEnt} conforms with the <a href="http://jsonpatch.com/">json patch standard</a>.
 *
 * Note: so far there is some overhead converting the gateway entities (the patch itself as well as the entity to be
 * patched) into intermediate json-representations and back again to entities again.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JsonPatchApplier implements EntityPatchApplier {

    private final ObjectMapper m_mapper = ObjectMapperUtil.getInstance().getObjectMapper();

    @Override
    public <T extends GatewayEntity> T applyPatch(final T entity, final PatchEnt patch) {
        if (!entity.getTypeID().equals(patch.getTargetTypeID())) {
            throw new IllegalArgumentException("The patch's target type id doesn't match the entity type id.");
        }

        //do nothing if patch doesn't contain any changes
        if (patch.getOps().size() == 0) {
            return entity;
        }

        //TODO how to get rid of all the overhead of transforming the java objects into json nodes?

        //turn entity into a json node
        JsonNode jsonObject = m_mapper.valueToTree(entity);

        try {
            //turn patch into a json patch
            JsonPatch jsonPatch = JsonPatch.fromJson(m_mapper.valueToTree(patch.getOps()));

            //apply patch
            jsonObject = jsonPatch.apply(jsonObject);

            //turn result into an entity again
            return (T)m_mapper.convertValue(jsonObject, entity.getClass());
        } catch (IllegalArgumentException | IOException | JsonPatchException ex) {
            throw new RuntimeException(ex);
        }
    }
}
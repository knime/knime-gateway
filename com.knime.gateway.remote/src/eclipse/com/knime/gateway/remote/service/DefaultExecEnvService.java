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
package com.knime.gateway.remote.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.knime.core.node.execenv.ExecEnvManager;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.entity.EntityBuilderManager;
import com.knime.gateway.v0.entity.ExecEnvEnt;
import com.knime.gateway.v0.entity.ExecEnvEnt.ExecEnvEntBuilder;
import com.knime.gateway.v0.service.ExecEnvService;

/**
 * Default implementation of {@link ExecEnvService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultExecEnvService implements ExecEnvService {
    private static final DefaultExecEnvService INSTANCE = new DefaultExecEnvService();

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultExecEnvService getInstance() {
       return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ExecEnvEnt> getAllExecEnvs(final UUID jobId) {
        return ExecEnvManager.getInstance().getAvailableExecEnvFactories().stream()
            .flatMap(f -> ExecEnvManager.getInstance().getRegisteredExecEnvsOfType(f.getExecEnvID()).stream())
            .map(ee -> {
                ExecEnvEnt execEnv = EntityBuilderManager.builder(ExecEnvEntBuilder.class)
                    .setAllowedNodeTypes(ee.getFactory().getSupportedExecEnvNodeTypes())
                    .setExecEnvID(ee.getFactory().getExecEnvID())
                    .setInstanceID(String.valueOf(System.identityHashCode(ee)))
                    .setTypeName(ee.getFactory().getExecEnvName()).build();
                return execEnv;
            }).collect(Collectors.toList());
    }

}

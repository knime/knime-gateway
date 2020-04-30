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

package org.knime.next.api.util;

import org.knime.next.api.NextWizardExecutionServiceWrapper;
import com.knime.gateway.service.WizardExecutionService;
import org.knime.next.api.NextNodeServiceWrapper;
import com.knime.gateway.service.NodeService;
import org.knime.next.api.NextWorkflowServiceWrapper;
import com.knime.gateway.service.WorkflowService;
import org.knime.next.api.NextAnnotationServiceWrapper;
import com.knime.gateway.service.AnnotationService;

import com.knime.gateway.service.GatewayService;

import org.knime.next.api.AbstractServiceWrapper;

import java.lang.reflect.InvocationTargetException;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.next.gateway-config.json"})
public class WrapWithNextService {

    private WrapWithNextService() {
        //utility class
    }
    
    /**
     * TODO
     *
     * @param service the service to be wrapped
     * @param serviceInterface the service interface to select the right wrapper
     *
     * @return the service wrapper
     */
    public static AbstractServiceWrapper wrap(final GatewayService service, final Class<?> serviceInterface) {
        try {
        
            if(serviceInterface == WizardExecutionService.class) {
                return NextWizardExecutionServiceWrapper.class.getConstructor(serviceInterface).newInstance(service);
            }
            if(serviceInterface == NodeService.class) {
                return NextNodeServiceWrapper.class.getConstructor(serviceInterface).newInstance(service);
            }
            if(serviceInterface == WorkflowService.class) {
                return NextWorkflowServiceWrapper.class.getConstructor(serviceInterface).newInstance(service);
            }
            if(serviceInterface == AnnotationService.class) {
                return NextAnnotationServiceWrapper.class.getConstructor(serviceInterface).newInstance(service);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalArgumentException("No wrapper available!");
    }
}

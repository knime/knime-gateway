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
package org.knime.gateway.json.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import org.knime.gateway.api.entity.WizardPageInputEnt;
import org.knime.gateway.impl.entity.DefaultWizardPageInputEnt;
import org.knime.gateway.impl.entity.DefaultWizardPageInputEnt.DefaultWizardPageInputEntBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "",
    visible = true,
    defaultImpl = DefaultWizardPageInputEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWizardPageInputEnt.class, name="WizardPageInput")
})
@JsonDeserialize(builder=DefaultWizardPageInputEntBuilder.class)
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
public interface WizardPageInputEntMixIn extends WizardPageInputEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("viewValues")
    public java.util.Map<String, String> getViewValues();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWizardPageInputEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWizardPageInputEnt.DefaultWizardPageInputEntBuilder.class, name="WizardPageInput")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WizardPageInputEntMixInBuilder extends WizardPageInputEntBuilder {
    
        @Override
        public WizardPageInputEntMixIn build();
    
        @Override
        @JsonProperty("viewValues")
        public WizardPageInputEntMixInBuilder setViewValues(final java.util.Map<String, String> viewValues);
        
    }


}

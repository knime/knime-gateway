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
package com.knime.gateway.json.entity;

import com.knime.gateway.entity.NodeMessageEnt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.knime.gateway.entity.WizardPageEnt;
import com.knime.gateway.entity.impl.DefaultWizardPageEnt;
import com.knime.gateway.entity.impl.DefaultWizardPageEnt.DefaultWizardPageEntBuilder;

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
    defaultImpl = DefaultWizardPageEnt.class)
@JsonSubTypes({
    @Type(value = DefaultWizardPageEnt.class, name="WizardPage")
})
@JsonDeserialize(builder=DefaultWizardPageEntBuilder.class)
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.json-config.json"})
public interface WizardPageEntMixIn extends WizardPageEnt {

    @Override
    @JsonIgnore
    public String getTypeID();

    @Override
    @JsonProperty("wizardPageContent")
    public Object getWizardPageContent();
    
    @Override
    @JsonProperty("wizardExecutionState")
    public WizardExecutionStateEnum getWizardExecutionState();
    
    @Override
    @JsonProperty("nodeMessages")
    public java.util.Map<String, NodeMessageEnt> getNodeMessages();
    
    @Override
    @JsonProperty("hasPreviousPage")
    public Boolean hasPreviousPage();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = DefaultWizardPageEntBuilder.class)
    @JsonSubTypes({
        @Type(value = DefaultWizardPageEnt.DefaultWizardPageEntBuilder.class, name="WizardPage")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface WizardPageEntMixInBuilder extends WizardPageEntBuilder {
    
        @Override
        public WizardPageEntMixIn build();
    
        @Override
        @JsonProperty("wizardPageContent")
        public WizardPageEntMixInBuilder setWizardPageContent(final Object wizardPageContent);
        
        @Override
        @JsonProperty("wizardExecutionState")
        public WizardPageEntMixInBuilder setWizardExecutionState(final WizardExecutionStateEnum wizardExecutionState);
        
        @Override
        @JsonProperty("nodeMessages")
        public WizardPageEntMixInBuilder setNodeMessages(final java.util.Map<String, NodeMessageEnt> nodeMessages);
        
        @Override
        @JsonProperty("hasPreviousPage")
        public WizardPageEntMixInBuilder setHasPreviousPage(final Boolean hasPreviousPage);
        
    }


}


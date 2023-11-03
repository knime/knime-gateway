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
 *   Dec 12, 2022 (hornm): created
 */
package org.knime.gateway.api.webui.util;

import static java.util.stream.Collectors.toList;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.knime.core.node.NoDescriptionProxy;
import org.knime.core.node.Node;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.context.ports.ConfigurablePortGroup;
import org.knime.core.node.context.ports.ModifiablePortsConfiguration;
import org.knime.core.node.context.ports.PortGroupConfiguration;
import org.knime.core.node.extension.NodeMetadata;
import org.knime.core.node.port.PortType;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;
import org.knime.gateway.api.webui.entity.FeatureMetadataEnt;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt.NativeNodeDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt.NodePortDescriptionEntBuilder;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt.NodePortTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.shared.workflow.def.VendorDef;

/**
 * See {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("static-method")
public final class NodeTemplateAndDescriptionEntityFactory {

    NodeTemplateAndDescriptionEntityFactory() {
        //
    }

    /**
     * Builds {@link NodeTemplateEnt}-instance that only has a minimal set of properties set. I.e. omitting some
     * properties such as icon and port infos.
     *
     * @param factory the node factory to create the template entity from
     * @return the new {@link NodeTemplateEnt}-instance
     */
    public NodeTemplateEnt buildMinimalNodeTemplateEnt(final NodeFactory<? extends NodeModel> factory) {
        NodeTemplateEntBuilder builder = builder(NodeTemplateEntBuilder.class)//
            .setId(factory.getFactoryId())//
            .setName(factory.getNodeName())//
            .setComponent(false)//
            .setType(TypeEnum.valueOf(factory.getType().toString().toUpperCase(Locale.ROOT)));
        return builder.build();
    }

    /**
     * Construct an entity representing the node description. The node description is potentially dynamically generated.
     * Information about ports is based on an instance of {@link org.knime.core.node.Node}.
     * @param coreNode The node instance to obtain information from.
     * @return an entity representing the node description.
     * @throws ServiceExceptions.NodeDescriptionNotAvailableException if node description could not be obtained.
     */
    public NativeNodeDescriptionEnt buildNativeNodeDescriptionEnt(final Node coreNode)
        throws ServiceExceptions.NodeDescriptionNotAvailableException {

        var nodeDescription = coreNode.invokeGetNodeDescription();
        if (nodeDescription instanceof NoDescriptionProxy) {
            // This will be the case when node description could not be read, cf. NodeDescription#init
            throw new ServiceExceptions.NodeDescriptionNotAvailableException("Could not read node description");
        }

        // intro and short description
        NativeNodeDescriptionEntBuilder builder = builder(NativeNodeDescriptionEntBuilder.class) //
            .setDescription(nodeDescription.getIntro().orElse(null)) //
            .setShortDescription(nodeDescription.getShortDescription().orElse(null));

        // dialog options
        builder.setOptions(buildDialogOptionGroupEnts(nodeDescription.getDialogOptionGroups()));

        // static/simple ports
        // Node#getInputType, #getOutputType, index 0 is the flow variable port, hence +1
        // Node#getNrInPorts adds 1 for flow variable port, hence -1
        // NodeDescription#getInportDescription is 0-indexed and does not contain description for flow variable port, hence +1
        builder.setInPorts(buildNativeNodePortDescriptionEnts(coreNode.getNrInPorts() - 1,
            nodeDescription::getInportName, nodeDescription::getInportDescription, i -> coreNode.getInputType(i + 1)));
        builder.setOutPorts( //
            buildNativeNodePortDescriptionEnts(coreNode.getNrOutPorts() - 1, nodeDescription::getOutportName,
                nodeDescription::getOutportDescription, i -> coreNode.getOutputType(i + 1)) //
        );

        // dynamic port group descriptions (not the dynamically generated individual port descriptions)
        final Optional<ModifiablePortsConfiguration> portConfigs =
            coreNode.getCopyOfCreationConfig().flatMap(ModifiableNodeCreationConfiguration::getPortConfig);
        builder.setDynamicInPortGroupDescriptions( //
            buildDynamicPortGroupDescriptions(nodeDescription.getDynamicInPortGroups(), portConfigs) //
        );
        builder.setDynamicOutPortGroupDescriptions( //
            buildDynamicPortGroupDescriptions(nodeDescription.getDynamicOutPortGroups(), portConfigs) //
        );

        // view descriptions
        builder.setViews(buildNodeViewDescriptionEnts(coreNode.getNrViews(), nodeDescription));

        // interactive view description
        if (nodeDescription.getInteractiveViewName() != null) {
            builder.setInteractiveView( //
                builder(NodeViewDescriptionEntBuilder.class) //
                    .setName(nodeDescription.getInteractiveViewName()) //
                    .setDescription(nodeDescription.getInteractiveViewDescription().orElse(null)) //
                    .build() //
            );
        }

        // links
        builder.setLinks(buildNodeDescriptionLinkEnts(nodeDescription.getLinks()));

        return builder.build();
    }

    /**
     * Builds a {@link NodeTemplateEnt}-instance.
     *
     * @param nodeMetadata the node factory and information about the node it creates
     * @return the new {@link NodeTemplateEnt}-instance, or {@code null} if it couldn't be created (see
     *         {@link CoreUtil#createNode(NodeFactory)})
     */
    public NodeTemplateEnt buildNodeTemplateEnt(final NodeMetadata nodeMetadata) {
        return builder(NodeTemplateEntBuilder.class)//
            .setId(nodeMetadata.factory().getFactoryId())//
            .setName(nodeMetadata.nodeProperties().metadata().nodeName())//
            .setComponent(false)//
            .setType(TypeEnum.valueOf(nodeMetadata.factory().getType().toString().toUpperCase(Locale.ROOT)))//
            .setInPorts(buildNodePortTemplateEnts(nodeMetadata.nodeProperties().ports().getInputPortTypes()))//
            .setOutPorts(buildNodePortTemplateEnts(nodeMetadata.nodeProperties().ports().getOutputPortTypes()))//
            .setIcon(WorkflowEntityFactory.createIconDataURL(nodeMetadata.factory()))//
            .setNodeFactory(EntityFactory.Workflow.buildNodeFactoryKeyEnt(nodeMetadata.factory())) //
            .setFeatureMetadata(buildFeatureMetadataEnt(nodeMetadata.nodeProperties().metadata().vendor().feature())) //
            .build();
    }


    private FeatureMetadataEnt buildFeatureMetadataEnt(final VendorDef featureVendor) {
        return builder(FeatureMetadataEnt.FeatureMetadataEntBuilder.class) //
                .setName(featureVendor.getName()) //
                .setVendor(featureVendor.getVendor()) //
                .build();
    }

    private List<NodeDialogOptionDescriptionEnt> buildDialogOptionDescriptionEnts(final List<NodeDescription.DialogOption> opts) {
        return listMapOrNull(opts, o -> //
            builder(NodeDialogOptionDescriptionEnt.NodeDialogOptionDescriptionEntBuilder.class) //
                .setName(o.getName()) //
                .setDescription(o.getDescription()) //
                .setOptional(o.isOptional()) //
                .build()
        );
    }

    private List<NodeDialogOptionGroupEnt> buildDialogOptionGroupEnts(final List<NodeDescription.DialogOptionGroup> groups) {
        return listMapOrNull(groups, g -> //
            builder(NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder.class) //
                .setSectionName(g.getName().orElse(null)) //
                .setSectionDescription(g.getDescription().orElse(null)) //
                .setFields( //
                    buildDialogOptionDescriptionEnts(g.getOptions()) //
                ).build()
        );
    }

    private List<DynamicPortGroupDescriptionEnt> buildDynamicPortGroupDescriptions(
        final List<NodeDescription.DynamicPortGroupDescription> portGroupDescriptions,
        final Optional<ModifiablePortsConfiguration> portConfigs) { // NOSONAR
        return listMapOrNull(portGroupDescriptions, pgd -> { // NOSONAR
            List<NodePortTemplateEnt> supportedPortTypes = portConfigs.map(pc -> {
                PortGroupConfiguration group = pc.getGroup(pgd.getGroupIdentifier());
                if (group instanceof ConfigurablePortGroup) {
                    ConfigurablePortGroup configurableGroupConfig = (ConfigurablePortGroup)group;
                    return buildNodePortTemplateEnts(Arrays.stream(configurableGroupConfig.getSupportedPortTypes()));
                } else {
                    return null; // map yields empty optional
                }
            }).orElse(null);

            return builder(DynamicPortGroupDescriptionEnt.DynamicPortGroupDescriptionEntBuilder.class) //
                .setName(pgd.getGroupName()) //
                .setDescription(pgd.getGroupDescription()) //
                .setIdentifier(pgd.getGroupIdentifier()) //
                .setSupportedPortTypes(supportedPortTypes) //
                .build();
        });
    }

    private List<NodePortDescriptionEnt> buildNativeNodePortDescriptionEnts(final int nrPorts,
        final IntFunction<String> nameGetter, final IntFunction<String> descGetter,
        final IntFunction<PortType> typeGetter) {
        return listMapOrNull(IntStream.range(0, nrPorts).boxed().collect(toList()), //
            index -> { // NOSONAR
                var portType = typeGetter.apply(index);
                return builder(NodePortDescriptionEntBuilder.class) //
                    .setName(nameGetter.apply(index)) //
                    .setDescription(descGetter.apply(index)) //
                    .setTypeId(CoreUtil.getPortTypeId(portType)) //
                    .setOptional(portType.isOptional()) //
                    .build();
            });
    }

    private List<LinkEnt> buildNodeDescriptionLinkEnts(final List<NodeDescription.DescriptionLink> links) {
        return listMapOrNull( //
            links, //
            el -> builder(LinkEntBuilder.class).setText(el.getText()).setUrl(el.getTarget()).build() //
        );
    }

    private List<NodePortTemplateEnt> buildNodePortTemplateEnts(final Stream<PortType> ptypes) {
        return ptypes.map(ptype -> builder((NodePortTemplateEntBuilder.class))//
                .setName(null)//
                .setTypeId(CoreUtil.getPortTypeId(ptype))//
                .setOptional(ptype.isOptional())//
                .build())//
            .collect(Collectors.toList());
    }

    private List<NodeViewDescriptionEnt> buildNodeViewDescriptionEnts(final int nrViews,
        final NodeDescription nodeDescription) {
        if (nrViews < 1) {
            return null;  // NOSONAR: returning null is useful here
        }
        return IntStream.range(0, nrViews).mapToObj(index -> //
        builder(NodeViewDescriptionEntBuilder.class) //
            .setName(nodeDescription.getViewName(index)) //
            .setDescription(nodeDescription.getViewDescription(index)) //
            .build()) //
            .collect(toList());
    }

    /**
     * Map operation over a list with special handling of null values: If the input list is null or empty, the method
     * returns null.
     *
     * @param input List of elements to be transformed
     * @param transformation Transformation to apply to each element
     * @param <I> Type of input elements
     * @param <O> Type of output elemens
     * @return Transformed list
     */
    private <I, O> List<O> listMapOrNull(final List<I> input, final Function<I, O> transformation) {
        if (input == null || input.isEmpty()) {
            return null;  // NOSONAR: returning null is useful here
        }
        return input.stream().map(transformation).collect(toList());
    }

}

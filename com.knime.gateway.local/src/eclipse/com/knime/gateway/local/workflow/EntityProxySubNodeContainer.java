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
package com.knime.gateway.local.workflow;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.xmlbeans.XmlException;
import org.knime.core.node.Node;
import org.knime.core.node.NodeDescription27Proxy;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.dialog.MetaNodeDialogNode;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.NodeExecutionJobManagerPool;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.MetaNodeDialogPane;
import org.knime.core.node.workflow.NodeContainer.NodeContainerSettings.SplitType;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.ui.node.workflow.SubNodeContainerUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.knime.gateway.v0.entity.MetaNodeDialogEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * Entity-proxy class that proxies {@link WrappedWorkflowNodeEnt} and implements {@link SubNodeContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxySubNodeContainer extends EntityProxySingleNodeContainer<WrappedWorkflowNodeEnt>
    implements SubNodeContainerUI {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AbstractEntityProxyNodeContainer.class);

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param node
     * @param access
     */
    EntityProxySubNodeContainer(final WrappedWorkflowNodeEnt node, final EntityProxyAccess access) {
        super(node, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeID getVirtualInNodeID() {
        return NodeID.fromString(getEntity().getVirtualInNodeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeID getVirtualOutNodeID() {
        return NodeID.fromString(getEntity().getVirtualOutNodeID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowManagerUI getWorkflowManager() {
        return getAccess().getWrappedWorkflowManager(getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getIcon() {
        return SubNodeContainer.class.getResource("virtual/subnode/empty.png");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDialogPane getDialogPaneWithSettings(final Future<NodeSettings> nodeSettings,
        final Future<PortObjectSpec[]> portObjectSpecs, final Future<FlowObjectStack> flowObjectStack,
        final NodeDialogPane dialogPane, final ExecutorService exec)
        throws NotConfigurableException, InterruptedException, ExecutionException {

        //get metanode dialog representation
        Future<MetaNodeDialogEnt> dialogEnt = exec.submit(() -> getAccess().nodeService()
            .getWMetaNodeDialog(getEntity().getRootWorkflowID(), getEntity().getNodeID()));

        shutdownExecutorsAndWait(exec);

        Map<NodeID, MetaNodeDialogNode> nodes = dialogEnt.get().getComponents().stream()
            .collect(Collectors.toMap(c -> getAccess().getNodeID(getEntity().getRootWorkflowID(), c.getNodeID()), c -> {
                return new EntityProxyDialogNode(c, getAccess());
            }));

        NodeDialogPane resDialogPane;
        if (dialogPane == null) {
            resDialogPane = createDialogPane();
        } else {
            resDialogPane = dialogPane;
        }

        ((MetaNodeDialogPane)resDialogPane).setQuickformNodes(nodes);

        // remove the flow variable port from the specs and data
        PortObjectSpec[] correctedInSpecs = ArrayUtils.remove(portObjectSpecs.get(), 0);
        // the next call will call dialogPane.internalLoadSettingsFrom()
        // dialogPane is a MetaNodeDialogPane and does not handle the flow variable port correctly
        // this is why we remove it first
        Node.invokeDialogInternalLoad(resDialogPane, nodeSettings.get(), correctedInSpecs, null, flowObjectStack.get(),
            CredentialsProvider.EMPTY_CREDENTIALS_PROVIDER, getParent().isWriteProtected());
        return resDialogPane;
    }

    private MetaNodeDialogPane createDialogPane() {
        if (hasDialog()) {
            // create sub node dialog with dialog nodes
            MetaNodeDialogPane nodeDialogPane = new MetaNodeDialogPane(true);
            // job managers tab
            if (NodeExecutionJobManagerPool.getNumberOfJobManagersFactories() > 1) {
                // TODO: set the SplitType depending on the nodemodel
                SplitType splitType = SplitType.USER;
                nodeDialogPane.addJobMgrTab(splitType);
            }
            Node.addMiscTab(nodeDialogPane);
            return nodeDialogPane;
        } else {
            throw new IllegalStateException("Workflow has no dialog");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLayoutJSONString() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLayoutJSONString(final String layoutJSONString) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getXMLDescription() {
        String noDescriptionAvailableText =
            "Node description for wrapped metanodes within remotely opened workflows not available, yet.";

        try {
            // Document
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation()
                .createDocument("http://knime.org/node2012", "knimeNode", null);
            // knimeNode
            Element knimeNode = doc.getDocumentElement();
            knimeNode.setAttribute("type", "Unknown");
            knimeNode.setAttribute("icon", "subnode.png");
            // name
            Element name = doc.createElement("name");
            knimeNode.appendChild(name);
            name.appendChild(doc.createTextNode(getName()));
            // shortDescription
            Element shortDescription = doc.createElement("shortDescription");
            knimeNode.appendChild(shortDescription);
            shortDescription.appendChild(doc.createTextNode(noDescriptionAvailableText));
            // fullDescription
            Element fullDescription = doc.createElement("fullDescription");
            knimeNode.appendChild(fullDescription);
            // intro
            Element intro = doc.createElement("intro");
            fullDescription.appendChild(intro);
            intro.appendChild(doc.createTextNode(noDescriptionAvailableText));

            // ports
            Element ports = doc.createElement("ports");
            knimeNode.appendChild(ports);
            //            // inPort
            //            for (int i = 0; i < inPortNames.length; i++) {
            //                Element inPort = doc.createElement("inPort");
            //                ports.appendChild(inPort);
            //                inPort.setAttribute("index", "" + i);
            //                inPort.setAttribute("name", inPortNames[i]);
            //                String defaultText = NO_DESCRIPTION_SET;
            //                if (i == 0) {
            //                    defaultText += "\nChange this label by browsing the input node contained in the Wrapped Metanode "
            //                            + "and changing its configuration.";
            //                }
            //                addText(inPort, inPortDescriptions[i], defaultText);
            //            }
            //            // outPort
            //            for (int i = 0; i < outPortNames.length; i++) {
            //                Element outPort = doc.createElement("outPort");
            //                ports.appendChild(outPort);
            //                outPort.setAttribute("index", "" + i);
            //                outPort.setAttribute("name", outPortNames[i]);
            //                String defaultText = NO_DESCRIPTION_SET;
            //                if (i == 0) {
            //                    defaultText += "\nChange this label by browsing the output node contained in the Wrapped Metanode "
            //                            + "and changing its configuration.";
            //                }
            //                addText(outPort, outPortDescriptions[i], defaultText);
            //            }

            return new NodeDescription27Proxy(doc).getXMLDescription();
        } catch (XmlException | DOMException | ParserConfigurationException ex) {
            LOGGER.warn("Could not generate Wrapped Metanode description (for a remotely opened workflow)", ex);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteProtected() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInactive() {
        return getEntity().isInactive();
    }
}

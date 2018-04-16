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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlException;
import org.knime.core.node.NodeDescription27Proxy;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.ui.node.workflow.SubNodeContainerUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * Entity-proxy class that proxies {@link WrappedWorkflowNodeEnt} and implements {@link SubNodeContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxySubNodeContainer extends EntityProxySingleNodeContainer<WrappedWorkflowNodeEnt>
    implements SubNodeContainerUI {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityProxyNodeContainer.class);

    /**
     * @param node
     * @param access
     */
    public EntityProxySubNodeContainer(final WrappedWorkflowNodeEnt node, final EntityProxyAccess access) {
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
}

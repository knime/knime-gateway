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
package com.knime.gateway.local.util.missing;

import org.knime.core.node.DynamicNodeFactory;
import org.knime.core.node.NodeDescription;
import org.knime.core.node.NodeDescription31Proxy;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeView;
import org.knime.node.v31.FullDescription;
import org.knime.node.v31.Intro;
import org.knime.node.v31.KnimeNode;
import org.knime.node.v31.KnimeNodeDocument;
import org.knime.node.v31.PDocument.P;

/**
 * Primitive placeholder node factory for nodes whose extensions are not available.
 * Provides an icon (cross) and a respective node description for those nodes.
 *
 * @author Martin Horn, University of Konstanz
 */
public class MissingNodeFactory extends DynamicNodeFactory<NodeModel> {

    private String m_name;
    private String m_cause;

    /**
     * Creates a new missing node factory.
     *
     * @param name the name of the node missing.
     * @param cause a possible cause why the node is missing that will be added to the node description. Can be
     *            <code>null</code>.
     */
    public MissingNodeFactory(final String name, final String cause) {
        m_name = name;
        m_cause = cause;
    }


    /** {@inheritDoc} */
    @Override
    public NodeModel createNodeModel() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean hasDialog() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDescription createNodeDescription() {
        KnimeNodeDocument doc = KnimeNodeDocument.Factory.newInstance();

        KnimeNode node = doc.addNewKnimeNode();
        node.setIcon("./missing.png");
        node.setType(org.knime.node.v31.NodeType.UNKNOWN);
        node.setName("MISSING " + m_name);

        String shortDescription = "No node description available.";
        node.setShortDescription(shortDescription);

        FullDescription fullDesc = node.addNewFullDescription();
        Intro intro = fullDesc.addNewIntro();
        P p = intro.addNewP();
        p.newCursor().setTextValue(shortDescription);
        if(m_cause != null) {
            p = intro.addNewP();
            p.newCursor().setTextValue("Cause: " + m_cause);
        }
        return new NodeDescription31Proxy(doc);
    }

    /** @return type of missing node. */
    @Override
    public NodeFactory.NodeType getType() {
        return NodeType.Missing;
    }

}

/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 * Created on Oct 26, 2012 by wiswedel
 */
package org.knime.gateway.local.util.missing;

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

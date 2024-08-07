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
 */
package org.knime.gateway.impl.webui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"javadoc","java:S5960"})
class TreeTest {

    @Test
    void testGetOrInsertCreatesKey() throws Tree.NodeCreationException {
        var tree = setUpTree();
        var queryPath = List.of("1", "1");
        tree.getOrInsert(queryPath); // does not already exist
        var created = tree.root().children().get("1").children().get("1");
        Assertions.assertNotNull(created);
        Assertions.assertIterableEquals(queryPath, created.path());
    }

    private Tree<String, TreeNode> setUpTree() {
        // set up tree structure (without using methods of the class under test)
        Tree<String, TreeNode> tree = new Tree<>(createNode(List.of()), TreeTest::createNode);
        var root = tree.root();
        var node0 = createNode(List.of("0"));
        root.children().put("0", node0);
        var node1 = createNode(List.of("1"));
        root.children().put("1", node1);

        var node00 = createNode(List.of("0", "0"));
        var node01 = createNode(List.of("0", "1"));
        node0.children().put("0", node00);
        node0.children().put("1", node01);

        var node10 = createNode(List.of("1", "0"));
        node1.children().put("0", node10);
        return tree;
    }

    private static TreeNode createNode(final List<String> path) {
        return new TreeNode(new ArrayList<>(path), new HashMap<>());
    }

    @Test
    void testGetFindsValue() {
        var tree = setUpTree();
        var queryPath = List.of("0", "1");
        var foundValue = tree.get(queryPath);
        Assertions.assertTrue(foundValue.isPresent());
        Assertions.assertIterableEquals(queryPath, foundValue.get().path());
    }

    @Test
    void testGetReturnsEmptyForNonExistingValue() {
        var tree = setUpTree();
        var queryPath = List.of("1", "1"); // does not exist in tree
        var foundValue = tree.get(queryPath);
        Assertions.assertFalse(foundValue.isPresent());
    }

    record TreeNode(
            // save what we think is node's full path into node at creation time to verify in testing that it is indeed
            // at this path in the tree
            ArrayList<String> path, //
            HashMap<String, TreeNode> children //
    ) implements Tree.TreeNode<String, TreeNode> {

    }
}

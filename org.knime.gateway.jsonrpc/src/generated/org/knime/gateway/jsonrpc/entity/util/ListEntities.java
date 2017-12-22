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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.jsonrpc.entity.util;

import org.knime.gateway.jsonrpc.entity.AnnotationEntMixIn;
import org.knime.gateway.jsonrpc.entity.BoundsEntMixIn;
import org.knime.gateway.jsonrpc.entity.ConnectionEntMixIn;
import org.knime.gateway.jsonrpc.entity.JobManagerEntMixIn;
import org.knime.gateway.jsonrpc.entity.MetaPortInfoEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeFactoryKeyEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeMessageEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodePortEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeUIInfoEntMixIn;
import org.knime.gateway.jsonrpc.entity.PortTypeEntMixIn;
import org.knime.gateway.jsonrpc.entity.StyleRangeEntMixIn;
import org.knime.gateway.jsonrpc.entity.WorkflowEntMixIn;
import org.knime.gateway.jsonrpc.entity.WorkflowUIInfoEntMixIn;
import org.knime.gateway.jsonrpc.entity.XYEntMixIn;
import org.knime.gateway.jsonrpc.entity.NativeNodeEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeAnnotationEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeInPortEntMixIn;
import org.knime.gateway.jsonrpc.entity.NodeOutPortEntMixIn;
import org.knime.gateway.jsonrpc.entity.WorkflowAnnotationEntMixIn;
import org.knime.gateway.jsonrpc.entity.WorkflowNodeEntMixIn;
import org.knime.gateway.jsonrpc.entity.WrappedWorkflowNodeEntMixIn;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>org.knime.gateway.jsonrpc.entity</code>.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class ListEntities {

    private ListEntities() {
        //utility class
    }

    /**
     * Lists all gateway entity classes of package <code>org.knime.gateway.jsonrpc.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEntMixIn.class);
        res.add(BoundsEntMixIn.class);
        res.add(ConnectionEntMixIn.class);
        res.add(JobManagerEntMixIn.class);
        res.add(MetaPortInfoEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeFactoryKeyEntMixIn.class);
        res.add(NodeMessageEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodeUIInfoEntMixIn.class);
        res.add(PortTypeEntMixIn.class);
        res.add(StyleRangeEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowUIInfoEntMixIn.class);
        res.add(XYEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeInPortEntMixIn.class);
        res.add(NodeOutPortEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
        res.add(WorkflowNodeEntMixIn.class);
        res.add(WrappedWorkflowNodeEntMixIn.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>org.knime.gateway.jsonrpc.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEntMixIn.AnnotationEntMixInBuilder.class);
        res.add(BoundsEntMixIn.BoundsEntMixInBuilder.class);
        res.add(ConnectionEntMixIn.ConnectionEntMixInBuilder.class);
        res.add(JobManagerEntMixIn.JobManagerEntMixInBuilder.class);
        res.add(MetaPortInfoEntMixIn.MetaPortInfoEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeFactoryKeyEntMixIn.NodeFactoryKeyEntMixInBuilder.class);
        res.add(NodeMessageEntMixIn.NodeMessageEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodeUIInfoEntMixIn.NodeUIInfoEntMixInBuilder.class);
        res.add(PortTypeEntMixIn.PortTypeEntMixInBuilder.class);
        res.add(StyleRangeEntMixIn.StyleRangeEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowUIInfoEntMixIn.WorkflowUIInfoEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeInPortEntMixIn.NodeInPortEntMixInBuilder.class);
        res.add(NodeOutPortEntMixIn.NodeOutPortEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowNodeEntMixIn.WorkflowNodeEntMixInBuilder.class);
        res.add(WrappedWorkflowNodeEntMixIn.WrappedWorkflowNodeEntMixInBuilder.class);
        return res;
    }
}

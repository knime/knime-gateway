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
package org.knime.gateway.v0.entity.util;

import org.knime.gateway.v0.entity.AnnotationEnt;
import org.knime.gateway.v0.entity.BoundsEnt;
import org.knime.gateway.v0.entity.ConnectionEnt;
import org.knime.gateway.v0.entity.JobManagerEnt;
import org.knime.gateway.v0.entity.MetaPortInfoEnt;
import org.knime.gateway.v0.entity.NodeEnt;
import org.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodePortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;
import org.knime.gateway.v0.entity.PortTypeEnt;
import org.knime.gateway.v0.entity.StyleRangeEnt;
import org.knime.gateway.v0.entity.WorkflowEnt;
import org.knime.gateway.v0.entity.WorkflowUIInfoEnt;
import org.knime.gateway.v0.entity.XYEnt;
import org.knime.gateway.v0.entity.NativeNodeEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import org.knime.gateway.v0.entity.WorkflowNodeEnt;
import org.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>org.knime.gateway.v0.entity</code>.
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public class ListEntities {

    private ListEntities() {
        //utility class
    }

    /**
     * Lists all gateway entity classes of package <code>org.knime.gateway.v0.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEnt.class);
        res.add(BoundsEnt.class);
        res.add(ConnectionEnt.class);
        res.add(JobManagerEnt.class);
        res.add(MetaPortInfoEnt.class);
        res.add(NodeEnt.class);
        res.add(NodeFactoryKeyEnt.class);
        res.add(NodeMessageEnt.class);
        res.add(NodePortEnt.class);
        res.add(NodeUIInfoEnt.class);
        res.add(PortTypeEnt.class);
        res.add(StyleRangeEnt.class);
        res.add(WorkflowEnt.class);
        res.add(WorkflowUIInfoEnt.class);
        res.add(XYEnt.class);
        res.add(NativeNodeEnt.class);
        res.add(NodeAnnotationEnt.class);
        res.add(NodeInPortEnt.class);
        res.add(NodeOutPortEnt.class);
        res.add(WorkflowAnnotationEnt.class);
        res.add(WorkflowNodeEnt.class);
        res.add(WrappedWorkflowNodeEnt.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>org.knime.gateway.v0.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEnt.AnnotationEntBuilder.class);
        res.add(BoundsEnt.BoundsEntBuilder.class);
        res.add(ConnectionEnt.ConnectionEntBuilder.class);
        res.add(JobManagerEnt.JobManagerEntBuilder.class);
        res.add(MetaPortInfoEnt.MetaPortInfoEntBuilder.class);
        res.add(NodeEnt.NodeEntBuilder.class);
        res.add(NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder.class);
        res.add(NodeMessageEnt.NodeMessageEntBuilder.class);
        res.add(NodePortEnt.NodePortEntBuilder.class);
        res.add(NodeUIInfoEnt.NodeUIInfoEntBuilder.class);
        res.add(PortTypeEnt.PortTypeEntBuilder.class);
        res.add(StyleRangeEnt.StyleRangeEntBuilder.class);
        res.add(WorkflowEnt.WorkflowEntBuilder.class);
        res.add(WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder.class);
        res.add(XYEnt.XYEntBuilder.class);
        res.add(NativeNodeEnt.NativeNodeEntBuilder.class);
        res.add(NodeAnnotationEnt.NodeAnnotationEntBuilder.class);
        res.add(NodeInPortEnt.NodeInPortEntBuilder.class);
        res.add(NodeOutPortEnt.NodeOutPortEntBuilder.class);
        res.add(WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder.class);
        res.add(WorkflowNodeEnt.WorkflowNodeEntBuilder.class);
        res.add(WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder.class);
        return res;
    }
}

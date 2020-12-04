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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeViewEnt;
import org.knime.gateway.api.webui.entity.XYEnt;

/**
 * Native node extension of a node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class DefaultNativeNodeEnt implements NativeNodeEnt {

  protected org.knime.gateway.api.entity.NodeIDEnt m_id;
  protected Boolean m_dialog;
  protected java.util.List<? extends NodePortEnt> m_inPorts;
  protected java.util.List<? extends NodePortEnt> m_outPorts;
  protected NodeAnnotationEnt m_annotation;
  protected XYEnt m_position;
  protected KindEnum m_kind;
  protected AllowedActionsEnt m_allowedActions;
  protected JobManagerEnt m_jobManager;
  protected String m_templateId;
  protected NodeStateEnt m_state;
  protected NodeViewEnt m_view;

  protected DefaultNativeNodeEnt() {
    //for sub-classes
  }

  @Override
  public String getTypeID() {
    return "NativeNode";
  }

  private DefaultNativeNodeEnt(final DefaultNativeNodeEntBuilder builder) {
    super();
    if(builder.m_id == null) {
        throw new IllegalArgumentException("id must not be null.");
    }
    m_id = immutable(builder.m_id);
    m_dialog = immutable(builder.m_dialog);
    if(builder.m_inPorts == null) {
        throw new IllegalArgumentException("inPorts must not be null.");
    }
    m_inPorts = immutable(builder.m_inPorts);
    if(builder.m_outPorts == null) {
        throw new IllegalArgumentException("outPorts must not be null.");
    }
    m_outPorts = immutable(builder.m_outPorts);
    m_annotation = immutable(builder.m_annotation);
    if(builder.m_position == null) {
        throw new IllegalArgumentException("position must not be null.");
    }
    m_position = immutable(builder.m_position);
    if(builder.m_kind == null) {
        throw new IllegalArgumentException("kind must not be null.");
    }
    m_kind = immutable(builder.m_kind);
    m_allowedActions = immutable(builder.m_allowedActions);
    m_jobManager = immutable(builder.m_jobManager);
    if(builder.m_templateId == null) {
        throw new IllegalArgumentException("templateId must not be null.");
    }
    m_templateId = immutable(builder.m_templateId);
    m_state = immutable(builder.m_state);
    m_view = immutable(builder.m_view);
  }

   /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DefaultNativeNodeEnt ent = (DefaultNativeNodeEnt)o;
        return Objects.equals(m_id, ent.m_id) && Objects.equals(m_dialog, ent.m_dialog) && Objects.equals(m_inPorts, ent.m_inPorts) && Objects.equals(m_outPorts, ent.m_outPorts) && Objects.equals(m_annotation, ent.m_annotation) && Objects.equals(m_position, ent.m_position) && Objects.equals(m_kind, ent.m_kind) && Objects.equals(m_allowedActions, ent.m_allowedActions) && Objects.equals(m_jobManager, ent.m_jobManager) && Objects.equals(m_templateId, ent.m_templateId) && Objects.equals(m_state, ent.m_state)  && Objects.equals(m_view, ent.m_view);
    }



   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
       return new HashCodeBuilder()
               .append(m_id)
               .append(m_dialog)
               .append(m_inPorts)
               .append(m_outPorts)
               .append(m_annotation)
               .append(m_position)
               .append(m_kind)
               .append(m_allowedActions)
               .append(m_jobManager)
               .append(m_templateId)
               .append(m_state)
               .append(m_view)
               .toHashCode();
   }



  @Override
  public org.knime.gateway.api.entity.NodeIDEnt getId() {
        return m_id;
  }

  @Override
  public Boolean isDialog() {
        return m_dialog;
  }

  @Override
  public java.util.List<? extends NodePortEnt> getInPorts() {
        return m_inPorts;
  }

  @Override
  public java.util.List<? extends NodePortEnt> getOutPorts() {
        return m_outPorts;
  }

  @Override
  public NodeAnnotationEnt getAnnotation() {
        return m_annotation;
  }

  @Override
  public XYEnt getPosition() {
        return m_position;
  }

  @Override
  public KindEnum getKind() {
        return m_kind;
  }

  @Override
  public AllowedActionsEnt getAllowedActions() {
        return m_allowedActions;
  }

  @Override
  public JobManagerEnt getJobManager() {
        return m_jobManager;
  }

  @Override
  public String getTemplateId() {
        return m_templateId;
  }

  @Override
  public NodeStateEnt getState() {
        return m_state;
  }

  @Override
  public NodeViewEnt getView() {
        return m_view;
  }


    public static class DefaultNativeNodeEntBuilder implements NativeNodeEntBuilder {

        public DefaultNativeNodeEntBuilder(){
            super();
        }

        private org.knime.gateway.api.entity.NodeIDEnt m_id;
        private Boolean m_dialog;
        private java.util.List<? extends NodePortEnt> m_inPorts = new java.util.ArrayList<>();
        private java.util.List<? extends NodePortEnt> m_outPorts = new java.util.ArrayList<>();
        private NodeAnnotationEnt m_annotation;
        private XYEnt m_position;
        private KindEnum m_kind;
        private AllowedActionsEnt m_allowedActions;
        private JobManagerEnt m_jobManager;
        private String m_templateId;
        private NodeStateEnt m_state;
        private NodeViewEnt m_view;

        @Override
        public DefaultNativeNodeEntBuilder setId(final org.knime.gateway.api.entity.NodeIDEnt id) {
             if(id == null) {
                 throw new IllegalArgumentException("id must not be null.");
             }
             m_id = id;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setDialog(final Boolean dialog) {
             m_dialog = dialog;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setInPorts(final java.util.List<? extends NodePortEnt> inPorts) {
             if(inPorts == null) {
                 throw new IllegalArgumentException("inPorts must not be null.");
             }
             m_inPorts = inPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setOutPorts(final java.util.List<? extends NodePortEnt> outPorts) {
             if(outPorts == null) {
                 throw new IllegalArgumentException("outPorts must not be null.");
             }
             m_outPorts = outPorts;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setAnnotation(final NodeAnnotationEnt annotation) {
             m_annotation = annotation;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setPosition(final XYEnt position) {
             if(position == null) {
                 throw new IllegalArgumentException("position must not be null.");
             }
             m_position = position;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setKind(final KindEnum kind) {
             if(kind == null) {
                 throw new IllegalArgumentException("kind must not be null.");
             }
             m_kind = kind;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setAllowedActions(final AllowedActionsEnt allowedActions) {
             m_allowedActions = allowedActions;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setJobManager(final JobManagerEnt jobManager) {
             m_jobManager = jobManager;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setTemplateId(final String templateId) {
             if(templateId == null) {
                 throw new IllegalArgumentException("templateId must not be null.");
             }
             m_templateId = templateId;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setState(final NodeStateEnt state) {
             m_state = state;
             return this;
        }

        @Override
        public DefaultNativeNodeEntBuilder setView(final NodeViewEnt view) {
             m_view = view;
             return this;
        }


        @Override
        public DefaultNativeNodeEnt build() {
            return new DefaultNativeNodeEnt(this);
        }

    }

}

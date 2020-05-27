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
package com.knime.gateway.local.workflow.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.knime.core.node.KNIMEConstants;
import org.knime.workbench.ui.KNIMEUIPlugin;
import org.knime.workbench.ui.preferences.HorizontalLineField;
import org.knime.workbench.ui.preferences.LabelField;
import org.knime.workbench.ui.preferences.PreferenceConstants;

/**
 * Preference page for global remote workflow editor such as refresh interval etc.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class RemoteWorkflowEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private IntegerFieldEditor m_refreshInterval;

    private BooleanFieldEditor m_autoRefresh;

    private BooleanFieldEditor m_disableWorkflowEdits;

    /**
     * Constructor.
     */
    public RemoteWorkflowEditorPreferencePage() {
        super(GRID);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
     * types of preferences. Each field editor knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();

        addField(
            new LabelField(parent, "The Remote Workflow Editor allows one to view and edit jobs on a KNIME Server.\n"));
        m_autoRefresh = new BooleanFieldEditor(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_AUTO_REFRESH,
            "Auto-refresh Remote Workflow Editor", parent) {
            @Override
            protected void valueChanged(final boolean oldValue, final boolean newValue) {
                super.valueChanged(oldValue, newValue);
                m_refreshInterval.setEnabled(newValue, parent);
                m_disableWorkflowEdits.setEnabled(newValue, parent);
            }
        };
        addField(m_autoRefresh);
        m_refreshInterval = new IntegerFieldEditor(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_AUTO_REFRESH_INTERVAL_MS,
            "Auto-refresh interval (in ms)", parent) {
        @Override
        public void setValidRange(final int min, final int max) {
                super.setValidRange(min, max);
                refreshValidState();
            }
        };
        m_refreshInterval.setValidRange(KNIMEConstants.MIN_GUI_REFRESH_INTERVAL, Integer.MAX_VALUE);
        addField(m_refreshInterval);

        addField(new HorizontalLineField(parent));

        final RemoteWorkflowEditorPreferencePage thisRef = this;
        m_disableWorkflowEdits =
            new BooleanFieldEditor(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_EDITS_DISABLED, "Disable edits", parent) {
                @Override
                protected void valueChanged(final boolean oldValue, final boolean newValue) {
                    super.valueChanged(oldValue, newValue);
                    if (!newValue) {
                        m_refreshInterval.setValidRange(KNIMEConstants.MIN_GUI_REFRESH_INTERVAL,
                            KNIMEConstants.WORKFLOW_EDITOR_CONNECTION_TIMEOUT);
                    } else {
                        m_refreshInterval.setValidRange(KNIMEConstants.MIN_GUI_REFRESH_INTERVAL, Integer.MAX_VALUE);
                    }
                    thisRef.checkState();
                }
            };
        addField(m_disableWorkflowEdits);
        addField(new LabelField(parent, "If workflow edits are enabled\nthe refresh interval must not be larger than "
                + KNIMEConstants.WORKFLOW_EDITOR_CONNECTION_TIMEOUT + " ms."));

        addField(new HorizontalLineField(parent));

        IntegerFieldEditor tableViewChunkSize =
            new IntegerFieldEditor(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_TABLE_VIEW_CHUNK_SIZE,
                "Chunk size for port table view", parent);
        tableViewChunkSize.setValidRange(1, Integer.MAX_VALUE);
        addField(tableViewChunkSize);
        addField(new LabelField(parent,
            "Specifies the number of rows loaded at once from\nthe server for the table view of a port."
                + "\nNote: only takes effect on port views opened for the first time"
                + "\nafter opening the workflow or node re-execution."));
    }

    /** {@inheritDoc} */
    @Override
    public void init(final IWorkbench workbench) {
        // we use the pref store of the UI plugin
        setPreferenceStore(KNIMEUIPlugin.getDefault().getPreferenceStore());
    }

    /** {@inheritDoc} */
    @Override
    protected void initialize() {
        super.initialize();
        m_refreshInterval.setEnabled(m_autoRefresh.getBooleanValue(), getFieldEditorParent());
        m_disableWorkflowEdits.setEnabled(m_autoRefresh.getBooleanValue(), getFieldEditorParent());
    }
}

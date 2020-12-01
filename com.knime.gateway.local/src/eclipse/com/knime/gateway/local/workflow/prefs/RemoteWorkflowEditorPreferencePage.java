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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.ui.util.SWTUtilities;
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

    private IntegerFieldEditor m_clientTimeout;

    private int m_initialTimeoutValue;

    private int m_appliedTimeoutValue;

    private boolean m_apply;

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

        addField(new HorizontalLineField(parent));

        m_clientTimeout = new IntegerFieldEditor(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_CLIENT_TIMEOUT,
            "Client timeout (in ms)", parent) {
            @Override
            public void setValidRange(final int min, final int max) {
                super.setValidRange(min, max);
                refreshValidState();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void valueChanged() {
                super.valueChanged();
                if (m_clientTimeout.getIntValue() != m_initialTimeoutValue) {
                    setMessage("You need to restart KNIME Analytics Platform to apply this setting!",
                        IMessageProvider.WARNING);
                } else {
                    setMessage("");
                }
            }
        };

        m_initialTimeoutValue = getPreferenceStore().getInt(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_CLIENT_TIMEOUT);
        m_appliedTimeoutValue = m_initialTimeoutValue;
        m_clientTimeout.setValidRange(10000, Integer.MAX_VALUE);
        addField(m_clientTimeout);
        addField(new LabelField(parent,
            "Specifies the timeout in milliseconds of KNIME Analytics Platform\nwhen communicating with the server."));
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

    /**
     * Overriden to display a message box in case the client timeout was changed.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean performOk() {
        super.performOk();
        m_appliedTimeoutValue = m_clientTimeout.getIntValue();
        checkChanges();
        return true;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected void performApply() {
        m_apply = true;
        m_appliedTimeoutValue = m_clientTimeout.getIntValue();
        super.performApply();
    }

    /**
     * Overriden to react when the users applies but then presses cancel.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean performCancel() {
        final boolean result = super.performCancel();
        checkChanges();
        return result;
    }

    private void checkChanges() {
        final boolean apply = m_apply;
        m_apply = false;

        if (apply) {
            return;
        }

        if (m_initialTimeoutValue != m_appliedTimeoutValue) {
            final String message = "Changes of the remote workflow editor client timeout become " //
                + "available after restarting the workbench.\n" //
                + "Do you want to restart the workbench now?";

            Display.getDefault().asyncExec(() -> promptRestartWithMessage(message));
        }
    }

    private static void promptRestartWithMessage(final String message) {
        final MessageBox mb = new MessageBox(SWTUtilities.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        mb.setText("Restart workbench...");
        mb.setMessage(message);
        if (mb.open() != SWT.YES) {
            return;
        }
        PlatformUI.getWorkbench().restart();
    }
}

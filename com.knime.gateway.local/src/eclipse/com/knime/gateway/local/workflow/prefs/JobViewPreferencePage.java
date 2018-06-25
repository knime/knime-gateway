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
import org.knime.workbench.ui.preferences.LabelField;
import org.knime.workbench.ui.preferences.PreferenceConstants;

/**
 * Preference page for global job view settings such as refresh interval etc.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JobViewPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private IntegerFieldEditor m_refreshInterval;

    private BooleanFieldEditor m_autoRefresh;

    private BooleanFieldEditor m_enableJobEdits;

    /**
     * Constructor.
     */
    public JobViewPreferencePage() {
        super(GRID);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
     * types of preferences. Each field editor knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();

        addField(new LabelField(parent, "Remote Job View auto-refresh and edit settings"));
        m_autoRefresh = new BooleanFieldEditor(PreferenceConstants.P_AUTO_REFRESH_JOB,
            "Auto-refresh Remote Job View", parent) {
            @Override
            protected void valueChanged(final boolean oldValue, final boolean newValue) {
                super.valueChanged(oldValue, newValue);
                m_refreshInterval.setEnabled(newValue, parent);
                m_enableJobEdits.setEnabled(newValue, parent);
            }
        };
        addField(m_autoRefresh);
        m_refreshInterval = new IntegerFieldEditor(PreferenceConstants.P_AUTO_REFRESH_JOB_INTERVAL_MS,
            "Auto-refresh interval (in ms)", parent) {
            @Override
            public void setValidRange(final int min, final int max) {
                super.setValidRange(min, max);
                refreshValidState();
            }
        };
        m_refreshInterval.setValidRange(KNIMEConstants.MIN_GUI_REFRESH_INTERVAL, Integer.MAX_VALUE);
        addField(m_refreshInterval);
        addField(new LabelField(parent, "If job edits are enabled the refresh interval must not be larger than "
            + KNIMEConstants.WORKFLOW_EDITOR_CONNECTION_TIMEOUT + " ms."));

        final JobViewPreferencePage thisRef = this;
        m_enableJobEdits =
            new BooleanFieldEditor(PreferenceConstants.P_JOB_EDITS_ENABLED, "Enable job edits", parent) {
                @Override
                protected void valueChanged(final boolean oldValue, final boolean newValue) {
                    super.valueChanged(oldValue, newValue);
                    if (newValue) {
                        m_refreshInterval.setValidRange(KNIMEConstants.MIN_GUI_REFRESH_INTERVAL,
                            KNIMEConstants.WORKFLOW_EDITOR_CONNECTION_TIMEOUT);
                    } else {
                        m_refreshInterval.setValidRange(KNIMEConstants.MIN_GUI_REFRESH_INTERVAL, Integer.MAX_VALUE);
                    }
                    thisRef.checkState();
                }
            };
        addField(m_enableJobEdits);
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
        m_enableJobEdits.setEnabled(m_autoRefresh.getBooleanValue(), getFieldEditorParent());
    }
}

package org.knime.next;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.knime.next.server.KnimeNextServer;

public class KnimeNextView extends ViewPart {
    public static final String ID = "org.knime.next.view";

    public static final String WEB_BROWSER_VIEW_ID = "org.eclipse.ui.browser.view"; //$NON-NLS-1$

    protected Browser m_browser;

    private KnimeNextServer m_server = null;

    @Override
    public void createPartControl(final Composite parent) {
        m_browser = new Browser(parent, SWT.NONE);
        m_browser.setUrl("http://localhost:3000/index.html");
        //m_browser.setUrl("https://hub.knime.com");

        if (m_server == null) {
            m_server = new KnimeNextServer(3000);
            m_server.start();
        }
    }

    @Override
    public void dispose() {
        if (m_server != null) {
            m_server.stop();
        }
    }

    @Override
    public void setFocus() {
        m_browser.setFocus();
    }

}

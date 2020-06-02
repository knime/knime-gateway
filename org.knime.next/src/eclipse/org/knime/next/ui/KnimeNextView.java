package org.knime.next.ui;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.knime.next.browserfunction.JsonRpcBrowserFunction;
import org.knime.next.jsonrpc.EventService;
import org.knime.next.server.KnimeNextServer;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class KnimeNextView extends ViewPart {
    public static final String ID = "org.knime.next.view";

    public static final String WEB_BROWSER_VIEW_ID = "org.eclipse.ui.browser.view"; //$NON-NLS-1$

    protected Browser m_browser;

    private KnimeNextServer m_server = null;

    @Override
    public void createPartControl(final Composite parent) {
        if (m_server == null) {
            m_server = new KnimeNextServer(3000);
            m_server.start();
        }

        m_browser = new Browser(parent, SWT.NONE);
        m_browser.setUrl("http://localhost:4000/");
    }

    /*
     * A setup without a server.
     * File-serving via file system.
     * Communication while BrowserFunctions (rpc) and script-execution (events).
     */
    private void serverlessSetup() {
        new JsonRpcBrowserFunction(m_browser);
        Bundle myBundle = FrameworkUtil.getBundle(getClass());
        try {
            URL url = myBundle.getEntry("webapp/index.html");
            String path = FileLocator.toFileURL(url).getPath();
            m_browser.setUrl("file://" + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EventService.MESSAGE_CONSUMER = message -> {
            Display.getDefault().asyncExec(() -> m_browser.execute("receiveMessage('" + message + "')"));
        };
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

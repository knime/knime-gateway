package st.theori.eclipse.chro;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.chromium.Browser;
import org.eclipse.swt.chromium.BrowserFunction;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * This class does little more than create a chromium-swt Browser and set-up a bridge function call.
 */
public class Editor extends EditorPart {
	static String ID = "st.theori.eclipse.chro.editor1";
	
	static Browser HACKY_DEMO_SINGLETON_BROWSER = null;
	
	
	private Browser m_browser;
	private BrowserFunction m_browserFunction;
	
	@Override
	public void dispose() {
		m_browserFunction.dispose();
		if (!m_browser.isDisposed()) {
			m_browser.dispose();
		}
		super.dispose();
	}
	
	@Override
	public void doSave(final IProgressMonitor monitor) { }

	@Override
	public void doSaveAs() { }

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());

		m_browser = new Browser(parent, SWT.NONE);
		HACKY_DEMO_SINGLETON_BROWSER = m_browser;
		m_browserFunction = new NotifyRCPBrowserFunction(m_browser);
		m_browser.setUrl("https://hub.knime.com");
	}

	@Override
	public void setFocus() { }

	
	/**
	 * Provides the bridge from javascript-land.
	 */
	static class NotifyRCPBrowserFunction extends BrowserFunction {
		private static final String CREATION_EVENT = "creation";
		private static final String MOVE_EVENT = "move";
		
		
		private final View m_view;
		
		NotifyRCPBrowserFunction(final Browser b) {
			super(b, "notifyRCP");
			
			m_view = (View)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(View.ID);
		}
		
		// This is the invocation from due to the javascript-land call.
		@Override
		public Object function(final Object[] arguments) {
			if (arguments.length > 0) {
				final String eventType = (String)arguments[0];
				
				if (MOVE_EVENT.equals(eventType)) {
					if (arguments.length == 4) {
						final String name = (String)arguments[1];
						final Double x = (Double)arguments[2];
						final Double y = (Double)arguments[3];
						
						m_view.moveCircle(name, x.intValue(), y.intValue());
					} else {
						System.out.println("Expected 4 arguments for move event - got " + arguments.length);
					}
				} else if (CREATION_EVENT.equals(eventType)) {
					if (arguments.length == 5) {
						final String name = (String)arguments[1];
						final Double x = (Double)arguments[2];
						final Double y = (Double)arguments[3];
						final String fillColor = (String)arguments[4];
						
						m_view.createCircle(name, fillColor, x.intValue(), y.intValue());
					} else {
						System.out.println("Expected 5 arguments for creation event - got " + arguments.length);
					}
				} else {
					System.out.println("Unknown notification event: " + eventType);
				}
			}
			return null;
		}
	}
}

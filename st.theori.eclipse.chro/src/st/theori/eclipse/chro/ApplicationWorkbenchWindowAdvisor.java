package st.theori.eclipse.chro;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.part.NullEditorInput;

@SuppressWarnings("restriction")	// NullEditorInput
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private Editor m_editor;
	
	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}
	
	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("MT's Chromium Tester");
	}
	
	@Override
	public void postWindowOpen() {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			m_editor = (Editor)page.openEditor(new NullEditorInput(), Editor.ID);
		} catch (final PartInitException pie) {
			pie.printStackTrace();
		}
	}
	
	@Override
	public boolean preWindowShellClose() {
		m_editor.dispose();
		
		return true;
	}
}

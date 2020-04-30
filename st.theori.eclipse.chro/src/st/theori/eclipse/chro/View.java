package st.theori.eclipse.chro;

import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.chromium.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart {
	public static final String ID = "st.theori.eclipse.chro.view";
	
	private static final int CIRCLE_DIAMETER = 18;

	
	@Inject IWorkbench workbench;
	
	private final HashMap<String, CircleDescriptor> m_nameCircleMap = new HashMap<>();
	private final Color m_strokeColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	
	private Canvas m_canvas;

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		m_canvas = new Canvas(parent, SWT.NONE);
		m_canvas.addPaintListener((e) -> {
			final GC gc = e.gc;
			
			gc.setAntialias(SWT.ON);
			gc.setForeground(m_strokeColor);
			gc.setLineWidth(4);
			for (final CircleDescriptor cd : m_nameCircleMap.values()) {
				gc.setBackground(cd.getFillColor());
				final Point location = cd.getLocation();
				gc.fillOval(location.x, location.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
				gc.drawOval(location.x, location.y, (CIRCLE_DIAMETER - 1), (CIRCLE_DIAMETER - 1));
			}
		});
		final MouseTracker tracker = new MouseTracker();
		m_canvas.addMouseListener(tracker);
		m_canvas.addMouseMoveListener(tracker);
	}

	@Override
	public void setFocus() { }
	
	void moveCircle(final String name, final int x, final int y) {
		final CircleDescriptor cd = m_nameCircleMap.get(name);
		if (cd != null) {
			cd.setLocation(x, y);
			
			m_canvas.redraw();
		}
	}
	
	void createCircle(final String name, final String color, final int x, final int y) {
		final java.awt.Color c = java.awt.Color.decode(color);
		final Color fillColor = new Color(PlatformUI.getWorkbench().getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
		final CircleDescriptor cd = new CircleDescriptor(name, x, y, fillColor);
		m_nameCircleMap.put(name,  cd);
		m_canvas.redraw();
	}

	
	private class MouseTracker implements MouseListener, MouseMoveListener {
		private CircleDescriptor m_circleInDrag;

		private void moveBrowserCircle(final MouseEvent me) {
			if (m_circleInDrag != null) {
				final Browser b = Editor.HACKY_DEMO_SINGLETON_BROWSER;
				m_circleInDrag.setLocation(me.x, me.y);
				m_canvas.redraw();
				final String script = "moveCircle('" + m_circleInDrag.getName() + "', " + me.x + ", " + me.y + ");";
				b.execute(script);
			}
		}
		
		@Override
		public void mouseMove(final MouseEvent me) {
			moveBrowserCircle(me);
		}

		@Override
		public void mouseDoubleClick(final MouseEvent me) { }

		@Override
		public void mouseDown(final MouseEvent me) {
			final Point p = new Point(me.x, me.y);
			for (final CircleDescriptor cd : m_nameCircleMap.values()) {
				if (cd.containsPoint(p)) {
					m_circleInDrag = cd;
					break;
				}
			}
		}

		@Override
		public void mouseUp(final MouseEvent me) {
			moveBrowserCircle(me);
			
			m_circleInDrag = null;
		}
	}
	
	
	private static class CircleDescriptor {
		private final String m_name;
		private final Point m_location;
		private final Color m_fillColor;
		private final Rectangle m_hitBounds;
		
		CircleDescriptor(final String name, final int x, final int y, final Color fill) {
			m_name = name;
			m_location = new Point(x, y);
			m_fillColor = fill;
			m_hitBounds = new Rectangle(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
		}

		String getName() {
			return m_name;
		}

		void setLocation(final int x, final int y) {
			m_location.x = x;
			m_location.y = y;
			m_hitBounds.x = x;
			m_hitBounds.y = y;
		}
		
		Point getLocation() {
			return m_location;
		}

		Color getFillColor() {
			return m_fillColor;
		}
		
		boolean containsPoint(final Point p) {
			return m_hitBounds.contains(p);
		}
	}
}
package vtm.rcp.app;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Frame;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart {

	public static final String	ID	= "VTM_RCP_App.view";

	private static boolean		_isMapAlreadyCreated;

	private GdxMapApp			_gdxMapApp;

	private IPartListener2		_partListener;

	private void addPartListener() {

		_partListener = new IPartListener2() {

			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {

				if (partRef.getPart(false) == View.this) {
					if (_gdxMapApp != null) {
						_gdxMapApp.closeMap();
					}
				}
			}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {}

			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {}
		};
		getViewSite().getPage().addPartListener(_partListener);
	}

	@Override
	public void createPartControl(Composite parent) {

		/*
		 * Map creation works only once, closing lwjgl app or destroy lwjgl display will also close
		 * the app :-(
		 */
		if (_isMapAlreadyCreated) {

			createUI_Restart(parent);

		} else {

			_isMapAlreadyCreated = true;

			createUI_Map(parent);
		}

		addPartListener();
	}

	private void createUI_Map(Composite parent) {

		final Composite swtContainer = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		final Frame awtContainer = SWT_AWT.new_Frame(swtContainer);

		Canvas awtCanvas = new Canvas();
		awtContainer.setLayout(new BorderLayout());
		awtCanvas.setIgnoreRepaint(true);

		awtContainer.add(awtCanvas);
		awtCanvas.setFocusable(true);
		awtCanvas.requestFocus();

		_gdxMapApp = new GdxMapApp();
		_gdxMapApp.run(awtCanvas);
	}

	private void createUI_Restart(Composite parent) {

		Composite containerOuter = new Composite(parent, SWT.NONE);
		containerOuter.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridDataFactory
				.fillDefaults()
				.grab(true, true)
				.align(SWT.CENTER, SWT.CENTER)
				.applyTo(containerOuter);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(containerOuter);
		{

			Composite containerInner = new Composite(containerOuter, SWT.NONE);
			GridDataFactory
					.fillDefaults()
					.grab(true, true)
					.align(SWT.CENTER, SWT.CENTER)
					.applyTo(containerInner);
			GridLayoutFactory.fillDefaults().numColumns(1).applyTo(containerInner);
			{
				{
					// Label: Restart

					Label label = new Label(containerInner, SWT.NONE);
					label.setText(
							"The VTM map cannot be opened a 2nd time,\npress 'Restart' to restart the app and open the map again.\n\n");
					GridDataFactory
							.fillDefaults()
							.align(SWT.CENTER, SWT.CENTER)
							.applyTo(label);
				}

				{
					// Button: Restart
					Button button = new Button(containerInner, SWT.PUSH);
					button.setText("  Restart  ");
					GridDataFactory
							.fillDefaults()
							.align(SWT.CENTER, SWT.CENTER)
							.applyTo(button);

					button.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							PlatformUI.getWorkbench().restart();
						}
					});
				}
			}
		}
	}

	@Override
	public void dispose() {

		getViewSite().getPage().removePartListener(_partListener);

		super.dispose();
	}

	@Override
	public void setFocus() {}

}

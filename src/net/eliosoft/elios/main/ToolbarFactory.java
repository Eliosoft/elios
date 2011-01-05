package net.eliosoft.elios.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.views.Messages;

/**
 * Create a {@link JToolBar} to offer easy access to most common configuration
 * and action.
 * 
 * @author acollign
 * @since Jan 5, 2011
 */
public class ToolbarFactory {

	/** The {@link RemoteModel}. **/
	private RemoteModel remoteModel;

	/**
	 * Constructs a {@link ToolbarFactory} based on the given
	 * {@link RemoteModel}.
	 * 
	 * @param remoteModel
	 *            a {@link RemoteModel} used to access to the Remote state.
	 */
	public ToolbarFactory(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
	}

	/**
	 * Builds the {@link JToolBar} mapped to the given {@link JFrame}. However
	 * the toolbar is not added in the frame.
	 * 
	 * @param frame
	 * 
	 * @return a {@link JToolBar} mapped to the given {@link JFrame}
	 */
	public JToolBar create(final JFrame frame) {
		final JToolBar toolBar = new JToolBar();

		addAdditiveMode(toolBar);
		toolBar.addSeparator();

		addUniverse(toolBar);
		toolBar.add(Box.createHorizontalStrut(3));

		addSubnet(toolBar);
		toolBar.addSeparator();
		toolBar.add(Box.createHorizontalGlue());

		addQuit(frame, toolBar);

		return toolBar;
	}

	/**
	 * Adds the subnet components to the toolbar
	 * 
	 * @param toolBar
	 *            a {@link JToolBar}
	 */
	private void addSubnet(final JToolBar toolBar) {
		JLabel subnetLabel = new JLabel(
				Messages.getString("prefsview.universe")); //$NON-NLS-1$
		toolBar.add(subnetLabel);

		JSpinner subnetSpinner = new JSpinner(
				remoteModel.getSubnetSpinnerModel());
		toolBar.add(subnetSpinner);
	}

	/**
	 * Adds a quit button to the toolbar.
	 * 
	 * @param toolBar
	 *            a {@link JToolBar}
	 */

	private void addQuit(final JFrame frame, final JToolBar toolBar) {
		final JButton quit = new JButton();
		quit.setToolTipText("Quit");
		quit.setIcon(new ImageIcon(Elios.class
				.getResource("/net/eliosoft/elios/gui/views/process-stop.png")));
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		toolBar.add(quit);
	}

	/**
	 * Adds the additive mode button to the toolbar.
	 * 
	 * @param toolBar
	 *            a {@link JToolBar}
	 */
	private void addAdditiveMode(final JToolBar toolBar) {
		final JToggleButton additiveMode = new JToggleButton();
		additiveMode.setToolTipText(Messages
				.getString("prefsview.additivemode"));
		additiveMode
				.setIcon(new ImageIcon(
						Elios.class
								.getResource("/net/eliosoft/elios/gui/views/preferences-desktop.png")));
		additiveMode.setSelected(remoteModel.isAdditiveModeEnabled());
		additiveMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remoteModel.setAdditiveModeEnabled(additiveMode.isSelected());
			}
		});
		toolBar.add(additiveMode);
	}

	/**
	 * Adds the universe components to the toolbar.
	 * 
	 * @param toolBar
	 *            a {@link JToolBar}
	 */
	private void addUniverse(final JToolBar toolBar) {
		JLabel universeLabel = new JLabel(
				Messages.getString("prefsview.subnet")); //$NON-NLS-1$
		toolBar.add(universeLabel);
		toolBar.add(Box.createHorizontalStrut(3));

		JSpinner universeSpinner = new JSpinner(
				remoteModel.getUniverseSpinnerModel());
		toolBar.add(universeSpinner);
	}

}

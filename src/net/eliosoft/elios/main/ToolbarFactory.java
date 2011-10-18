package net.eliosoft.elios.main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.eliosoft.elios.gui.events.ArtNetStartedEvent;
import net.eliosoft.elios.gui.events.ArtNetStoppedEvent;
import net.eliosoft.elios.gui.events.CommandLineValueChangedEvent;
import net.eliosoft.elios.gui.events.HttpStartedEvent;
import net.eliosoft.elios.gui.events.HttpStoppedEvent;
import net.eliosoft.elios.gui.listeners.RemoteModelListener;
import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.views.CuesViewHelper;
import net.eliosoft.elios.gui.views.Messages;
import net.eliosoft.elios.main.ApplicationState.State;
import net.eliosoft.elios.server.events.AdditiveModeValueChangedEvent;

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
	private ApplicationState state;

	/**
	 * Constructs a {@link ToolbarFactory} based on the given
	 * {@link RemoteModel}.
	 * 
	 * @param remoteModel
	 *            a {@link RemoteModel} used to access to the Remote state.
	 * @param state a {@link ApplicationState} 
	 */
	public ToolbarFactory(RemoteModel remoteModel, ApplicationState state) {
		this.remoteModel = remoteModel;
		this.state = state;
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

		addSubnet(toolBar);
		toolBar.add(Box.createHorizontalStrut(3));

		addUniverse(toolBar);
		toolBar.addSeparator();

		addSaveCue(frame, toolBar, remoteModel);
		toolBar.addSeparator();

		addFullScreen(frame, toolBar);

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
				Messages.getString("prefsview.subnet")); //$NON-NLS-1$
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
			    state.changeState(State.SHUTTING_DOWN);
			}
		});
		toolBar.add(quit);
	}

	private void addFullScreen(final JFrame frame, final JToolBar toolBar) {
		final JToggleButton button = new JToggleButton();
		button.setToolTipText(Messages
				.getString("main.fullscreen")); //$NON-NLS-1$
		button.setIcon(new ImageIcon(
				Elios.class
						.getResource("/net/eliosoft/elios/gui/views/view-fullscreen.png")));

		final GraphicsDevice myDevice = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		// take care of disable the button is full screen mode is not supported
		button.setEnabled(myDevice.isFullScreenSupported());

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (myDevice.getFullScreenWindow() == null) {
					turnOnFullScreen(frame, myDevice);
					button.setSelected(true);
				} else {
					turnOffFullScreen(frame, myDevice);
					button.setSelected(false);
				}
			}
		});
		toolBar.add(button);
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
								.getResource("/net/eliosoft/elios/gui/views/additive-mode.png")));
		additiveMode.setSelected(remoteModel.isAdditiveModeEnabled());
		additiveMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remoteModel.setAdditiveModeEnabled(additiveMode.isSelected());
			}
		});
		remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			
			@Override
			public void httpStopped(HttpStoppedEvent event) {}
			
			@Override
			public void httpStarted(HttpStartedEvent event) {}
			
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) {}
			
			@Override
			public void artNetStopped(ArtNetStoppedEvent event) {}
			
			@Override
			public void artNetStarted(ArtNetStartedEvent event) {}
			
			@Override
			public void additiveModeValueChanged(AdditiveModeValueChangedEvent event) {
				additiveMode.setSelected(event.isAdditiveModeEnabled());
			}
		});
		toolBar.add(additiveMode);
	}

	/**
	 * Add a button that open a dialog to save the current DMX array into a cue.
	 *
	 * @param frame the root frame (set as the parent of the dialog)
	 * @param toolBar the {@link JToolBar} in which the button will be added
	 * @param remoteModel the {@link RemoteModel} used to store the cue
	 */
	private void addSaveCue(final JFrame frame, final JToolBar toolBar, final RemoteModel remoteModel) {
		JButton storeButton = new JButton();
		storeButton.setIcon(new ImageIcon(Elios.class
				.getResource("/net/eliosoft/elios/gui/views/document-save-as.png")));

		storeButton.setToolTipText(Messages.getString("cuesview.storebutton"));

		storeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String cueName = CuesViewHelper.askForCueName(frame,remoteModel.getCuesListModel().getNextDefaultCueName());

				if(cueName != null){
					try {
						remoteModel.storeCue(cueName);						
					} catch (Exception exception) {
						CuesViewHelper.printError(frame, exception, cueName);
					}
				}
			}
		});

		toolBar.add(storeButton);
	}


	/**
	 * Adds the universe components to the toolbar.
	 * 
	 * @param toolBar
	 *            a {@link JToolBar}
	 */
	private void addUniverse(final JToolBar toolBar) {
		JLabel universeLabel = new JLabel(
				Messages.getString("prefsview.universe")); //$NON-NLS-1$
		toolBar.add(universeLabel);
		toolBar.add(Box.createHorizontalStrut(3));

		JSpinner universeSpinner = new JSpinner(
				remoteModel.getUniverseSpinnerModel());
		toolBar.add(universeSpinner);
	}
	
	/**
	 * Try to turn the full screen off. If something goes wrong, just try to set the frame visible.
	 * 
	 * @param frame the {@link JFrame} to display in full screen mode
	 * @param device the {@link GraphicsDevice} on which the frame must be display
	 */
	private void turnOffFullScreen(JFrame frame, GraphicsDevice device){
		try {
			//hide the frame so we can change it.
			frame.setVisible(false);
			 
			//remove the frame from being displayable.
			frame.dispose();
			 
			//put the borders back on the frame.
			frame.setUndecorated(false);
			 
			//needed to unset this window as the fullscreen window.
			device.setFullScreenWindow(null);
			 
			//reset the display mode to what it was before
			//we changed it.
			frame.setVisible(true);
		} catch(Exception e) { // ugly catch
			JOptionPane.showMessageDialog(frame, MessageFormat.format(
					Messages.getString("error.fullscreen.off.message"),
					e.getMessage()), Messages
					.getString("error.fullscreen.title"),
					JOptionPane.ERROR_MESSAGE);
		} finally {
			//show the frame
			frame.setVisible(true);
		}
		
	}
	
	/**
	 * Try to turn the full screen on. If something goes wrong, just try to set the frame visible.
	 * 
	 * @param frame the {@link JFrame} to display in full screen mode
	 * @param device the {@link GraphicsDevice} on which the frame must be display
	 */
	private void turnOnFullScreen(JFrame frame, GraphicsDevice device){		 
		try {
			//hide everything
			frame.setVisible(false);
			 
			//remove the frame from being displayable.
			frame.dispose();
			 
			//remove borders around the frame
			frame.setUndecorated(true);
			 
			//make the window fullscreen.
			device.setFullScreenWindow(frame);
		} catch(Exception e) { // ugly catch
			JOptionPane.showMessageDialog(frame, MessageFormat.format(
					Messages.getString("error.fullscreen.on.message"),
					e.getMessage()), Messages
					.getString("error.fullscreen.title"),
					JOptionPane.ERROR_MESSAGE);
		} finally {
			//show the frame
			frame.setVisible(true);
		}
	}

}

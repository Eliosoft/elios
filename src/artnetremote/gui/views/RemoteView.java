package artnetremote.gui.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.gui.models.RemoteModel;

public class RemoteView {
	private final RemoteModel remoteModel;

	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel remotePanel = new JPanel();
	private final JTextField commandLine = new JTextField();
	
	private final List<JButton> valueButtonsList = new ArrayList<JButton>();
	private final JButton enterButton;
	private final JButton resetButton;
	private final JButton delButton;

	
	public RemoteView(final RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.remoteModel.addRemoteModelChangedListener(new RemoteModelListener() {
			@Override
			public void commandLineValueChanged(CommandLineValueChangedEvent event) {
				commandLine.setText(event.getCommand());
				if(event.getCommand().isEmpty()){
					delButton.setEnabled(false);
					resetButton.setEnabled(false);
				}
				else{
					delButton.setEnabled(true);
					resetButton.setEnabled(true);
				}
			}
			@Override
			public void artNetStarted(ArtNetStartedEvent event) {
				enterButton.setEnabled(true);
			}
			@Override
			public void artNetStopped(ArtNetStoppedEvent event) {
				enterButton.setEnabled(false);
			}
		});
		
		this.remotePanel.setLayout(layout);
		
		this.commandLine.setEditable(false);
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridy = 0;
		this.constraints.gridwidth = 6;
		this.remotePanel.add(this.commandLine,this.constraints);
		
		this.addValueButton("1", 1, 2, true);
		this.addValueButton("2", 1, 2, true);
		this.addValueButton("3", 1, 2, true);
		this.addValueButton("4", 2, 2, true);
		this.addValueButton("5", 2, 2, true);
		this.addValueButton("6", 2, 2, true);
		this.addValueButton("7", 3, 2, true);
		this.addValueButton("8", 3, 2, true);
		this.addValueButton("9", 3, 2, true);
		this.addValueButton(";", 4, 2, true);
		this.addValueButton("0", 4, 2, true);
		this.addValueButton("@", 4, 2, true);
		this.delButton = this.addButton("Del", 5, 2, false);
		this.enterButton = this.addButton("Enter", 5, 2, false);
		this.resetButton = this.addButton("Reset", 5, 2, false);
	}
	
	public JPanel getRemotePanel(){
		return this.remotePanel;
	}
	
	public void setCommandLineFieldValue(String value){
		this.commandLine.setText(value);
	}
	
	public void addValueButtonsListener(ActionListener listener) {
		for(JButton button : valueButtonsList){
			button.addActionListener(listener);
		}
	}

	public void removeValueButtonsListener(ActionListener listener) {
		for(JButton button : valueButtonsList){
			button.removeActionListener(listener);
		}
	}
	
	public void addDelButtonListener(ActionListener actionListener) {
		this.delButton.addActionListener(actionListener);
	}

	public void removeDelButtonListener(ActionListener actionListener) {
		this.delButton.removeActionListener(actionListener);
	}
	
	public void addEnterButtonListener(ActionListener actionListener) {
		this.enterButton.addActionListener(actionListener);
	}

	public void removeEnterButtonListener(ActionListener actionListener) {
		this.enterButton.removeActionListener(actionListener);
	}
	
	public void addResetButtonListener(ActionListener actionListener) {
		this.resetButton.addActionListener(actionListener);
	}
	
	public void removeResetButtonListener(ActionListener actionListener) {
		this.resetButton.removeActionListener(actionListener);
	}
	
	private void addValueButton(String text, int gridY, int gridWidth, boolean enabled){
		JButton button = addButton(text, gridY, gridWidth, enabled);
		valueButtonsList.add(button);
	}

	private JButton addButton(String text, int gridY, int gridWidth, boolean enabled) {
		JButton button = new JButton(text);
		button.setEnabled(enabled);
		
		this.constraints.fill = GridBagConstraints.HORIZONTAL;
		this.constraints.gridy = gridY;
		this.constraints.gridwidth = gridWidth;
		
		remotePanel.add(button,constraints);
		return button;
	}
	
	
}

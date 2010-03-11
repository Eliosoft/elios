/*
 * This file is part of ArtNet-Remote.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * ArtNet-Remote is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArtNet-Remote is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ArtNet-Remote. If not, see <http://www.gnu.org/licenses/>.
 */

package artnetremote.gui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import artnet4j.ArtNet;
import artnet4j.ArtNetServer;
import artnet4j.packets.ArtDmxPacket;
import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.listeners.RemoteModelListener;

/**
 * This model describes almost all data of the artnet-remote.
 *
 * @author Jeremie GASTON-RAOUL
 * @author Alexandre COLLIGNON
 */
public class RemoteModel {
	private LogsListModel logsListModel;
	private SpinnerNumberModel inPortSpinnerModel;
	private SpinnerNumberModel outPortSpinnerModel;
	private ComboBoxModel broadcastAddressComboModel;

	private StringBuilder commandLine;
	private List<RemoteModelListener> remoteModelChangedListeners;
	private byte[] dmxArray;

	private ArtNetServer artnetServer;
	private int subnet;
	private int universe;
	private int sequenceId;

	private static final int MIN_PORT = 0;
	private static final int MAX_PORT = 65535;
	private static final String[] BROADCAST_ADDRESSES = {"2.255.255.255","10.255.255.255","127.255.255.255"};
	
	private final transient Logger logger = Logger.getLogger(RemoteModel.class.getName());

	/**
	 * Default constructor of the remote model
	 */
	public RemoteModel() {
		this.logsListModel = new LogsListModel(ArtNet.logger);
		this.logsListModel.addLogger(logger);
		this.inPortSpinnerModel = new SpinnerNumberModel(ArtNetServer.DEFAULT_PORT, RemoteModel.MIN_PORT, RemoteModel.MAX_PORT, 1);
		this.outPortSpinnerModel = new SpinnerNumberModel(ArtNetServer.DEFAULT_PORT, RemoteModel.MIN_PORT, RemoteModel.MAX_PORT, 1);
		this.broadcastAddressComboModel = new DefaultComboBoxModel(RemoteModel.BROADCAST_ADDRESSES);
				
		this.commandLine = new StringBuilder();
		this.remoteModelChangedListeners = new ArrayList<RemoteModelListener>();
		this.dmxArray = new byte[512];
		this.artnetServer = new ArtNetServer();
		
		this.broadcastAddressComboModel.addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {}
			
			@Override
			public void intervalAdded(ListDataEvent e) {}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				artnetServer.setBroadcastAddress((String)broadcastAddressComboModel.getSelectedItem());
			}
		});

	}

	/**
	 * Add a character to the command line.
	 * @param c the character added
	 */
	public void addToCommandLine(Character c) {
		this.commandLine.append(c);
		this.fireCommandLineValueChanged();
	}

	/**
	 * Resets value of the command line.
	 */
	public void resetCommandLine() {
		this.commandLine.delete(0, this.commandLine.length());
		this.fireCommandLineValueChanged();
	}

	/**
	 * Deletes the last character of the command line.
	 */
	public void delLastCommandLineChar() {
		this.commandLine.delete(this.commandLine.length() - 1, this.commandLine.length());
		this.fireCommandLineValueChanged();
	}

	/**
	 * Returns the value of the command line.
	 * @return the value of the command line
	 */
	public String getCommandLineValue() {
		return this.commandLine.toString();
	}

	/**
	 * Process the value of the command line.
	 */
	public void processCommandLine() {
		List<String> commands = Arrays.asList(commandLine.toString().split(";"));
		HashMap<String, String> channelsValues = new HashMap<String, String>();

		for (String command : commands) {
			String[] commandSplit = command.split("@");
			if (commandSplit.length == 2) {
				channelsValues.put(commandSplit[0], commandSplit[1]);
			} else {
				logger.warning("bad syntax in command line");
				return;
			}
		}

		//TODO : ajouter la gestion des "," et des "-"

		for (Entry<String, String> channelValue : channelsValues.entrySet()) {
			int channel = Integer.parseInt(channelValue.getKey()) - 1;
			byte value = (byte) (Integer.parseInt(channelValue.getValue()));

			this.dmxArray[channel] = value;
		}

		this.sendDmxCommand();

		logger.info("Comand sent : " + commandLine.toString());
		this.resetCommandLine();
	}

	private void sendDmxCommand() {
		ArtDmxPacket artDmxPacket = new ArtDmxPacket();
		artDmxPacket.setUniverse(this.subnet, this.universe);
		artDmxPacket.setSequenceID(this.sequenceId  % 255);
		artDmxPacket.setDMX(this.dmxArray, this.dmxArray.length);
		this.artnetServer.broadcastPacket(artDmxPacket);

		this.sequenceId++;
		logger.info("broadcast DMX packet sent");
	}

	/**
	 * Starts the ArtNet Server.
	 */
	public void startArtNet() {
		try {
			this.artnetServer = new ArtNetServer((Integer) this.inPortSpinnerModel.getValue(), (Integer) this.outPortSpinnerModel.getValue());
			this.artnetServer.setBroadcastAddress("127.255.255.255");
			this.artnetServer.start();
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		logger.info("ArtNet Started");
		this.fireArtNetStarted();
	}

	/**
	 * Stops the ArtNet Server.
	 */
	public void stopArtNet() {
		this.artnetServer.stop();
		logger.info("ArtNet Stopped");
		this.fireArtNetStopped();
	}

	private void fireArtNetStarted() {
		for (RemoteModelListener listener : this.remoteModelChangedListeners) {
			ArtNetStartedEvent e = new ArtNetStartedEvent();
			listener.artNetStarted(e);
		}
	}

	private void fireArtNetStopped() {
		for (RemoteModelListener listener : this.remoteModelChangedListeners) {
			ArtNetStoppedEvent e = new ArtNetStoppedEvent();
			listener.artNetStopped(e);
		}
	}

	private void fireCommandLineValueChanged() {
		for (RemoteModelListener listener : this.remoteModelChangedListeners) {
			CommandLineValueChangedEvent e = new CommandLineValueChangedEvent(this.getCommandLineValue());
			listener.commandLineValueChanged(e);
		}
	}

	/**
	 * Returns the model of the logs list.
	 * @return the Logs list model
	 */
	public LogsListModel getLogsListModel() {
		return this.logsListModel;
	}

	/**
	 * Gets the model of the in port.
	 * @return the in port spinner model
	 */
	public SpinnerModel getInPortSpinnerModel() {
		return this.inPortSpinnerModel;
	}

	/**
	 * Gets the model of the out port.
	 * @return the out port spinner model
	 */
	public SpinnerModel getOutPortSpinnerModel() {
		return this.outPortSpinnerModel;
	}

	/**
	 * Gets the model of the broadcast address
	 * @return the broadcast address combo model
	 */
	public ComboBoxModel getBroadcastAddressComboModel() {
		return this.broadcastAddressComboModel;
	}
	
	/**
	 * Adds an element to the list of listener of the remote model.
	 * @param listener the listener to add
	 */
	public void addRemoteModelChangedListener(RemoteModelListener listener) {
		this.remoteModelChangedListeners.add(listener);
	}

	/**
	 * Removes an element to the list of listener of the remote model.
	 * @param listener the listener to remove
	 */
	public void removeRemoteModelChangedChangedListener(RemoteModelListener listener) {
		this.remoteModelChangedListeners.remove(listener);
	}
}

//example of command line parsing...
//
//for($i = 0;$i<count($chanArray);$i++){
//$chanArray[$i][0]=split(',',$chanArray[$i][0]);
//}
//for($i = 0;$i<count($chanArray);$i++){
//for($j = 0;$j<count($chanArray[$i][0]);$j++){
//	$chanArray[$i][0][$j]=split('-',$chanArray[$i][0][$j]);
//	if(count($chanArray[$i][0][$j])==1){
//		$command .= $chanArray[$i][0][$j][0].' '.$chanArray[$i][1].' ';
//	}
//	else{
//		for($k = $chanArray[$i][0][$j][0];$k<=$chanArray[$i][0][$j][1];$k++){
//			$command .= $k.' '.$chanArray[$i][1].' ';
//		}
//	}
//}
//}

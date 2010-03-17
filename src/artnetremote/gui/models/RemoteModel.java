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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import artnet4j.ArtNet;
import artnetremote.gui.events.ArtNetStartedEvent;
import artnetremote.gui.events.ArtNetStoppedEvent;
import artnetremote.gui.events.CommandLineValueChangedEvent;
import artnetremote.gui.events.HttpStartedEvent;
import artnetremote.gui.events.HttpStoppedEvent;
import artnetremote.gui.listeners.RemoteModelListener;
import artnetremote.server.ArtNetServerManager;
import artnetremote.server.BadSyntaxException;
import artnetremote.server.HttpServerManager;

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
	private SpinnerNumberModel universeSpinnerModel;
	private SpinnerNumberModel subnetSpinnerModel;
	private SpinnerNumberModel httpPortSpinnerModel;
	private ComboBoxModel broadcastAddressComboModel;

	private StringBuilder commandLine;
	private List<RemoteModelListener> remoteModelChangedListeners;

	private final static ArtNetServerManager artNetServerManager = ArtNetServerManager.getInstance();
	private final static HttpServerManager httpServerManager = HttpServerManager.getInstance();

	private static final int MIN_PORT = 0;
	private static final int MAX_PORT = 65535;
	private static final int MIN_SUBNET = 0;
	private static final int MAX_SUBNET = 15;
	private static final int MIN_UNIVERSE = 0;
	private static final int MAX_UNIVERSE = 15;
	private static final String[] BROADCAST_ADDRESSES = {"2.255.255.255","10.255.255.255","127.255.255.255"};
	
	private final transient Logger logger = Logger.getLogger(RemoteModel.class.getName());

	/**
	 * Default constructor of the remote model
	 */
	public RemoteModel() {
		this.logsListModel = new LogsListModel(ArtNet.logger);
		this.logsListModel.addLogger(logger);
		this.inPortSpinnerModel = new SpinnerNumberModel(ArtNetServerManager.DEFAULT_ARTNET_PORT, RemoteModel.MIN_PORT, RemoteModel.MAX_PORT, 1);
		this.outPortSpinnerModel = new SpinnerNumberModel(ArtNetServerManager.DEFAULT_ARTNET_PORT, RemoteModel.MIN_PORT, RemoteModel.MAX_PORT, 1);
		this.subnetSpinnerModel = new SpinnerNumberModel(0, RemoteModel.MIN_SUBNET, RemoteModel.MAX_SUBNET, 1);
		this.universeSpinnerModel = new SpinnerNumberModel(0, RemoteModel.MIN_UNIVERSE, RemoteModel.MAX_UNIVERSE, 1);
		this.httpPortSpinnerModel = new SpinnerNumberModel(HttpServerManager.DEFAULT_HTTP_PORT, RemoteModel.MIN_PORT, RemoteModel.MAX_PORT, 1);
		this.broadcastAddressComboModel = new DefaultComboBoxModel(RemoteModel.BROADCAST_ADDRESSES);
				
		this.commandLine = new StringBuilder();
		this.remoteModelChangedListeners = new ArrayList<RemoteModelListener>();
		
		this.initModelsListeners();
	}

	private void initModelsListeners(){
		this.broadcastAddressComboModel.addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {}
			
			@Override
			public void intervalAdded(ListDataEvent e) {}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				RemoteModel.artNetServerManager.setBroadcastAddress((String)broadcastAddressComboModel.getSelectedItem());
			}
		});
		
		this.inPortSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RemoteModel.artNetServerManager.setInPort((Integer)inPortSpinnerModel.getValue());
			}
		});
		
		this.outPortSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RemoteModel.artNetServerManager.setOutPort((Integer)outPortSpinnerModel.getValue());
			}
		});
		
		this.subnetSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RemoteModel.artNetServerManager.setSubnet((Integer)subnetSpinnerModel.getValue());
			}
		});
		
		this.universeSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RemoteModel.artNetServerManager.setUniverse((Integer)universeSpinnerModel.getValue());
			}
		});
		
		this.httpPortSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RemoteModel.httpServerManager.setInPort((Integer)httpPortSpinnerModel.getValue());
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
	 * process the command line and send a Dmx command over the network
	 */
	public void sendCommand() {
		try {
			RemoteModel.artNetServerManager.processCommandLine(this.commandLine.toString());
			logger.info("Command line : "+commandLine);
			RemoteModel.artNetServerManager.sendDmxCommand();
			logger.info("broadcast DMX packet sent");
		} catch (BadSyntaxException e) {
			logger.severe("Bad syntax in Command Line");
		}
		this.resetCommandLine();
	}

	/**
	 * Starts the ArtNet Server.
	 */
	public void startArtNet() {
		try {
			RemoteModel.artNetServerManager.startArtNet();
			logger.info("ArtNet Started");
			this.fireArtNetStarted();
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Stops the ArtNet Server.
	 */
	public void stopArtNet() {
		RemoteModel.artNetServerManager.stopArtNet();
		logger.info("ArtNet Stopped");
		this.fireArtNetStopped();
	}

	/**
	 * Starts the Http Server.
	 */
	public void startHttp() {
		try {
			RemoteModel.httpServerManager.startHttp();
			logger.info("Http Started");
			this.fireHttpStarted();
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Stops the Http Server.
	 */
	public void stopHttp() {
		RemoteModel.httpServerManager.stopHttp();
		logger.info("Http Stopped");
		this.fireHttpStopped();
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

	private void fireHttpStarted() {
		for (RemoteModelListener listener : this.remoteModelChangedListeners) {
			HttpStartedEvent e = new HttpStartedEvent();
			listener.httpStarted(e);
		}
	}

	private void fireHttpStopped() {
		for (RemoteModelListener listener : this.remoteModelChangedListeners) {
			HttpStoppedEvent e = new HttpStoppedEvent();
			listener.httpStopped(e);
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
	 * Gets the model of the subnet.
	 * @return the subnet spinner model
	 */
	public SpinnerModel getSubnetSpinnerModel() {
		return this.subnetSpinnerModel;
	}
	
	/**
	 * Gets the model of the universe.
	 * @return the universe spinner model
	 */
	public SpinnerModel getUniverseSpinnerModel() {
		return this.universeSpinnerModel;
	}

	/**
	 * Gets the model of the http port.
	 * @return the http port spinner model
	 */
	public SpinnerModel getHttpPortSpinnerModel() {
		return this.httpPortSpinnerModel;
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

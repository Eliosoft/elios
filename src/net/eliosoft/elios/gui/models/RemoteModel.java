/*
 * This file is part of Elios.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * Elios is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Elios is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Elios. If not, see <http://www.gnu.org/licenses/>.
 */

package net.eliosoft.elios.gui.models;

import java.io.IOException;
import java.net.SocketException;
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

import net.eliosoft.elios.gui.events.ArtNetStartedEvent;
import net.eliosoft.elios.gui.events.ArtNetStoppedEvent;
import net.eliosoft.elios.gui.events.CommandLineValueChangedEvent;
import net.eliosoft.elios.gui.events.HttpStartedEvent;
import net.eliosoft.elios.gui.events.HttpStoppedEvent;
import net.eliosoft.elios.gui.listeners.RemoteModelListener;
import net.eliosoft.elios.main.LoggersManager;
import net.eliosoft.elios.server.ArtNetServerManager;
import net.eliosoft.elios.server.BadSyntaxException;
import net.eliosoft.elios.server.Cue;
import net.eliosoft.elios.server.CuesManager;
import net.eliosoft.elios.server.HttpServerManager;
import net.eliosoft.elios.server.events.AdditiveModeValueChangedEvent;
import net.eliosoft.elios.server.events.SubnetValueChangedEvent;
import net.eliosoft.elios.server.events.UniverseValueChangedEvent;
import net.eliosoft.elios.server.listeners.ArtNetServerManagerListener;
import artnet4j.ArtNet;
import artnet4j.ArtNetException;

/**
 * This model describes almost all data of Elios.
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
	private boolean httpServerEnabled;
	private boolean additiveModeEnabled;
	
	private List<RemoteModelListener> remoteModelChangedListeners;

	private final ArtNetServerManager artNetServerManager;
	private final HttpServerManager httpServerManager;
	private final CuesManager cuesManager;
	private CuesListModel cuesListModel;

	private static final int MIN_PORT = 0;
	private static final int MAX_PORT = 65535;
	private static final int MIN_SUBNET = 0;
	private static final int MAX_SUBNET = 15;
	private static final int MIN_UNIVERSE = 0;
	private static final int MAX_UNIVERSE = 15;
	
	private final Logger logger = LoggersManager.getInstance().getLogger(RemoteModel.class.getCanonicalName());

	/**
	 * Broadcast Address.
	 *
	 * @author Alexandre COLLIGNON
	 */
	public enum BroadCastAddress {

	    /** Primary broadcast address. **/
	    PRIMARY("2.255.255.255"), 

	    /** Secondary broadcast address. **/
	    SECONDAY("10.255.255.255"), 

	    /** Local broadcast address. **/
	    LOCAL("127.255.255.255");

	    /** Address representation. **/
	    private String address;

	    /** Private constructor. **/
	    private BroadCastAddress(String address) {
	        this.address = address;
	    }

	    /**
	     * Returns the String representation of the address.
	     * 
	     * @return String representation of the address
	     */
	    public String getAddress() {
	        return address;
	    }

	    /**
	     * {@inheritDoc}
	     */
	    @Override
	    public String toString() {
	        return address;
	    }
	}

	/**
	 * Default constructor of the remote model
	 * @param serverManager the server manager used by the model
	 * @param httpManager the http manager used by the model
	 * @param cuesManager the cues manager used by the model
	 */
	public RemoteModel(ArtNetServerManager serverManager, HttpServerManager httpManager, CuesManager cuesManager) {
		this.artNetServerManager = serverManager;
		this.httpServerManager = httpManager;
		this.cuesManager = cuesManager;
		this.logsListModel = new LogsListModel();
		this.cuesListModel = new CuesListModel(this.cuesManager);
		this.logsListModel.addLogger(ArtNet.logger);
		
		this.inPortSpinnerModel = new SpinnerNumberModel(
				ArtNetServerManager.DEFAULT_ARTNET_PORT, RemoteModel.MIN_PORT,
				RemoteModel.MAX_PORT, 1);
		this.outPortSpinnerModel = new SpinnerNumberModel(
				ArtNetServerManager.DEFAULT_ARTNET_PORT, RemoteModel.MIN_PORT,
				RemoteModel.MAX_PORT, 1);
		this.subnetSpinnerModel = new SpinnerNumberModel(0,
				RemoteModel.MIN_SUBNET, RemoteModel.MAX_SUBNET, 1);
		this.universeSpinnerModel = new SpinnerNumberModel(0,
				RemoteModel.MIN_UNIVERSE, RemoteModel.MAX_UNIVERSE, 1);
		this.httpPortSpinnerModel = new SpinnerNumberModel(
				HttpServerManager.DEFAULT_HTTP_PORT, RemoteModel.MIN_PORT,
				RemoteModel.MAX_PORT, 1);
		this.broadcastAddressComboModel = new DefaultComboBoxModel(
				BroadCastAddress.values());

		this.commandLine = new StringBuilder();
		this.remoteModelChangedListeners = new ArrayList<RemoteModelListener>();
		this.initModelsListeners();
	}

	private void initModelsListeners() {
		this.broadcastAddressComboModel
				.addListDataListener(new ListDataListener() {
					@Override
					public void intervalRemoved(ListDataEvent e) {
					}

					@Override
					public void intervalAdded(ListDataEvent e) {
					}

					@Override
					public void contentsChanged(ListDataEvent e) {
						artNetServerManager
								.setBroadcastAddress(((BroadCastAddress) broadcastAddressComboModel
										.getSelectedItem()).getAddress());
					}
				});

		this.subnetSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				artNetServerManager
						.setSubnet((Integer) subnetSpinnerModel.getValue());
			}
		});

		this.universeSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				artNetServerManager
						.setUniverse((Integer) universeSpinnerModel.getValue());
			}
		});

		this.httpPortSpinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				httpServerManager
						.setInPort((Integer) httpPortSpinnerModel.getValue());
			}
		});
		
		this.artNetServerManager.addArtNetServerManagerChangedListener(new ArtNetServerManagerListener() {
			
			@Override
			public void universeValueChanged(UniverseValueChangedEvent event) {
				universeSpinnerModel.setValue(event.getUniverse());
			}
			
			@Override
			public void subnetValueChanged(SubnetValueChangedEvent event) {
				subnetSpinnerModel.setValue(event.getSubnet());
			}
			
			@Override
			public void additiveModeValueChanged(AdditiveModeValueChangedEvent event) {
				additiveModeEnabled = event.isAdditiveModeEnabled();
				fireAdditiveModeValueChanged();
			}
		});
	}

	/**
	 * Add a character to the command line.
	 * 
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
		this.commandLine.delete(this.commandLine.length() - 1, this.commandLine
				.length());
		this.fireCommandLineValueChanged();
	}

	/**
	 * Returns the value of the command line.
	 * 
	 * @return the value of the command line
	 */
	public String getCommandLineValue() {
		return this.commandLine.toString();
	}

	/**
	 * process the command line and send a Dmx command over the network
	 * @throws BadSyntaxException if the command line to process and send has a bad syntax
	 */
	public void sendCommand() throws BadSyntaxException {
		artNetServerManager.processCommandLine(this.commandLine
				.toString());
		artNetServerManager.sendDmxCommand();
		this.resetCommandLine();
	}

	/**
	 * Starts the ArtNet Server.
	 * 
	 * @throws ArtNetException
	 *             if a server is already running
	 * @throws SocketException
	 *             if there is a problem with the server socket
	 */
	public void startArtNet() throws SocketException, ArtNetException {
		artNetServerManager.startArtNet();
		this.fireArtNetStarted();
	}

	/**
	 * Stops the ArtNet Server.
	 */
	public void stopArtNet() {
		artNetServerManager.stopArtNet();
		this.fireArtNetStopped();
	}

	/**
	 * Starts the Http Server.
	 * 
	 * @throws IOException
	 *             if the server is unable to start
	 */
	public void startHttp() throws IOException {
		httpServerManager.startHttp();
		this.fireHttpStarted();
	}

	/**
	 * Stops the Http Server.
	 */
	public void stopHttp() {
		httpServerManager.stopHttp();
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
			CommandLineValueChangedEvent e = new CommandLineValueChangedEvent(
					this.getCommandLineValue());
			listener.commandLineValueChanged(e);
		}
	}
	
	private void fireAdditiveModeValueChanged() {
		for (RemoteModelListener listener : this.remoteModelChangedListeners) {
			AdditiveModeValueChangedEvent e = new AdditiveModeValueChangedEvent(
					this.isAdditiveModeEnabled());
			listener.additiveModeValueChanged(e);
		}
	}

	/**
	 * Returns the model of the logs list.
	 * 
	 * @return the Logs list model
	 */
	public LogsListModel getLogsListModel() {
		return this.logsListModel;
	}

	/**
	 * Gets the model of the in port.
	 * 
	 * @return the in port spinner model
	 */
	public SpinnerModel getInPortSpinnerModel() {
		return this.inPortSpinnerModel;
	}

	/**
	 * Gets the model of the out port.
	 * 
	 * @return the out port spinner model
	 */
	public SpinnerModel getOutPortSpinnerModel() {
		return this.outPortSpinnerModel;
	}

	/**
	 * Gets the model of the subnet.
	 * 
	 * @return the subnet spinner model
	 */
	public SpinnerModel getSubnetSpinnerModel() {
		return this.subnetSpinnerModel;
	}

	/**
	 * Gets the model of the universe.
	 * 
	 * @return the universe spinner model
	 */
	public SpinnerModel getUniverseSpinnerModel() {
		return this.universeSpinnerModel;
	}

	/**
	 * Gets the model of the http port.
	 * 
	 * @return the http port spinner model
	 */
	public SpinnerModel getHttpPortSpinnerModel() {
		return this.httpPortSpinnerModel;
	}

	/**
	 * Gets the model of the broadcast address
	 * 
	 * @return the broadcast address combo model
	 */
	public ComboBoxModel getBroadcastAddressComboModel() {
		return this.broadcastAddressComboModel;
	}
	
	/**
	 * Give the status of the http server enabling
	 * 
	 * @return true if http server is enabled, false if disabled
	 */
	public boolean isHttpServerEnabled() {
		return this.httpServerEnabled;
	}

	/**
	 * Enable or disable the http server
	 * 
	 * @param httpServerEnabled
	 *            true to enable the http server, false to disable
	 */
	public void setHttpServerEnabled(boolean httpServerEnabled) {
		this.httpServerEnabled = httpServerEnabled;
	}

		/**
	 * Give the status of the additive mode enabling
	 * 
	 * @return true if additive mode is enabled, false if disabled
	 */
	public boolean isAdditiveModeEnabled() {
		return this.additiveModeEnabled;
	}

	/**
	 * Enable or disable the additive mode
	 * 
	 * @param additiveModeEnabled
	 *            true to enable the additive mode, false to disable
	 */
	public void setAdditiveModeEnabled(boolean additiveModeEnabled) {
		this.additiveModeEnabled = additiveModeEnabled;
		artNetServerManager.setAdditiveModeEnabled(this.additiveModeEnabled);
	}
	
	/**
	 * Adds an element to the list of listener of the remote model.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addRemoteModelChangedListener(RemoteModelListener listener) {
		this.remoteModelChangedListeners.add(listener);
	}

	/**
	 * Removes an element to the list of listener of the remote model.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeRemoteModelChangedListener(
			RemoteModelListener listener) {
		this.remoteModelChangedListeners.remove(listener);
	}

	/**
	 * Returns the input port.
	 * 
	 * @return the input port
	 */
	public int getInPort() {
	    return getSelectedIntValue(inPortSpinnerModel);
	}

	/**
     * Returns the output port.
     * 
     * @return the output port
     */
	public int getOutPort() {
        return getSelectedIntValue(outPortSpinnerModel);
    }

	/**
     * Returns the universe.
     * 
     * @return the universe
     */
	public int getUniverse() {
        return getSelectedIntValue(universeSpinnerModel);
    }

    /**
     * Returns the subnet.
     * 
     * @return the subnet
     */
	public int getSubnet() {
        return getSelectedIntValue(subnetSpinnerModel);
    }

    /**
     * Returns the Http port.
     * 
     * @return the Http port
     */
	public int getHttpPort() {
        return getSelectedIntValue(httpPortSpinnerModel);
    }

    /**
     * Returns the broadcast address.
     * 
     * @return the broadcast address
     */	
	public BroadCastAddress getBroadCastAddress() {
        return (BroadCastAddress) broadcastAddressComboModel.getSelectedItem();
    }

	/**
	 * Returns the selected value of the model casted to int (no check).
	 * 
	 * @param model the model
	 * @return the selected value of the model
	 */
	private int getSelectedIntValue(SpinnerModel model) {
	    return (Integer) model.getValue();
	}

	/**
     * Sets the input port.
     * 
     * @param inPort the input port
     */
    public void setInPort(int inPort) {
        inPortSpinnerModel.setValue(inPort);
    }

    /**
     * Sets the output port.
     * 
     * @param outPort the output port
     */
    public void setOutputPort(int outPort) {
        outPortSpinnerModel.setValue(outPort);
    }

    /**
     * Sets the universe.
     * 
     * @param universe the universe
     */
    public void setUniverse(int universe) {
        universeSpinnerModel.setValue(universe);
    }

    /**
     * Sets the subnet.
     * 
     * @param subnet the subnet
     */
    public void setSubnet(int subnet) {
        subnetSpinnerModel.setValue(subnet);
    }

    /**
     * Sets the Http port.
     * 
     * @param httpPort the Http port
     */
    public void setHttpPort(int httpPort) {
        httpPortSpinnerModel.setValue(httpPort);
    }

    /**
     * set the value of the broadcast address.
     * 
     * @param broadcastAddress the broadcast address
     */ 
    public void setBroadCastAddress(BroadCastAddress broadcastAddress){
        broadcastAddressComboModel.setSelectedItem(broadcastAddress);
    }

    /**
     * Restores the configuration according to the current configuration
     * of the {@link ArtNetServerManager}.
     */
	public void restoreArtNetServerManagerConfig() {
		inPortSpinnerModel.setValue(artNetServerManager.getInPort());
		outPortSpinnerModel.setValue(artNetServerManager.getOutPort());
	}

	/**
	 * Applies the configuration to the {@link ArtNetServerManager}.
	 * As a consequence, the server is restarted.
	 * @throws ArtNetException exception thrown if server can't be started.
	 */
	public void applyArtNetServerManagerConfig() throws ArtNetException {
		artNetServerManager.setInPort((Integer)inPortSpinnerModel.getValue());
		artNetServerManager.setOutPort((Integer)outPortSpinnerModel.getValue());
		this.stopArtNet();
		this.stopHttp();
		try {
			this.startArtNet();
			if(isHttpServerEnabled())
				try {
					this.startHttp();
				} catch (IOException e) {
					new ArtNetException(e.getMessage(), e.getCause());
				}
		} catch (SocketException e) {
			throw new ArtNetException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Gets the model of the cues list.
	 * 
	 * @return the cues list model
	 */
	public CuesListModel getCuesListModel() {
		return this.cuesListModel;
	}

	/**
	 * Stores the current state of the dmx array in a cue.
	 * @param cueName the name of the cue to store
	 */
	public void storeCue(String cueName) {
		cuesListModel.addCue(new Cue(cueName,artNetServerManager.getCurrentOutputDmxArray()));
	}


	/**
	 * Load the given cue.
	 * @param cue the cue to load
	 */
	public void loadCue(Cue cue) {;
		artNetServerManager.setCurrentOutputDmxArray(cue.getDmxArray());
		artNetServerManager.sendDmxCommand();
		logger.info("Cue [" + cue.getName() + "] sent");
	}
	
	/**
	 * Removes the given cue from the model.
	 * @param cue the cue to remove
	 */
	public void removeCue(Cue cue) {
		cuesListModel.removeCue(cue);
	}
}

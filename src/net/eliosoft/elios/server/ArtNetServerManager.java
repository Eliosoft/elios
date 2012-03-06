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

package net.eliosoft.elios.server;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.eliosoft.elios.main.LoggersManager;
import net.eliosoft.elios.server.events.AdditiveModeValueChangedEvent;
import net.eliosoft.elios.server.events.SubnetValueChangedEvent;
import net.eliosoft.elios.server.events.UniverseValueChangedEvent;
import net.eliosoft.elios.server.listeners.ArtNetServerManagerListener;
import artnet4j.ArtNetException;
import artnet4j.ArtNetServer;
import artnet4j.events.ArtNetServerListener;
import artnet4j.packets.ArtDmxPacket;
import artnet4j.packets.ArtNetPacket;

/**
 * The Manager of the Artnet Server.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class ArtNetServerManager {

    /**
     * The max number of DMX channels.
     */
    public static final int DMX_CHANNELS_COUNT = 512;

    private static ArtNetServerManager instance;

    /**
     * default value for ArtNet port.
     */
    public static final int DEFAULT_ARTNET_PORT = ArtNetServer.DEFAULT_PORT;

    private ArtNetServer artnetServer = null;
    private int inPort = ArtNetServerManager.DEFAULT_ARTNET_PORT;
    private int outPort = ArtNetServerManager.DEFAULT_ARTNET_PORT;
    private String broadcastAddress = ArtNetServer.DEFAULT_BROADCAST_IP;
    private int serverSubnet = 0;
    private int serverUniverse = 0;
    private int sequenceId = 0;
    private boolean additiveModeEnabled = false;

    private static final Pattern COMMANDLINE_PATTERN = Pattern
	    .compile(
		    "^(((\\d{1,3})(/(\\d{1,3}))?)([\\+\\-]((\\d{1,3})(/(\\d{1,3}))?))*)(@(F|(D?)(\\d{1,3}))){0,1}$",
		    Pattern.CASE_INSENSITIVE);
    private static final Pattern CHANNEL_PATTERN = Pattern.compile(
	    "([\\+\\-]?)(\\d{1,3})(/(\\d{1,3}))?", Pattern.CASE_INSENSITIVE);

    private static final int COMMAND_LINE_PATTERN_CHANNEL_GROUP = 1;
    private static final int COMMAND_LINE_PATTERN_AT_LEVEL_GROUP = 11;
    private static final int COMMAND_LINE_PATTERN_LEVEL_GROUP = 12;
    private static final int COMMAND_LINE_PATTERN_PERCENT_GROUP = 13;
    private static final int COMMAND_LINE_PATTERN_LEVEL_VALUE_GROUP = 14;

    private static final int CHANNEL_PATTERN_EXCEPT_GROUP = 1;
    private static final int CHANNEL_PATTERN_CHAN1_GROUP = 2;
    private static final int CHANNEL_PATTERN_CHAN2_GROUP = 4;

    private static final int MIN_CHANNEL_NUMBER = 1;
    private static final int MAX_CHANNEL_NUMBER = DMX_CHANNELS_COUNT;

    private static final int MAX_DMX_VALUE = 255;
    private static final int MAX_PERCENT_VALUE = 100;

    private static final int SUBNET_COUNT = 16;
    private static final int UNIVERSE_COUNT = 16;

    private byte[][][] outputDmxArrays = new byte[SUBNET_COUNT][UNIVERSE_COUNT][DMX_CHANNELS_COUNT];
    private byte[][][] inputDmxArrays = new byte[SUBNET_COUNT][UNIVERSE_COUNT][DMX_CHANNELS_COUNT];

    private final transient Logger logger = LoggersManager.getInstance()
	    .getLogger(ArtNetServerManager.class.getName());

    private List<ArtNetServerManagerListener> artnetServerManagerChangedListeners = new ArrayList<ArtNetServerManagerListener>();

    private ArtNetServerManager() {
    }

    /**
     * get the singleton instance of the ArtnetServerManager.
     * 
     * @return the instance
     */
    public static ArtNetServerManager getInstance() {
	if (ArtNetServerManager.instance == null) {
	    ArtNetServerManager.instance = new ArtNetServerManager();
	}
	return ArtNetServerManager.instance;
    }

    /**
     * send a Dmx Command containing the value of the Dmx array over the
     * network.
     */
    public void sendDmxCommand() {
	ArtDmxPacket artDmxPacket = new ArtDmxPacket();
	artDmxPacket.setUniverse(this.serverSubnet, this.serverUniverse);
	artDmxPacket.setSequenceID(this.sequenceId % 255);
	artDmxPacket.setDMX(getCurrentOutputDmxArray(), DMX_CHANNELS_COUNT);
	this.artnetServer.broadcastPacket(artDmxPacket);
	logger.info("broadcast DMX packet sent");

	this.sequenceId++;
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
	this.artnetServer = new ArtNetServer(this.inPort, this.outPort);
	this.artnetServer.setBroadcastAddress(this.broadcastAddress);
	this.artnetServer.start();
	initArtNetReceiver();

	logger.info("ArtNet Started (in:" + this.inPort + ", out:"
		+ this.outPort + ")");
    }

    /**
     * Stops the ArtNet Server.
     */
    public void stopArtNet() {
	if (this.artnetServer != null) {
	    this.artnetServer.stop();
	    this.artnetServer = null;
	    logger.info("ArtNet Stopped");
	}
    }

    /**
     * Process the value of the command line.
     * 
     * @param commandLine
     *            the command line to process
     * @throws BadSyntaxException
     *             thrown when the command line has a bad syntax
     */
    public void processCommandLine(final String commandLine)
	    throws BadSyntaxException {
	String[] commands = commandLine.split(";");
	HashMap<Integer, Byte> valuesToPush = new HashMap<Integer, Byte>();

	for (String command : commands) {
	    Matcher commandLineMatcher = COMMANDLINE_PATTERN.matcher(command);
	    if (commandLineMatcher.find()) {
		Matcher channelMatcher = CHANNEL_PATTERN
			.matcher(commandLineMatcher
				.group(COMMAND_LINE_PATTERN_CHANNEL_GROUP));
		HashSet<Integer> channels = new HashSet<Integer>();
		while (channelMatcher.find()) {
		    boolean except = (channelMatcher.group(
			    CHANNEL_PATTERN_EXCEPT_GROUP).compareTo("-") == 0);
		    int chan1 = Integer.parseInt(channelMatcher
			    .group(CHANNEL_PATTERN_CHAN1_GROUP));
		    checkChannelNumber(chan1);
		    int chan2 = chan1;
		    if (channelMatcher.group(CHANNEL_PATTERN_CHAN2_GROUP) != null) {
			chan2 = Integer.parseInt(channelMatcher
				.group(CHANNEL_PATTERN_CHAN2_GROUP));
			checkChannelNumber(chan2);
		    }
		    for (int j = Math.min(chan1, chan2); j <= Math.max(chan1,
			    chan2); j++) {
			if (except)
			    channels.remove(j);
			else
			    channels.add(j);
		    }
		}

		boolean hasLevel = !(commandLineMatcher
			.group(COMMAND_LINE_PATTERN_AT_LEVEL_GROUP) == null || commandLineMatcher
			.group(COMMAND_LINE_PATTERN_AT_LEVEL_GROUP).isEmpty());
		boolean percent = false;
		int value = MAX_DMX_VALUE;

		if (hasLevel) {
		    percent = (commandLineMatcher
			    .group(COMMAND_LINE_PATTERN_PERCENT_GROUP) == null || commandLineMatcher
			    .group(COMMAND_LINE_PATTERN_PERCENT_GROUP)
			    .isEmpty());
		    value = (commandLineMatcher
			    .group(COMMAND_LINE_PATTERN_LEVEL_GROUP)
			    .toUpperCase().compareTo("F") == 0) ? MAX_PERCENT_VALUE
			    : Integer
				    .parseInt(commandLineMatcher
					    .group(COMMAND_LINE_PATTERN_LEVEL_VALUE_GROUP));
		}

		if ((percent && value > MAX_PERCENT_VALUE)
			|| value > MAX_DMX_VALUE) {
		    throw new BadSyntaxException();
		}

		// adding values to HashMap of values to push

		byte dmxValue = (byte) value;
		if (percent) {
		    dmxValue = (byte) (value / 100.0 * 255);
		}
		for (int channel : channels) {
		    valuesToPush.put(channel - 1, dmxValue);
		}
	    } else {
		throw new BadSyntaxException();
	    }
	}
	pushValuesInCurrentOutputDmxArray(valuesToPush);
	logger.info("Command line parsed : " + commandLine);

    }

    private static void checkChannelNumber(final int channel)
	    throws BadSyntaxException {
	if (channel < MIN_CHANNEL_NUMBER || channel > MAX_CHANNEL_NUMBER) {
	    throw new BadSyntaxException();
	}
    }

    private void resetCurrentOutputDmxArray() {
	setCurrentOutputDmxArray(new byte[DMX_CHANNELS_COUNT]);
    }

    private void pushValuesInCurrentOutputDmxArray(
	    final HashMap<Integer, Byte> valuesMap) {
	if (!additiveModeEnabled) {
	    resetCurrentOutputDmxArray();
	}
	for (Entry<Integer, Byte> value : valuesMap.entrySet()) {
	    getCurrentOutputDmxArray()[value.getKey()] = value.getValue();
	}
    }

    private void initArtNetReceiver() {
	this.artnetServer.addListener(new ArtNetServerListener() {
	    @Override
	    public void artNetPacketReceived(final ArtNetPacket artNetPacket) {
		switch (artNetPacket.getType()) {
		case ART_OUTPUT:
		    ArtDmxPacket artDmxPacket = (ArtDmxPacket) artNetPacket;
		    int subnet = artDmxPacket.getSubnetID();
		    int universe = artDmxPacket.getUniverseID();
		    System.arraycopy(artDmxPacket.getDmxData(), 0,
			    inputDmxArrays[subnet][universe], 0,
			    artDmxPacket.getNumChannels());
		    break;

		default:
		    break;
		}
	    }

	    @Override
	    public void artNetServerStopped(final ArtNetServer artNetServer) {
	    }

	    @Override
	    public void artNetServerStarted(final ArtNetServer artNetServer) {
	    }

	    @Override
	    public void artNetPacketUnicasted(final ArtNetPacket artNetPacket) {
	    }

	    @Override
	    public void artNetPacketBroadcasted(final ArtNetPacket artNetPacket) {
	    }
	});

    }

    /**
     * set the broadcast address.
     * 
     * @param broadcastAddress
     */
    public void setBroadcastAddress(final String broadcastAddress) {
	this.broadcastAddress = broadcastAddress;
	if (this.artnetServer != null) {
	    this.artnetServer.setBroadcastAddress(broadcastAddress);
	}
    }

    /**
     * get the server output dmx array for a given subnet and universe.
     * 
     * @param subnet
     *            the subnet to select
     * @param universe
     *            the universe to select
     * @return the array
     */
    public byte[] getOutputDmxArray(final int subnet, final int universe) {
	return outputDmxArrays[subnet][universe];
    }

    /**
     * get the server current output dmx array.
     * 
     * @return the array
     */
    public byte[] getCurrentOutputDmxArray() {
	return getOutputDmxArray(this.serverSubnet, this.serverUniverse);
    }

    /**
     * set the server output dmx array for a given subnet and universe.
     * 
     * @param subnet
     *            the subnet to select
     * @param universe
     *            the universe to select
     * @param dmxArray
     *            the value of the dmx array
     */
    public void setOutputDmxArray(final int subnet, final int universe,
	    final byte[] dmxArray) {
	System.arraycopy(dmxArray, 0, outputDmxArrays[subnet][universe], 0,
		DMX_CHANNELS_COUNT);
    }

    /**
     * set the server current output dmx array.
     * 
     * @param dmxArray
     *            the value of the dmx array
     */
    public void setCurrentOutputDmxArray(final byte[] dmxArray) {
	setOutputDmxArray(this.serverSubnet, this.serverUniverse, dmxArray);
    }

    /**
     * get the server input dmx array for a given subnet and universe.
     * 
     * @param subnet
     *            the subnet to select
     * @param universe
     *            the universe to select
     * @return the array
     */
    public byte[] getInputDmxArray(final int subnet, final int universe) {
	return inputDmxArrays[subnet][universe];
    }

    /**
     * get the server input dmx array with current server subnet and universe
     * settings.
     * 
     * @return the array
     */
    public byte[] getCurrentInputDmxArray() {
	return getInputDmxArray(this.serverSubnet, this.serverUniverse);
    }

    /**
     * set the in port of the server.
     * 
     * @param inPort
     *            the value of the port
     */
    public void setInPort(final int inPort) {
	this.inPort = inPort;
    }

    /**
     * set the out port of the server.
     * 
     * @param outPort
     *            the value of the port
     */
    public void setOutPort(final int outPort) {
	this.outPort = outPort;
    }

    /**
     * set the dmx subnet.
     * 
     * @param subnet
     *            the value of the subnet
     */
    public void setSubnet(final int subnet) {
	this.serverSubnet = subnet;
	fireSubnetValueChanged();
    }

    /**
     * set the dmx universe.
     * 
     * @param universe
     *            the value of the universe
     */
    public void setUniverse(final int universe) {
	this.serverUniverse = universe;
	fireUniverseValueChanged();
    }

    /**
     * Enable or disable the additive mode.
     * 
     * @param additiveModeEnabled
     *            true to enable the additive mode, false to disable
     */
    public void setAdditiveModeEnabled(final boolean additiveModeEnabled) {
	this.additiveModeEnabled = additiveModeEnabled;
	fireAdditiveModeValueChanged();
    }

    /**
     * Returns the in port.
     * 
     * @return the in port
     */
    public int getInPort() {
	return this.inPort;
    }

    /**
     * Returns the out port.
     * 
     * @return the out port
     */
    public int getOutPort() {
	return this.outPort;
    }

    /**
     * Returns the dmx subnet.
     * 
     * @return the dmx subnet
     */
    public int getSubnet() {
	return this.serverSubnet;
    }

    /**
     * Returns the dmx universe.
     * 
     * @return the dmx universe
     */
    public int getUniverse() {
	return this.serverUniverse;
    }

    /**
     * Returns the status of the additive mode.
     * 
     * @return the status of additive mode
     */
    public boolean isAdditiveModeEnabled() {
	return this.additiveModeEnabled;
    }

    /**
     * Adds an element to the list of listener of the artnet server manager.
     * 
     * @param listener
     *            the listener to add
     */
    public void addArtNetServerManagerChangedListener(
	    final ArtNetServerManagerListener listener) {
	this.artnetServerManagerChangedListeners.add(listener);
    }

    /**
     * Removes an element to the list of listener of the artnet server manager.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeArtNetServerManagerChangedListener(
	    final ArtNetServerManagerListener listener) {
	this.artnetServerManagerChangedListeners.remove(listener);
    }

    private void fireSubnetValueChanged() {
	for (ArtNetServerManagerListener listener : this.artnetServerManagerChangedListeners) {
	    SubnetValueChangedEvent e = new SubnetValueChangedEvent(
		    this.serverSubnet);
	    listener.subnetValueChanged(e);
	}
    }

    private void fireUniverseValueChanged() {
	for (ArtNetServerManagerListener listener : this.artnetServerManagerChangedListeners) {
	    UniverseValueChangedEvent e = new UniverseValueChangedEvent(
		    this.serverUniverse);
	    listener.universeValueChanged(e);
	}
    }

    private void fireAdditiveModeValueChanged() {
	for (ArtNetServerManagerListener listener : this.artnetServerManagerChangedListeners) {
	    AdditiveModeValueChangedEvent e = new AdditiveModeValueChangedEvent(
		    this.additiveModeEnabled);
	    listener.additiveModeValueChanged(e);
	}
    }
}

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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.eliosoft.elios.main.LoggersManager;


import artnet4j.ArtNetException;
import artnet4j.ArtNetServer;
import artnet4j.packets.ArtDmxPacket;

/**
 * The Manager of the Artnet Server
 *
 * @author Jeremie GASTON-RAOUL
 */
public class ArtNetServerManager {
	private static ArtNetServerManager instance;
	
	/**
	 * default value for ArtNet port
	 */
	public static final int DEFAULT_ARTNET_PORT = ArtNetServer.DEFAULT_PORT;
	
	private ArtNetServer artnetServer = null;
	private int inPort = ArtNetServerManager.DEFAULT_ARTNET_PORT;
	private int outPort = ArtNetServerManager.DEFAULT_ARTNET_PORT;
	private String broadcastAddress = ArtNetServer.DEFAULT_BROADCAST_IP;
	private int subnet = 0;
	private int universe = 0;
	private int sequenceId = 0;
	private boolean additiveModeEnabled = false;
	
	private static final Pattern commandLinePattern = Pattern.compile("^(((\\d{1,3})(/(\\d{1,3}))?)([\\+\\-]((\\d{1,3})(/(\\d{1,3}))?))*)@(F|(D?)(\\d{1,3}))$",Pattern.CASE_INSENSITIVE);
	private static final Pattern channelPattern = Pattern.compile("([\\+\\-]?)(\\d{1,3})(/(\\d{1,3}))?",Pattern.CASE_INSENSITIVE);
	
	private static final int COMMAND_LINE_PATTERN_CHANNEL_GROUP = 1;
	private static final int COMMAND_LINE_PATTERN_LEVEL_GROUP = 11;
	private static final int COMMAND_LINE_PATTERN_PERCENT_GROUP = 12;
	private static final int COMMAND_LINE_PATTERN_LEVEL_VALUE_GROUP = 13;
	
	private static final int CHANNEL_PATTERN_EXCEPT_GROUP = 1;
	private static final int CHANNEL_PATTERN_CHAN1_GROUP = 2;
	private static final int CHANNEL_PATTERN_CHAN2_GROUP = 4;
	
	private static final int MIN_CHANNEL_NUMBER = 1;
	private static final int MAX_CHANNEL_NUMBER = 512;
	
	private static final int MAX_DMX_VALUE = 255;
	private static final int MAX_PERCENT_VALUE = 100;
	
	private byte[] dmxArray = new byte[512];
	
	private final transient Logger logger = LoggersManager.getInstance().getLogger(ArtNetServerManager.class
			.getName());
	
	private ArtNetServerManager(){}
	
	/**
	 * get the singleton instance of the ArtnetServerManager
	 * @return the instance
	 */
	public static ArtNetServerManager getInstance(){
		if(ArtNetServerManager.instance == null){
			ArtNetServerManager.instance = new ArtNetServerManager();
		}
		return ArtNetServerManager.instance;
	}
	
	/**
	 * send a Dmx Command containing the value of the Dmx array over the network
	 */
	public void sendDmxCommand() {
		ArtDmxPacket artDmxPacket = new ArtDmxPacket();
		artDmxPacket.setUniverse(this.subnet, this.universe);
		artDmxPacket.setSequenceID(this.sequenceId  % 255);
		artDmxPacket.setDMX(this.dmxArray, this.dmxArray.length);
		this.artnetServer.broadcastPacket(artDmxPacket);

		logger.info("broadcast DMX packet sent");

		this.sequenceId++;
	}
	
	/**
	 * Starts the ArtNet Server.
	 * @throws ArtNetException if a server is already running
	 * @throws SocketException if there is a problem with the server socket
	 */
	public void startArtNet() throws SocketException, ArtNetException {
		this.artnetServer = new ArtNetServer(this.inPort, this.outPort);
		this.artnetServer.setBroadcastAddress(this.broadcastAddress);
		this.artnetServer.start();
		logger.info("ArtNet Started");
	}

	/**
	 * Stops the ArtNet Server.
	 */
	public void stopArtNet() {
		if(this.artnetServer != null){
			this.artnetServer.stop();
			this.artnetServer = null;
			logger.info("ArtNet Stopped");
		}
	}
	
	/**
	 * Process the value of the command line.
	 * @param commandLine the command line to process
	 * @throws BadSyntaxException thrown when the command line has a bad syntax
	 */
	public void processCommandLine(String commandLine) throws BadSyntaxException {
		String[] commands = commandLine.split(";");
		for(String command : commands){
			Matcher commandLineMatcher = commandLinePattern.matcher(command);
			if (commandLineMatcher.find()) {
				Matcher channelMatcher = channelPattern.matcher(commandLineMatcher.group(COMMAND_LINE_PATTERN_CHANNEL_GROUP));
				HashSet<Integer> channels = new HashSet<Integer>();
				while (channelMatcher.find()) {
					boolean except = (channelMatcher.group(CHANNEL_PATTERN_EXCEPT_GROUP).compareTo("-") == 0);
					int chan1 = Integer.parseInt(channelMatcher.group(CHANNEL_PATTERN_CHAN1_GROUP));
					checkChannelNumber(chan1);
					int chan2 = chan1;
					if(channelMatcher.group(CHANNEL_PATTERN_CHAN2_GROUP) != null){
						chan2 = Integer.parseInt(channelMatcher.group(CHANNEL_PATTERN_CHAN2_GROUP));
						checkChannelNumber(chan2);
					}
					for(int j = Math.min(chan1, chan2) ; j <= Math.max(chan1, chan2) ; j++){
						if(except)
							channels.remove(j);
						else
							channels.add(j);
					}
				}
				
				boolean percent = (commandLineMatcher.group(COMMAND_LINE_PATTERN_PERCENT_GROUP) == null || commandLineMatcher.group(COMMAND_LINE_PATTERN_PERCENT_GROUP).isEmpty());
				int value = (commandLineMatcher.group(COMMAND_LINE_PATTERN_LEVEL_GROUP).toUpperCase().compareTo("F") == 0) ? MAX_PERCENT_VALUE : Integer.parseInt(commandLineMatcher.group(COMMAND_LINE_PATTERN_LEVEL_VALUE_GROUP));
				
				if((percent && value > MAX_PERCENT_VALUE) || value > MAX_DMX_VALUE)
					throw new BadSyntaxException();
				
				insertInDmxArray(new ArrayList<Integer>(channels), value, percent);
			}
			else{
				throw new BadSyntaxException();
			}
		}
		logger.info("Command line parsed : " + commandLine);

	}

	private static void checkChannelNumber(int channel) throws BadSyntaxException {
		if( channel < MIN_CHANNEL_NUMBER || channel > MAX_CHANNEL_NUMBER){
			throw new BadSyntaxException();
		}
	}

	private void insertInDmxArray(List<Integer> channels, int value, boolean percent){
		if(!additiveModeEnabled){
			dmxArray = new byte[512];
		}
		byte dmxValue = (byte)value;
		if(percent){
			dmxValue = (byte)(value/100.0*255);
		}
		for(int channel : channels){
			dmxArray[channel-1]=dmxValue;
		}
	}

	/**
	 * set the broadcast address
	 * @param broadcastAddress
	 */
	public void setBroadcastAddress(String broadcastAddress) {
		this.broadcastAddress = broadcastAddress;
		if(this.artnetServer != null){
			this.artnetServer.setBroadcastAddress(broadcastAddress);
		}
	}

	/**
	 * get the server dmxArray
	 * @return the array
	 */
	public byte[] getDmxArray() {
		return dmxArray;
	}

	/**
	 * set the in port of the server
	 * @param inPort the value of the port
	 */
	public void setInPort(int inPort) {
		this.inPort = inPort;
	}

	/**
	 * set the out port of the server
	 * @param outPort the value of the port
	 */
	public void setOutPort(int outPort) {
		this.outPort = outPort;
	}

	/**
	 * set the dmx subnet
	 * @param subnet the value of the subnet
	 */
	public void setSubnet(int subnet) {
		this.subnet = subnet;
	}

	/**
	 * set the dmx universe
	 * @param universe the value of the universe
	 */
	public void setUniverse(int universe) {
		this.universe = universe;
	}
	
	/**
	 * Enable or disable the additive mode
	 * 
	 * @param additiveModeEnabled
	 *            true to enable the additive mode, false to disable
	 */
	public void setAdditiveModeEnabled(boolean additiveModeEnabled) {
		this.additiveModeEnabled = additiveModeEnabled;
	}
	
}

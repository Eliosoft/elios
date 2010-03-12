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


package artnetremote.server;

import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import artnet4j.ArtNetException;
import artnet4j.ArtNetServer;
import artnet4j.packets.ArtDmxPacket;

/**
 * The Manager of the Artnet Server
 *
 * @author Jeremie GASTON-RAOUL
 */
public class ArtnetServerManager {
	private static ArtnetServerManager instance;
	
	private ArtNetServer artnetServer;
	private int inPort = ArtNetServer.DEFAULT_PORT;
	private int outPort = ArtNetServer.DEFAULT_PORT;
	private String broadcastAddress = ArtNetServer.DEFAULT_BROADCAST_IP;
	private int subnet = 0;
	private int universe = 0;
	private int sequenceId = 0;
	private byte[] dmxArray = new byte[512];
	
	private ArtnetServerManager(){
		this.artnetServer = new ArtNetServer();
	}
	
	/**
	 * get the singleton instance of the ArtnetServerManager
	 * @return the instance
	 */
	public static ArtnetServerManager getInstance(){
		if(ArtnetServerManager.instance == null){
			ArtnetServerManager.instance = new ArtnetServerManager();
		}
		return ArtnetServerManager.instance;
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
	}

	/**
	 * Stops the ArtNet Server.
	 */
	public void stopArtNet() {
		this.artnetServer.stop();
	}
	
	/**
	 * Process the value of the command line.
	 * @param commandLine the command line to process
	 */
	public void processCommandLine(String commandLine) {
		List<String> commands = Arrays.asList(commandLine.toString().split(";"));
		HashMap<String, String> channelsValues = new HashMap<String, String>();

		for (String command : commands) {
			String[] commandSplit = command.split("@");
			if (commandSplit.length == 2) {
				channelsValues.put(commandSplit[0], commandSplit[1]);
			} else {
				//TODO : throws exception
				//logger.warning("bad syntax in command line");
				return;
			}
		}

		//TODO : ajouter la gestion des "," et des "-"

		for (Entry<String, String> channelValue : channelsValues.entrySet()) {
			int channel = Integer.parseInt(channelValue.getKey()) - 1;
			byte value = (byte) (Integer.parseInt(channelValue.getValue()));

			this.dmxArray[channel] = value;
			System.out.println(channel +" "+value);
		}
	}

	/**
	 * @param broadcastAddress
	 */
	public void setBroadcastAddress(String broadcastAddress) {
		this.broadcastAddress = broadcastAddress;
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
	
}

package artnetremote.gui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

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
 * @author jeremie
 * @author Alexandre COLLIGNON
 */
public class RemoteModel {
	private LogsListModel logsListModel;
	private SpinnerNumberModel inPortSpinnerModel;
	private SpinnerNumberModel outPortSpinnerModel;
	private StringBuilder commandLine;
	private List<RemoteModelListener> remoteModelChangedListeners;
	private byte[] dmxArray;
	
	private ArtNetServer artnetServer;
	private int subnet;
	private int universe;
	private int sequenceId;

	private final transient Logger logger = Logger.getLogger(RemoteModel.class.getName());
	
	public RemoteModel() {
		logsListModel = new LogsListModel(ArtNet.logger);
		logsListModel.addLogger(logger);
		inPortSpinnerModel = new SpinnerNumberModel(ArtNetServer.DEFAULT_PORT,0,65535,1);
		outPortSpinnerModel = new SpinnerNumberModel(ArtNetServer.DEFAULT_PORT,0,65535,1);
		commandLine = new StringBuilder();
		remoteModelChangedListeners = new ArrayList<RemoteModelListener>();
		dmxArray = new byte[512];
		artnetServer = new ArtNetServer();
	}
	
	/**
	 * add a character to the command line
	 * @param c the character added
	 */
	public void addToCommandLine(Character c) {
		this.commandLine.append(c);
		this.fireCommandLineValueChanged();
	}

	/**
	 * reset value of the command line
	 */
	public void resetCommandLine() {
		this.commandLine.delete(0, this.commandLine.length());
		this.fireCommandLineValueChanged();
	}
	
	/**
	 * delete the last character of the command line 
	 */
	public void delLastCommandLineChar() {
		this.commandLine.delete(this.commandLine.length()-1, this.commandLine.length());
		this.fireCommandLineValueChanged();
	}
	
	/**
	 * get the value of the command line
	 * @return the value of the command line
	 */
	public String getCommandLineValue(){
		return this.commandLine.toString();
	}
	
	/**
	 * process the value of the command line
	 */
	public void processCommandLine(){
		List<String> commands = Arrays.asList(commandLine.toString().split(";"));
		HashMap<String, String> channelsValues = new HashMap<String, String>();
		
		for(String command : commands){
			String[] commandSplit = command.split("@");
			if(commandSplit.length == 2){
				channelsValues.put(commandSplit[0], commandSplit[1]);
			}
			else{
				logger.warning("bad syntax in command line");
				return;
			}
		}
		
		//TODO : ajouter la gestion des "," et des "-"
		
		for(Entry<String, String> channelValue : channelsValues.entrySet()){
			int channel = Integer.parseInt(channelValue.getKey()) -1;
			byte value = (byte) (Integer.parseInt(channelValue.getValue()));
			
			this.dmxArray[channel]=value;
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
	 * start the ArtNet Server
	 */
	public void startArtNet(){
		try {
			this.artnetServer = new ArtNetServer((Integer)this.inPortSpinnerModel.getValue(),(Integer)this.outPortSpinnerModel.getValue());
			this.artnetServer.start();
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		logger.info("ArtNet Started");
		this.fireArtNetStarted();
	}
	
	/**
	 * stop the ArtNet Server 
	 */
	public void stopArtNet(){
		this.artnetServer.stop();
		logger.info("ArtNet Stopped");
		this.fireArtNetStopped();
	}

	private void fireArtNetStarted() {
		for(RemoteModelListener listener : this.remoteModelChangedListeners){
			ArtNetStartedEvent e = new ArtNetStartedEvent();
			listener.artNetStarted(e);
		}
	}

	private void fireArtNetStopped() {
		for(RemoteModelListener listener : this.remoteModelChangedListeners){
			ArtNetStoppedEvent e = new ArtNetStoppedEvent();
			listener.artNetStopped(e);
		}
	}

	private void fireCommandLineValueChanged() {
		for(RemoteModelListener listener : this.remoteModelChangedListeners){
			CommandLineValueChangedEvent e = new CommandLineValueChangedEvent(this.getCommandLineValue());
			listener.commandLineValueChanged(e);
		}
	}

	/**
	 * get the model of the logs list
	 * @return the Logs list model
	 */
	public LogsListModel getLogsListModel() {
		return this.logsListModel;
	}

	/**
	 * get the model of the in port
	 * @return the in port spinner model
	 */
	public SpinnerModel getInPortSpinnerModel() {
		return this.inPortSpinnerModel;
	}
	
	/**
	 * get the model of the out port
	 * @return the out port spinner model
	 */
	public SpinnerModel getOutPortSpinnerModel() {
		return this.outPortSpinnerModel;
	}
	
	/**
	 * add an element to the list of listener of the remote model
	 * @param listener the listener to add
	 */
	public void addRemoteModelChangedListener(RemoteModelListener listener) {
		this.remoteModelChangedListeners.add(listener);
	}
	
	/**
	 * remove an element to the list of listener of the remote model
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
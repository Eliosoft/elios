package artnetremote.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * @author jeremie
 * The model of the LogList
 */
public class LogsListModel extends AbstractListModel {

	private static final long serialVersionUID = -3782927318483496410L;

	private List<String> logs = new ArrayList<String>();
	
	/**
	 * add a Log Line in the list
	 * @param logLine the log line to add
	 */
	public void addLogLine(String logLine){
		logs.add(logLine);
		this.fireIntervalAdded(this, logs.size()-1, logs.size()-1);
	}
	
	/**
	 * remove all log lines of the list
	 */
	public void clearLogsList(){
		int lastSize = logs.size();
		if(lastSize>0){
			logs.clear();
			this.fireIntervalRemoved(this, 0, lastSize-1);			
		}
	}
	
	@Override
	public Object getElementAt(int index) {
		return logs.get(index);
	}

	@Override
	public int getSize() {
		return logs.size();
	}
	
}

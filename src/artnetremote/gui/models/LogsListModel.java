package artnetremote.gui.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public class LogsListModel extends AbstractListModel {

	private List<String> logs = new ArrayList<String>();
	
	public void addLogLine(String logLine){
		logs.add(logLine);
		this.fireIntervalAdded(this, logs.size()-1, logs.size()-1);
	}
	
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

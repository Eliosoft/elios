package artnetremote.gui.views;

import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import artnetremote.gui.models.RemoteModel;

/**
 * @author jeremie
 * The view of the Logs Line
 * This view prints the last line of logs in a textfield
 */
public class LogsLineView {

	private final RemoteModel remoteModel;
	
	private final JTextField logField = new JTextField();

	/**
	 * The constructor of the class
	 * @param remoteModel the model associated with this view
	 */
	public LogsLineView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		this.logField.setEditable(false);

		this.initRemoteModelListener();		
	}

	private void initRemoteModelListener(){
		this.remoteModel.getLogsListModel().addListDataListener(new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				logField.setText("");
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				logField.setText((String)remoteModel.getLogsListModel().getElementAt(e.getIndex1()));
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				logField.setText((String)remoteModel.getLogsListModel().getElementAt(e.getIndex1()));
			}
		});

	}
	
	/**
	 * gets the log field
	 * @return the log field of the view
	 */
	public JTextField getLogField() {
		return this.logField;
	}
	
}

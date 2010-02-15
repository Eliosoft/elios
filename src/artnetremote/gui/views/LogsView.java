package artnetremote.gui.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import artnetremote.gui.models.RemoteModel;

public class LogsView {

	private RemoteModel remoteModel;
	private JList logsList;
	
	private final GridBagLayout layout = new GridBagLayout();
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final JPanel logsPanel = new JPanel();
	private final JButton clearLogsButton;

	public LogsView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		
		this.logsPanel.setLayout(this.layout);
		
		this.logsList = new JList(remoteModel.getLogsListModel());
		this.logsList.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(final ListDataEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						logsList.ensureIndexIsVisible(e.getIndex0());
					}
				});
			}
			
			@Override
			public void intervalRemoved(ListDataEvent e) {}
			
			@Override
			public void contentsChanged(ListDataEvent e) {}
		});
		
		JScrollPane scrollPane = new JScrollPane(logsList);
		scrollPane.setMinimumSize(new Dimension(200, 100));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.constraints.fill = GridBagConstraints.BOTH;
		this.constraints.gridy = 0;
		this.logsPanel.add(scrollPane, this.constraints);
		
		this.clearLogsButton = new JButton("Clear Logs");
		this.constraints.fill = GridBagConstraints.NONE;
		this.constraints.gridy = 1;
		logsPanel.add(this.clearLogsButton, this.constraints);
	}

	public JPanel getLogsPanel() {
		return this.logsPanel;
	}
	
	public void addClearLogsButtonListener(ActionListener listener){
		this.clearLogsButton.addActionListener(listener);
	}
	
	public void removeClearLogsButtonListener(ActionListener listener){
		this.clearLogsButton.removeActionListener(listener);
	}
}

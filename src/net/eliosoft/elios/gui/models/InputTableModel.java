package net.eliosoft.elios.gui.models;

import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import net.eliosoft.elios.server.ArtNetServerManager;

/**
 * This is the model of the DMX input table
 * @author Jeremie GASTON-RAOUL
 */
public class InputTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -3171182606809834583L;
	
	private final ArtNetServerManager artNetServerManager;
	private static final int COLUMN_COUNT = 16;
	private static final int ROW_COUNT = 512/COLUMN_COUNT;
	private static final Integer[] ROW_HEADERS;
	
	private final SwingWorker<Void, byte[]> inputDmxArrayUpdater;

	static{
		ROW_HEADERS = new Integer[ROW_COUNT];
		for(int i=0 ; i<ROW_COUNT ; i++){
			ROW_HEADERS[i]=i*COLUMN_COUNT;
		}
	}
	
	/**
	 * Default constructor of the class
	 */
	public InputTableModel(ArtNetServerManager serverManager) {
		this.artNetServerManager = serverManager;
		inputDmxArrayUpdater = new SwingWorker<Void, byte[]>(){

			@Override
			protected Void doInBackground() throws Exception {
				while(true){
					fireTableDataChanged();
					Thread.sleep(40);
				}
			}
			
		};
		
		inputDmxArrayUpdater.execute();
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}
	
	@Override
	public String getColumnName(int column) {
		return Integer.toString(column+1);
	}
	
	@Override
	public int getRowCount() {
		return ROW_COUNT;
	}
	
	@Override
	public Integer getValueAt(int row, int column) {
		int index = column+row*COLUMN_COUNT;
		int intValue = ((Byte)artNetServerManager.getCurrentInputDmxArray()[index]).intValue();
		return intValue < 0 ? intValue + 256 : intValue;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Integer.class;
	}

	/**
	 * get the table row headers
	 * @return an integer array representing the row headers
	 */
	public Integer[] getRowHeaders() {
		return ROW_HEADERS;
	}

	public void dispose() {
		inputDmxArrayUpdater.cancel(true);
	}

}

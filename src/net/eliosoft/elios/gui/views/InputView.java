package net.eliosoft.elios.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.eliosoft.elios.gui.models.InputTableModel;
import net.eliosoft.elios.gui.models.RemoteModel;

/**
 * The view of ArtNet input. This class display DMX value braodcasted on the network in a table
 * @author Jeremie GASTON-RAOUL
 */
public class InputView implements ViewInterface {
	
	private final RemoteModel remoteModel;
	private final InputTableModel inputTableModel = new InputTableModel();
	private final JPanel inputPanel = new JPanel(new BorderLayout());
	
	/**
	 * The constructor of the Input View.
	 * @param remoteModel the RemoteModel used by the view
	 */
	public InputView(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
		
		final JTable dmxTable = new JTable(this.inputTableModel);
		dmxTable.setEnabled(false);
		dmxTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dmxTable.getTableHeader().setReorderingAllowed(false);
		dmxTable.getTableHeader().setResizingAllowed(false);
		dmxTable.setGridColor(Color.LIGHT_GRAY);
				
		dmxTable.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 8556900685268227709L;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				int nonBlueLevel = 255-((Integer)value).intValue()*4/5;
				c.setBackground(new Color(nonBlueLevel, nonBlueLevel, 255));
				return c;
			}
		});
		
		int width = dmxTable.getFontMetrics(dmxTable.getFont()).stringWidth("255 ");
		Enumeration<TableColumn> columns = dmxTable.getColumnModel().getColumns();
		while(columns.hasMoreElements()){
			TableColumn column = columns.nextElement();
			column.setPreferredWidth(width);
		}
		
		JList dmxTableRowHeader = new JList(this.inputTableModel.getRowHeaders());
		dmxTableRowHeader.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = -6533501111809246770L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				return dmxTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(dmxTable, value, isSelected, cellHasFocus, index,0);
			}
		});

		JScrollPane scrollPane = new JScrollPane(dmxTable);
		scrollPane.setRowHeaderView(dmxTableRowHeader);
		scrollPane.setPreferredSize(new Dimension(200,200));
		this.inputPanel.add(scrollPane,BorderLayout.CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedTitle() {
	    return Messages.getString("inputview.title");
	}

	/**
	 * Returns the input panel of the view.
	 * @return the input panel
	 */
	@Override
	public JComponent getViewComponent() {
		return this.inputPanel;
	}
}

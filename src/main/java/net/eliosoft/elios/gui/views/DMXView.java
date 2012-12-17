package net.eliosoft.elios.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.eliosoft.elios.gui.models.DMXTableModel;
import net.eliosoft.elios.gui.models.RemoteModel;

/**
 * The view of DMX table. This class display DMX values in input or output of
 * the server
 *
 * @author Jeremie GASTON-RAOUL
 */
public class DMXView implements ViewInterface {

    private final DMXTableModel dmxTableModel;
    private final JPanel dmxPanel = new JPanel(new BorderLayout());
    private final int cellWidth = dmxPanel.getFontMetrics(dmxPanel.getFont())
            .stringWidth("255") * 2;
    private final int cellHeight = dmxPanel.getFontMetrics(dmxPanel.getFont())
            .getHeight() * 2;
    private final JRadioButton inRadio;
    private final JRadioButton outRadio;

    /**
     * The constructor of the DMX View.
     *
     * @param remoteModel
     *            the RemoteModel used by the view
     * @param tableModel
     *            the DMXTableModel used by the view
     */
    public DMXView(final RemoteModel remoteModel, final DMXTableModel tableModel) {
        this.dmxTableModel = tableModel;

        ButtonGroup inOutRadioGroup = new ButtonGroup();
        JPanel inOutRadioPanel = new JPanel();
        inRadio = new JRadioButton(Messages.getString("dmxview.inputradio"),
                false);
        inRadio.setActionCommand("input");
        outRadio = new JRadioButton(Messages.getString("dmxview.outputradio"),
                true);
        outRadio.setActionCommand("output");
        inOutRadioGroup.add(inRadio);
        inOutRadioGroup.add(outRadio);
        inOutRadioPanel.add(inRadio);
        inOutRadioPanel.add(outRadio);
        this.dmxPanel.add(inOutRadioPanel, BorderLayout.NORTH);

        final TableCellRenderer headerCellRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = -7628430686172752221L;

            @Override
            public Component getTableCellRendererComponent(final JTable table,
                    final Object value, final boolean isSelected,
                    final boolean hasFocus, final int row, final int column) {
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(getFont().deriveFont(getFont().getStyle() ^ Font.BOLD));
                if ((row == 0 && column == -1) || (row == -1 && column == 0)) {
                    setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                            Color.LIGHT_GRAY));
                } else if (row == -1) {
                    setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1,
                            Color.LIGHT_GRAY));
                } else if (column == -1) {
                    setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
                            Color.LIGHT_GRAY));
                }

                setBackground(new Color(230, 230, 230));
                return this;
            }
        };

        final JTable dmxTable = new JTable(this.dmxTableModel);
        dmxTable.setEnabled(false);
        dmxTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dmxTable.getTableHeader().setReorderingAllowed(false);
        dmxTable.getTableHeader().setResizingAllowed(false);
        dmxTable.setGridColor(Color.LIGHT_GRAY);
        dmxTable.getTableHeader().setPreferredSize(
                new Dimension(dmxTableModel.getColumnCount() * cellWidth,
                        cellHeight));
        dmxTable.setRowHeight(cellHeight);

        dmxTable.getTableHeader().setDefaultRenderer(headerCellRenderer);

        dmxTable.setDefaultRenderer(Integer.class,
                new DefaultTableCellRenderer() {
                    private static final long serialVersionUID = 8556900685268227709L;

                    @Override
                    public Component getTableCellRendererComponent(
                            final JTable table, final Object value,
                            final boolean isSelected, final boolean hasFocus,
                            final int row, final int column) {
                        super.getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
                        int nonBlueLevel = 255 - ((Integer) value).intValue() * 4 / 5;
                        setBackground(new Color(nonBlueLevel, nonBlueLevel, 255));
                        int channel = row * table.getColumnCount() + column + 1;
                        int percentValue = (int) Math.ceil(((Integer) value)
                                .intValue() * 100 / 255.0);
                        setToolTipText(MessageFormat.format(
                                Messages.getString("dmxview.tooltipmessage"),
                                channel, value, percentValue));
                        setHorizontalAlignment(SwingConstants.CENTER);
                        return this;
                    }
                });

        Enumeration<TableColumn> columns = dmxTable.getColumnModel()
                .getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            column.setPreferredWidth(cellWidth);
        }

        JList dmxTableRowHeader = new JList(this.dmxTableModel.getRowHeaders());
        dmxTableRowHeader.setFixedCellHeight(cellHeight);
        dmxTableRowHeader.setFixedCellWidth(cellWidth);
        dmxTableRowHeader.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -6533501111809246770L;

            @Override
            public Component getListCellRendererComponent(final JList list,
                    final Object value, final int index,
                    final boolean isSelected, final boolean cellHasFocus) {
                Component c = headerCellRenderer.getTableCellRendererComponent(
                        dmxTable, value, isSelected, cellHasFocus, index, -1);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(dmxTable);
        scrollPane.setRowHeaderView(dmxTableRowHeader);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        this.dmxPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Add an action listener to in and out radio buttons.
     *
     * @param actionListener
     *            the listener to add
     */
    public void addInOutRadioActionListener(final ActionListener actionListener) {
        inRadio.addActionListener(actionListener);
        outRadio.addActionListener(actionListener);
    }

    /**
     * Remove an action listener from in and out radion buttons.
     *
     * @param actionListener
     *            the listener to remove
     */
    public void removeInOutRadioActionListener(
            final ActionListener actionListener) {
        inRadio.removeActionListener(actionListener);
        outRadio.removeActionListener(actionListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedTitle() {
        return Messages.getString("dmxview.title");
    }

    /**
     * Returns the DMX panel of the view.
     *
     * @return the DMX panel
     */
    @Override
    public JComponent getViewComponent() {
        return this.dmxPanel;
    }
}

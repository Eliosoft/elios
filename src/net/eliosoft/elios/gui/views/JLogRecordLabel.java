package net.eliosoft.elios.gui.views;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;

/**
 * This class represent a custom label for a log record.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public class JLogRecordLabel extends JLabel {

    private static final long serialVersionUID = 1532542887313639565L;

    private final LogRecord record;

    /**
     * The default constructor.
     * 
     * @param record
     *            the log record of the label
     */
    public JLogRecordLabel(final LogRecord record) {
	super("[" + record.getLevel().getName() + "] " + record.getMessage());
	this.record = record;
	if (this.record.getLevel().equals(Level.SEVERE)) {
	    this.setForeground(Color.RED);
	}
    }

}

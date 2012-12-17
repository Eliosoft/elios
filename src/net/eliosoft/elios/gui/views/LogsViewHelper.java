package net.eliosoft.elios.gui.views;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;

/**
 * Helper class for building and decorate components which display log
 * information.
 *
 * @author acollign
 * @since Feb 2, 2011
 */
public final class LogsViewHelper {

    /**
     * Ensure that this class will not be instantiate.
     */
    private LogsViewHelper() {
    }

    /** default text for label. **/
    public static final String DEFAULT_TEXT = " ";

    /**
     * Creates a {@link JLabel} that displays {@link LogRecord} information.
     *
     * @param record
     *            the {@link LogRecord} to display
     * @return a decorated {@link JLabel} that displays the {@link LogRecord}
     *         information
     */
    public static JLabel createLogsLabel(final LogRecord record) {
        return LOG_DECORATOR.update(new JLabel(), record);
    }

    /**
     * A {@link LogLabelDecorator} aims to update a {@link JLabel} according to
     * a {@link LogRecord}.
     *
     * @author acollign
     * @since Feb 2, 2011
     */
    public interface LogLabelDecorator {

        /**
         * Update the given label according to the {@link LogRecord} text value
         * and states.
         *
         * @param label
         *            {@link JLabel} that must be decorated
         * @param record
         *            {@link LogRecord} that contains information to display
         * @return the given label
         */
        JLabel update(JLabel label, LogRecord record);
    }

    /**
     * Default log decorator.
     */
    public static final LogLabelDecorator LOG_DECORATOR = new LogLabelDecorator() {
        @Override
        public JLabel update(final JLabel label, final LogRecord record) {
            if (record == null) {
                label.setText(DEFAULT_TEXT);
                return label;
            }

            label.setText("[" + record.getLevel().getName() + "] "
                    + record.getMessage());

            if (record.getLevel().equals(Level.SEVERE)) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }

            return label;
        }
    };
}

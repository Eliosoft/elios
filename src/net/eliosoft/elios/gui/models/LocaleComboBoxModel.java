package net.eliosoft.elios.gui.models;

import java.util.Locale;

import javax.swing.DefaultComboBoxModel;

/**
 * {@link javax.swing.ComboBoxModel} that wraps available locales for UI translation.
 * 
 * @author acollign
 */
public class LocaleComboBoxModel extends DefaultComboBoxModel {

    /**
     * uid of serialization.
     */
    private static final long serialVersionUID = -1488500296982883031L;

    /**
     * Locales available for i18n.
     */
    private Locale[] locales = new Locale[] { Locale.ENGLISH, Locale.FRENCH, };

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getElementAt(int index) {
	return locales[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
	return locales.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectedItem(Object anObject) {
	super.setSelectedItem(anObject);
    }
}

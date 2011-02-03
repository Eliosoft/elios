package net.eliosoft.elios.gui.views;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * This class provides some useful static method for Cue views.
 *
 * @author acollign
 * @since Feb 3, 2011
 */
public class CuesViewHelper {

	/**
	 * Helper class can not be instantiate.
	 */
	private CuesViewHelper() {
		// nothing
	}

	/**
	 * Prompts a dialog that will be closed only if the given cue name is not an
	 * empty string after trim. The return string is always valid (not null and
	 * not empty).
	 *
	 * @param parent
	 *            the parent component of the dialog
	 * @return the cue name (not null and not empty)
	 */
	public static String askForCueName(final Component parent) {
		String cueName = "";
		while ("".equals(cueName.trim())) {
			// ask for a cue name as long as the String is empty
			cueName = (String) JOptionPane.showInputDialog(parent,
					Messages.getString("cuesview.getnamemessage"), null,
					JOptionPane.PLAIN_MESSAGE, null, null, null);
		}
		return cueName;
	}
}

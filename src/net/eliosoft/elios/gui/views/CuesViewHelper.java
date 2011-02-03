package net.eliosoft.elios.gui.views;

import java.awt.Component;

import javax.swing.JOptionPane;

import net.eliosoft.elios.gui.models.CuesListModel;

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
	 * empty string after trim. The null value is possible if the user cancels the operation or closes the dialog
	 * The input is filled with the default name given in argument.
	 *
	 *
	 * @param parent
	 *            the parent component of the dialog
	 * @return the cue name or null if the dialog has been closed or cancelled
	 */
	public static String askForCueName(final Component parent, String defaultName) {
		String cueName = "";
		while (cueName != null && "".equals(cueName.trim())) {
			// ask for a cue name as long as the String is empty
			cueName = (String) JOptionPane.showInputDialog(parent,
					Messages.getString("cuesview.getnamemessage"), null,
					JOptionPane.PLAIN_MESSAGE, null, null, defaultName);
		}
		return cueName;
	}

	/**
	 * Returns a Cue name according to the state of the model.
	 *
	 * @param model CuesListModel used to compute the name.
	 * @return a string that could be used as a cue name.
	 */
	public static String getNextDefaultCueName(CuesListModel model) {
		return "Cue#" + model.getSize();
	}
}

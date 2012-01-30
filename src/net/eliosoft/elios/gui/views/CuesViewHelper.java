package net.eliosoft.elios.gui.views;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

/**
 * This class provides some useful static method for Cue views.
 *
 * @author acollign
 * @since Feb 3, 2011
 */
public final class CuesViewHelper {

	/**
	 * Helper class can not be instantiate.
	 */
	private CuesViewHelper() {
		// nothing
	}

	/**
	 * Ask user for confirmation before to delete a cue.
	 * @param parent the parent component of the dialog
	 * @param cueName the name of the cue to delete
	 * @return true if confirmed, false in not
	 */
	public static boolean confirmCueRemove(final Component parent, String cueName){
		int option = JOptionPane.showConfirmDialog(parent,MessageFormat.format(Messages.getString("cuesview.confirmremovemessage"),cueName),null,JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
		if(option == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Prompts a dialog that will be closed only if the given cue name is not an
	 * empty string after trim. The null value is possible if the user cancels the operation or closes the dialog
	 * The input is filled with the default name given in argument.
	 * @param parent the parent component of the dialog
	 * @param defaultName the default name for the cue
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
	 * Prompts a dialog that print an error
	 * @param parent the parent component of the dialog
	 * @param exception the exception thrown
	 * @param cueName the name of the concerned cue
	 */
	public static void printError(final Component parent, Exception exception, String cueName) {
		String message;
		if(exception instanceof IllegalArgumentException){
			message = MessageFormat.format(Messages.getString("cuesview.cuenamealreadyusedmessage"),cueName);
		}
		else{
			message = exception.getLocalizedMessage();
		}
		JOptionPane.showMessageDialog(parent, message, null, JOptionPane.ERROR_MESSAGE);
	}
}

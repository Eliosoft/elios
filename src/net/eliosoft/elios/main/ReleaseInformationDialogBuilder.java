package net.eliosoft.elios.main;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.eliosoft.elios.gui.models.UpdateModel;
import net.eliosoft.elios.gui.views.Messages;
import net.eliosoft.elios.gui.views.UpdateFrequencyChooserView;
import net.eliosoft.elios.server.ReleaseCode;
import net.eliosoft.elios.server.ReleaseInformation;
import net.eliosoft.elios.server.ReleaseInformationRepository;

/**
 * A convenient builder that creates a {@link JDialog} which display the data of
 * a {@link ReleaseInformation}.
 * 
 * <code>new ReleaseInformationDialogBuilder(frame, repo).forReleaseCode(
				new ReleaseCode("0.2-dev")).build()</code>
 * 
 * @author E362200
 */
public class ReleaseInformationDialogBuilder {

    /**
     * A simple interface to apply the GOF {@link Builder} pattern.
     * 
     * @author E362200
     * 
     * @param <T>
     *            the object built by the implementation
     */
    public interface Builder<T> {

	/**
	 * Build an instance of T.
	 * 
	 * @return an instance of T
	 */
	T build();
    }

    /** parent {@link Frame}. **/
    private final Frame parent;

    /** identifier of the shown release. **/
    private ReleaseCode releaseCode;

    /** repository used to retrieve {@link ReleaseInformation}. **/
    private final ReleaseInformationRepository repository;

    /** update support. **/
    private final UpdateModel uSupport;

    /**
     * Creates a {@link ReleaseInformationDialogBuilder}. The parent
     * {@link Frame} will be used as a parent of the created {@link JDialog},
     * the {@link ReleaseInformationRepository} as a source of data.
     * 
     * @param parent
     *            the parent of the {@link JDialog} that will be created
     * @param repository
     *            the source of information
     * @param uSupport
     *            the {@link UpdateModel} instance
     */
    public ReleaseInformationDialogBuilder(final Frame parent,
	    final ReleaseInformationRepository repository,
	    final UpdateModel uSupport) {

	if (repository == null) {
	    throw new IllegalArgumentException(
		    "The ReleaseInformationRepository could not be null");
	}

	this.parent = parent;
	this.repository = repository;
	this.uSupport = uSupport;
    }

    /**
     * Build the {@link JDialog} of the release identified by the
     * {@link ReleaseCode}.
     * 
     * @param code
     *            the {@link ReleaseCode} of the release to display
     * @return the built dialog
     */
    private JDialog build(final ReleaseCode code) {

	// TODO loading must be done in a swing worker
	final ReleaseInformation ri = repository.getLatest();

	final JDialog dialog = new JDialog(parent,
		Messages.getString("updatedialog.title"));
	dialog.setSize(360, 360);

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	dialog.add(panel);

	Box box = createRevisionPanel(ri);
	panel.add(box, BorderLayout.NORTH);

	JComponent comp = createReleaseNoteAndUpdatePane(ri);

	panel.add(comp, BorderLayout.CENTER);

	Box buttonBox = createButtonPanel(ri, dialog);
	panel.add(buttonBox, BorderLayout.SOUTH);

	return dialog;
    }

    private JComponent createReleaseNoteAndUpdatePane(
	    final ReleaseInformation ri) {

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());

	URL url = ri.getReleaseNoteUrl();
	JEditorPane view = new JEditorPane();
	JScrollPane editorScrollPane = new JScrollPane(view);
	try {
	    view.setPage(url);
	} catch (IOException e) {
	    view.setText(Messages.getString("updatedialog.error.network", url));
	}
	panel.add(editorScrollPane, BorderLayout.CENTER);

	Box uBox = Box.createHorizontalBox();
	UpdateFrequencyChooserView updateFrequencyChooserView = new UpdateFrequencyChooserView(
		uSupport);
	uBox.add(new JLabel(updateFrequencyChooserView.getLocalizedTitle()));

	JComponent updateComponent = updateFrequencyChooserView
		.getViewComponent();
	uBox.add(updateComponent);

	panel.add(uBox, BorderLayout.SOUTH);
	return panel;
    }

    private Box createButtonPanel(final ReleaseInformation ri,
	    final JDialog dialog) {
	Box buttonBox = Box.createHorizontalBox();
	buttonBox.add(Box.createHorizontalGlue());
	JButton closeBtn = new JButton(Messages.getString("updatedialog.close"));

	closeBtn.setIcon(new ImageIcon(Elios.class
		.getResource("/net/eliosoft/elios/gui/views/dialog-close.png")));

	closeBtn.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		dialog.dispose();
		uSupport.markAsChecked();
	    }
	});
	buttonBox.add(closeBtn);

	if (Desktop.isDesktopSupported()) {
	    final Desktop desktop = Desktop.getDesktop();
	    JButton openInBrowser = new JButton(
		    Messages.getString("updatedialog.openwebbrowser"));

	    openInBrowser
		    .setIcon(new ImageIcon(
			    Elios.class
				    .getResource("/net/eliosoft/elios/gui/views/internet-web-browser.png")));

	    openInBrowser.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
		    try {
			desktop.browse(ri.getDownloadUrl().toURI());
		    } catch (IOException e1) {
			JOptionPane.showMessageDialog(
				dialog,
				Messages.getString(
					"updatedialog.error.cannotopenbrowser.message",
					e1.getLocalizedMessage()),
				Messages.getString("updatedialog.error.cannotopenbrowser.title"),
				JOptionPane.ERROR_MESSAGE);
			dialog.dispose();
		    } catch (URISyntaxException e1) {
			throw new AssertionError(e1.getMessage());
		    }
		}
	    });
	    buttonBox.add(openInBrowser);
	} else {
	    buttonBox.add(Box.createGlue());
	}
	return buttonBox;
    }

    private Box createRevisionPanel(final ReleaseInformation ri) {
	JLabel currentReleaseLbl = new JLabel(Messages.getString(
		"updatedialog.release.current", repository
			.getInstalledReleaseCode().getCode()));
	JLabel newReleaseLbl = new JLabel(Messages.getString(
		"updatedialog.release.new", ri.getReleaseCode().getCode(),
		new SimpleDateFormat().format(new Date(ri.getReleaseTime()))));
	Box box = Box.createVerticalBox();

	box.add(currentReleaseLbl);
	box.add(newReleaseLbl);
	return box;
    }

    /**
     * Defines the release code that identified the {@link ReleaseInformation}
     * to display.
     * 
     * @param code
     *            the string representation of the release code that identified
     *            the {@link ReleaseInformation} to display
     * @return a {@link Builder} implementation that allow you to finally build
     *         the {@link JDialog}.
     */
    public Builder<JDialog> forReleaseCode(final String code) {
	return forReleaseCode(ReleaseCode.create(code));
    }

    /**
     * Defines the release code that identified the {@link ReleaseInformation}
     * to display.
     * 
     * @param code
     *            the {@link ReleaseCode} that identified the
     *            {@link ReleaseInformation} to display
     * @return a {@link Builder} implementation that allow you to finally build
     *         the {@link JDialog}.
     */
    public Builder<JDialog> forReleaseCode(final ReleaseCode code) {
	this.releaseCode = code;
	return new Builder<JDialog>() {
	    @Override
	    public JDialog build() {
		return ReleaseInformationDialogBuilder.this.build(releaseCode);
	    }
	};
    }
}

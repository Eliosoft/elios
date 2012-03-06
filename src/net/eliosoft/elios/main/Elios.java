/*
 * This file is part of Elios.
 *
 * Copyright 2010 Jeremie GASTON-RAOUL & Alexandre COLLIGNON
 *
 * Elios is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Elios is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Elios. If not, see <http://www.gnu.org/licenses/>.
 */

package net.eliosoft.elios.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.eliosoft.elios.gui.controllers.CuesController;
import net.eliosoft.elios.gui.controllers.DMXController;
import net.eliosoft.elios.gui.controllers.LogsController;
import net.eliosoft.elios.gui.controllers.PrefsController;
import net.eliosoft.elios.gui.controllers.RemoteController;
import net.eliosoft.elios.gui.models.DMXTableModel;
import net.eliosoft.elios.gui.models.LocaleComboBoxModel;
import net.eliosoft.elios.gui.models.RemoteModel;
import net.eliosoft.elios.gui.models.RemoteModel.BroadCastAddress;
import net.eliosoft.elios.gui.models.UpdateModel;
import net.eliosoft.elios.gui.views.AboutView;
import net.eliosoft.elios.gui.views.CuesView;
import net.eliosoft.elios.gui.views.DMXView;
import net.eliosoft.elios.gui.views.LogsLineView;
import net.eliosoft.elios.gui.views.LogsView;
import net.eliosoft.elios.gui.views.Messages;
import net.eliosoft.elios.gui.views.PrefsView;
import net.eliosoft.elios.gui.views.RemoteView;
import net.eliosoft.elios.gui.views.ViewInterface;
import net.eliosoft.elios.main.ApplicationState.State;
import net.eliosoft.elios.server.ArtNetServerManager;
import net.eliosoft.elios.server.CuesManager;
import net.eliosoft.elios.server.HttpServerManager;
import net.eliosoft.elios.server.ReleaseCode;
import net.eliosoft.elios.server.ReleaseInformation;
import net.eliosoft.elios.server.ReleaseInformationRepository;
import net.eliosoft.elios.server.ReleaseInformationRepositoryImpl;
import artnet4j.ArtNetException;

/**
 * Main Class of Elios Software contains the main method used to launch the
 * application.
 * 
 * @author Jeremie GASTON-RAOUL
 */
public final class Elios {

    /** Folder in which data are stored. **/
    private static final String ELIOS_DATA_FOLDER = System
	    .getProperty("user.home") + File.separator + ".elios";

    /** Filename of the file used for cues list persistence. **/
    private static final String CUESLIST_FILENAME = ELIOS_DATA_FOLDER
	    + File.separator + "elios.cues";

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggersManager.getInstance()
	    .getLogger(Elios.class.getName());

    /**
     * Icons array for the Elios application.
     */
    private static final Image[] ICONS = new Image[] {
	    new ImageIcon(
		    Elios.class
			    .getResource("/net/eliosoft/elios/gui/views/elios_e_24x24.png"))
		    .getImage(),
	    new ImageIcon(
		    Elios.class
			    .getResource("/net/eliosoft/elios/gui/views/elios_e_32x32.png"))
		    .getImage(),
	    new ImageIcon(
		    Elios.class
			    .getResource("/net/eliosoft/elios/gui/views/elios_e_48x48.png"))
		    .getImage(),
	    new ImageIcon(
		    Elios.class
			    .getResource("/net/eliosoft/elios/gui/views/elios_e_64x64.png"))
		    .getImage() };

    /**
     * Do nothing more than ensure that no object can be construct.
     */
    private Elios() {
	// nothing
    }

    /**
     * Launches Elios.
     * 
     * @param args
     *            command-line argument. Currently unused !
     */
    public static void main(final String[] args) {

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e1) {
	    LOGGER.info("Can not load system look and feel");
	}

	final Preferences prefs = Preferences.userNodeForPackage(Elios.class);

	Locale locale = loadLocale(prefs);

	try {
	    final ApplicationState state = new ApplicationState();

	    final RemoteModel remoteModel = createRemoteModel(prefs);

	    final RemoteView remoteView = new RemoteView(remoteModel);

	    UpdateModel uModel = new UpdateModel(prefs);
	    // used to make relation between view and model
	    new RemoteController(remoteModel, remoteView);

	    final LocaleComboBoxModel localeModel = new LocaleComboBoxModel();
	    localeModel.setSelectedItem(locale);
	    PrefsView prefsView = new PrefsView(remoteModel, localeModel,
		    uModel);
	    // used to make relation between view and model
	    new PrefsController(remoteModel, localeModel, prefsView);

	    LogsView logsView = new LogsView(remoteModel);
	    // used to make relation between view and model
	    new LogsController(remoteModel, logsView);

	    final ArtNetServerManager artNetServerManager = ArtNetServerManager
		    .getInstance();
	    final DMXTableModel dmxTableModel = new DMXTableModel(
		    artNetServerManager);
	    DMXView dmxView = new DMXView(remoteModel, dmxTableModel);
	    // used to make relation between view and model
	    new DMXController(dmxTableModel, dmxView);

	    CuesView cuesView = new CuesView(remoteModel);
	    // used to make relation between view and model
	    new CuesController(remoteModel, cuesView);

	    LogsLineView logsLineView = new LogsLineView(remoteModel);
	    AboutView aboutView = new AboutView();

	    for (Logger l : LoggersManager.getInstance().getLoggersList()) {
		remoteModel.getLogsListModel().addLogger(l);
	    }

	    final JFrame frame = new JFrame(Messages.getString("ui.title"));
	    frame.setIconImages(Arrays.<Image> asList(ICONS));
	    final JTabbedPane tabbedPane = new JTabbedPane();
	    Container contentPane = frame.getContentPane();
	    contentPane.setLayout(new BorderLayout());
	    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    frame.setIconImages(Arrays.asList(ICONS));

	    contentPane.add(
		    new ToolbarFactory(remoteModel, state).create(frame),
		    BorderLayout.NORTH);

	    contentPane.add(tabbedPane, BorderLayout.CENTER);
	    contentPane
		    .add(logsLineView.getViewComponent(), BorderLayout.SOUTH);
	    addViewToTab(tabbedPane, remoteView);
	    addViewToTab(tabbedPane, cuesView);
	    addViewToTab(tabbedPane, dmxView);
	    addViewToTab(tabbedPane, prefsView);
	    addViewToTab(tabbedPane, logsView);
	    addViewToTab(tabbedPane, aboutView);

	    tabbedPane.addChangeListener(new ChangeListener() {
		@Override
		public void stateChanged(final ChangeEvent e) {
		    tabbedPane.getSelectedComponent().requestFocusInWindow();
		}
	    });
	    tabbedPane.setSelectedIndex(0);

	    state.addListener(new ApplicationState.Listener() {

		@Override
		public void stateChanged(final State oldState,
			final State newState) {
		    try {
			if (State.SHUTTING_DOWN == newState) {
			    LOGGER.info("Application is shutting down");
			    persistRemoteModel(remoteModel, prefs);
			    persistLocale(prefs, localeModel);
			    dmxTableModel.dispose();
			    artNetServerManager.stopArtNet();
			    HttpServerManager.getInstance().stopHttp();
			}
		    } finally {
			System.exit(0);
		    }
		}
	    });

	    frame.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(final WindowEvent e) {
		    state.changeState(State.SHUTTING_DOWN);
		}
	    });

	    frame.pack();
	    frame.setVisible(true);

	    checkForUpdate(prefs, uModel, frame);

	    tabbedPane.getSelectedComponent().requestFocusInWindow();

	    // last call because we want to be sure that every listeners are
	    // registered
	    remoteModel.applyArtNetServerManagerConfig();
	} catch (ArtNetException e1) {
	    JOptionPane.showMessageDialog(null, MessageFormat.format(
		    Messages.getString("error.server.cannotstart.message"),
		    e1.getMessage()), Messages
		    .getString("error.server.cannotstart.title"),
		    JOptionPane.ERROR_MESSAGE);
	}
    }

    /**
     * Check update of Elios.
     * 
     * @param prefs
     *            the preferences
     * @param uModel
     *            the update model
     * @param frame
     *            the Elios frame
     */
    private static void checkForUpdate(final Preferences prefs,
	    final UpdateModel uModel, final JFrame frame) {

	new Thread() {

	    @Override
	    public void run() {
		try {
		    ReleaseInformationRepository riRepo = new ReleaseInformationRepositoryImpl(
			    prefs);
		    ReleaseInformationDialogBuilder ridBuilder = new ReleaseInformationDialogBuilder(
			    frame, riRepo, uModel);
		    if (uModel.updateIsNecessary()) {
			LOGGER.fine("Check for update");
			ReleaseInformation ri = riRepo.getLatest();
			if (ri != null) { // null if data could not be fetch
			    ReleaseCode currentRelease = riRepo
				    .getInstalledReleaseCode();
			    if (currentRelease.before(ri.getReleaseCode())) {
				ridBuilder.forReleaseCode(ri.getReleaseCode())
					.build().setVisible(true);
			    }
			} else {
			    LOGGER.fine("No release information found");
			}
		    }
		} catch (IllegalStateException ise) {
		    LOGGER.info("Error during update process, launch the post install process to fix context.");
		    // TODO post install process must be done during the
		    // install,
		    // here we consider that the user is still using the first
		    // public release.
		    PostInstallProcess.main(new String[] { "0.1",
			    "http://update.eliosoft.net/" });
		}
	    }
	}.start();
    }

    /**
     * Returns the {@link Locale} according to the {@link Preferences} given in
     * argument or the result of <code>Locale.getDefault()</code> if no
     * configuration as been already saved.
     * 
     * @param prefs
     *            {@link Preferences} node that contains the locale
     *            configuration
     * @return the {@link Locale} of the configuration or
     *         <code>Locale.getDefault()</code>
     */
    private static Locale loadLocale(final Preferences prefs) {
	String lang = prefs.get("ui.lang", Locale.getDefault().getLanguage());
	Locale locale = new Locale(lang);
	Locale.setDefault(locale);
	return locale;
    }

    /**
     * Persists the current {@link Locale} to the given {@link Preferences}.
     * 
     * @param prefs
     *            {@link Preferences} where saved the current {@link Locale}
     * @param localeModel
     *            the {@link LocaleComboBoxModel} instance that contains the
     *            configured {@link Locale}
     */
    private static void persistLocale(final Preferences prefs,
	    final LocaleComboBoxModel localeModel) {
	Locale l = (Locale) localeModel.getSelectedItem();
	prefs.put("ui.lang", l.getLanguage());
    }

    /**
     * Create a <code>RemoteModel</code> and retrieve the configuration from the
     * given <code>Preferences</code>. If any configuration is found for a
     * specific parameter, the default value is used according to
     * <code>RemoteModel</code>.
     * 
     * @param prefs
     *            <code>Preferences</code> used to retrieve the configuration
     * @return a configured <code>RemoteModel</code>
     */
    public static RemoteModel createRemoteModel(final Preferences prefs) {
	RemoteModel model = new RemoteModel(ArtNetServerManager.getInstance(),
		HttpServerManager.getInstance(), CuesManager.getInstance());
	model.setSubnet(prefs.getInt("server.subnet", 0));
	model.setUniverse(prefs.getInt("server.universe", 0));
	model.setBroadCastAddress(Enum.valueOf(
		BroadCastAddress.class,
		prefs.get("server.broadcast.address",
			BroadCastAddress.PRIMARY.name())));

	model.setInPort(prefs.getInt("server.inport",
		ArtNetServerManager.DEFAULT_ARTNET_PORT));
	model.setOutputPort(prefs.getInt("server.outport",
		ArtNetServerManager.DEFAULT_ARTNET_PORT));

	model.setHttpServerEnabled(prefs.getBoolean("server.httpserver.enable",
		false));
	model.setAdditiveModeEnabled(prefs.getBoolean(
		"server.additivemode.enable", false));
	model.setHttpPort(prefs.getInt("server.httpserver.port",
		HttpServerManager.DEFAULT_HTTP_PORT));

	try {
	    model.load(new FileInputStream(CUESLIST_FILENAME));
	} catch (IOException e) {
	    LOGGER.warning("Can not load cues " + e.getMessage());
	}

	return model;
    }

    /**
     * Persits a remote model to the given <code>Preferences</code>.
     * 
     * @param model
     *            the <code>RemoteModel</code> to store
     * @param prefs
     *            the <code>Preferences</code> used to persist
     */
    public static void persistRemoteModel(final RemoteModel model,
	    final Preferences prefs) {
	prefs.putInt("server.subnet", model.getSubnet());
	prefs.putInt("server.universe", model.getUniverse());
	prefs.put("server.broadcast.address", model.getBroadCastAddress()
		.name());

	prefs.putInt("server.inport", model.getInPort());
	prefs.putInt("server.outport", model.getOutPort());

	prefs.putBoolean("server.httpserver.enable",
		model.isHttpServerEnabled());
	prefs.putBoolean("server.additivemode.enable",
		model.isAdditiveModeEnabled());
	prefs.putInt("server.httpserver.port", model.getHttpPort());

	try {
	    File dir = new File(ELIOS_DATA_FOLDER);
	    if (!dir.isDirectory()) { // create root folder if needed
		dir.mkdir();
	    }

	    model.persist(new FileOutputStream(CUESLIST_FILENAME));
	} catch (IOException e) {
	    LOGGER.warning("Can not persist current cues : " + e.getMessage());
	}

    }

    /**
     * Adds a {@link ViewInterface} to a {@link JTabbedPane}. The localized
     * title of the {@link ViewInterface} is used to define the title of the
     * pane.
     * 
     * @param pane
     *            the {@link JTabbedPane}
     * @param v
     *            the {@link ViewInterface} to add
     */
    private static void addViewToTab(final JTabbedPane pane,
	    final ViewInterface v) {
	pane.addTab(v.getLocalizedTitle(), v.getViewComponent());
    }
}

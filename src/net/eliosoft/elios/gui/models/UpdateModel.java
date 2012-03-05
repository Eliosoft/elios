package net.eliosoft.elios.gui.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * A model that manage update frequency.
 * 
 * @author acollign
 */
public class UpdateModel {

    /**
     * Update frequency.
     * 
     * @author acollign
     */
    public enum Frequency {
	/**
	 * perform on every start up.
	 */
	ALWAYS(-1, "always"),
	/**
	 * perform once a day.
	 */
	DAILY(86400000, "daily"),
	/**
	 * perform once a week.
	 */
	WEEKLY(604800000, "weekly"),
	/**
	 * perform once a month.
	 */
	MONTHLY(2628000000L, "monthly"),
	/**
	 * never perform
	 */
	NEVER(-1, "never");

	private long value;
	private String key;

	private Frequency(long value, String key) {
	    this.value = value;
	    this.key = key;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 */
	public String getKey() {
	    return key;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 */
	public long getValue() {
	    return value;
	}
    }

    /**
     * Key used to save the update frequency value
     */
    public static final String UPDATE_FREQ_PREFS_KEY = "update.frequency";

    /**
     * Last update check preference key.
     */
    public static final String UPDATE_LATEST_CHECK_PREFS_KEY = "update.latest.check";

    private Preferences prefs;

    private ArrayList<UpdateListener> listeners;

    /**
     * Constructs an {@link UpdateModel} on top of the given {@link Preferences}
     * instance.
     * 
     * @param prefs
     *            a {@link Preferences} used to fetch and store update frequency
     *            configuration
     */
    public UpdateModel(Preferences prefs) {
	this.prefs = prefs;
	this.listeners = new ArrayList<UpdateModel.UpdateListener>();

	this.prefs.addPreferenceChangeListener(new PreferenceChangeListener() {
	    @Override
	    public void preferenceChange(PreferenceChangeEvent evt) {
		if (UPDATE_FREQ_PREFS_KEY.equals(evt.getKey())
			&& evt.getNewValue() != null) {

		    fireUpdateFrequencySaved(Frequency.valueOf(evt
			    .getNewValue()));
		}
	    }
	});
    }

    /**
     * Returns the current frequency.
     * 
     * @return the current frequency
     */
    public Frequency getFrequency() {
	return Frequency.valueOf(prefs.get(UPDATE_FREQ_PREFS_KEY,
		Frequency.ALWAYS.name()));
    }

    /**
     * Save the frequency.
     * 
     * @param freq
     *            frequency to save
     */
    public void saveUpdateFrequency(Frequency freq) {
	prefs.put(UPDATE_FREQ_PREFS_KEY, freq.name());
    }

    /**
     * Marks the version as checked.
     */
    public void markAsChecked() {
	prefs.putLong(UPDATE_LATEST_CHECK_PREFS_KEY, new Date().getTime());
    }

    /**
     * Returns true if the update info must be updated according to the
     * frequency, false otherwise.
     * 
     * @return true if the update info must be updated according to the
     *         frequency, false otherwise
     */
    public boolean updateIsNecessary() {
	Frequency frequency = getFrequency();
	if (frequency == Frequency.ALWAYS) {
	    return true;
	}
	if (frequency == Frequency.NEVER) {
	    return false;
	}
	long latest = prefs.getLong(UpdateModel.UPDATE_LATEST_CHECK_PREFS_KEY,
		Frequency.DAILY.getValue());

	return new Date(latest + frequency.value).before(new Date());
    }

    /**
     * Adds an {@link UpdateListener}.
     * 
     * @param l
     *            an {@link UpdateListener}
     */
    public void addUpdateListener(UpdateListener l) {
	listeners.add(l);
    }

    private void fireUpdateFrequencySaved(Frequency newValue) {
	for (UpdateListener l : listeners)
	    l.updateFrequencySaved(newValue);
    }

    /**
     * A listener of update frequency change.
     * 
     * @author acollign
     */
    public interface UpdateListener {
	/**
	 * Called when a new {@link Frequency} value has been saved
	 * 
	 * @param newValue
	 *            the new {@link Frequency} value
	 */
	void updateFrequencySaved(Frequency newValue);
    }
}

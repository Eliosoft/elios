package net.eliosoft.elios.gui.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class UpdateModel {

	public enum Frequency {
		ALWAYS(-1, "always"), DAILY(86400000, "daily"), WEEKLY(604800000,
				"weekly"), MONTHLY(2628000000L, "monthly"), NEVER(-1, "never");

		private long value;
		private String key;

		private Frequency(long value, String key) {
			this.value = value;
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public long getValue() {
			return value;
		}
	}

	/**
	 * Key used to save the update frequency value
	 */
	public static final String UPDATE_FREQ_PREFS_KEY = "update.frequency";

	public static final String UPDATE_LATEST_CHECK_PREFS_KEY = "update.latest.check";

	private Preferences prefs;

	private ArrayList<UpdateListener> listeners;

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

	public Frequency getFrequency() {
		return Frequency.valueOf(prefs.get(UPDATE_FREQ_PREFS_KEY,
				Frequency.ALWAYS.name()));
	}

	public void saveUpdateFrequency(Frequency freq) {
		prefs.put(UPDATE_FREQ_PREFS_KEY, freq.name());
	}

	public void markAsChecked() {
		prefs.putLong(UPDATE_LATEST_CHECK_PREFS_KEY, new Date().getTime());
	}

	public boolean updateIsNecessary() {
		Frequency frequency = getFrequency();
		if (frequency == Frequency.ALWAYS)
			return true;
		if (frequency == Frequency.NEVER)
			return false;
		long latest = prefs.getLong(UpdateModel.UPDATE_LATEST_CHECK_PREFS_KEY,
				Frequency.DAILY.getValue());

		return new Date(latest + frequency.value).before(new Date());
	}

	public void addUpdateListener(UpdateListener l) {
		listeners.add(l);
	}

	private void fireUpdateFrequencySaved(Frequency newValue) {
		for (UpdateListener l : listeners)
			l.updateFrequencySaved(newValue);
	}

	public interface UpdateListener {
		void updateFrequencySaved(Frequency newValue);
	}
}

package com.liberty.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.liberty.settings.util.Helpers;

public class Tools extends PreferenceActivity  {

	private static final String KEY_DSP_MANAGER = "dsp_manager";
	private static final String KEY_SPARE_PARTS = "spare_parts";
	private static final String KEY_TESTING_MENU = "testing_menu";
	private static final String KEY_DEV_TOOLS = "dev_tools";
	public static SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.tools);

		final PackageManager pm = getPackageManager();
		final PreferenceCategory pc = (PreferenceCategory) getPreferenceScreen().findPreference("general");

		final String keys[] = {KEY_DSP_MANAGER, KEY_SPARE_PARTS, KEY_TESTING_MENU, KEY_DEV_TOOLS};
		final String pkgNames[] = {"com.bel.android.dspmanager", "com.android.spare_parts", "com.android.settings", "com.android.development"};
		for (int i = 0; i < keys.length; i++) {
			final PreferenceScreen ps = (PreferenceScreen)findPreference(keys[i]);
			if (!Helpers.isPackageInstalled(pkgNames[i], pm)) {        	 
				pc.removePreference(ps);
			}
		}

	}

}
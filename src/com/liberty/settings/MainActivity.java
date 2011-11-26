package com.liberty.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.liberty.settings.util.CMDProcessor;
import com.liberty.settings.util.Helpers;

public class MainActivity extends PreferenceActivity implements OnPreferenceClickListener  {

	public static SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);

		final PreferenceCategory pc = (PreferenceCategory) getPreferenceScreen().findPreference("general");
		PreferenceScreen ps = (PreferenceScreen)findPreference("mod_version");
		String mod = new CMDProcessor().sh.runWaitFor("getprop ro.modversion").stdout;
		if (mod == null) mod = "Liberty ROM";
		ps.setSummary(mod);

		ps = (PreferenceScreen)findPreference("changelog");
		if (!Helpers.isPackageInstalled("com.liberty.customizerFree", getPackageManager())) {        	 
			pc.removePreference(ps);
		} else {
			ps.setOnPreferenceClickListener(this);
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals("changelog")) {
			final Intent intent = new Intent();
			intent.setClassName("com.liberty.customizerFree", "com.liberty.customizerFree.Changelog");
			startActivity(intent);
		}
		return false;
	}

}
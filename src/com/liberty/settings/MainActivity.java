package com.liberty.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.liberty.settings.util.CMDProcessor;
import com.liberty.settings.util.Helpers;

public class MainActivity extends PreferenceActivity implements OnPreferenceClickListener {
	
	public static SharedPreferences preferences;
	private static CMDProcessor mCmdProcessor;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	mCmdProcessor = new CMDProcessor();
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);
        
        final PackageManager pm = getPackageManager();
        final PreferenceCategory pc = (PreferenceCategory) getPreferenceScreen().findPreference("general");
        
        PreferenceScreen ps = (PreferenceScreen)findPreference("lockscreen");
        if (!Helpers.isPackageInstalled("org.cvpcs.android.gem_settings", pm)) {        	 
     		pc.removePreference(ps);
        } else {
        	ps.setOnPreferenceClickListener(this);
        }
        
        ps = (PreferenceScreen)findPreference("customizer");
        if (!Helpers.isPackageInstalled("com.liberty.customizer", pm)) {        	 
     		pc.removePreference(ps);
        }
    }

	@Override
	public boolean onPreferenceClick(final Preference preference) {
		final String key = preference.getKey();
		if (key.equals("lockscreen")) {
			// for some reason gem settings wouldn't work right unless I started the activity w/ root
			mCmdProcessor.su.runWaitFor(
					"am start -a android.intent.action.MAIN -n org.cvpcs.android.gem_settings/.activities.GEMSettings");
		}
		return false;
	}
	
	
}
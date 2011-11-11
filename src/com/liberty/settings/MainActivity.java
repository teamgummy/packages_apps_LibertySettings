package com.liberty.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.liberty.settings.util.CMDProcessor;

public class MainActivity extends PreferenceActivity  {
	
	public static SharedPreferences preferences;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);

        PreferenceScreen ps = (PreferenceScreen)findPreference("mod_version");
        String mod = new CMDProcessor().sh.runWaitFor("getprop ro.modversion").stdout;
        if (mod == null) mod = "Liberty ROM";
        ps.setSummary(mod);
        
    }

}
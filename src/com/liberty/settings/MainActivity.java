package com.liberty.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.liberty.settings.util.Helpers;

public class MainActivity extends PreferenceActivity  {
	
	public static SharedPreferences preferences;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);
        
        final PackageManager pm = getPackageManager();
        final PreferenceCategory pc = (PreferenceCategory) getPreferenceScreen().findPreference("general");

        PreferenceScreen ps = (PreferenceScreen)findPreference("dsp_manager");
        if (!Helpers.isPackageInstalled("com.bel.android.dspmanager", pm)) {        	 
     		pc.removePreference(ps);
        }
        
    }

}
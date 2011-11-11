package com.liberty.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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
        
        ps = (PreferenceScreen)findPreference("dsp_manager");
        if (!Helpers.isPackageInstalled("com.bel.android.dspmanager", pm)) {        	 
     		pc.removePreference(ps);
        }
        
        ((PreferenceScreen)findPreference("pulldown_text"))
        .setOnPreferenceClickListener(this);
        
    }

	@Override
	public boolean onPreferenceClick(final Preference preference) {
		final String key = preference.getKey();
		if (key.equals("lockscreen")) {
			// for some reason gem settings wouldn't work right unless I started the activity w/ root
			mCmdProcessor.su.runWaitFor(
					"am start -a android.intent.action.MAIN -n org.cvpcs.android.gem_settings/.activities.GEMSettings");
		} else if (key.equals("pulldown_text")) {

			final String current_text = getPulldownText();
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.edittext_dialog, null);
			final EditText pulldownText = (EditText) textEntryView.findViewById(R.id.EditTextDialogEntry);
			pulldownText.setText(current_text==null?"":current_text);
			
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("Pulldown Text")
			.setView(textEntryView)
			.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					final String newText = pulldownText.getText().toString();
					Helpers.getMount("rw");
					mCmdProcessor.su.runWaitFor("busybox sed -i 's|ro.pulldown.text=.*|ro.pulldown.text="+newText+"|' /system/build.prop");
					mCmdProcessor.su.runWaitFor("setprop ro.pulldown.text \"" + newText + "\"");
					Helpers.getMount("ro");
					new AlertDialog.Builder(MainActivity.this)
					.setTitle("Restart Status Bar")
					.setMessage("For changes to take affect the status bar needs to be restarted.\n\nRestart now?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {					
							mCmdProcessor.su.runWaitFor("busybox killall com.android.systemui");
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})
					.show();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
			.show();
		}
		return false;
	}
	
	private static String getPulldownText() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/system/build.prop"));
			String line;
			while(reader.ready()){
				line = reader.readLine();
				if (line.startsWith("ro.pulldown.text")) {
					return (line.substring(line.lastIndexOf("=")+1));
				}	
			}
		} catch (IOException e) { }
		
		return ( mCmdProcessor.su.runWaitFor("getprop ro.pulldown.text").stdout );
	}
	
	
	
}
package com.liberty.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Customizer extends PreferenceActivity implements OnPreferenceClickListener {

	public static SharedPreferences preferences;
	private PackageManager mPackageManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.customizer);

		mPackageManager = getPackageManager();
		final PreferenceCategory pc = (PreferenceCategory) getPreferenceScreen().findPreference("general");

		PreferenceScreen ps = (PreferenceScreen)findPreference("lockscreen");
		if (!Helpers.isPackageInstalled("com.liberty.parts", mPackageManager)) {        	 
			pc.removePreference(ps);
		}

		if (!Helpers.isPackageInstalled("com.liberty.customizerFree", mPackageManager)
				&& !Helpers.isPackageInstalled("com.liberty.customizer", mPackageManager)) {
			final String customizerKeys[] = {"customizer", "ui_status_bar", "theme_manager"};
			for (final String key : customizerKeys) {
				ps = (PreferenceScreen)findPreference(key);
				pc.removePreference(ps);
			}
		} else {
			ps = (PreferenceScreen)findPreference("theme_manager");
			ps.setOnPreferenceClickListener(this);
		}

		((PreferenceScreen)findPreference("pulldown_text"))
		.setOnPreferenceClickListener(this);

	}

	@Override
	public boolean onPreferenceClick(final Preference preference) {
		final String key = preference.getKey();
		if (key.equals("pulldown_text")) {

			final CMDProcessor cmd = new CMDProcessor();
			final String current_text = getPulldownText();
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.edittext_dialog, null);
			final EditText pulldownText = (EditText) textEntryView.findViewById(R.id.EditTextDialogEntry);
			pulldownText.setText(current_text==null?"":current_text);

			new AlertDialog.Builder(Customizer.this)
			.setTitle("Pulldown Text")
			.setView(textEntryView)
			.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					final String newText = pulldownText.getText().toString();
					Helpers.getMount("rw");
					cmd.su.runWaitFor("busybox sed -i 's|ro.pulldown.text=.*|ro.pulldown.text="+newText+"|' /system/build.prop");
					cmd.su.runWaitFor("setprop ro.pulldown.text \"" + newText + "\"");
					Helpers.getMount("ro");
					new AlertDialog.Builder(Customizer.this)
					.setTitle("Restart Status Bar")
					.setMessage("For changes to take affect the status bar needs to be restarted.\n\nRestart now?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {					
							cmd.su.runWaitFor("busybox killall com.android.systemui");
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
		} else if (key.equals("theme_manager")) {
			String pkgName = "com.liberty.customizerFree";
			if (Helpers.isPackageInstalled("com.liberty.customizer", mPackageManager)) {
				pkgName = "com.liberty.customizer";
			}
			final Intent intent = new Intent();
			intent.setClassName(pkgName, pkgName + ".ListThemes");
			startActivity(intent);
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

		return ( new CMDProcessor().su.runWaitFor("getprop ro.pulldown.text").stdout );
	}



}
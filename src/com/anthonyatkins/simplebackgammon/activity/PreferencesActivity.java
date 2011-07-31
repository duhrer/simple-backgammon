package com.anthonyatkins.simplebackgammon.activity;

import com.anthonyatkins.simplebackgammon.Constants;
import com.anthonyatkins.simplebackgammon.R;
import com.anthonyatkins.simplebackgammon.R.xml;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	public static final int EDIT_PREFERENCES = 999;
	public static final int RESULT_PREFS_CHANGED = 134;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


		// register for preference changes
		preferences.registerOnSharedPreferenceChangeListener(new PreferenceChangeListener(this));

		boolean fullScreen = preferences.getBoolean(Constants.FULL_SCREEN_PREF,false);
		if (fullScreen) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		setResult(RESULT_OK);
	}


	private class PreferenceChangeListener implements OnSharedPreferenceChangeListener {
		private Context context;

		public PreferenceChangeListener(Context context) {
			this.context = context;
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			setResult(RESULT_PREFS_CHANGED);
			
			Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
			if (Constants.FULL_SCREEN_PREF.equals(key)) {
				// This one will be taken care of when we reopen the activity,
				// so we're just displaying a confirmation
				String message = "";
				if (sharedPreferences.getBoolean(Constants.FULL_SCREEN_PREF,
						false))
					message = "Full screen enabled.";
				else
					message = "Full screen disabled";
				toast.setText(message);
			}

			toast.show();
		}

	}
}

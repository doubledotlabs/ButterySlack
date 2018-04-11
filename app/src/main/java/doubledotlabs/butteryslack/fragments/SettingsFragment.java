package doubledotlabs.butteryslack.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import doubledotlabs.butteryslack.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
  public SettingsFragment(){
  }
  
  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
      addPreferencesFromResource(R.xml.preferences);
      getActivity().setTitle("Settings");
  }
  
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    switch (key)
    {
    
    }
  }
}

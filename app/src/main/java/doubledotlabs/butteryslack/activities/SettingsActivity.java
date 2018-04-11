package doubledotlabs.butteryslack.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.app.AppCompatActivity;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
  public SettingsActivity(){
  }
  
  @Override
  public void onCreate(Bundle bundle) {
      super.onCreate(bundle);
      setContentView(R.layout.activity_settings);
  }
}

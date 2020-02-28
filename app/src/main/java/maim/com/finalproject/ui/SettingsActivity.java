package maim.com.finalproject.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.SeekBarPreference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int maxRange = sp.getInt("range_preference_seekbar",120);
        try{
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


            FirebaseAuth.getInstance().getCurrentUser().getUid();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("maxRange", String.valueOf(maxRange));
            ref.updateChildren(hashMap);
        }
        catch (NullPointerException e){
            Log.d("SA", "no user uid");
        }

        //Toast.makeText(this, "new max range: " + maxRange, Toast.LENGTH_SHORT).show();

        super.onBackPressed();
    }
}

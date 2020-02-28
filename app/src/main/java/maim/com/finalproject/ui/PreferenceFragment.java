package maim.com.finalproject.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import maim.com.finalproject.R;
import maim.com.finalproject.services.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class PreferenceFragment extends PreferenceFragmentCompat {
    CheckBoxPreference new_checkBoxPreference;
    CheckBoxPreference user_checkBoxPreference;
    CheckBoxPreference all_checkBoxPreference;
    NotificationManager notificationManager;
    SeekBarPreference age_seekBarPreference;
    SeekBarPreference range_seekBarPreference;
    DropDownPreference dropDownPreference;
    long totalTime;

    final int NOTIF_NEWS_ID = 2;
    final int NOTIF_WEATHER_ID = 3;

    public static final String CHANNEL_1_ID = "News";
    public static final String CHANNEL_2_ID = "Weather";

    //The settings will automatically be saved in the shared preferences.
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new_checkBoxPreference =  (CheckBoxPreference)getPreferenceManager().findPreference("new_preference_checkbox");
        user_checkBoxPreference =  (CheckBoxPreference)getPreferenceManager().findPreference("user_preference_checkbox");
        all_checkBoxPreference =  (CheckBoxPreference)getPreferenceManager().findPreference("all_preference_checkbox");
        age_seekBarPreference = getPreferenceManager().findPreference("age_preference_seekbar");
        range_seekBarPreference = getPreferenceManager().findPreference("range_preference_seekbar");
        dropDownPreference =  (DropDownPreference)getPreferenceManager().findPreference("dropdown_preference");

        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence channelName = "NewsChannel";
            CharSequence channelName2 = "WeatherChannel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel newsChannel = new NotificationChannel(CHANNEL_1_ID, channelName, importance);
            newsChannel.enableVibration(true);
            NotificationChannel weatherChannel = new NotificationChannel(CHANNEL_2_ID, channelName2, importance);
            notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(newsChannel);
            notificationManager.createNotificationChannel(weatherChannel);
        }

        new_checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (new_checkBoxPreference.isChecked()) {

                    //gets the time from dropDownPreference
                    String dropDownSelectedAnswer = dropDownPreference.getValue();
                    String[] splitted = dropDownSelectedAnswer.split(" ");
                    totalTime = Long.valueOf(splitted[0]) * 1000;

                    setAlarm("NEWS", totalTime, 0);

                    //Toast.makeText(getActivity(), getString(R.string.on_toast), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    cancelAlarm(0);
                    notificationManager.cancel(NOTIF_NEWS_ID);
                    //Toast.makeText(getActivity(), getString(R.string.off_toast), Toast.LENGTH_SHORT).show();
                    return true;
                }

            }
        });

        /*user_checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (user_checkBoxPreference.isChecked()) {

                    setAlarm("USER", 5000, 1);
                    Toast.makeText(getActivity(), getString(R.string.on_toast), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    cancelAlarm(1);
                    notificationManager.cancel(NOTIF_USERS_ID);
                    Toast.makeText(getActivity(), getString(R.string.off_toast), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });*/


        /*all_checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (all_checkBoxPreference.isChecked()) {

                    setAlarm("NEW", 5000, 0);
                    setAlarm("USER", 5000, 1);
                    Toast.makeText(getActivity(), getString(R.string.on_toast), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    cancelAlarm(0);
                    cancelAlarm(1);
                    notificationManager.cancel(NOTIF_NEW_ID);
                    notificationManager.cancel(NOTIF_USERS_ID);
                    Toast.makeText(getActivity(), getString(R.string.off_toast), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });*/

        age_seekBarPreference.setMin(18);
        age_seekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                age_seekBarPreference.setValue((Integer) newValue);
                return false;
            }
        });

        range_seekBarPreference.setMin(5);
        range_seekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                range_seekBarPreference.setValue((Integer) newValue);
                return false;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    /*public void newChannel() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID); // 1. building the notif
        builder.setSmallIcon(android.R.drawable.star_on);
        //RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.new_pending_intent); // 3.after attaching PendingIntent(2) ,inflate it and wrap it in RemoteViews (adding a custom view)

        Intent newsIntent = new Intent(getActivity(), MainActivity.class);
        newsIntent.putExtra("notif_txt", "author");
        PendingIntent newPendingIntent = PendingIntent.getActivity(getActivity(), 0, newsIntent, PendingIntent.FLAG_UPDATE_CURRENT); // 2.pending intent to wrap Intent then need to attach to RemoteView
        //remoteViews.setOnClickPendingIntent(R.string.app_name, newPendingIntent); // 4.attach to the View we made in the custom layout ,an event

        builder.setContentIntent(newPendingIntent);
        notificationManager.notify(NOTIF_NEW_ID, builder.build());

    }*/


    private void cancelAlarm(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(String type, long progress, int requestCode) {
        /*Log.d("PF", "Setting Alarm --");
        Log.d("PF", "progress : " + progress);
        Log.d("PF", "type : " + type);
        Log.d("PF", "triggerAtMillis : " + SystemClock.elapsedRealtime() + progress);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        alarmIntent.putExtra("progress", progress);
        alarmIntent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + (10 * 1000), pendingIntent);
*/
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("progress", progress);
        intent.putExtra("type", type);
        intent.putExtra("currentUserUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT);


        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + progress, pendingIntent);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + progress, pendingIntent);
        //Toast.makeText(getContext(), "Alarm set", Toast.LENGTH_LONG).show();
    }



}




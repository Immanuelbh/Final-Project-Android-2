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
    DropDownPreference dropDownPreference;
    long totalTime;

    final int NOTIF_NEW_ID = 2;
    final int NOTIF_USERS_ID = 3;

    public static final String CHANNEL_1_ID = "New";
    public static final String CHANNEL_2_ID = "Users";

    //The settings will automatically be saved in the shared preferences.
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new_checkBoxPreference =  getPreferenceManager().findPreference("new_preference_checkbox");
        user_checkBoxPreference =  getPreferenceManager().findPreference("user_preference_checkbox");
        all_checkBoxPreference =  getPreferenceManager().findPreference("all_preference_checkbox");
        age_seekBarPreference = getPreferenceManager().findPreference("age_preference_seekbar");
        dropDownPreference =  getPreferenceManager().findPreference("dropdown_preference");

        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence channelName = "NewChannel";
            CharSequence channelName2 = "UsersChannel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel newChannel = new NotificationChannel(CHANNEL_1_ID, channelName, importance);
            newChannel.enableVibration(true);
            NotificationChannel userChannel = new NotificationChannel(CHANNEL_2_ID, channelName2, importance);
            notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(newChannel);
            notificationManager.createNotificationChannel(userChannel);
        }

        new_checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (new_checkBoxPreference.isChecked()) {

                    //gets the time from dropDownPreference
                    String dropDownSelectedAnswer = dropDownPreference.getValue();
                    String[] splitted = dropDownSelectedAnswer.split(" ");
                    totalTime = Long.valueOf(splitted[0]) * 1000;

                    setAlarm("NEW", totalTime, 0);

                    Toast.makeText(getActivity(), getString(R.string.on_toast), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    cancelAlarm(0);
                    notificationManager.cancel(NOTIF_NEW_ID);
                    Toast.makeText(getActivity(), getString(R.string.off_toast), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });

        user_checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
        });


        all_checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
        });

        age_seekBarPreference.setMin(18);
        age_seekBarPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                age_seekBarPreference.getValue();
                Toast.makeText(getActivity(), String.valueOf(age_seekBarPreference.getValue()), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
}


    public void newsChannel() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID); // 1. building the notif
        builder.setSmallIcon(android.R.drawable.star_on);
        //RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.new_pending_intent); // 3.after attaching PendingIntent(2) ,inflate it and wrap it in RemoteViews (adding a custom view)

        Intent newsIntent = new Intent(getActivity(), MainActivity.class);
        newsIntent.putExtra("notif_txt", "author");
        PendingIntent newPendingIntent = PendingIntent.getActivity(getActivity(), 0, newsIntent, PendingIntent.FLAG_UPDATE_CURRENT); // 2.pending intent to wrap Intent then need to attach to RemoteView
        //remoteViews.setOnClickPendingIntent(R.string.app_name, newPendingIntent); // 4.attach to the View we made in the custom layout ,an event

        builder.setContentIntent(newPendingIntent);
        notificationManager.notify(NOTIF_NEW_ID, builder.build());

    }

    /*public void WeatherChannel() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID); // 1. building the notif
        builder.setSmallIcon(android.R.drawable.ic_menu_compass);
        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.weather_pending_intent); // 3.after attaching PendingIntent(2) ,inflate it and wrap it in RemoteViews (adding a custom view)

        Intent weatherIntent = new Intent(getActivity(), MainActivity.class);
        weatherIntent.putExtra("notif1_txt", "author");
        //Intent.ACTION_VIEW, Uri.parse(article.getUrl())

        PendingIntent newsPendingIntent = PendingIntent.getActivity(getActivity(), 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT); // 2.pending intent to wrap Intent then need to attach to RmoteView
        remoteViews.setOnClickPendingIntent(R.id.toppphead1, newsPendingIntent); // 4.attach to the View we made in the custom layout ,an event

        builder.setContent(remoteViews);
        notificationManager.notify(NOTIF_WEATHER_ID, builder.build());
    }*/

    private void cancelAlarm(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(String type, long progress, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        alarmIntent.putExtra("progress", progress);
        alarmIntent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + progress, pendingIntent);

    }

}




package maim.com.finalproject.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    final int NEARBY_USERS_TAG = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            long repeatingMillis = intent.getExtras().getLong("progress");
            String forecastType = intent.getExtras().getString("type");
            if (forecastType == null) return;

            //AlarmService
            Intent serviceIntent = new Intent(context, AlarmService.class);
            serviceIntent.putExtra("type", forecastType);
            ContextCompat.startForegroundService(context, serviceIntent);


            // Apply repeating intents
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NEARBY_USERS_TAG, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + repeatingMillis, pendingIntent);
        }
    }
}

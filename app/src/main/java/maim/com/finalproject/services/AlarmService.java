package maim.com.finalproject.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.example.ex2_2.api.ApiClient;
//import com.example.ex2_2.api.ApiInterface;
//import com.example.ex2_2.models.Article;
//import com.example.ex2_2.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.ui.MainActivity;


//import static com.example.ex2_2.NewsFragment.NEWS_API_KEY;

public class AlarmService extends IntentService {

    final String WEATHER_SERVICE_LINK = "http://api.openweathermap.org/data/2.5/weather?id=2172797&APPID=5bda833ea98063658162aac5ac577075&units=metric&";


    final int NEWS_TAG = 0;
    final int ALARM_SERVICE_ID = 1;

    String city;
    String desc;
    NotificationManager notificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AlarmService(String name) {
        super(name);
    }

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > 26)
            startMyOwnForeground();
        else {
            startForeground(ALARM_SERVICE_ID, new Notification());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {

            // Build notification channel
            final String CHANNEL_ID = "RecordChannelId";
            NotificationChannel mNotificationChannel = new NotificationChannel(CHANNEL_ID, "Record Channel", NotificationManager.IMPORTANCE_HIGH);
            mNotificationChannel.enableLights(true);
            mNotificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(mNotificationChannel);

            // Building the notification itself
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("מתבצעת הקלטה ברקע")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            Log.d("START---MY---OWN", "startMyOwnForeground");
            startForeground(2, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            String forecastType = intent.getExtras().getString("type");
            if (forecastType == null) return;

            //TODO Get the number of users nearby
            //fetchData(NEW_TAG);

        }
    }



    private void buildNotification(String desc, int forecastType, boolean error) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("----FOREGROUND........", "foreground service");
        String channelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "ChatMessageChannel";
            NotificationChannel channel = new NotificationChannel(channelId, "Chat Messages Update", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);

            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FromNotification", true);
        intent.putExtra("FromNotification_city", city);
        //final int INTENT_REQUEST_CODE = 5;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (error) {
            builder = new NotificationCompat.Builder(this, channelId)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.eat_icon)
                    .setAutoCancel(true)
                    .setContentTitle("Something went wrong!")
                    .setContentText("Too many requests maybe? Low the rate!")
                    .setSubText("API problem occurred.");
            if (desc.equals("LocationError")) {
                builder.setContentText("Location service doesn't function well.");
            }

        } else {    //if forecastType == NEWS_TAG
            builder = new NotificationCompat.Builder(this, channelId)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.sleep_icon)
                    .setContentTitle("NEWS UPDATE")
                    .setAutoCancel(true)
                    .setContentText(desc)
                    .setSubText("Want to hear some more?");
        }

        notificationManager.notify(forecastType, builder.build());
    }

}
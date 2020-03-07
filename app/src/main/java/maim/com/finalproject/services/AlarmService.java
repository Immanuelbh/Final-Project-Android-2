package maim.com.finalproject.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.example.ex2_2.api.ApiClient;
//import com.example.ex2_2.api.ApiInterface;
//import com.example.ex2_2.models.Article;
//import com.example.ex2_2.models.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import maim.com.finalproject.R;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.model.User;
import maim.com.finalproject.ui.MainActivity;


//import static com.example.ex2_2.NewsFragment.NEWS_API_KEY;

public class AlarmService extends IntentService {
    final static double EARTH_RADIUS = 6378.137;

    final int NEWS_TAG = 0;
    final int ALARM_SERVICE_ID = 1;

    private String city;
    private String desc;
    private NotificationManager notificationManager;
    private List<SubGenre> sgList = new ArrayList<>();
    private Context context;
    private User cUser;
    private double userRadius;
    private List<User> foundUsers = new ArrayList<>();
    private int count;
    private NotificationManager mNotificationManager;
    private PendingIntent pendingIntent2;


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
        this.context = getApplicationContext();
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
            if (forecastType.equals("NEWS")) {
                //buildNotification("title1", 0, false);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child("myLearnList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("AS", "learn list");
                        sgList.clear();
                        if(dataSnapshot.exists()){
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                SubGenre subGenre = ds.getValue(SubGenre.class);
                                if(subGenre != null){
                                    sgList.add(subGenre);
                                    Log.d("AR", "sgList: " + (sgList.size()-1));
                                }
                            }
                            createNotification();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            } /*else {
                buildNotification("title2", 1, false);
            }*/

        }
    }

    private void createNotification() {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //pendingIntent2 = PendingIntent.getActivity(context, 0, ii, 0);

        if(sgList != null) {
            int min = 0;
            int max = sgList.size() - 1;
            Log.d("AR", "sgList size : " + (sgList.size() - 1));
            if (max > 0) {
                Random rand = new Random();
                String sgName = sgList.get(rand.nextInt(max - min + 1) + min).getName();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

                int age_seekbar_sp = sp.getInt("age_preference_seekbar",120); //Age Seek-bar value from sp
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference currentUserInfo = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid);
                currentUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cUser = dataSnapshot.getValue(User.class);
                        try{
                            userRadius = Double.parseDouble(cUser.getMaxRange());
                            Log.d("AR", "User Radius : " + userRadius);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        foundUsers.clear();
                        if(dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                User user = snapshot.getValue(User.class);
                                try{
                                    if(!user.getUID().equals(currentUserUid)){

                                        if(user.getMySkillsList().containsKey(sgName.toLowerCase()) &&
                                                Integer.parseInt(user.getAge()) - age_seekbar_sp <= 0 &&
                                                haversine(user.getLocationLat(),user.getLocationLon()) <= userRadius){
                                            foundUsers.add(user);
                                        }
                                    }
                                }
                                catch (NullPointerException e){
                                    e.printStackTrace();
                                    if(user == null){
                                        Toast.makeText(context, "user is null", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(context, "Database is corrupted", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            //send notification
                            count = foundUsers.size();
                            if(count > 0){
                                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                                bigText.bigText("Found " + count + " users that can teach you " + sgName + "!");
                                bigText.setBigContentTitle("Meet new People");
                                bigText.setSummaryText("Click for more details");

                                ii.putExtra("skillToFind", sgName);
                                pendingIntent2 = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);


                                mBuilder.setContentIntent(pendingIntent2);
                                mBuilder.setSmallIcon(R.drawable.ic_deal);
                                mBuilder.setContentTitle("Your Title");
                                mBuilder.setContentText("Your text");
                                mBuilder.setPriority(Notification.PRIORITY_MAX);
                                mBuilder.setStyle(bigText);
                            }
                            else{
                                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                                bigText.bigText("Add a skill you would like to learn!");
                                bigText.setBigContentTitle("New learning possibilities");
                                bigText.setSummaryText("Click for more details");

                                pendingIntent2 = PendingIntent.getActivity(context, 0, ii, 0);


                                mBuilder.setContentIntent(pendingIntent2);
                                mBuilder.setSmallIcon(R.drawable.ic_deal);
                                mBuilder.setContentTitle("Your Title");
                                mBuilder.setContentText("Your text");
                                mBuilder.setPriority(Notification.PRIORITY_MAX);
                                mBuilder.setStyle(bigText);
                            }



                            mNotificationManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            // === Removed some obsoletes
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            {
                                String channelId = "Your_channel_id";
                                NotificationChannel channel = new NotificationChannel(
                                        channelId,
                                        "Channel human readable title",
                                        NotificationManager.IMPORTANCE_HIGH);
                                mNotificationManager.createNotificationChannel(channel);
                                mBuilder.setChannelId(channelId);
                            }

                            mNotificationManager.notify(0, mBuilder.build());

                            /*// Apply repeating intents

                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + repeatingMillis, pendingIntent);*/

                            //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatingMillis, pendingIntent);
                            //Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }




    }

    private double haversine(double locationLat, double locationLon) {
        if(cUser != null){
            double lat2 = cUser.getLocationLat();
            double long2 = cUser.getLocationLon();

            double lat = Math.toRadians(lat2 - locationLat);
            double lon = Math.toRadians(long2 - locationLon);
            locationLat = Math.toRadians(locationLat);
            lat2 = Math.toRadians(lat2);

            double a = Math.pow(Math.sin(lat / 2), 2)
                    + Math.pow(Math.sin(lon / 2), 2)
                    * Math.cos(locationLat) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));
            return EARTH_RADIUS * c;
        }

        Toast.makeText(context, "current user is null", Toast.LENGTH_SHORT).show();
        return 0;
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
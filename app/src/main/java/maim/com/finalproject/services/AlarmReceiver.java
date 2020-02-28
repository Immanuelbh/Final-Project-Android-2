package maim.com.finalproject.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import maim.com.finalproject.R;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.model.User;
import maim.com.finalproject.ui.MainActivity;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    final static double EARTH_RADIUS = 6378.137;
    final int NEWS_TAG = 0;
    final int WEATHER_TAG = 1;

    List<SubGenre> sgList = new ArrayList<>();
    List<User> foundUsers = new ArrayList<>();
    Context context;
    User cUser;
    double userRadius;
    long repeatingMillis;
    PendingIntent pendingIntent;
    String currentUserUid;
    private int count;
    NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("AR", "Alarm Receiver --");

        if (intent.getExtras() != null) {
            long repeatingMillis = intent.getExtras().getLong("progress");
            String forecastType = intent.getExtras().getString("type");
            if (forecastType == null) return;

            Intent serviceIntent = new Intent(context, AlarmService.class);
            serviceIntent.putExtra("type", forecastType);
            ContextCompat.startForegroundService(context, serviceIntent);

            int requestCode;
            if (forecastType.equals("NEWS")) {
                requestCode = NEWS_TAG;
            } else {
                requestCode = NEWS_TAG;
            }

            // Apply repeating intents
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + repeatingMillis, pendingIntent);
        }
        //sample db
/*
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child("myLearnList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            });*/

    }

    private void createNotification() {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, ii, 0);

        if(sgList != null) {
            int min = 0;
            int max = sgList.size() - 1;
            Log.d("AR", "sgList size : " + (sgList.size() - 1));
            if (max > 0) {
                Random rand = new Random();
                String sgName = sgList.get(rand.nextInt(max - min + 1) + min).getName();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

                int age_seekbar_sp = sp.getInt("age_preference_seekbar",120); //Age Seek-bar value from sp

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

                                mBuilder.setContentIntent(pendingIntent2);
                                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
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

                                mBuilder.setContentIntent(pendingIntent2);
                                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
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
}

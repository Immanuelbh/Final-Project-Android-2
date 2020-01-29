package maim.com.finalproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.GenreAdapter;
import maim.com.finalproject.adapters.MessageAdapter;
import maim.com.finalproject.model.Message;
import maim.com.finalproject.model.User;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbUsers;

    ValueEventListener seenListener;
    DatabaseReference seenDbReference;

    List<Message> messages = new ArrayList<>();
    MessageAdapter adapter;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;

    String myUid;
    String hisUid;
    String hisImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.chat_toolbar);
        profileIv = findViewById(R.id.chat_profile_iv);
        nameTv = findViewById(R.id.chat_name_tv);
        userStatusTv = findViewById(R.id.chat_status_tv);
        messageEt = findViewById(R.id.chat_message_et);
        sendBtn = findViewById(R.id.chat_send_btn);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        myUid = user.getUid();

        updateUserStatus("Online");

        //display chat messages
        recyclerView = findViewById(R.id.chat_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false); // items gravity sticks to top
        linearLayoutManager.setReverseLayout(false); // item list sorting (new messages start from the bottom)
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new MessageAdapter(this, messages);
        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        hisUid = intent.getStringExtra("user_uid");
        Log.d("CHAT_ACTIVITY", "receiver uid: " + hisUid);

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        //search user to get his information
        Query query = dbUsers.orderByChild("uid").equalTo(hisUid+"");
        Log.d("CHAT_ACTIVITY", "Query hisUid " + hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User hisUser = ds.getValue(User.class);
                    String name = hisUser.getName();
                    //String name = ds.child("name").getValue()+"";
                    Log.d("CHAT_ACTIVITY", "his user name: " + name);
                    //TODO image = hisUser.getImageUrl();

                    //check online status
                    String status = hisUser.getOnlineStatus();

                    nameTv.setText(name);
                    if(status.equals("Online"))
                        userStatusTv.setText(status);
                    else{
                        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                        calendar.setTimeInMillis(Long.parseLong(status));
                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                        userStatusTv.setText("Last seen at: " + dateTime);

                    }
                    //set image with glide


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get text from et
                String message = messageEt.getText().toString().trim();
                if(!TextUtils.isEmpty(message)){
                    sendMessage(message);
                }
                else{
                    Toast.makeText(ChatActivity.this, "Message is empty..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        
        readMessages();
        seenMessage();
        
    }

    private void updateUserStatus(String status) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update
        userRef.updateChildren(hashMap);
    }

    private void seenMessage() {
        seenDbReference = FirebaseDatabase.getInstance().getReference("chats");
        seenListener = seenDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    if (message.getReceiver().equals(hisUid) && message.getSender().equals(myUid) ||
                            message.getReceiver().equals(myUid) && message.getSender().equals(hisUid)) { //TODO create unique identifier to pull from
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {

        DatabaseReference dbMessages = FirebaseDatabase.getInstance().getReference("chats");
        dbMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    if(message.getReceiver().equals(hisUid) && message.getSender().equals(myUid)||
                            message.getReceiver().equals(myUid) && message.getSender().equals(hisUid)){ //TODO create unique identifier to pull from
                        messages.add(message);
                        //Log.d("CHAT_ACTIVITY", message.toString());
                    }
                }
                adapter.notifyDataSetChanged();
                 //TODO check if possible to snap recycler to bottom after each message
                if(recyclerView.getAdapter().getItemCount() > 0)
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
        dbMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    if(message.getReceiver().equals(hisUid) && message.getSender().equals(myUid)||
                            message.getReceiver().equals(myUid) && message.getSender().equals(hisUid)){ //TODO create unique identifier to pull from
                        messages.add(message);
                        Log.d("CHAT_ACTIVITY", message.toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */

    }

    private void sendMessage(String message) {
        DatabaseReference dbChats = FirebaseDatabase.getInstance().getReference("chats");

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("isSeen", false);

        dbChats.push().setValue(hashMap); //TODO have custom push value

        //reset message et
        messageEt.setText("");


    }

    @Override
    protected void onStart() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            Toast.makeText(this, "Cannot open chat - not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onStart();
        
    }

    @Override
    protected void onPause() {
        super.onPause();

        //set user status to time stamp
        String timeStamp = String.valueOf(System.currentTimeMillis());
        updateUserStatus(timeStamp);

        seenDbReference.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        updateUserStatus("Online");
        super.onResume();
    }
}

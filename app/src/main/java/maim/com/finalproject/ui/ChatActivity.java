package maim.com.finalproject.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.MessageAdapter;
import maim.com.finalproject.model.Confirmation;
import maim.com.finalproject.model.Message;
import maim.com.finalproject.model.User;
import maim.com.finalproject.notifications.APIService;
import maim.com.finalproject.notifications.Client;
import maim.com.finalproject.notifications.Data;
import maim.com.finalproject.notifications.Response;
import maim.com.finalproject.notifications.Sender;
import maim.com.finalproject.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    private final int ALARM_SERVICE_ID = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbUsers;

    private ValueEventListener seenListener;
    private DatabaseReference seenDbReference;

    private List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView profileIv;
    private TextView nameTv, userStatusTv;
    private EditText messageEt;
    private ImageButton sendBtn;

    private String myUid;
    private String hisUid;
    private String hisImage;
    //-------------------
    private APIService apiService;
    private boolean notify=false;
    //-------------------

    private DatabaseReference myDbRef;
    private DatabaseReference hisDbRef;
    private String stringChatKey;

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

        //get current user's id and update status
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

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        //get other user's id
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("user_uid");
        int chatKey = myUid.hashCode() + hisUid.hashCode();
        stringChatKey = String.valueOf(chatKey);

        String confirmationMsg = intent.getStringExtra("confirmation_msg");
        Confirmation myConfirmation = (Confirmation) intent.getSerializableExtra("confirmation_pojo");
        if(confirmationMsg != null){
            notify=true;
            createConfirmations(myConfirmation);
            sendMessage(confirmationMsg, "confirmation");
        }
        //Log.d("CHAT_ACTIVITY", "receiver uid: " + hisUid);

        //search user to get his information
        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        Query query = dbUsers.orderByChild("uid").equalTo(hisUid+"");
        Log.d("CHAT_ACTIVITY", "Query hisUid " + hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User hisUser = ds.getValue(User.class);
                    String name = hisUser.getName();
                    Log.d("CHAT_ACTIVITY", "his user name: " + name);

                    //TODO image = hisUser.getImageUrl();
                    /*
                    Glide.with(ChatActivity.this)
                            .load(hisUser.getImageUrl())
                            .thumbnail(0.01f)
                            .dontAnimate()
                            .error(R.drawable.ic_user) //change to default profile image
                            .into(profileIv);
*/
                    //check online status
                    String status = hisUser.getOnlineStatus();
                    String typingTo = hisUser.getTypingTo();

                    //set ui
                    nameTv.setText(name);

                    try{
                        if(typingTo.equals(myUid)){
                            userStatusTv.setText(getString(R.string.typing_tv));
                        }
                        else{
                            if(status.equals("Online"))
                                userStatusTv.setText(status);
                            else{
                                userStatusTv.setText(getString(R.string.last_seen_at_tv) + currentTime(status));

                            }
                        }
                    }
                    catch (NullPointerException e){
                        Toast.makeText(ChatActivity.this, getString(R.string.failed_to_update_ui_toast), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //handle sending message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-------------
                notify=true;
                //-------------

                // get text from et
                String message = messageEt.getText().toString().trim();
                if(!TextUtils.isEmpty(message)){
                    sendMessage(message, "regular");

                }
                else{
                    Toast.makeText(ChatActivity.this, getString(R.string.empty_msg_et), Toast.LENGTH_SHORT).show();
                }
                //----------------
                //reset message et after sending message
                messageEt.setText("");
                //----------------
            }
        });

        //listen to et
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0){
                    updateUserTypeTo("noOne");
                }
                else{
                    updateUserTypeTo(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //display messages
        readMessages();
        seenMessage();
        
    }


    private void createConfirmations(Confirmation myConfirmation) {
        DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        DatabaseReference hisUserRef = FirebaseDatabase.getInstance().getReference("users").child(hisUid);

        if(myConfirmation == null){
            //Toast.makeText(this, getString(R.string.confirmation_is_null_toast), Toast.LENGTH_SHORT).show();
        }

        //update confirmation lists
        myDbRef = myUserRef.child("myConfirmations").push();
        hisDbRef = hisUserRef.child("myConfirmations").push();

        myConfirmation.setSenderCid(myDbRef.getKey());
        myConfirmation.setReceiverCid(hisDbRef.getKey());
        myConfirmation.setCompleteStatus("incomplete");

        myDbRef.setValue(myConfirmation);
        hisDbRef.setValue(myConfirmation);


        //update chat lists
        //myDbRef = myUserRef.child("myChatList");
        //hisDbRef = hisUserRef.child("myChatList");

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("userUid", hisUid);
        myUserRef.child("myChatList").setValue(hashMap);

        hashMap.clear();
        hashMap.put("userUid", myUid);
        hisUserRef.child("myChatList").setValue(hashMap);


    }

    private String currentTime(String time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));
        return DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

    }

    private void updateUserStatus(String status) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        //update
        userRef.updateChildren(hashMap);
    }

    private void updateUserTypeTo(String typingTo) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typingTo);
        //update
        userRef.updateChildren(hashMap);
    }

    private void seenMessage() {
        seenDbReference = FirebaseDatabase.getInstance().getReference("chats").child(stringChatKey);
        seenListener = seenDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    if (message.getReceiver().equals(myUid) && message.getSender().equals(hisUid)) { //TODO create unique identifier to pull from
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
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

        DatabaseReference dbMessages = FirebaseDatabase.getInstance().getReference("chats").child(stringChatKey);
        dbMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    if(message.getReceiver().equals(hisUid) && message.getSender().equals(myUid)||
                            message.getReceiver().equals(myUid) && message.getSender().equals(hisUid)){ //TODO create unique identifier to pull from
                        messages.add(message);
                    }
                }
                adapter.notifyDataSetChanged();

                if(recyclerView.getAdapter().getItemCount() > 0)
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(final String message, String type) {
        DatabaseReference dbChats = FirebaseDatabase.getInstance().getReference("chats").child(stringChatKey);

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("seen", false);
        hashMap.put("type", type);
        if(type.equals("confirmation")){
            hashMap.put("senderCid", myDbRef.getKey());
            hashMap.put("receiverCid", hisDbRef.getKey());
            hashMap.put("completeStatus", "incomplete");
        }

        int chatKey = (myUid).hashCode() + (hisUid).hashCode();
        Log.d("CA", "ChatKEY: " + chatKey);
        dbChats.push().setValue(hashMap);
        //dbChats.push().setValue(hashMap); //TODO have custom push value

        //-----------------------------
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);

                if(notify){
                    senNotification(hisUid,user.getName(),message);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //-----------------------------
    }
    //-----------------------------
    private void senNotification(final String hisUid,final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);

                    Data data = new Data(myUid,message,getString(R.string.new_message_title) + " " + name,hisUid,R.drawable.ic_deal);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {

                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed_to_send_notification_toast), Toast.LENGTH_SHORT).show();
                                    t.printStackTrace();

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //-----------------------------
    @Override
    protected void onStart() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            Toast.makeText(this, getString(R.string.cannot_open_chat_not_logged_in_toast), Toast.LENGTH_SHORT).show();
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
        updateUserTypeTo("noOne");
        seenDbReference.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        updateUserStatus("Online");
        super.onResume();
    }
}

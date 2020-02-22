package maim.com.finalproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
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

    DatabaseReference myDbRef;
    DatabaseReference hisDbRef;

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

        //get other user's id
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("user_uid");
        String confirmationMsg = intent.getStringExtra("confirmation_msg");
        Confirmation myConfirmation = (Confirmation) intent.getSerializableExtra("confirmation_pojo");
        if(confirmationMsg != null){
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
                    Glide.with(ChatActivity.this)
                            .load(hisUser.getImageUrl())
                            .thumbnail(0.01f)
                            .dontAnimate()
                            .error(R.drawable.ic_user) //change to default profile image
                            .into(profileIv);

                    //check online status
                    String status = hisUser.getOnlineStatus();
                    String typingTo = hisUser.getTypingTo();

                    //set ui
                    nameTv.setText(name);

                    try{
                        if(typingTo.equals(myUid)){
                            userStatusTv.setText("Typing...");
                        }
                        else{
                            if(status.equals("Online"))
                                userStatusTv.setText(status);
                            else{
                                userStatusTv.setText("Last seen at: " + currentTime(status));

                            }
                        }
                    }
                    catch (NullPointerException e){
                        Toast.makeText(ChatActivity.this, "failed to update ui", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    //set image with glide


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
                // get text from et
                String message = messageEt.getText().toString().trim();
                if(!TextUtils.isEmpty(message)){
                    sendMessage(message, "regular");
                }
                else{
                    Toast.makeText(ChatActivity.this, "Message is empty..", Toast.LENGTH_SHORT).show();
                }
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
            Toast.makeText(this, "confirmation is null", Toast.LENGTH_SHORT).show();
        }

        myDbRef = myUserRef.child("myConfirmations").push();
        hisDbRef = hisUserRef.child("myConfirmations").push();

        myConfirmation.setSenderCid(myDbRef.getKey());
        myConfirmation.setReceiverCid(hisDbRef.getKey());

        myDbRef.setValue(myConfirmation);
        hisDbRef.setValue(myConfirmation);

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
        seenDbReference = FirebaseDatabase.getInstance().getReference("chats");
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

                if(recyclerView.getAdapter().getItemCount() > 0)
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String message, String type) {
        DatabaseReference dbChats = FirebaseDatabase.getInstance().getReference("chats");

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
        }

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
        updateUserTypeTo("noOne");
        seenDbReference.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        updateUserStatus("Online");
        super.onResume();
    }
}

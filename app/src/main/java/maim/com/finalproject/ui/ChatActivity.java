package maim.com.finalproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.HashMap;

import maim.com.finalproject.R;
import maim.com.finalproject.model.User;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbUsers;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;

    String myUid;
    String hisUid;


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
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("user_uid");

        dbUsers = firebaseDatabase.getInstance().getReference("users");
        //search user to get his information
        Query query = dbUsers.orderByChild("uid").equalTo(hisUid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User hisUser = ds.getValue(User.class);
                    String name = hisUser.getName();
                    //String image = hisUser.getImageUrl();

                    nameTv.setText(name);
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

    }

    private void sendMessage(String message) {
        DatabaseReference dbChats = FirebaseDatabase.getInstance().getReference("chats");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);

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
}

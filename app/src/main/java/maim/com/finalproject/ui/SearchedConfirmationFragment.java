package maim.com.finalproject.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.SignupGenreAdapter;
import maim.com.finalproject.adapters.SignupSubGenreAdapter;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.Confirmation;
import maim.com.finalproject.model.Message;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.model.User;

public class SearchedConfirmationFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser myUser;
    private User hisUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference users;
    private String senderUid, receiverUid, senderCid, receiverCid;

    private ImageView profileIv;
    private TextView nameTv, ageTv;
    private TextView dateTimeTv, locationTv;
    private View rootView;
    private RecyclerView skillRecyclerView;
    private RecyclerView learnRecyclerView;
    private List<SubGenre> mySkills = new ArrayList<>();
    private List<SubGenre> learnList = new ArrayList<>();
    private CharSequence skillWant;
    private Calendar calendar;
    private BottomNavigationView bottomNav;
    private Bundle bundle;
    private HashMap<String,SubGenre> mySkillsList;
    private HashMap<String, SubGenre> myLearnList;
    private Context context;

    public static SearchedConfirmationFragment newInstance(){

        return new SearchedConfirmationFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.searched_confirmation_fragment, container, false);


        //init firebase
        firebaseAuth= FirebaseAuth.getInstance();
        myUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");

        profileIv = rootView.findViewById(R.id.profileIv);
        nameTv = rootView.findViewById(R.id.profile_name_tv);
        ageTv = rootView.findViewById(R.id.profile_age_tv);
        locationTv = rootView.findViewById(R.id.confirmation_user_location);
        skillRecyclerView = rootView.findViewById(R.id.confirmation_myskills_recycler);
        learnRecyclerView = rootView.findViewById(R.id.confirmation_learn_recycler);
        ImageView scheduleTime = rootView.findViewById(R.id.confirmation_schedule_time);
        bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        //bottomNav.setVisibility(View.GONE);
        Button scheduleBtn = rootView.findViewById(R.id.confirmation_schedule_btn);

        //create simple adapter with radio button
        skillRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        skillRecyclerView.setHasFixedSize(true);

        learnRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        learnRecyclerView.setHasFixedSize(true);

        bundle = this.getArguments();
        if(bundle != null){

            if (bundle.containsKey("user")){

                hisUser = (User) bundle.getSerializable("user");
                skillWant = bundle.getCharSequence("skillWant");

                //update ui
                nameTv.setText(hisUser.getName());
                ageTv.setText(hisUser.getAge());
                locationTv.setText(hisUser.getLocationAddress());
                mySkillsList = hisUser.getMySkillsList();

                if(mySkillsList != null){

                    for (String subGenre:
                            mySkillsList.keySet()) {
                        mySkills.add(mySkillsList.get(subGenre));
                    }

                    //SignupSubGenreAdapter radioSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), mySkills, "radio");
                    SubGenreAdapter subGenreAdapter = new SubGenreAdapter(rootView.getContext(), mySkills);
                    skillRecyclerView.setAdapter(subGenreAdapter);
                }
                else{
                    Toast.makeText(getContext(), getString(R.string.skill_list_is_empty_toast), Toast.LENGTH_SHORT).show();
                }



            }
            else if (bundle.containsKey("confirmation")){
                Confirmation confirmation = (Confirmation) bundle.getSerializable("confirmation");
                loadUser(confirmation.getSenderUid());

            }
            else{
                String senderUid = bundle.getString("senderUid");
                loadUser(senderUid);


            }

        }

        dateTimeTv = rootView.findViewById(R.id.date_time_tv);
        scheduleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDateTimeDialog(dateTimeTv);

            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            scheduleBtn.setVisibility(View.VISIBLE);
        }
        else{
            scheduleBtn.setVisibility(View.GONE);
        }

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bundle.containsKey("user")) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("user_uid", hisUser.getUID());
                    //intent.putExtra("confirmation_msg", "this is the confirmation message");
                    if(calendar != null){
                        Confirmation confirmation = new Confirmation(
                                myUser.getUid(),
                                hisUser.getUID(),
                                myUser.getDisplayName(),
                                hisUser.getName(),
                                skillWant.toString(),
                                DateFormat.format("hh:mm aa dd/MM/yy", calendar).toString(),
                                "home1" //TODO add the user preferred location
                        );
                        intent.putExtra("confirmation_pojo", confirmation);

                        intent.putExtra("confirmation_msg",
                                myUser.getDisplayName() + " would like to schedule a skill swap with you at: " +
                                        DateFormat.format("hh:mm aa dd/MM/yy", calendar).toString()
                        );

                        getContext().startActivity(intent);

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack();


                    }
                    else{
                        Toast.makeText(getContext(), getString(R.string.please_enter_time_and_date_toast), Toast.LENGTH_SHORT).show();
                    }

                }
                else if (bundle.containsKey("confirmation")){
                    Confirmation confirmation = (Confirmation) bundle.getSerializable("confirmation");
                    updateDb(confirmation.getSenderUid(),confirmation.getReceiverUid(), confirmation.getSenderCid(),confirmation.getReceiverCid());
                }
                else{ //for confirmation update
                    //check shared pref is not empty
                    //check that date exists and save to shared pref
                    //update db
                    //close the fragment
                    final Bundle bundle = getArguments();
                    if(bundle != null){
                        updateDb(bundle.getString("senderUid"), bundle.getString("receiverUid"), bundle.getString("senderCid"), bundle.getString("receiverCid"));
                    }
                }
            }
        });

        return rootView;
    }

    private void loadUser(String senderUid) {
        Log.d("SCF", "senderUid: " + senderUid);

        DatabaseReference hisRef = FirebaseDatabase.getInstance().getReference("users").child(senderUid);

        hisRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    hisUser = dataSnapshot.getValue(User.class);
                    Log.d("SCF", "found his user: " + hisUser.toString());
                    if(hisUser != null) { //user exists

                        Log.d("SCF", "User is not null!");

                        //update ui
                        /*
                        Glide.with(context)
                                .load(hisUser.getImageUrl())
                                .thumbnail(0.01f)
                                .dontAnimate()
                                .error(R.drawable.ic_user) //change to default profile image
                                .into(profileIv);
                        */

                        nameTv.setText(hisUser.getName());
                        ageTv.setText(hisUser.getAge());
                        locationTv.setText(hisUser.getLocationAddress());
                        Log.d("SCF", "user location : " + hisUser.getLocationAddress());
                        mySkillsList = hisUser.getMySkillsList();
                        myLearnList = hisUser.getMyLearnList();

                        if (mySkillsList != null) {

                            for (String subGenre : mySkillsList.keySet()) {
                                mySkills.add(mySkillsList.get(subGenre));
                            }
                            SignupSubGenreAdapter radioSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), mySkills, "radio", null, null);
                            skillRecyclerView.setAdapter(radioSubGenreAdapter);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.skill_list_is_empty_toast), Toast.LENGTH_SHORT).show();
                        }

                        if (myLearnList != null) {

                            for (String subGenre : myLearnList.keySet()) {
                                learnList.add(myLearnList.get(subGenre));
                            }
                            Log.d("SCF", "learnList size : " + learnList.size());
                            SignupSubGenreAdapter learnSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), learnList, "plain", null, null);
                            learnRecyclerView.setAdapter(learnSubGenreAdapter);
                            Log.d("SCF", "attempting to write to adapter");

                        } else {
                            //Toast.makeText(getContext(), getString(R.string.skill_list_is_empty_toast), Toast.LENGTH_SHORT).show();
                        }


                    }
                }
                else{
                    Log.d("SCF", "getValue is null!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateDb(String senderUid, String receiverUid, String senderCid, String receiverCid) {

        try{
            Log.d("SCF", "start of try-catch");
            SharedPreferences sp = getContext().getSharedPreferences("skillSelected", Context.MODE_PRIVATE);

            if(sp == null)
                Log.d("SCF", "sp is null");
            String skillSelected = sp.getString("skillSelected", "");
            if(skillSelected.equals(""))
                Log.d("SCF", "skill selected is null");

            if(!skillSelected.equals("") && dateTimeTv.length()>0){
                Log.d("SCF", "Attempting to save date2 (" + dateTimeTv.getText().toString() + ") to shared pref");

                //update db
                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("users").child(senderUid).child("myConfirmations").child(senderCid);
                DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("users").child(receiverUid).child("myConfirmations").child(receiverCid);

                String receiverDate = dateTimeTv.getText().toString();
                Log.d("CDF", "selected skill :" + skillSelected);
                Log.d("CDF", "receiver date:" + receiverDate);

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("skill2", skillSelected);
                hashMap.put("date2", receiverDate);
                hashMap.put("location2", "home2");
                hashMap.put("completeStatus", "complete");

                //update
                Log.d("CDF", "Attempting to update the db with new receiver skill, date and status");
                senderRef.updateChildren(hashMap);
                receiverRef.updateChildren(hashMap);

                //update chat confirmation message
                updateChatDb(senderUid, receiverUid, senderCid, receiverCid);

                getFragmentManager().popBackStack();
            }
            else{
                Log.d("CDF", "selected skill or receiver date is null");
            }
        }
        catch (NullPointerException e){
            Snackbar.make(getView(), getString(R.string.no_skill_found_toast), Snackbar.LENGTH_SHORT);
            e.printStackTrace();
        }

        SharedPreferences spSelectedSkill = getContext().getSharedPreferences("skillSelected", Context.MODE_PRIVATE);
        SharedPreferences spReceiverDate = getContext().getSharedPreferences("receiverDate", Context.MODE_PRIVATE);

        if(spSelectedSkill == null || spReceiverDate == null){
            Log.d("CDF", "spSelectedSkill or spReceiverDate is null");
        }

        String selectedSkill = spSelectedSkill.getString("skillSelected", "");
        String receiverDate = spReceiverDate.getString("receiverDate", "");

        Log.d("CDF", "selected skill = " + selectedSkill + " receiver date = " + receiverDate);

    }

    private void updateChatDb(String senderUid, String receiverUid, String senderCid, String receiverCid) {
        int chatKey = senderUid.hashCode() + receiverUid.hashCode();
        DatabaseReference dbMessages = FirebaseDatabase.getInstance().getReference("chats").child(String.valueOf(chatKey));
        dbMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Message message = ds.getValue(Message.class);
                    if(message.getReceiver().equals(senderUid) && message.getSender().equals(receiverUid) ||
                            message.getReceiver().equals(receiverUid) && message.getSender().equals(senderUid)){ //TODO create unique identifier to pull from
                        if(message.getReceiverCid().equals(senderCid) && message.getSenderCid().equals(receiverCid) ||
                            message.getReceiverCid().equals(receiverCid) && message.getSenderCid().equals(senderCid)){
                            HashMap hashMap = new HashMap();
                            hashMap.put("completeStatus", "complete");

                            String ref = ds.getKey();
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats").child(String.valueOf(chatKey)).child(ref);
                            dbRef.updateChildren(hashMap);

                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDateTimeDialog(final TextView dateTimeTv) {
        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yy");
                        //dateTimeTv.setText(simpleDateFormat.format(calendar));
                        dateTimeTv.setText(DateFormat.format("hh:mm aa dd/MM/yy", calendar).toString());

                    }
                };

                new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };


        DatePickerDialog datePickerDialog  = new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

}

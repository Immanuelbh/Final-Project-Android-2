package maim.com.finalproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.model.User;

public class SignupDetailsFragment extends Fragment {

    private static final int MIN_AGE = 18;
    private static final int GENRE_FRAGMENT_REQ = 1001;
    private static final String GENRE_FRAGMENT_TAG = "my_skils_genre_fragment";
    private static final int FINE_PERMISSION_REQ = 202;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference users = database.getReference("users");

    HashSet<String> skills;
    HashMap<String, HashSet<String>> mySkills = new HashMap<>();

    Context context;
    private CoordinatorLayout coordinatorLayout;
    private SeekBar ageSb;
    private SeekBar rangeSb;
    private String rangeProgress;
    private String ageProgress;
    private ImageView chooseLocationIv, addMySkillBtn, addLearnBtn;
    private TextView signupAgeTv, signupRangeTv, locationResultTv;

    private RecyclerView skillRecycler, learnRecycler;

    HashMap<String,SubGenre> theSkill;
    private List<SubGenre> skillList = new ArrayList<>();
    private List<SubGenre> learnList = new ArrayList<>();

    public static SignupDetailsFragment newInstance(){
        SignupDetailsFragment signupDetailsFragment = new SignupDetailsFragment();
        return signupDetailsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        skills = (HashSet<String>) context.getSharedPreferences("mySkills", Context.MODE_PRIVATE).getStringSet("mySkills", new HashSet<String>());
        if(!skills.isEmpty()){
            skills.clear();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.signup_details_layout, container, false);

        signupAgeTv = rootView.findViewById(R.id.signup_age_tv);
        signupRangeTv = rootView.findViewById(R.id.signup_range_tv);
        locationResultTv = rootView.findViewById(R.id.signup_location_result_tv);
        coordinatorLayout = rootView.findViewById(R.id.coordinator);


        //initPlaces();
        //setupPlaceAutoComplete();

        ageSb = rootView.findViewById(R.id.signup_age_seekbar);
        rangeSb = rootView.findViewById(R.id.signup_range_seekbar);
        addMySkillBtn = rootView.findViewById(R.id.signup_add_myskill);
        addLearnBtn = rootView.findViewById(R.id.signup_add_learn);
        chooseLocationIv = rootView.findViewById(R.id.signup_choose_location);

        skillRecycler = rootView.findViewById(R.id.signup_skill_recycler);
        learnRecycler = rootView.findViewById(R.id.signup_learn_recycler);

        loadRecyclers();

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        final BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        fab.hide();
        bottomNav.setVisibility(View.GONE);

        ageProgress = String.valueOf(ageSb.getProgress() + MIN_AGE);
        //ageProgress = signupAgeTv.getText().toString();//String.valueOf(ageSb.getProgress() + MIN_AGE);
        //ageSb.setSecondaryProgress(Integer.parseInt(ageProgress));
        ageSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress >= 0 && progress <= ageSb.getMax()){
                        ageProgress = String.valueOf(progress + MIN_AGE);

                        signupAgeTv.setText(ageProgress);
                        ageSb.setSecondaryProgress(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rangeProgress = String.valueOf((rangeSb.getProgress() * 5)+5);
        //rangeProgress = signupRangeTv.getText().toString();//String.valueOf((rangeSb.getProgress() * 5)+5);
        //rangeSb.setSecondaryProgress(Integer.parseInt(rangeProgress));
        rangeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress >= 0 && progress <= rangeSb.getMax()){
                        rangeProgress = String.valueOf((progress * 5)+5);

                        signupRangeTv.setText(rangeProgress);
                        rangeSb.setSecondaryProgress(progress);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        chooseLocationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask permission

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startMap();
                } else {
                    // Show rationale and request permission.
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQ);

                }

            }
        });

        //track location update
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        locationRef.child("locationAddress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String location = "Haven't chosen an address yet.";
                if (dataSnapshot.exists()){
                    location = dataSnapshot.getValue()+"";
                    locationResultTv.setText(location);
                }

                Log.d("SDF", "the current location is : " + location);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        addMySkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open addSkill Activity
                Intent addSkillIntent = new Intent(context, SignupAddSkills.class);
                addSkillIntent.putExtra("type", "skill");
                startActivity(addSkillIntent);

            }
        });

        addLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addLearnIntent = new Intent(context, SignupAddSkills.class);
                addLearnIntent.putExtra("type", "learn");
                startActivity(addLearnIntent);
            }
        });

        Button saveBtn = rootView.findViewById(R.id.signup_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseUser fbUser = firebaseAuth.getCurrentUser();
                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("users").child(fbUser.getUid());

                Log.d("SDF", "skillList.size = " + skillList.size());
                Log.d("SDF", "learnList.size = " + learnList.size());
                if(validLocation() && skillList.size() > 0 && learnList.size() > 0){//validSkills()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", fbUser.getUid());
                    hashMap.put("name", fbUser.getDisplayName());
                    hashMap.put("email", fbUser.getEmail());
                    hashMap.put("age", signupAgeTv.getText().toString());
                    hashMap.put("maxRange", signupRangeTv.getText().toString());
                    hashMap.put("onlineStatus", "Online");
                    hashMap.put("typingTo", "noOne");

                    updateRef.updateChildren(hashMap);

                    bottomNav.setVisibility(View.VISIBLE);

                    Toast.makeText(context, getString(R.string.added_details_toast), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStackImmediate();

                }
                else{
                    if (skillList.size() == 0){
                        Snackbar.make(getActivity().findViewById(R.id.coordinator), "Please choose at least one skill you know", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (learnList.size() == 0){
                        Snackbar.make(getActivity().findViewById(R.id.coordinator), "Please choose at least one skill to learn", Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        Snackbar.make(getActivity().findViewById(R.id.coordinator), "Please enter a location", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    private boolean validLocation() {
        FirebaseUser fbUser = firebaseAuth.getCurrentUser();
        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("users").child(fbUser.getUid());

        //validation if tree
        String curLocation = locationResultTv.getText().toString();
        if(!curLocation.equals("")){
            return true;
        }

        return false;
    }

    private void loadRecyclers() {
        //skill
        skillRecycler.setLayoutManager(new LinearLayoutManager(context));
        skillRecycler.setHasFixedSize(true);

        //learn
        learnRecycler.setLayoutManager(new LinearLayoutManager(context));
        learnRecycler.setHasFixedSize(true);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.child("mySkillsList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    skillList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        SubGenre subGenre = ds.getValue(SubGenre.class);
                        skillList.add(subGenre);
                    }
                    SubGenreAdapter skillSubGenreAdapter = new SubGenreAdapter(context, skillList);
                    skillRecycler.setAdapter(skillSubGenreAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child("myLearnList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    learnList.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        SubGenre subGenre = ds.getValue(SubGenre.class);
                        learnList.add(subGenre);
                    }
                    SubGenreAdapter learnSubGenreAdapter = new SubGenreAdapter(context, learnList);
                    learnRecycler.setAdapter(learnSubGenreAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void startMap() {
        Intent mapIntent = new Intent(context, MapsActivity.class);
        context.startActivity(mapIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_REQ) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMap();
            } else {
                // Permission was denied. Display an error message.
                Snackbar.make(getActivity().findViewById(R.id.coordinator), getString(R.string.the_map_cannot_start_sb), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

}

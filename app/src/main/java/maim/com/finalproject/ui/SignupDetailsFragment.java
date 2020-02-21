package maim.com.finalproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import maim.com.finalproject.R;
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
    DatabaseReference dbGenres = database.getReference("genres");

    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment placesFragment;

    HashSet<String> skills;
    HashMap<String, HashSet<String>> mySkills = new HashMap<>();

    Context context;
    CoordinatorLayout coordinatorLayout;
    SeekBar ageSb;
    SeekBar rangeSb;
    String rangeProgress;
    String ageProgress;
    ListView mySkillsLv;
    ImageView chooseLocationIv;

    HashMap<String,SubGenre> theSkill;

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

        final TextView signupAgeTv = rootView.findViewById(R.id.signup_age_tv);
        final TextView signupRangeTv = rootView.findViewById(R.id.signup_range_tv);
        coordinatorLayout = rootView.findViewById(R.id.coordinator);

        //initPlaces();
        //setupPlaceAutoComplete();

        ageSb = rootView.findViewById(R.id.signup_age_seekbar);
        rangeSb = rootView.findViewById(R.id.signup_range_seekbar);
        ImageView addMySkillBtn = rootView.findViewById(R.id.signup_add_myskill);
        mySkillsLv = rootView.findViewById(R.id.signup_mySkills_listview);
        chooseLocationIv = rootView.findViewById(R.id.signup_choose_location);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        final BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        fab.hide();
        bottomNav.setVisibility(View.GONE);

        ageProgress = String.valueOf(ageSb.getProgress() + MIN_AGE);
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
        rangeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress >= 0 && progress <= rangeSb.getMax()){
                        rangeProgress = String.valueOf((progress * 5)+5);

                        signupRangeTv.setText(rangeProgress + " km");
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

        addMySkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GenreFragment genreFragment = GenreFragment.newInstance();
                Bundle bundle = new Bundle();

                bundle.putCharSequence("action", "signup");
                //genreFragment.setTargetFragment(this, GENRE_FRAGMENT_REQ);
                genreFragment.setArguments(bundle);
                FragmentTransaction mySkillsTransaction = getFragmentManager().beginTransaction();
                mySkillsTransaction.replace(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
                mySkillsTransaction.addToBackStack("signup").commit();
            }
        });


        //TODO clear the list from the sharedpref at the beginning
        skills = (HashSet<String>) context.getSharedPreferences("mySkills", Context.MODE_PRIVATE).getStringSet("mySkills", new HashSet<String>());
        final String[] skillList = new String[skills.size()];
        skills.toArray(skillList);
        //mySkillsList = new HashMap<>();
        theSkill = new HashMap<>();
        if(!skills.isEmpty()){
            Toast.makeText(context, skillList[0] + " and " + (skills.size()-1) + " other skills found", Toast.LENGTH_SHORT).show();

            mySkills.put("mySkills", skills);

            //update ui
            ArrayAdapter<String> skillAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, skillList);
            mySkillsLv.setAdapter(skillAdapter);

            //get subgenres from db
            Query matchSubGenre = dbGenres;
            matchSubGenre.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (String skill : skills){
                        for (DataSnapshot genres: dataSnapshot.getChildren()){
                            if(genres.child("subGenres").hasChild(skill.toLowerCase())){
                                //Toast.makeText(context, "found " + skill, Toast.LENGTH_SHORT).show();
                                theSkill.put(skill.toLowerCase(), genres.child("subGenres").child(skill.toLowerCase()).getValue(SubGenre.class));
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Button saveBtn = rootView.findViewById(R.id.signup_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser fbUser = firebaseAuth.getCurrentUser();
                try{

                    User newUser = new User(fbUser.getEmail(),
                            fbUser.getUid(),
                            fbUser.getDisplayName(),
                            ageProgress,
                            rangeProgress,
                            "Online",
                            "noOne",
                            theSkill);
                    users.child(fbUser.getUid()).setValue(newUser);

                }
                catch (NullPointerException e){
                    Toast.makeText(context, "exception when reading data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                bottomNav.setVisibility(View.VISIBLE);

                Toast.makeText(context, "Added details!", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStackImmediate();
            }
        });

        return rootView;
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
                Snackbar.make(coordinatorLayout, "The map cannot start without permission", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void setupPlaceAutoComplete() {
        placesFragment = (AutocompleteSupportFragment) getChildFragmentManager()//getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        
        try {
            placesFragment.setPlaceFields(placeFields);

        }
        catch (NullPointerException e){
            Toast.makeText(context, "np exception", Toast.LENGTH_SHORT).show();
        }
        
        placesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(context, ""+place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(context, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPlaces() {
        //Places.initialize(this.context, getString(R.string.places_api_key));
        placesClient = Places.createClient(this.context);
    }


}

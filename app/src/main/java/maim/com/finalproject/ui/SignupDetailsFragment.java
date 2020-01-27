package maim.com.finalproject.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.User;

public class SignupDetailsFragment extends Fragment {

    private static final int MIN_AGE = 18;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference users = database.getReference("users");

    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment placesFragment;


    Context context;
    CoordinatorLayout coordinatorLayout;
    SeekBar ageSb;
    SeekBar rangeSb;
    String rangeProgress;
    String ageProgress;

    public static SignupDetailsFragment newInstance(){
        SignupDetailsFragment signupDetailsFragment = new SignupDetailsFragment();
        return signupDetailsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_details_layout, container, false);

        final TextView signupAgeTv = rootView.findViewById(R.id.signup_age_tv);
        final TextView signupRangeTv = rootView.findViewById(R.id.signup_range_tv);
        coordinatorLayout = rootView.findViewById(R.id.coordinator);

        initPlaces();
        setupPlaceAutoComplete();



        ageSb = rootView.findViewById(R.id.signup_age_seekbar);
        rangeSb = rootView.findViewById(R.id.signup_range_seekbar);

        //signupAgeTv.setText(ageSb.getProgress());
        //signupRangeTv.setText(ageSb.getProgress());
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
                            rangeProgress);
                    users.child(fbUser.getUid()).setValue(newUser);

                }
                catch (NullPointerException e){
                    Toast.makeText(context, "exception when reading data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //Snackbar.make(coordinatorLayout, "Added details!", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(context, "Added details!", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStackImmediate();
            }
        });
        /*
        View signupDialog = getLayoutInflater().inflate(R.layout.signup_details_layout, null);
        builder.setView(signupDialog).setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        }).show();
        */
        return rootView;
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
        Places.initialize(this.context, getString(R.string.places_api_key));
        placesClient = Places.createClient(this.context);
    }
}

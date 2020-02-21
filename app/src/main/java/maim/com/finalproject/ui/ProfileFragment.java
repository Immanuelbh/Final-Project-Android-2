package maim.com.finalproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import maim.com.finalproject.R;

public class ProfileFragment extends Fragment {
    private static final int FINE_PERMISSION_REQ = 202;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    ImageView profileIv;
    TextView nameTv, ageTv, emailTv, rangeTv, locationResultTv;
    Button editLocationBtn, editSkillsBtn;
    CoordinatorLayout coordinatorLayout;

    Context context;
    View rootView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    public static ProfileFragment newInstance(){
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        //init firebase
        firebaseAuth= FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");

        profileIv = rootView.findViewById(R.id.profileIv);
        nameTv = rootView.findViewById(R.id.profile_name_tv);
        ageTv = rootView.findViewById(R.id.profile_age_tv);
        emailTv = rootView.findViewById(R.id.profile_email_tv);
        rangeTv = rootView.findViewById(R.id.profile_range_tv);
        locationResultTv = rootView.findViewById(R.id.location_result);
        coordinatorLayout = rootView.findViewById(R.id.coordinator);
        editLocationBtn = rootView.findViewById(R.id.profile_edit_location_btn);
        editSkillsBtn = rootView.findViewById(R.id.profile_edit_skills_btn);



        //query the db
        Query query = users.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    //get data
                    String name = ds.child("name").getValue()+"";
                    String age = ds.child("age").getValue()+"";
                    String email = ds.child("email").getValue()+"";
                    String range = ds.child("maxRange").getValue()+"";
                    String lat = ds.child("locationLat").getValue()+"";
                    String lon = ds.child("locationLon").getValue()+"";
                    String image = ds.child("imageUrl").getValue()+"";

                    nameTv.setText(name);
                    ageTv.setText(age);
                    emailTv.setText(email);
                    rangeTv.setText(range);
                    locationResultTv.setText("lat: " + lat + " lon: " + lon);
                    Glide.with(rootView.getContext())
                            .load(image)
                            .error(R.drawable.ic_add_image)
                            .into(profileIv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editLocationBtn.setOnClickListener(new View.OnClickListener() {
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

        editSkillsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open addSkill Activity
                Intent addSkillIntent = new Intent(context, SignupAddSkills.class);
                startActivity(addSkillIntent);

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
}

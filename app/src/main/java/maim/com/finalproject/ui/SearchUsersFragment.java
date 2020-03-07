package maim.com.finalproject.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.UserAdapter;
import maim.com.finalproject.model.User;

public class SearchUsersFragment extends Fragment {
    final static double EARTH_RADIUS = 6378.137;

    private RecyclerView recyclerView;
    private TextView noUsersTv;
    private List<User> userList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbUsers;
    private UserAdapter adapter; //for now
    private String skillToFind;
    private User currentUser;
    private Double userRadius, lat2, long2;
    private SharedPreferences sp;
    private int age_seekbar_sp;
    private boolean loggedIn;
    private View rootView;
    private FirebaseUser fbUser;
    private ProgressDialog progressDialog;


    public static SearchUsersFragment newInstance() {
        SearchUsersFragment searchUsersFragment  = new SearchUsersFragment();
        return searchUsersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.users_fragment, container, false);

        Log.d("SearchUsersFragment", "starting fragment");


        firebaseAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = rootView.findViewById(R.id.users_recycler);
        noUsersTv = rootView.findViewById(R.id.search_no_users_tv);

        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setHasFixedSize(true);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        //read genres from database
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage(getString(R.string.loading_users_please_wait_pd));
        progressDialog.show();
        fbUser = firebaseAuth.getCurrentUser();
        loggedIn = false;
        try{
            String currentUserUid = fbUser.getUid();
            loggedIn = true;
        }
        catch (NullPointerException e){
            loggedIn = false;
        }

        if(loggedIn){
            DatabaseReference currentUserInfo = FirebaseDatabase.getInstance().getReference("users").child(fbUser.getUid());
            currentUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    try{
                        userRadius = Double.parseDouble(currentUser.getMaxRange());
                        Log.d("SUF", "User Radius : " + userRadius);
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
        else{
            userRadius = (double) sp.getInt("range_preference_seekbar", 50);
        }

        age_seekbar_sp = sp.getInt("age_preference_seekbar",120); //Age Seek-bar value from sp

        Bundle bundle = getArguments();
        if (bundle != null){
            CharSequence skill = bundle.getCharSequence("subGenre");
            if(skill != null && sp != null){
                skillToFind = skill.toString().toLowerCase();

                adapter = new UserAdapter(rootView.getContext(), userList, skillToFind);
                recyclerView.setAdapter(adapter);

                dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        if(dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                User user = snapshot.getValue(User.class);

                                if(loggedIn){
                                    try{
                                        if(!user.getUID().equals(fbUser.getUid())){
                                            if(validUser(user)){
                                                if(user.getMySkillsList().containsKey(skillToFind) &&
                                                        Integer.parseInt(user.getAge()) - age_seekbar_sp <= 0 &&
                                                        haversine(user.getLocationLat(),user.getLocationLon()) <= userRadius){
                                                    userList.add(user);
                                                }

                                            }
                                            else{
                                                //continue;
                                            }
                                        }
                                    }
                                    catch (NullPointerException e){
                                        e.printStackTrace();
                                        if(user == null){
                                            Toast.makeText(getContext(), getString(R.string.user_is_null_toast), Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            //Toast.makeText(getContext(), getString(R.string.something_else_is_wrong_toast), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else{

                                    try{
                                        if(user.getMySkillsList().containsKey(skillToFind) &&
                                                Integer.parseInt(user.getAge()) - age_seekbar_sp <= 0){
                                            //haversine(user.getLocationLat(),user.getLocationLon()) <= userRadius){
                                            userList.add(user);
                                        }
                                    }
                                    catch (NullPointerException e){
                                        //e.printStackTrace();
                                        //continue;
                                    }

                                }

                            }
                            adapter.notifyDataSetChanged();
                            if(userList.isEmpty()){
                                recyclerView.setVisibility(View.GONE);
                                noUsersTv.setVisibility(View.VISIBLE);
                            }
                            else {
                                noUsersTv.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                            }
                        }
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else{
                Toast.makeText(getContext(), getString(R.string.failed_to_transfer_subgenre_toast), Toast.LENGTH_SHORT).show();
            }

        }
        else{ //from notification
            Toast.makeText(getContext(), "From Notification!", Toast.LENGTH_SHORT).show();
            Bundle bundle2 = getArguments();
            if(bundle2 != null){
                String skillToFind = bundle2.getCharArray("skillToFind").toString();
                if(skillToFind != null){
                    displayUsers(skillToFind.toLowerCase());
                    Toast.makeText(getContext(), "With Intent", Toast.LENGTH_SHORT).show();
                }

            }
            else{
                Toast.makeText(getContext(), "No Intent", Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }

    private void displayUsers(String skillToFind) {
        adapter = new UserAdapter(rootView.getContext(), userList, skillToFind);
        recyclerView.setAdapter(adapter);

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);

                        if(loggedIn){
                            try{
                                if(!user.getUID().equals(fbUser.getUid())){
                                    if(validUser(user)){
                                        if(user.getMySkillsList().containsKey(skillToFind) &&
                                                Integer.parseInt(user.getAge()) - age_seekbar_sp <= 0 &&
                                                haversine(user.getLocationLat(),user.getLocationLon()) <= userRadius){
                                            userList.add(user);
                                        }

                                    }
                                    else{
                                        //continue;
                                    }
                                }
                            }
                            catch (NullPointerException e){
                                e.printStackTrace();
                                if(user == null){
                                    Toast.makeText(getContext(), getString(R.string.user_is_null_toast), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    //Toast.makeText(getContext(), getString(R.string.something_else_is_wrong_toast), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else{

                            try{
                                if(user.getMySkillsList().containsKey(skillToFind) &&
                                        Integer.parseInt(user.getAge()) - age_seekbar_sp <= 0){
                                    //haversine(user.getLocationLat(),user.getLocationLon()) <= userRadius){
                                    userList.add(user);
                                }
                            }
                            catch (NullPointerException e){
                                //e.printStackTrace();
                                //continue;
                            }

                        }

                    }
                    adapter.notifyDataSetChanged();
                    if(userList.isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                        noUsersTv.setVisibility(View.VISIBLE);
                    }
                    else {
                        noUsersTv.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                    }
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean validUser(User user) {
        if (user.getMySkillsList().size() == 0) {
            return false;
        } else if (Integer.parseInt(user.getAge()) > 120 || Integer.parseInt(user.getAge()) < 18) {
            return false;
        } else if (user.getLocationLat() == 0 || user.getLocationLon() == 0){
            return false;
        }
        //Toast.makeText(getContext(), "not a valid user", Toast.LENGTH_SHORT).show();
        return true;
    }

    private double haversine(double locationLat, double locationLon) {
        if(currentUser != null){
            double lat2 = currentUser.getLocationLat();
            double long2 = currentUser.getLocationLon();

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

        Toast.makeText(getContext(), getString(R.string.current_user_is_null_toast), Toast.LENGTH_SHORT).show();
        return 0;
    }
}

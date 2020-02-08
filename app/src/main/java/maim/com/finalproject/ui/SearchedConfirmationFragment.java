package maim.com.finalproject.ui;

import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.model.User;

public class SearchedConfirmationFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser myUser;
    User hisUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    ImageView profileIv;
    TextView nameTv, ageTv;
    View rootView;
    RecyclerView recyclerView;
    private HashMap<String,SubGenre> mySkillsList;
    List<SubGenre> mySkills = new ArrayList<>();


    public static SearchedConfirmationFragment newInstance(){

        return new SearchedConfirmationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.searched_confirmation_fragment, container, false);


        //init firebase
        firebaseAuth= FirebaseAuth.getInstance();
        myUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");

        profileIv = rootView.findViewById(R.id.profileIv);
        nameTv = rootView.findViewById(R.id.profile_name_tv);
        ageTv = rootView.findViewById(R.id.profile_age_tv);
        recyclerView = rootView.findViewById(R.id.confirmation_myskills);
        ImageView scheduleTime = rootView.findViewById(R.id.confirmation_schedule_time);
        final BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.GONE);
        Button scheduleBtn = rootView.findViewById(R.id.confirmation_schedule_btn);

        recyclerView.setLayoutManager((new GridLayoutManager(rootView.getContext(), 2, LinearLayoutManager.HORIZONTAL, true)));
        recyclerView.setHasFixedSize(true);

        //Toast.makeText(getContext(),"you made it", Toast.LENGTH_SHORT).show();
        Bundle bundle = this.getArguments();
        if(bundle != null){
            //List<SubGenre> mySkills = (List<SubGenre>) bundle.getSerializable("subGenres");



            hisUser = (User) bundle.getSerializable("user");

            //update ui
            nameTv.setText(hisUser.getName());
            ageTv.setText(hisUser.getAge());
            mySkillsList = hisUser.getMySkillsList();
            
            if(mySkillsList != null){

                for (String subGenre:
                        mySkillsList.keySet()) {
                    mySkills.add(mySkillsList.get(subGenre));

                }

                SubGenreAdapter subGenreAdapter = new SubGenreAdapter(rootView.getContext(), mySkills);
                recyclerView.setAdapter(subGenreAdapter);
            }
            else{
                Toast.makeText(getContext(), "skill list is empty", Toast.LENGTH_SHORT).show();
            }

        }

        scheduleTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleTimeFragment scheduleTimeFragment = ScheduleTimeFragment.newInstance();

                FragmentTransaction scheduleTransaction = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
                scheduleTransaction.replace(R.id.recycler_container, scheduleTimeFragment, "schedule_time_fragment");
                scheduleTransaction.commit();
            }
        });

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user_uid", hisUser.getUID());
                intent.putExtra("confirmation", "this is the confirmation message");
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }
}

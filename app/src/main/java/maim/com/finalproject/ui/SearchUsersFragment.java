package maim.com.finalproject.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
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

    RecyclerView recyclerView;
    TextView noUsersTv;
    private List<User> userList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    DatabaseReference dbUsers;
    UserAdapter adapter; //for now
    String skillToFind;

    public static SearchUsersFragment newInstance() {
        SearchUsersFragment searchUsersFragment  = new SearchUsersFragment();
        return searchUsersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.users_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = rootView.findViewById(R.id.users_recycler);
        noUsersTv = rootView.findViewById(R.id.search_no_users_tv);

        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setHasFixedSize(true);

        //read genres from database
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading users, please wait..");
        progressDialog.show();
        final FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        Bundle bundle = getArguments();
        if (bundle != null){
            CharSequence skill = bundle.getCharSequence("subGenre");
            if(skill != null){
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
                                if(!user.getUID().equals(fbUser.getUid())){
                                    if(user.getMySkillsList().containsKey(skillToFind)){
                                        userList.add(user);
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
                Toast.makeText(getContext(), "failed to transfer subgenre", Toast.LENGTH_SHORT).show();
            }

        }

        return rootView;
    }
}

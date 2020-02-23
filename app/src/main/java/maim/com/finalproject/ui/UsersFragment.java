package maim.com.finalproject.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import maim.com.finalproject.adapters.GenreAdapter;
import maim.com.finalproject.adapters.UserAdapter;
import maim.com.finalproject.model.User;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    private List<User> userList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    DatabaseReference dbUsers;
    UserAdapter adapter;

    public static UsersFragment newInstance() {
        UsersFragment usersFragment  = new UsersFragment();
        return usersFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.users_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        final RecyclerView recyclerView = rootView.findViewById(R.id.users_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new UserAdapter(rootView.getContext(), userList);
        recyclerView.setAdapter(adapter);

        //read genres from database

        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage(getString(R.string.loading_users_please_wait_pd));
        progressDialog.show();
        final FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        if(!user.getUID().equals(fbUser.getUid())){
                            userList.add(user);


                        }
                        //userList.add(user);
                        //Log.d("GENRE_FRAGMENT:", genre.toString());
                    }
                    adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }
}

package maim.com.finalproject.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.SignupSubGenreAdapter;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;

public class SubGenreFragment extends Fragment {

    private DatabaseReference dbGenres;
    HashSet<String> mySkills = new HashSet<>();
    SignupSubGenreAdapter signupSubGenreAdapter;
    View rootView;
    List<SubGenre> list;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public static SubGenreFragment newInstance(){
        SubGenreFragment subGenreFragment = new SubGenreFragment();
        return subGenreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.genre_fragment, container, false);

        final RecyclerView recyclerView = rootView.findViewById(R.id.genre_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        recyclerView.setHasFixedSize(true);

        //TODO: add model

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            CharSequence actionCs = bundle.getCharSequence("action");
            String action = null;
            if(actionCs != null){
                action = actionCs.toString();
            }

            Genre genre = (Genre) bundle.getSerializable("genre");
            if(genre != null){
                list = new ArrayList<SubGenre>(genre.getSubGenres().values());

                if(action != null){ //during signup
                    if(action.equals("signup")){
                        String type = bundle.getString("type");

                        //add listener to db - update local list
                        //pass local list to adapter - show checkbox marks if appears on list
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        if(type.equals("skill")){
                            dbRef.child("mySkillsList").addValueEventListener(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d("SGF", "onDataChange");
                                    mySkills.clear();
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        SubGenre subGenre = ds.getValue(SubGenre.class);
                                        mySkills.add(subGenre.getName());
                                        Log.d("SGF", "added " + subGenre.getName());

                                    }
                                    Log.d("SGF", "mySkills.isEmpty = " + mySkills.isEmpty());

                                    signupSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), list, "checkbox", mySkills, type);
                                    recyclerView.setAdapter(signupSubGenreAdapter);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else if(type.equals("learn")){
                            dbRef.child("myLearnList").addValueEventListener(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d("SGF", "onDataChange");
                                    mySkills.clear();
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        SubGenre subGenre = ds.getValue(SubGenre.class);
                                        mySkills.add(subGenre.getName());
                                        Log.d("SGF", "added " + subGenre.getName());

                                    }
                                    Log.d("SGF", "mySkills.isEmpty = " + mySkills.isEmpty());

                                    signupSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), list, "checkbox", mySkills, type);
                                    recyclerView.setAdapter(signupSubGenreAdapter);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }


                    }

                }
                else{ //during search
                    SubGenreAdapter subGenreAdapter = new SubGenreAdapter(rootView.getContext(), list);
                    recyclerView.setAdapter(subGenreAdapter);

                }

            }
            else{
                Toast.makeText(this.getContext(), getString(R.string.could_not_open_genre_toast), Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }


}

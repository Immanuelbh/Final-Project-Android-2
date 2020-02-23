package maim.com.finalproject.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import maim.com.finalproject.adapters.GenreAdapter;
import maim.com.finalproject.adapters.SignupGenreAdapter;
import maim.com.finalproject.model.Genre;


public class GenreFragment extends Fragment {

    public static final String GENRE_FRAGMENT_TAG = "genre_fragment";
    public static final int CODE_REQUEST = 1002;
    private List<Genre> genresList = new ArrayList<>();
    private HashSet<String> mySkills;
    private GenreAdapter genreAdapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbGenres;

    FloatingActionButton fab;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    public static GenreFragment newInstance(){
        GenreFragment genreFragment = new GenreFragment();
        return genreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.genre_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        dbGenres = FirebaseDatabase.getInstance().getReference("genres");




        //fab
        /*
        fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder genreBuilder = new AlertDialog.Builder(getContext());
                View genreDialogView = getLayoutInflater().inflate(R.layout.add_genre_dialog, null);
                final EditText editText = genreDialogView.findViewById(R.id.genre_name_et);
                genreBuilder.setView(genreDialogView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String genreName = editText.getText().toString();
                        genresList.add(new Genre(genreName));
                        adapter.notifyItemInserted(genresList.size()-1);
                        dbGenres.child(firebaseAuth.getCurrentUser().getUid()).setValue(genresList);
                        Toast.makeText(getContext(), "Added genre " + genreName, Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });

        */
        final RecyclerView recyclerView = rootView.findViewById(R.id.genre_recycler);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            String action = bundle.getCharSequence("action").toString();

            switch (action){
                case "signup":
                    String type = bundle.getCharSequence("type").toString();
                    recyclerView.setLayoutManager(new LinearLayoutManager((rootView.getContext())));
                    recyclerView.setHasFixedSize(true);

                    final SignupGenreAdapter signupGenreAdapter = new SignupGenreAdapter(rootView.getContext(), genresList, type);
                    recyclerView.setAdapter(signupGenreAdapter);

                    //read genres from database

                    final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
                    progressDialog.setMessage(getString(R.string.loading_genres_pd));
                    progressDialog.show();

                    dbGenres.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            genresList.clear();
                            if(dataSnapshot.exists()){
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    Genre genre = snapshot.getValue(Genre.class);
                                    genresList.add(genre);
                                    Log.d("GENRE_FRAGMENT:", genre.toString());
                                }
                                signupGenreAdapter.notifyDataSetChanged();
                            }
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    break;
            }
        }
        else {

            recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
            recyclerView.setHasFixedSize(true);
            genreAdapter = new GenreAdapter(rootView.getContext(), genresList);
            recyclerView.setAdapter(genreAdapter);

            //read genres from database

            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setMessage(getString(R.string.loading_genres_pd));
            progressDialog.show();

            dbGenres.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    genresList.clear();
                    if(dataSnapshot.exists()){
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Genre genre = snapshot.getValue(Genre.class);
                            genresList.add(genre);
                            Log.d("GENRE_FRAGMENT:", genre.toString());
                        }
                        genreAdapter.notifyDataSetChanged();
                    }
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



        //TODO: add model
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == GenreFragment.CODE_REQUEST){
            HashSet<String> returnedSet = (HashSet<String>) data.getSerializableExtra("mySkills");

            for (String skill:
                 returnedSet) {
                mySkills.add(skill);
                Toast.makeText(getContext(), skill+"", Toast.LENGTH_SHORT).show();
                //TODO fix: if you checked a subgenre - returned (saved) - entered again and removed the checkbox - returned again - it will not be removed
                //
            }
        }
    }
}

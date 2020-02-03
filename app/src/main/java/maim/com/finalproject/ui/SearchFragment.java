package maim.com.finalproject.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.GenreAdapter;
import maim.com.finalproject.adapters.MultiRecyclerAdapter;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;


public class SearchFragment extends Fragment {

    private List<Genre> genresList = new ArrayList<>();
    private List<SubGenre> subGenresList = new ArrayList<>();
    private List<Object> typeList = new ArrayList<>();

    //private GenreAdapter genre_adapter;
    //private SubGenreAdapter subGenre_adapter;
    private MultiRecyclerAdapter multiRecycler_adapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbGenres;
    private DatabaseReference dbSubGenres;


    public static SearchFragment newInstance(){
        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.genre_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        dbGenres = FirebaseDatabase.getInstance().getReference("genres");
        dbSubGenres =FirebaseDatabase.getInstance().getReference("genres").child("subGenres");
        final RecyclerView recyclerView = rootView.findViewById(R.id.genre_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        recyclerView.setHasFixedSize(true);

        multiRecycler_adapter=new MultiRecyclerAdapter(rootView.getContext(),typeList);
        recyclerView.setAdapter(multiRecycler_adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            final String genreName_query = (String) bundle.getCharSequence("search_genre_query");
            if (dbGenres != null) {
                dbGenres.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        genresList.clear();
                        subGenresList.clear();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Genre genre = snapshot.getValue(Genre.class);
                                HashMap<String, SubGenre> subGenres = genre.getSubGenres();
                                if (genre.getName().toLowerCase().contains(genreName_query.toLowerCase())) {
                                    typeList.add(genre);
                                    Log.d("GENRE_SEARCH........", genre.toString());

                                    /*dbSubGenres.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final SubGenre subGenre = dataSnapshot.getValue(SubGenre.class);
                                            subGenresList.add(subGenre);
                                            //if (subGenre.getName().toLowerCase().contains(genreName_query.toLowerCase())) {
                                                subGenresList.add(subGenre);
                                                //Log.d("SUBGENRE_SEARCH........", subGenre.toString());
                                            //}
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });*/

                                    multiRecycler_adapter.notifyDataSetChanged();
                                }

                                for(Map.Entry<String, SubGenre> entry : subGenres.entrySet()) {
                                    String key = entry.getKey();
                                    SubGenre sg_value = entry.getValue();
                                    Log.d("SUBGENRE_SEARCH........", key);
                                    if (key.contains(genreName_query.toLowerCase())) {
                                        //add to list
                                        typeList.add(sg_value);
                                        Log.d("SUBGENRE_SEARCH........", "Added Key");
                                    }
                                    multiRecycler_adapter.notifyDataSetChanged();
                                }
                                //if(subGenres.containsKey())
                                //progressDialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        //read genres from database

       /* final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading genres, please wait..");
        progressDialog.show();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        */
       return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    /*inflate options menu*/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //infalte menu

        super.onCreateOptionsMenu(menu, inflater);
    }
}



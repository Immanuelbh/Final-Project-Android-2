package maim.com.finalproject.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.GenreAdapter;
import maim.com.finalproject.model.Genre;

public class GenreFragment extends Fragment {

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

        final RecyclerView recyclerView = rootView.findViewById(R.id.genre_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        recyclerView.setHasFixedSize(true);

        //TODO: add model


        //example genres
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Music"));
        genres.add(new Genre("Computers"));
        genres.add(new Genre("Writing"));
        genres.add(new Genre("Knitting"));
        genres.add(new Genre("Cooking"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));
        genres.add(new Genre("Guitar"));

        GenreAdapter adapter = new GenreAdapter(rootView.getContext(), genres);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}

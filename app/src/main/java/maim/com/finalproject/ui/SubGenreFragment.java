package maim.com.finalproject.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.GenreAdapter;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;

public class SubGenreFragment extends Fragment {


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
        View rootView = inflater.inflate(R.layout.genre_fragment, container, false);

        final RecyclerView recyclerView = rootView.findViewById(R.id.genre_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        recyclerView.setHasFixedSize(true);

        //TODO: add model


        //example subgenres

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            int i = bundle.getInt("current_genre", -1);

            List<SubGenre> subGenres = new ArrayList<>();

            if(i >= 0){
                if(i == 0) {// 'Music'
                    subGenres.add(new SubGenre("Guitar"));
                    subGenres.add(new SubGenre("Piano"));
                    subGenres.add(new SubGenre("Drums"));
                    subGenres.add(new SubGenre("Bass"));
                    subGenres.add(new SubGenre("Kazoo"));
                    subGenres.add(new SubGenre("Harmonica"));
                }


                SubGenreAdapter adapter = new SubGenreAdapter(rootView.getContext(), subGenres);
                recyclerView.setAdapter(adapter);
            }
            else{
                Toast.makeText(this.getContext(), "couldn't open genre", Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }
}

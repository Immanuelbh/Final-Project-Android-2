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

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.SignupSubGenreAdapter;
import maim.com.finalproject.adapters.SubGenreAdapter;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;

public class SubGenreFragment extends Fragment {

    private DatabaseReference dbGenres;

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

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            CharSequence actionCs = bundle.getCharSequence("action");
            String action = null;
            if(actionCs != null){
                action = actionCs.toString();
            }

            Genre genre = (Genre) bundle.getSerializable("genre");
            if(genre != null){
                List<SubGenre> list = new ArrayList<SubGenre>(genre.getSubGenres().values());

                if(action != null){ //during signup
                    final SignupSubGenreAdapter signupSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), list, "checkbox");
                    recyclerView.setAdapter(signupSubGenreAdapter);

                }
                else{ //during search
                    SubGenreAdapter subGenreAdapter = new SubGenreAdapter(rootView.getContext(), list);
                    recyclerView.setAdapter(subGenreAdapter);

                }

            }

            else{
                Toast.makeText(this.getContext(), "couldn't open genre", Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }


}

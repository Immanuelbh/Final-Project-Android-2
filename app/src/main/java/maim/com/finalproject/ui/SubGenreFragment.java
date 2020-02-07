package maim.com.finalproject.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

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
            String action = bundle.getCharSequence("action").toString();

            Genre genre = (Genre) bundle.getSerializable("genre");
            if(genre != null){
                List<SubGenre> list = new ArrayList<SubGenre>(genre.getSubGenres().values());

                if(action != null){
                    final SignupSubGenreAdapter signupSubGenreAdapter = new SignupSubGenreAdapter(rootView.getContext(), list);
                    recyclerView.setAdapter(signupSubGenreAdapter);
/*
                    try{
                        this.getView().setFocusableInTouchMode(true);
                        this.getView().requestFocus();
                        this.getView().setOnKeyListener( new View.OnKeyListener()
                        {
                            @Override
                            public boolean onKey( View v, int keyCode, KeyEvent event )
                            {
                                if( keyCode == KeyEvent.KEYCODE_BACK )
                                {
                                    FragmentManager fm = getFragmentManager();
                                    Fragment ft = fm.findFragmentByTag(GenreFragment.GENRE_FRAGMENT_TAG);
                                    ft.setTargetFragment(ft, GenreFragment.CODE_REQUEST);
                                    //sendCurrentMySkills(Activity.RESULT_OK, signupSubGenreAdapter.getMySkills());

                                    return true;
                                }
                                return false;
                            }
                        } );

                    }
                    catch (NullPointerException e){
                        e.getStackTrace();
                        Toast.makeText(getContext(), "failed on getView", Toast.LENGTH_SHORT).show();
                    }*/
                }
                else{
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

    private void sendCurrentMySkills(int resultCode, HashSet<String> mySkills) {
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra("mySkills", mySkills);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}

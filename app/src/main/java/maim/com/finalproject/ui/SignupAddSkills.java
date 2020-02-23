package maim.com.finalproject.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import maim.com.finalproject.R;

public class SignupAddSkills extends AppCompatActivity {

    private static final String GENRE_FRAGMENT_TAG = "genre_fragment";
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_add_skills);

        fab = findViewById(R.id.fab_add_skill);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String type = getIntent().getStringExtra("type");

        GenreFragment genreFragment = GenreFragment.newInstance();
        Bundle bundle = new Bundle();

        bundle.putCharSequence("action", "signup");
        bundle.putCharSequence("type", type);
        genreFragment.setArguments(bundle);
        FragmentTransaction mySkillsTransaction = this.getSupportFragmentManager().beginTransaction();
        mySkillsTransaction.replace(R.id.as_recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
        //mySkillsTransaction.addToBackStack("signup").commit();
        mySkillsTransaction.commit();
    }
}

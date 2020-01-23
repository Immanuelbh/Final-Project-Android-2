package maim.com.finalproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;

public class MainActivity extends AppCompatActivity {

    private static final String GENRE_FRAGMENT_TAG = "genres_fragment";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CoordinatorLayout coordinatorLayout;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;

    String fullName;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbGenres = database.getReference("genres");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        DatabaseReference pushedKey = dbGenres.child("genres").push();
        List<SubGenre> subGenres = new ArrayList<>();
        subGenres.add(new SubGenre("Guitar"));
        subGenres.add(new SubGenre("Piano"));
        subGenres.add(new SubGenre("Drums"));
        Genre music = new Genre("Music", subGenres);
        pushedKey.setValue(music);
        */

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        coordinatorLayout = findViewById(R.id.coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView  = getLayoutInflater().inflate(R.layout.sign_in_dialog,null);

                final EditText emailEt = dialogView.findViewById(R.id.email_input);
                final EditText fullNameEt = dialogView.findViewById(R.id.full_name_input);
                //final EditText firstNameEt = dialogView.findViewById(R.id.first_name_input);
                //final EditText lastNameEt = dialogView.findViewById(R.id.last_name_input);
                final EditText passwordEt = dialogView.findViewById(R.id.password_input);

                switch (item.getItemId()){

                    case R.id.item_sign_up:
                        builder.setView(dialogView).setPositiveButton("Register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = emailEt.getText().toString();
                                fullName = fullNameEt.getText().toString();
                                //firstName = firstNameEt.getText().toString();
                                //String lastName = lastNameEt.getText().toString();
                                String password = passwordEt.getText().toString();

                                //sign up
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful())
                                            Snackbar.make(coordinatorLayout, "Signup successful", Snackbar.LENGTH_SHORT).show();
                                        else
                                            Snackbar.make(coordinatorLayout, "Signup failed", Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).show();
                        break;
                    case R.id.item_login:
                        fullNameEt.setVisibility(View.GONE);

                        builder.setView(dialogView).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = emailEt.getText().toString();
                                //String firstName = firstNameEt.getText().toString();
                                //String lastName = lastNameEt.getText().toString();
                                String password = passwordEt.getText().toString();

                                //login
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                            Snackbar.make(coordinatorLayout, "Login successful", Snackbar.LENGTH_SHORT).show();
                                        else
                                            Snackbar.make(coordinatorLayout, "Login failed", Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).show();
                        break;
                    case R.id.item_logout:
                        firebaseAuth.signOut();
                        Snackbar.make(coordinatorLayout, "Logged out", Snackbar.LENGTH_SHORT).show();

                        break;
                }

                return false;
            }
        });


        final CollapsingToolbarLayout ctl = findViewById(R.id.collapsing_layout);
        ctl.setTitle("Please Log In");


        //initializing authlistener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //update header textview
                View headerView = navigationView.getHeaderView(0);
                TextView userTv = headerView.findViewById(R.id.navigation_header_text_view);

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                //login or sign up
                if(user != null){
                    if(fullName != null){ //sign up
                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName).build())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                fullName = null;
                                if(task.isSuccessful())
                                    Snackbar.make(coordinatorLayout, user.getDisplayName() + "Welcome!", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //update ui -
                    userTv.setText(user.getDisplayName() + " logged in");
                    ctl.setTitle(user.getDisplayName()+"");

                    navigationView.getMenu().findItem(R.id.item_login).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(true);






                }
                else{ //logged out or not sign in yet
                    userTv.setText("Please Log In");
                    ctl.setTitle("Please Log In");

                    navigationView.getMenu().findItem(R.id.item_login).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(false);

                    //genres.clear();
                    //adapter.notifyDataSetChanged();
                }
            }
        };

        //adding genres fragment
        GenreFragment genreFragment = GenreFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
        transaction.commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            //  Toast.makeText(this, "Home button pressed", Toast.LENGTH_SHORT).show();
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}

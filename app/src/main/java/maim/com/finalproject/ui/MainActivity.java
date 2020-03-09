package maim.com.finalproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.notifications.Token;
import maim.com.finalproject.services.AlarmService;

public class MainActivity extends AppCompatActivity {

    private static final String GENRE_FRAGMENT_TAG = "genres_fragment";
    private static final String SIGNUP_FRAGMENT_TAG = "signup_details_fragment";
    private static final String PROFILE_FRAGMENT_TAG = "profile_fragment";
    private static final String MESSAGES_FRAGMENT_TAG = "messages_fragment";
    private static final String SEARCH_FRAGMENT_TAG = "search_fragment";
    private static final String CONFIRMATIONS_FRAGMENT_TAG = "confirmations_fragment";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout ctl;
    private AppBarLayout appBarLayout;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private String mUID;

    private String fullName;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbGenres = database.getReference("genres");

    private SearchView searchView;
    private SearchView toolbarSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        coordinatorLayout = findViewById(R.id.coordinator);
        appBarLayout = findViewById(R.id.app_bar_layout);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        searchView=findViewById(R.id.collapsing_search_view);
        toolbarSearchView = findViewById(R.id.toolbar_search_view);
        //toolbarSearchView=searchView;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    toolbarSearchView.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.GONE);
                } else if (verticalOffset == 0) {
                    // Expanded
                    toolbarSearchView.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    // Somewhere in between
                }
            }
        });

        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView  = getLayoutInflater().inflate(R.layout.sign_in_dialog,null);

                final EditText emailEt = dialogView.findViewById(R.id.email_input);
                final EditText fullNameEt = dialogView.findViewById(R.id.full_name_input);
                //final EditText firstNameEt = dialogView.findViewById(R.id.first_name_input);
                //final EditText lastNameEt = dialogView.findViewById(R.id.last_name_input);
                final EditText passwordEt = dialogView.findViewById(R.id.password_input);


                switch (item.getItemId()){
                    case R.id.item_users:
                        //TODO return to initial fragment (genres)
                        UsersFragment usersFragment = UsersFragment.newInstance();

                        FragmentTransaction usersTransaction = getSupportFragmentManager().beginTransaction();
                        usersTransaction.replace(R.id.recycler_container, usersFragment, PROFILE_FRAGMENT_TAG);
                        usersTransaction.addToBackStack(null).commit();
                        break;
                    case R.id.item_sign_up:
                        builder.setView(dialogView).setPositiveButton(getString(R.string.next_btn), new DialogInterface.OnClickListener() {
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

                                        if(task.isSuccessful()){

                                            //task is successful so should not be null
                                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                            //create initial entry in db
                                            DatabaseReference initialRef = FirebaseDatabase.getInstance()
                                                                            .getReference("users").child(currentUser.getUid());

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("uid", currentUser.getUid());
                                            hashMap.put("name", currentUser.getDisplayName());

                                            //open up signup extra detail fragment
                                            SignupDetailsFragment signupDetailsFragment = SignupDetailsFragment.newInstance();

                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            transaction.replace(R.id.recycler_container, signupDetailsFragment, SIGNUP_FRAGMENT_TAG);
                                            transaction.addToBackStack(null).commit();


                                            Snackbar.make(coordinatorLayout, getString(R.string.signup_successful_sb), Snackbar.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Snackbar.make(coordinatorLayout, getString(R.string.signup_failed_sb), Snackbar.LENGTH_SHORT).show();
                                        }


                                    }
                                });
                            }
                        }).show();
                        break;
                    case R.id.item_login:
                        fullNameEt.setVisibility(View.GONE);

                        builder.setView(dialogView).setPositiveButton(getString(R.string.login_tv), new DialogInterface.OnClickListener() {
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
                                            Snackbar.make(coordinatorLayout, getString(R.string.login_successful_sb), Snackbar.LENGTH_SHORT).show();
                                        else
                                            Snackbar.make(coordinatorLayout, getString(R.string.login_failed_sb), Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).show();
                        break;
                    case R.id.item_search:
                        //TODO return to initial fragment (genres)
                        GenreFragment homeFragment = GenreFragment.newInstance();

                        FragmentTransaction genreTransaction = getSupportFragmentManager().beginTransaction();
                        genreTransaction.replace(R.id.recycler_container, homeFragment, GENRE_FRAGMENT_TAG);
                        genreTransaction.addToBackStack(null).commit();
                        break;

                    case R.id.item_profile:
                        ProfileFragment profileFragment = ProfileFragment.newInstance();

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, profileFragment, PROFILE_FRAGMENT_TAG);
                        transaction.addToBackStack(null).commit();
                        break;
                    case R.id.item_confirmations:
                        ConfirmationsFragment confirmationsFragment = ConfirmationsFragment.newInstance();

                        FragmentTransaction confirmationsTransaction = getSupportFragmentManager().beginTransaction();
                        confirmationsTransaction.replace(R.id.recycler_container, confirmationsFragment, CONFIRMATIONS_FRAGMENT_TAG);
                        confirmationsTransaction.addToBackStack(null).commit();
                        break;
                    case R.id.item_messages:
                        //ChatFragment chatFragment = ChatFragment.newInstance();

                        UsersFragment usersFragment1 = UsersFragment.newInstance();
                        FragmentTransaction chatTransaction = getSupportFragmentManager().beginTransaction();
                        chatTransaction.replace(R.id.recycler_container, usersFragment1, MESSAGES_FRAGMENT_TAG);
                        chatTransaction.addToBackStack(null).commit();
                        break;
                    case R.id.item_settings:
                        //TODO open settings activity
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;

                    case R.id.item_logout:

                        firebaseAuth.signOut();
                        Snackbar.make(coordinatorLayout, getString(R.string.logged_out_sb), Snackbar.LENGTH_SHORT).show();
                        GenreFragment genreFragment = GenreFragment.newInstance();

                        FragmentTransaction homeTransaction = getSupportFragmentManager().beginTransaction();
                        homeTransaction.replace(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
                        homeTransaction.commit();
                        break;
                }

                return false;
            }
        });


        ctl = findViewById(R.id.collapsing_layout);
        ctl.setTitle(getString(R.string.please_log_in_tv));
        ctl.setExpandedTitleColor(getResources().getColor(R.color.white));//;setCollapsedTitleTextColor(R.color.white);

        //initializing authlistener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //update header textview
                View headerView = navigationView.getHeaderView(0);
                TextView userTv = headerView.findViewById(R.id.navigation_header_text_view);

                user = firebaseAuth.getCurrentUser();

                //login or sign up
                if(user != null){
                    //----------------------------
                    mUID=user.getUid();
                    //----------------------------
                    if(fullName != null){ //sign up
                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName).build())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                fullName = null;
                                if(task.isSuccessful())
                                    Snackbar.make(coordinatorLayout, getString(R.string.welcome_sb) + user.getDisplayName() + "!", Snackbar.LENGTH_SHORT).show();
                                    Snackbar.make(coordinatorLayout, user.getDisplayName() + getString(R.string.is_now_connected_sb), Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //update menu ui - user logged in
                    userTv.setText(user.getDisplayName() + getString(R.string.is_now_connected_sb));
                    ctl.setTitle(getString(R.string.welcome_sb));
                    ctl.setCollapsedTitleTextColor(0xffffff);


                    navigationView.getMenu().findItem(R.id.item_login).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_search).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_profile).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_confirmations).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_messages).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_settings).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(true);

                    //---------------------------
                    //save uid of currently signed in user in shared preferences
                    SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
                    SharedPreferences.Editor editor= sp.edit();
                    editor.putString("Current_USERID",mUID);
                    editor.apply();
                    //---------------------------


                }
                else{ //logged out or not sign in yet
                    //update ui
                    userTv.setText(getString(R.string.please_log_in_tv));
                    ctl.setTitle(getString(R.string.please_log_in_tv));

                    navigationView.getMenu().findItem(R.id.item_login).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(false);

                    navigationView.getMenu().findItem(R.id.item_search).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_profile).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_confirmations).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_messages).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_settings).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(false);

                }
            }
        };

        Log.d("MainActivity", "starting MainActivity");
        Bundle bundle = new Bundle();
        String skillToFind = getIntent().getStringExtra("skillToFind");
        if (skillToFind != null) { //from notification
            Log.d("MainActivity", "string in intent is not null: " + skillToFind);

            bundle.putCharSequence("subGenre", skillToFind);

            SearchUsersFragment searchUsersFragment = SearchUsersFragment.newInstance();
            searchUsersFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.recycler_container, searchUsersFragment, "SEARCH_USERS_FRAG");
            transaction.commit();

        } else { //regular
            Log.d("MainActivity", "string from intent is null");

            GenreFragment genreFragment = GenreFragment.newInstance();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
            transaction.commit();
        }






        //adding genres fragment
/*

        GenreFragment genreFragment = GenreFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
        transaction.commit();
*/


        initSearch();


        //update Token -------------------
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){
                            String token =task.getResult().getToken();
                            Log.d("Token---" ,token);
                            Log.d("Token---getResult" ,task.getResult().toString());
                            updateToken(token);
                        }
                    }
                });
        //updateToken(String.valueOf(FirebaseInstanceId.getInstance().getInstanceId()));
        //----------------------
    }
    //----------------------
    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Log.d("MA", "current user is not null");
            String currentUserUID = currentUser.getUid();
            ref.child(currentUserUID).setValue(mToken);

        }
        else{
            Log.d("MA", "current user is null");

        }

        /*if(user!=null) {
            DatabaseReference temp = ref.child(mUID);
            temp.setValue(mToken);
        }*/
    }
    //----------------------
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment=null;//dbGenres to the frag we want to open

                    switch(menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new GenreFragment();
                            break;
                        case R.id.nav_favorites:
                            //getActionBar().setTitle("Favorites");
                            selectedFragment = new FavoritesFragment();
                            break;
                        case R.id.nav_chat:
                            //getActionBar().setTitle("Chat");
                            selectedFragment = new UsersFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recycler_container, selectedFragment)
                            .commit();

                    return true; //select the clicked item
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else if(id == R.id.toolbar_menu){
            //return true;
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

    public void initSearch(){

        if(searchView!=null){
            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setIconified(false);
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(query.length()>0) {
                        //adding genres fragment
                        SearchFragment searchFragment = SearchFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("search_genre_query", query);
                        searchFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, searchFragment, SEARCH_FRAGMENT_TAG);
                        transaction.commit();
                    }
                    else{
                        GenreFragment genreFragment = GenreFragment.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
                        transaction.commit();

                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText.length()>0) {
                        //adding genres fragment
                        SearchFragment searchFragment = SearchFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("search_genre_query", newText);
                        searchFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, searchFragment, SEARCH_FRAGMENT_TAG);
                        transaction.commit();
                    }
                    else{
                        GenreFragment genreFragment = GenreFragment.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
                        transaction.commit();
                    }
                    return true;
                }
            });
        }

        if(toolbarSearchView!=null){
            toolbarSearchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolbarSearchView.setIconified(false);
                }
            });
            toolbarSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(query.length()>0) {
                        //adding genres fragment
                        SearchFragment searchFragment = SearchFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("search_genre_query", query);
                        searchFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, searchFragment, SEARCH_FRAGMENT_TAG);
                        transaction.commit();
                    }
                    else{
                        GenreFragment genreFragment = GenreFragment.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
                        transaction.commit();

                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText.length()>0) {
                        //adding genres fragment
                        SearchFragment searchFragment = SearchFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("search_genre_query", newText);
                        searchFragment.setArguments(bundle);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, searchFragment, SEARCH_FRAGMENT_TAG);
                        transaction.commit();
                    }
                    else{
                        GenreFragment genreFragment = GenreFragment.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.recycler_container, genreFragment, GENRE_FRAGMENT_TAG);
                        transaction.commit();
                    }
                    return true;
                }
            });
        }
    }
}

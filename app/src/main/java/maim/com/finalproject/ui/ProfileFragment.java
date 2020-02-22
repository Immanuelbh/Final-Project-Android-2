package maim.com.finalproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import maim.com.finalproject.R;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final int FINE_PERMISSION_REQ = 202;
    private static final int CAMERA_REQUEST = 1010;
    private static final int WRITE_PERMISSION_REQUEST = 1213;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference users;

    private File imgFile;
    private String currentImagePath;

    private ImageView profileIv;
    private TextView nameTv, ageTv, emailTv, rangeTv, locationResultTv;
    private Button editLocationBtn, editSkillsBtn;
    private CoordinatorLayout coordinatorLayout;

    private Context context;
    private View rootView;
    private Bitmap profileImgBitmap = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    public static ProfileFragment newInstance(){
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        //init firebase
        firebaseAuth= FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");

        profileIv = rootView.findViewById(R.id.profileIv);
        nameTv = rootView.findViewById(R.id.profile_name_tv);
        ageTv = rootView.findViewById(R.id.profile_age_tv);
        emailTv = rootView.findViewById(R.id.profile_email_tv);
        rangeTv = rootView.findViewById(R.id.profile_range_tv);
        locationResultTv = rootView.findViewById(R.id.location_result);
        coordinatorLayout = rootView.findViewById(R.id.coordinator);
        editLocationBtn = rootView.findViewById(R.id.profile_edit_location_btn);
        editSkillsBtn = rootView.findViewById(R.id.profile_edit_skills_btn);


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "no permission for camera", Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                    } else {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            imgFile = new File(String.valueOf(createImageFile()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (imgFile != null) {
                            Uri imgUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", imgFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                            startActivityForResult(intent, CAMERA_REQUEST);
                        }

                    }
                } else {
                    Toast.makeText(context, "SDK lower than 24!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //query the db
        if(user != null){

            if(user.getPhotoUrl() != null){
                Glide.with(rootView.getContext())
                        .load(user.getPhotoUrl())
                        .error(R.drawable.ic_add_image)
                        .into(profileIv);
            }

            Query query = users.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        //get data
                        String name = ds.child("name").getValue()+"";
                        String age = ds.child("age").getValue()+"";
                        String email = ds.child("email").getValue()+"";
                        String range = ds.child("maxRange").getValue()+"";
                        String lat = ds.child("locationLat").getValue()+"";
                        String lon = ds.child("locationLon").getValue()+"";
                        String image = ds.child("imageUrl").getValue()+"";

                        nameTv.setText(name);
                        ageTv.setText(age);
                        emailTv.setText(email);
                        rangeTv.setText(range);
                        locationResultTv.setText("lat: " + lat + " lon: " + lon);

                        /*Glide.with(rootView.getContext())
                                .load(image)
                                .error(R.drawable.ic_add_image)
                                .into(profileIv);*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        editLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startMap();
                } else {
                    // Show rationale and request permission.
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQ);
                }
            }
        });

        editSkillsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open addSkill Activity
                Intent addSkillIntent = new Intent(context, SignupAddSkills.class);
                startActivity(addSkillIntent);

            }
        });




        return rootView;
    }

    private File createImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("HHmmss_ddMMyyyy").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,timestamp,storageDir);

        currentImagePath = image.getAbsolutePath();
        return image;
    }

    private void startMap() {
        Intent mapIntent = new Intent(context, MapsActivity.class);
        context.startActivity(mapIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_REQ) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMap();
            } else {
                // Permission was denied. Display an error message.
                Snackbar.make(coordinatorLayout, "The map cannot start without permission", Snackbar.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == WRITE_PERMISSION_REQUEST){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "Cannot take picture", Toast.LENGTH_SHORT).show();
            }
            else{

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){

            File file = new File(currentImagePath);
            try {
                profileImgBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(profileImgBitmap != null){
                profileIv.setImageBitmap(profileImgBitmap);
                handleUpload(profileImgBitmap);
            }

        }
    }

    private void handleUpload(Bitmap profileImgBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImgBitmap.compress(Bitmap.CompressFormat.JPEG,70,baos); //save 70% quality to db

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PF", "onFailure: ", e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("PF", "onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Profile image failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

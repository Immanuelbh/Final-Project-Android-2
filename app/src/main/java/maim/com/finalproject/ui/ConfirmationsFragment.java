package maim.com.finalproject.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.ConfirmationsAdapter;
import maim.com.finalproject.model.Confirmation;


public class ConfirmationsFragment extends Fragment {


    private List<Confirmation> confirmationsList = new ArrayList<>();

    public static ConfirmationsFragment newInstance(){
        ConfirmationsFragment confirmationsFragment = new ConfirmationsFragment();
        return confirmationsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.confirmations_fragment, container, false);

        RecyclerView confirmationsRecycler = rootView.findViewById(R.id.confirmations_recycler);
        confirmationsRecycler.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        confirmationsRecycler.setHasFixedSize(true);
        
        final ConfirmationsAdapter confirmationsAdapter = new ConfirmationsAdapter(rootView.getContext(), confirmationsList);
        confirmationsRecycler.setAdapter(confirmationsAdapter);
        
        //load the confirmations
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage(getString(R.string.loading_confirms_pd));
        progressDialog.show();

        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbConfirmations = FirebaseDatabase.getInstance().getReference("users").child(myUser.getUid()).child("myConfirmations");

        dbConfirmations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                confirmationsList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Confirmation confirmation = snapshot.getValue(Confirmation.class);
                        if(confirmation == null){
                            Log.d("Confirmation", "Confirmation object is null");
                        }
                        confirmationsList.add(confirmation);
                    }
                    confirmationsAdapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootView;
    }
}

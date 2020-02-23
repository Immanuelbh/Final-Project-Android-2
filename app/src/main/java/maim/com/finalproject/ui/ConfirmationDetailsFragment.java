package maim.com.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Confirmation;

public class ConfirmationDetailsFragment extends Fragment {

    private TextView learn1, learn2, skill1Tv, skill2Tv, date1Tv, date2Tv, chooseSkillTv;
    private RelativeLayout bgRl, user2Rl;
    private LinearLayout receiverConfirmationLl;
    private String senderUid, receiverUid, senderCid, receiverCid;
    private FirebaseUser fbUser;

    public static ConfirmationDetailsFragment newInstance(){
        return new ConfirmationDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.confirmation_details_fragment, container, false);

        bgRl = rootView.findViewById(R.id.bg_confirmation_details);
        receiverConfirmationLl = rootView.findViewById(R.id.conf_his_conf);
        learn1 = rootView.findViewById(R.id.conf_learn_1);
        skill1Tv = rootView.findViewById(R.id.conf_skill_1);
        date1Tv = rootView.findViewById(R.id.conf_date_1);
        learn2 = rootView.findViewById(R.id.conf_learn_2);
        skill2Tv = rootView.findViewById(R.id.conf_skill_2);
        date2Tv = rootView.findViewById(R.id.conf_date_2);
        chooseSkillTv = rootView.findViewById(R.id.conf_choose_skill);
        user2Rl = rootView.findViewById(R.id.conf_user2_layout);


        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        final Bundle bundle = this.getArguments();
        senderUid = bundle.getString("senderUid");
        receiverUid = bundle.getString("receiverUid");
        senderCid = bundle.getString("senderCid");
        receiverCid = bundle.getString("receiverCid");

        //search through the confirmations of the current user
        ///get current user
        ///add event listener and run through confirmations
        ///check if current confirmation equals senderCid or receiverCid

        DatabaseReference confirmationRef = FirebaseDatabase.getInstance().getReference("users").child(fbUser.getUid()).child("myConfirmations");
        confirmationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Confirmation confirmation = ds.getValue(Confirmation.class);
                    if(confirmation.getSenderUid().equals(fbUser.getUid()) ||
                        confirmation.getReceiverUid().equals(fbUser.getUid())){
                        //found the correct confirmation || applies for a single confirmation
                        //load ui
                        learn1.setText(confirmation.getSenderName() + " wants to learn ");
                        skill1Tv.setText(confirmation.getSkill1());
                        date1Tv.setText(confirmation.getDate1());

                        learn2.setText(confirmation.getReceiverName());

                        skill2Tv.setText(confirmation.getSkill2());
                        date2Tv.setText(confirmation.getDate2());
                        /*
                        if(skill2Tv.getText().toString().equals("")){
                            chooseSkillTv.setVisibility(View.VISIBLE);
                            user2Rl.setVisibility(View.GONE);
                        }
                        else{
                            user2Rl.setVisibility(View.VISIBLE);
                            chooseSkillTv.setVisibility(View.GONE);

                        }*/

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //TODO make layout clickable only for the other user.
        bgRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        if(fbUser.getUid().equals(receiverUid)){
            receiverConfirmationLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open the confirmation profile of the other user

                    SearchedConfirmationFragment searchedConfirmationFragment = SearchedConfirmationFragment.newInstance();
                    Bundle bundleToSend = bundle;
                    searchedConfirmationFragment.setArguments(bundleToSend);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.cover_confirmation_frame, searchedConfirmationFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }


        return rootView;
    }

}

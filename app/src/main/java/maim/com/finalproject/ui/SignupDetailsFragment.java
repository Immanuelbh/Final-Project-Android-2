package maim.com.finalproject.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import maim.com.finalproject.R;

public class SignupDetailsFragment extends Fragment {

    private static final int MIN_AGE = 18;
    SeekBar ageSb;
    SeekBar rangeSb;

    public static SignupDetailsFragment newInstance(){
        SignupDetailsFragment signupDetailsFragment = new SignupDetailsFragment();
        return signupDetailsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_details_layout, container, false);

        final TextView signupAgeTv = rootView.findViewById(R.id.signup_age_tv);
        final TextView signupRangeTv = rootView.findViewById(R.id.signup_range_tv);

        ageSb = rootView.findViewById(R.id.signup_age_seekbar);
        rangeSb = rootView.findViewById(R.id.signup_range_seekbar);

        //signupAgeTv.setText(ageSb.getProgress());
        //signupRangeTv.setText(ageSb.getProgress());

        ageSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress >= 0 && progress <= ageSb.getMax()){
                        String rangeProgress = String.valueOf(progress + MIN_AGE);

                        signupAgeTv.setText(rangeProgress);
                        ageSb.setSecondaryProgress(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rangeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress >= 0 && progress <= rangeSb.getMax()){
                        String rangeProgress = String.valueOf((progress * 5)+5);

                        signupRangeTv.setText(rangeProgress + " km");
                        rangeSb.setSecondaryProgress(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*
        View signupDialog = getLayoutInflater().inflate(R.layout.signup_details_layout, null);
        builder.setView(signupDialog).setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        }).show();
        */
        return rootView;
    }
}

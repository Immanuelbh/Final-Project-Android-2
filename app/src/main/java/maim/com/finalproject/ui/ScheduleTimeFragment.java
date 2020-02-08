package maim.com.finalproject.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import maim.com.finalproject.R;

public class ScheduleTimeFragment extends Fragment {
  public static ScheduleTimeFragment newInstance(){
        return new ScheduleTimeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_time_fragment, container, false);


        return rootView;
    }
}

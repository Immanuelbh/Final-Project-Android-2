package maim.com.finalproject.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.ui.SubGenreFragment;

public class SignupSubGenreAdapter extends RecyclerView.Adapter<SignupSubGenreAdapter.signupSubGenreViewHolder> {

    private Context ssCtx;
    private List<SubGenre> subGenres;
    private boolean checked = false;
    private String type;
    private SubGenre rbSubGenre;
    private int skillSelected = -1;
    SharedPreferences sp;
    private HashSet<String> mySkills;// = new HashSet<>();

    public SignupSubGenreAdapter(Context ssCtx, List<SubGenre> subGenres, String type){
        this.ssCtx = ssCtx;
        this.subGenres = subGenres;
        this.type = type;
        if(type.equals("checkbox")){
            sp = ssCtx.getSharedPreferences("mySkills", Context.MODE_PRIVATE);
        }
        else{
            sp = ssCtx.getSharedPreferences("skillSelected", Context.MODE_PRIVATE);
        }
    }

    public class signupSubGenreViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout;
        TextView titleTv;
        CheckBox checkBox;
        RadioButton radioButton;
        //ImageView bgIv;

        public signupSubGenreViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.signup_genre_ll);
            titleTv = itemView.findViewById(R.id.signup_genre_tv);
            checkBox = itemView.findViewById(R.id.signup_genre_cb);
            radioButton = itemView.findViewById(R.id.signup_genre_rb);
            //bgIv = itemView.findViewById(R.id.genre_cell_bg);
        }
    }

    @NonNull
    @Override
    public signupSubGenreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ssCtx).inflate(R.layout.signup_genre_cell, parent, false);

        SharedPreferences sharedPreferences =  ssCtx.getSharedPreferences("mySkills", Context.MODE_PRIVATE);
        mySkills = (HashSet<String>) sharedPreferences.getStringSet("mySkills", new HashSet<String>());

        final signupSubGenreViewHolder gvh = new signupSubGenreViewHolder(view);
        gvh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    mySkills.add(subGenres.get(gvh.getAdapterPosition()).getName());
                }
                else {
                    mySkills.remove(subGenres.get(gvh.getAdapterPosition()).getName());
                }

                sp.edit().putStringSet("mySkills", mySkills).apply();

            }
        });

        gvh.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skillSelected >= 0){
                    notifyItemChanged(skillSelected);
                }
                skillSelected = gvh.getAdapterPosition();
                notifyItemChanged(skillSelected);
            }
        });

        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SignupSubGenreAdapter.signupSubGenreViewHolder holder, int position) {
        SubGenre subGenre = subGenres.get(position);
        holder.titleTv.setText(subGenre.getName());

        switch (type){
            case "radio":
                holder.radioButton.setVisibility(View.VISIBLE);
                break;
            case "checkbox":
                holder.checkBox.setVisibility(View.VISIBLE);
                break;
        }

        if (skillSelected == position){
            holder.radioButton.setChecked(true);
            //save to shared pref

            Log.d("SSGA", "Attempting to write skill selected (" + subGenre.getName() + ") to shared pref");
            sp.edit().putString("skillSelected", subGenre.getName().toLowerCase()).apply();

        }
        else{
            holder.radioButton.setChecked(false);
        }

        if (!mySkills.isEmpty() && mySkills.contains(subGenre.getName())){
            holder.checkBox.setChecked(true);
        }

        /*
        Glide.with(sgCtx)
                .load(genre.getImageUrl()+"")
                .error(R.drawable.no_image_available_comp)
                .into(holder.bgIv);

         */
        //TODO change image
    }

    @Override
    public int getItemCount() {
        return subGenres.size();
    }


}
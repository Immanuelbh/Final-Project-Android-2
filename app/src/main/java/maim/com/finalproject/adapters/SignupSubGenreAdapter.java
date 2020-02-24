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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private final String skillType;
    private Context ssCtx;
    private List<SubGenre> subGenres;
    private String type;
    private int skillSelected = -1;
    private SharedPreferences sp;
    private HashSet<String> mySkills;
    private HashMap<String, Object> hashMap = new HashMap<>();
    private DatabaseReference skillRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public SignupSubGenreAdapter(Context ssCtx, List<SubGenre> subGenres, String type, HashSet<String> mySkills, String skillType){
        this.ssCtx = ssCtx;
        this.subGenres = subGenres;
        this.type = type;
        this.mySkills = mySkills;
        this.skillType = skillType;

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

        final signupSubGenreViewHolder gvh = new signupSubGenreViewHolder(view);
        if(type.equals("checkbox")){

            gvh.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = subGenres.get(gvh.getAdapterPosition()).getName();
                    String imageUrl = subGenres.get(gvh.getAdapterPosition()).getImageUrl();

                    //update db
                    if(((CompoundButton) view).isChecked()){
                        HashMap<String, Object> dbSubGenre = new HashMap<>();
                        dbSubGenre.put("imageUrl", imageUrl);
                        dbSubGenre.put("name", name);

                        hashMap.put(name.toLowerCase(), dbSubGenre);
                        //mySkills.add(name);
                        if(skillType.equals("skill"))
                            skillRef.child("mySkillsList").updateChildren(hashMap);
                        else
                            skillRef.child("myLearnList").updateChildren(hashMap);


                    }
                    else {

                        hashMap.remove(name.toLowerCase());
                        //mySkills.remove(name);
                        if(skillType.equals("skill"))
                            skillRef.child("mySkillsList").child(name.toLowerCase()).removeValue();
                        else
                            skillRef.child("myLearnList").child(name.toLowerCase()).removeValue();

                    }

                }
            });
        }
        else if (type.equals("radio")){
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

        }


        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SignupSubGenreAdapter.signupSubGenreViewHolder holder, int position) {
        SubGenre subGenre = subGenres.get(position);
        holder.titleTv.setText(subGenre.getName());

        switch (type){
            case "radio":
                holder.radioButton.setVisibility(View.VISIBLE);
                if (skillSelected == position){
                    holder.radioButton.setChecked(true);
                    //save to shared pref

                    Log.d("SSGA", "Attempting to write skill selected (" + subGenre.getName() + ") to shared pref");
                    sp.edit().putString("skillSelected", subGenre.getName().toLowerCase()).apply();

                }
                else{
                    holder.radioButton.setChecked(false);
                }
                break;
            case "checkbox":
                holder.checkBox.setVisibility(View.VISIBLE);
                //check if exists in db
                //if it does - set checkbox to checked
                DatabaseReference db;
                if(skillType.equals("skill"))
                    db = skillRef.child("mySkillsList").child(subGenre.getName().toLowerCase());
                else
                    db = skillRef.child("myLearnList").child(subGenre.getName().toLowerCase());

                if(mySkills.contains(subGenre.getName())){
                    holder.checkBox.setChecked(true);
                }
                break;
            case "plain":
                holder.checkBox.setVisibility(View.GONE);
                holder.radioButton.setVisibility(View.GONE);


        }

        /*
        Glide.with(ssCtx)
                .load(subGenre.getImageUrl()+"")
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
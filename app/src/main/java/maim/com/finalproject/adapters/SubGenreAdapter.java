package maim.com.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.ui.SearchUsersFragment;

public class SubGenreAdapter extends RecyclerView.Adapter<SubGenreAdapter.SubGenreViewHolder> {

    private static final String SEARCH_USERS_FRAGMENT = "search_users_fragment";
    private Context sCtx;
    private List<SubGenre> subGenres;
    private SubGenreListener listener;

    public interface SubGenreListener{
        void onSubGenreClicked(int position, View view);
    }

    public void setListener(SubGenreListener listener){
        this.listener = listener;
    }

    public SubGenreAdapter(Context sCtx, List<SubGenre> subGenres){
        this.sCtx = sCtx;
        this.subGenres = subGenres;
    }

    public class SubGenreViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        TextView titleTv;
        ImageView imageView;

        public SubGenreViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.genre_rl);
            titleTv = itemView.findViewById(R.id.genre_title_tv);
            imageView = itemView.findViewById(R.id.genre_cell_bg);
        }
    }

    @NonNull
    @Override
    public SubGenreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(sCtx).inflate(R.layout.genre_cell, parent, false);

        final SubGenreViewHolder svh = new SubGenreViewHolder(view);
        svh.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchUsersFragment searchUsersFragment = SearchUsersFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("subGenre", subGenres.get(svh.getAdapterPosition()).getName());

                searchUsersFragment.setArguments(bundle);
                FragmentTransaction usersTransaction = ((AppCompatActivity)sCtx).getSupportFragmentManager().beginTransaction();
                usersTransaction.replace(R.id.recycler_container, searchUsersFragment, SEARCH_USERS_FRAGMENT);
                usersTransaction.addToBackStack(null).commit();
                //searchUsersSkills(subGenres.get(svh.getAdapterPosition()));
            }
        });
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubGenreViewHolder holder, int position) {
        SubGenre subGenre = subGenres.get(position);
        holder.titleTv.setText(subGenre.getName());
        Glide.with(sCtx)
                .load(subGenre.getImageUrl()+"")
                .error(R.drawable.no_image_available_comp)
                .into(holder.imageView);
        Log.d("SGA", "image url: " + subGenre.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return subGenres.size();
    }
}

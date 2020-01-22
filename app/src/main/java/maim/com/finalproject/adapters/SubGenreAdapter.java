package maim.com.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.ui.GenreFragment;
import maim.com.finalproject.ui.SubGenreFragment;

public class SubGenreAdapter extends RecyclerView.Adapter<SubGenreAdapter.SubGenreViewHolder> {

    private Context sCtx;
    private List<SubGenre> subGenres;
    private SubGenreListener listener;
    //TODO genre listener

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

        public SubGenreViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.genre_rl);
            titleTv = itemView.findViewById(R.id.genre_title_tv);
        }
    }

    @NonNull
    @Override
    public SubGenreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(sCtx).inflate(R.layout.genre_cell, parent, false);

        final SubGenreViewHolder svh = new SubGenreViewHolder(view);
        //TODO set on click listener
        svh.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO add query

            }
        });
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubGenreViewHolder holder, int position) {
        SubGenre subGenre = subGenres.get(position);
        holder.titleTv.setText(subGenre.getName());
        //TODO change image
    }

    @Override
    public int getItemCount() {
        return subGenres.size();
    }
}

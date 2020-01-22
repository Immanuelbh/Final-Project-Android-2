package maim.com.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private Context gCtx;
    private List<Genre> genres;
    //TODO genre listener

    public GenreAdapter(Context gCtx, List<Genre> genres){
        this.gCtx = gCtx;
        this.genres = genres;
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        TextView titleTv;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.genre_rl);
            titleTv = itemView.findViewById(R.id.genre_title_tv);
        }
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(gCtx).inflate(R.layout.genre_cell, parent, false);

        final GenreViewHolder gvh = new GenreViewHolder(view);
        //TODO set on click listener

        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.titleTv.setText(genre.getName());
        //TODO change image
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }
}

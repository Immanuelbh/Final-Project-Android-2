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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.io.Serializable;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.ui.SubGenreFragment;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private Context gCtx;
    private List<Genre> genres;
    private GenreListener listener;

    public interface GenreListener{
        void onGenreClicked(int position, View view);
    }

    public void setListener(GenreListener listener){
        this.listener = listener;
    }

    public GenreAdapter(Context gCtx, List<Genre> genres){
        this.gCtx = gCtx;
        this.genres = genres;
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        TextView titleTv;
        ImageView bgIv;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.genre_rl);
            titleTv = itemView.findViewById(R.id.genre_title_tv);
            bgIv = itemView.findViewById(R.id.genre_cell_bg);
        }
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(gCtx).inflate(R.layout.genre_cell, parent, false);

        final GenreViewHolder gvh = new GenreViewHolder(view);
        gvh.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adding genres fragment
                SubGenreFragment subGenreFragment = SubGenreFragment.newInstance();
                Bundle bundle = new Bundle();

                bundle.putSerializable("genre", (Serializable) genres.get(gvh.getAdapterPosition()));
                bundle.putCharSequence("genre_name",genres.get(gvh.getAdapterPosition()).getName());
                bundle.putInt("current_genre", gvh.getAdapterPosition());
                subGenreFragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)gCtx).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.recycler_container, subGenreFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.titleTv.setText(genre.getName());
        Glide.with(gCtx)
                .load(genre.getImageUrl()+"")
                .error(R.drawable.no_image_available_comp)
                .into(holder.bgIv);


        //TODO change image
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }
}

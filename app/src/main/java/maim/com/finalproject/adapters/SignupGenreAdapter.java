package maim.com.finalproject.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Genre;
import maim.com.finalproject.ui.SubGenreFragment;

public class SignupGenreAdapter extends RecyclerView.Adapter<SignupGenreAdapter.signupGenreViewHolder> {

    private final String type;
    private Context sgCtx;
    private List<Genre> genres;

    public SignupGenreAdapter(Context sgCtx, List<Genre> genres, String type){
        this.sgCtx = sgCtx;
        this.genres = genres;
        this.type = type;
    }

    public class signupGenreViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout;
        TextView titleTv;
        //ImageView bgIv;

        public signupGenreViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.signup_genre_ll);
            titleTv = itemView.findViewById(R.id.signup_genre_tv);
            //bgIv = itemView.findViewById(R.id.genre_cell_bg);
        }
    }

    @NonNull
    @Override
    public signupGenreViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(sgCtx).inflate(R.layout.signup_genre_cell, parent, false);

        final signupGenreViewHolder gvh = new signupGenreViewHolder(view);
        gvh.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adding genres fragment
                SubGenreFragment subGenreFragment = SubGenreFragment.newInstance();
                Bundle bundle = new Bundle();

                bundle.putSerializable("genre", (Serializable) genres.get(gvh.getAdapterPosition()));
                bundle.putCharSequence("action","signup");
                bundle.putCharSequence("type",type);

                subGenreFragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)sgCtx).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.as_recycler_container, subGenreFragment);
                transaction.addToBackStack(null);

                transaction.commit();

            }
        });
        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SignupGenreAdapter.signupGenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.titleTv.setText(genre.getName());
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
        return genres.size();
    }
}

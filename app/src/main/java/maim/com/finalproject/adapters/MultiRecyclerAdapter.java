package maim.com.finalproject.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import maim.com.finalproject.model.SubGenre;
import maim.com.finalproject.ui.SearchUsersFragment;
import maim.com.finalproject.ui.SubGenreFragment;

public class MultiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG= "RecyclerAdapter";
    private static int TYPE_GENRE = 1;
    private static int TYPE_SUBGENRE = 2;
    private static int TYPE_USERS = 3;

    private Context gCtx;
    private List<Object> typeList;


    public MultiRecyclerAdapter(Context gCtx, List<Object> typeList) {
        this.gCtx = gCtx;
        this.typeList = typeList;
    }

//-----------------------------------------------------------------------------------------------
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

    public class SubGenreViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        TextView titleTv;
        ImageView bgIv;

        public SubGenreViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.genre_rl);
            titleTv = itemView.findViewById(R.id.genre_title_tv);
            bgIv = itemView.findViewById(R.id.genre_cell_bg);

        }
    }
//-----------------------------------------------------------------------------------------------

    //returns the type according to our logic
    @Override
    public int getItemViewType(int position) {
        if(typeList.get(position) instanceof Genre){
            return TYPE_GENRE;
        }
        return TYPE_SUBGENRE;
    }

    @NonNull
    @Override //returns all the view-holders with switch/if statement
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(gCtx).inflate(R.layout.genre_cell, parent, false);
        final GenreViewHolder gvh = new GenreViewHolder(view);
        final SubGenreViewHolder svh = new SubGenreViewHolder(view);

        if(viewType==TYPE_GENRE) {     //the returned TYPE_value from getItemViewType

            gvh.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //adding genres fragment
                    SubGenreFragment subGenreFragment = SubGenreFragment.newInstance();
                    Bundle bundle = new Bundle();

                    bundle.putSerializable("genre", (Serializable) typeList.get(gvh.getAdapterPosition()));
                    bundle.putCharSequence("genre_name", ((Genre)typeList.get(gvh.getAdapterPosition())).getName());
                    bundle.putInt("current_genre", gvh.getAdapterPosition());
                    subGenreFragment.setArguments(bundle);

                    FragmentTransaction transaction = ((AppCompatActivity) gCtx).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.recycler_container, subGenreFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            return gvh;
        }
      //else
        svh.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO add query
                SearchUsersFragment searchUsersFragment = SearchUsersFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("subGenre", ((SubGenre)typeList.get(svh.getAdapterPosition())).getName());

                searchUsersFragment.setArguments(bundle);
                FragmentTransaction usersTransaction = ((AppCompatActivity)gCtx).getSupportFragmentManager().beginTransaction();
                usersTransaction.replace(R.id.recycler_container, searchUsersFragment);
                usersTransaction.addToBackStack(null).commit();
            }
        });
        return svh;
    }

//-----------------------------------------------------------------------------------------------
    //binds the view holder according to the position of the recycler view item
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position)==TYPE_GENRE){
            //bind viewholder one
            GenreViewHolder genreViewHolder = (GenreViewHolder) holder; //casting to the correct VH
            Genre genre = (Genre) typeList.get(position);
            genreViewHolder.titleTv.setText(genre.getName());
            Glide.with(gCtx)
                    .load(genre.getImageUrl()+"")
                    .error(R.drawable.no_image_available_comp)
                    .into(genreViewHolder.bgIv);

        } else{
           //bind viewholder two
            SubGenreViewHolder subGenreViewHolder =(SubGenreViewHolder) holder; //casting to the correct VH
            SubGenre subGenre = (SubGenre) typeList.get(position);
            subGenreViewHolder.titleTv.setText(subGenre.getName());
            Glide.with(gCtx)
                    .load(subGenre.getImageUrl()+"")
                    .error(R.drawable.no_image_available_comp)
                    .into(subGenreViewHolder.bgIv);        }
    }


    @Override
    public int getItemCount() {
        return typeList.size();
    }

}

package maim.com.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.User;
import maim.com.finalproject.ui.ChatActivity;
import maim.com.finalproject.ui.SearchedConfirmationFragment;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private Context uCtx;
    private List<User> userList;
    private String skillWant;
    //StorageReference reference;

    public UserAdapter(Context uCtx, List<User> users){
        this.uCtx = uCtx;
        this.userList = users;
    }
    public UserAdapter(Context uCtx, List<User> users, String skillWant){
        this.uCtx = uCtx;
        this.userList = users;
        this.skillWant = skillWant;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView profileIv;
        TextView nameTv, ageTv, locationTv;
        LinearLayout rowLl;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.row_image_iv);
            nameTv = itemView.findViewById(R.id.row_name_tv);
            //ageTv = itemView.findViewById(R.id.row_age_tv);
            locationTv = itemView.findViewById(R.id.row_location_tv);
            rowLl = itemView.findViewById(R.id.row_ll);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(uCtx).inflate(R.layout.users_row, parent, false);
        final UserViewHolder uvh = new UserViewHolder(view);

        uvh.rowLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(skillWant != null){
                    SearchedConfirmationFragment searchedConfirmationFragment = SearchedConfirmationFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", userList.get(uvh.getAdapterPosition()));
                    bundle.putCharSequence("skillWant", skillWant);
                    searchedConfirmationFragment.setArguments(bundle);

                    FragmentTransaction transaction = ((AppCompatActivity)uCtx).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.recycler_container, searchedConfirmationFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else{
                    //opens user chat
                    Intent intent = new Intent(parent.getContext(), ChatActivity.class);
                    intent.putExtra("user_uid", userList.get(uvh.getAdapterPosition()).getUID());
                    uCtx.startActivity(intent);

                }

            }
        });
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String userUid = user.getUID();
        Log.d("CHAT_ACTIVITY", "userAdapter: useruid : " + userUid);

        holder.nameTv.setText(user.getName());
        //holder.ageTv.setText(user.getAge());
        holder.locationTv.setText(user.getLocationAddress());


        Glide.with(holder.itemView.getContext())
                .load(user.getImageUrl())
                .thumbnail(0.01f)
                .dontAnimate()
                .error(R.drawable.ic_user) //change to default profile image
                .into(holder.profileIv);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}

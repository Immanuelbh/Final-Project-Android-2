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

import java.io.Serializable;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.User;
import maim.com.finalproject.ui.ChatActivity;
import maim.com.finalproject.ui.SearchUsersFragment;
import maim.com.finalproject.ui.SearchedConfirmationFragment;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    Context uCtx;
    List<User> userList;

    public UserAdapter(Context uCtx, List<User> users){
        this.uCtx = uCtx;
        this.userList = users;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView profileIv;
        TextView nameTv;
        LinearLayout rowLl;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.row_image_iv);
            nameTv = itemView.findViewById(R.id.row_name_tv);
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

                SearchedConfirmationFragment searchedConfirmationFragment = SearchedConfirmationFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userList.get(uvh.getAdapterPosition()));
                searchedConfirmationFragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)uCtx).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.recycler_container, searchedConfirmationFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                /* opens user chat
                Intent intent = new Intent(parent.getContext(), ChatActivity.class);
                intent.putExtra("user_uid", userList.get(uvh.getAdapterPosition()).getUID());
                uCtx.startActivity(intent);

                 */
            }
        });
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String userUid = user.getUID();
        Log.d("CHAT_ACTIVITY", "userAdapter: useruid : " + userUid);
        //TODO add image
        holder.nameTv.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}

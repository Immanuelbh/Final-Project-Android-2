package maim.com.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    Context uCtx;
    List<User> userList;

    public UserAdapter(Context uCtx, List<User> users){
        this.uCtx = uCtx;
        this.userList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(uCtx).inflate(R.layout.users_row, parent, false);
        UserViewHolder uvh = new UserViewHolder(view);

        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        //TODO add image
        holder.nameTv.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    //TODO fill in the class

    public class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView profileIv;
        TextView nameTv;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.row_image_iv);
            nameTv = itemView.findViewById(R.id.row_name_tv);

        }
    }
}

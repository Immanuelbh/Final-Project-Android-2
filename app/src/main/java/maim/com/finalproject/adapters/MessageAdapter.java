package maim.com.finalproject.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int MSG_TYPE_RIGHT = 0;
    private static final int MSG_TYPE_LEFT = 1;
    Context mCtx;
    List<Message> messages = new ArrayList<>();
    String imageUrl;
    private FirebaseUser fbUser;

    public MessageAdapter(Context mCtx, List<Message> messages){
        this.mCtx = mCtx;
        this.messages = messages;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{

        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.m_profile_iv);
            messageTv = itemView.findViewById(R.id.m_message_tv);
            timeTv = itemView.findViewById(R.id.m_time_stamp);
            isSeenTv = itemView.findViewById(R.id.m_delivered);

        }
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("M_ADAPTER", "viewType: " + viewType);

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mCtx).inflate(R.layout.chat_row_right, parent, false);
            MessageViewHolder mvh = new MessageViewHolder(view);

            return mvh;
        }
        else { //left
            View view = LayoutInflater.from(mCtx).inflate(R.layout.chat_row_left, parent, false);
            //MessageViewHolder mvh = new MessageViewHolder(view);

            return new MessageViewHolder(view);
        }


        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Message message = messages.get(position);
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(message.getTimeStamp()));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.messageTv.setText(message.getMessage());
        holder.timeTv.setText(dateTime);
        //TODO add image
        if(position == messages.size()-1){
            if(message.isSeen())
                holder.isSeenTv.setText("Seen");
            else
                holder.isSeenTv.setText("Delivered");
        }
        else{
            holder.isSeenTv.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemViewType(int position) {

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if(messages.get(position).getSender().equals(fbUser.getUid())){
            return MSG_TYPE_RIGHT;
        }

        return MSG_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}

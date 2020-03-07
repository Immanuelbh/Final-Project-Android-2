package maim.com.finalproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import maim.com.finalproject.R;
import maim.com.finalproject.model.Confirmation;
import maim.com.finalproject.ui.ConfirmationDetailsFragment;

public class ConfirmationsAdapter extends RecyclerView.Adapter<ConfirmationsAdapter.ConfirmationViewHolder> {

    private Context cCtx;
    private List<Confirmation> confirmationList;
    private LinearLayout confirmationCardLl;

    public ConfirmationsAdapter(Context cCtx, List<Confirmation> confirmations){
        this.cCtx = cCtx;
        this.confirmationList = confirmations;
    }

    public class ConfirmationViewHolder extends RecyclerView.ViewHolder{

        TextView confIndex;
        TextView confCardSName;
        TextView confCardRName;
        TextView confCardSkill1;
        TextView confCardSkill2;
        TextView confCardDate1;
        TextView confCardDate2;
        CardView confCardView;


        public ConfirmationViewHolder(@NonNull View itemView) {
            super(itemView);

            confIndex = itemView.findViewById(R.id.confirmation_card_index);
            confCardSName = itemView.findViewById(R.id.confirmation_card_sname);
            confCardRName = itemView.findViewById(R.id.confirmation_card_rname);
            confCardSkill1 = itemView.findViewById(R.id.confirmation_card_skill1);
            confCardSkill2 = itemView.findViewById(R.id.confirmation_card_skill2);
            confCardDate1 = itemView.findViewById(R.id.confirmation_card_date1);
            confCardDate2 = itemView.findViewById(R.id.confirmation_card_date2);
            confirmationCardLl = itemView.findViewById(R.id.confirmation_card_ll);
            confCardView = itemView.findViewById(R.id.confirmation_card_bg);
        }
    }

    @NonNull
    @Override
    public ConfirmationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cCtx).inflate(R.layout.confirmation_card, parent, false);
        final ConfirmationViewHolder cvh = new ConfirmationViewHolder(view);

        confirmationCardLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationDetailsFragment confirmationDetailsFragment = ConfirmationDetailsFragment.newInstance();
                Bundle bundle = new Bundle();

                bundle.putSerializable("confirmation", confirmationList.get(cvh.getAdapterPosition()));

                confirmationDetailsFragment.setArguments(bundle);
                FragmentTransaction transaction = ((AppCompatActivity)cCtx).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.cover_confirmation_frame, confirmationDetailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmationViewHolder holder, int position) {
        Confirmation confirmation = confirmationList.get(position);

        holder.confIndex.setText((position+1)+"");
        holder.confCardSName.setText(confirmation.getSenderName());
        holder.confCardRName.setText(confirmation.getReceiverName());
        holder.confCardSkill1.setText(confirmation.getSkill1());
        holder.confCardSkill2.setText(confirmation.getSkill2());
        holder.confCardDate1.setText(confirmation.getDate1());
        holder.confCardDate2.setText(confirmation.getDate2());

        if(confirmation.getCompleteStatus().equals("complete")){
            //green bg
            holder.confCardView.setCardBackgroundColor(cCtx.getResources().getColor(R.color.flatGreen));

        }
        else{
            holder.confCardView.setCardBackgroundColor(cCtx.getResources().getColor(R.color.flatYellow));
        }

    }

    @Override
    public int getItemCount() {
        return confirmationList.size();
    }




}

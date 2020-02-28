package maim.com.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import maim.com.finalproject.R;

public class SlideTutorialAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SlideTutorialAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.eat_icon,
            R.drawable.sleep_icon,
            R.drawable.code_icon};

    public String[] slide_headings = {
            "Share a skill you know!",
            "Full notification integration",
            "Sign up and start swapping!"
    };

    public String[] slide_descs = {
            "Sign up and choose the skills you know and the skills you would like to learn.\n " +
                    "Schedule a meeting time and chat with your new teacher!\n" +
                    " ",
            "Get notify when a new user would like to schedule a skill swap with you!\n" +
                    "Our integrated chat will help you keep in touch with the people you had fun with.\n",
            "Feel free to browse through our categories and different genres.\n" +
                    "To get the full experience - finding users in your local area - sign up for free!"
    };

    //count of total headings /total num of slides
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    //assign the view to the main object
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    //inflate all the things in the adapter
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView= view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);
        return view;
    }

    //stops at the last page ,preventing us getting any errors ,will stop there instead of creating multiple slides
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}

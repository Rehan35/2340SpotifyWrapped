package com.example.spotifywrapped2340.Wrapped;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.spotifywrapped2340.R;

import java.util.ArrayList;

public class WrappedAdapter extends PagerAdapter{
    private Context context;
    private ArrayList<WrappedModel> modelArrayList;

    public WrappedAdapter(Context context, ArrayList<WrappedModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, container, false);
        TextView titleTV = view.findViewById(R.id.card_title_wrapped);
        TextView firstTV = view.findViewById(R.id.card_wrapped_first);
        TextView secondTV = view.findViewById(R.id.card_wrapped_second);
        TextView thirdTV = view.findViewById(R.id.card_wrapped_third);
        TextView fourthTV = view.findViewById(R.id.card_wrapped_fourth);
        TextView fifthTV = view.findViewById(R.id.card_wrapped_fifth);

        WrappedModel model = modelArrayList.get(position);
        String title = model.getTitle();
        String first = model.getFirstElement();
        String second = model.getSecondElement();
        String third = model.getThirdElement();
        String fourth = model.getFourthElement();
        String fifth = model.getFifthElement();

        titleTV.setText(title);
        firstTV.setText(first);
        secondTV.setText(second);
        thirdTV.setText(third);
        fourthTV.setText(fourth);
        fifthTV.setText(fifth);

        container.addView(view, position);

        return view;
    }

}

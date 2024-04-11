package com.example.spotifywrapped2340.Wrapped;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.spotifywrapped2340.R;
import com.example.spotifywrapped2340.UserInformation;

import java.util.ArrayList;

public class WrappedActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ArrayList<WrappedModel> modelArrayList;
    private WrappedAdapter wrappedAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapped);

        TextView introName = findViewById(R.id.heyNameTitle);
        introName.setText("Hey, " + UserInformation.getName());

        this.viewPager = findViewById(R.id.view_pager);
        loadCards();

        wrappedAdapter = new WrappedAdapter(this, modelArrayList);
        viewPager.setAdapter(wrappedAdapter);



    }

    private void loadCards() {
        modelArrayList = new ArrayList<>();

        modelArrayList.add(new WrappedModel("Top Albums", "1)",
                "2)", "3)", "4)", "5)"));
        modelArrayList.add(new WrappedModel("Top Songs", "1)",
                "2)", "3)", "4)", "5)"));
        modelArrayList.add(new WrappedModel("Top Artists", "1)",
                "2)", "3)", "4)", "5"));
    }
    protected void onDestroy() {
        Log.d("WrappedActivity", "onDestroy()");
        super.onDestroy();
    }

}



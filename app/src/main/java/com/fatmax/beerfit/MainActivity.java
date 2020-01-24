package com.fatmax.beerfit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView beerCounter;
    ImageButton drankBeer;

    int beersRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beerCounter = findViewById(R.id.beersLeft);
        drankBeer = findViewById(R.id.drankABeer);

        // TODO - need to fix this to pull in total beers (will need log, etc)
        beersRemaining = 10;
        //on app launch, set beer to 10
        beerCounter.setText(String.valueOf(beersRemaining));

        drankBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beersRemaining = beersRemaining - 1;
                beerCounter.setText(String.valueOf(beersRemaining));
            }
        });
    }
}

package com.applications.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.imageview.ShapeableImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/*
@Author Dominic Drury
Tutorial for first time launch of app and for future access through button in settings page
 */
public class Tutorial extends AppCompatActivity {

    private final int[] tutorialImages = {
        R.drawable.tutorial_add_budget,
        R.drawable.tutorial_delete,
        R.drawable.tutorial_add_bill,
        R.drawable.tutorial_calculate,
        R.drawable.tutorial_add_income,
        R.drawable.tutorial_settings,
        R.drawable.tutorial_budget_dropdown,
        R.drawable.tutorial_budget_recyclerview,
        R.drawable.tutorial_totals,
        R.drawable.tutorial_percentiles
    };
    private int currentIndex = 0;

    private LinearLayout dotsLayout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dotsLayout = findViewById(R.id.dotsLayout);
        ImageView tutorialImage = findViewById(R.id.tutorialImage);
        ImageButton backButton = findViewById(R.id.tutorialBackButton);
        ImageButton nextButton = findViewById(R.id.tutorialNextButton);
        ShapeableImageView exitButton = findViewById(R.id.tutorialExitButton);

        // Set the initial image
        tutorialImage.setImageResource(tutorialImages[currentIndex]);

        dotsLayout = findViewById(R.id.dotsLayout);
        addDots(currentIndex);

        backButton.setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + tutorialImages.length) % tutorialImages.length; // wrap around
            tutorialImage.setImageResource(tutorialImages[currentIndex]);
            addDots(currentIndex); // <-- update dots
        });

        nextButton.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % tutorialImages.length; // wrap around
            tutorialImage.setImageResource(tutorialImages[currentIndex]);
            addDots(currentIndex); // <-- update dots
        });

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(Tutorial.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addDots(int position) {
        dotsLayout.removeAllViews();
        dots = new ImageView[tutorialImages.length];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            if (i == position) {
                dots[i].setImageResource(R.drawable.dot_active);
            } else {
                dots[i].setImageResource(R.drawable.dot_inactive);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(dots[i], params);
        }
    }
}
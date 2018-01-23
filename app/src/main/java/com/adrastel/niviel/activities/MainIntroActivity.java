package com.adrastel.niviel.activities;

import android.os.Bundle;

import com.adrastel.niviel.R;
import com.adrastel.niviel.fragments.RedirectFragment;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntroActivity extends IntroActivity {

    private NavigationPolicy policy = new NavigationPolicy() {
        @Override
        public boolean canGoForward(int i) {
            return i != 0;
        }

        @Override
        public boolean canGoBackward(int i) {
            return i != 1;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);


        setNavigationPolicy(policy);

        addSlide(new FragmentSlide.Builder()
            .fragment(new RedirectFragment())
            .background(R.color.indigo_200)
            .build());

        addSlide(new SimpleSlide.Builder()
            .title(R.string.profile)
            .description(R.string.intro_slide_2)
            .background(R.color.purple_200)
            .image(R.drawable.ic_profile)
            .build());

        addSlide(new SimpleSlide.Builder()
            .title(R.string.followers)
            .description(R.string.intro_slide_3)
            .background(R.color.green_200)
            .image(R.drawable.ic_followers)
            .build());
    }

    @Override
    public void finish() {
        setResult(MainActivity.RESTART_ACTIVITY);
        super.finish();
    }

    @Override
    public void nextSlide() {
        setNavigationPolicy(null);
        super.nextSlide();
        setNavigationPolicy(policy);
    }
}

package com.sofialopes.android.popularmoviesapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.HelperClasses.DisplayMetricsUtils;
import com.squareup.picasso.Picasso;

import static com.sofialopes.android.popularmoviesapp.MainActivity.INTENT_EXTRA_MOVIE;

public class DetailActivity extends AppCompatActivity {

    private static final int NORMAL_MARGIN = 16;
    private static final int MARGIN_WITH_APPBAR = 36;
    private static final int MARGIN_WITHOUT_APPBAR = 24;
    
    private static final int MIN_VERSION = 16;
    private static final int MIN_VERSION_WITH_CONSTRAINT_LAYOUT = 19;

    private static final int DELAY_TIME = 100;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;
    private ImageView background;
    private ImageView posterImage;
    private ImageView floatImage;

    private TextView titleTv;
    private TextView releaseDateTv;
    private TextView voteAverageTv;
    private TextView overviewTv;
    private TextView titleInImage;

    private String title;

    private int smallMargin;
    private int bigMargin;
    private int intermediateMargin;
    private int version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_details);
        appBarLayout = findViewById(R.id.appbar);
        background = findViewById(R.id.app_bar_background);

        posterImage = findViewById(R.id.poster);
        titleTv = findViewById(R.id.title);
        releaseDateTv = findViewById(R.id.release_date);
        voteAverageTv = findViewById(R.id.vote_average);
        overviewTv = findViewById(R.id.overview);

        Intent fromMain = getIntent();
        MovieObject movieObject = (MovieObject) fromMain.getSerializableExtra(INTENT_EXTRA_MOVIE);

        settingTheUI(movieObject);
        settingMarginsValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(appBarListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(appBarListener);
    }

    private void settingTheUI(MovieObject movieObject) {
        version = Build.VERSION.SDK_INT;

        title = movieObject.getTitle();
        String moviePoster = movieObject.getMoviePosterPath();
        String movieBackdrop = movieObject.getBackdropImage();
        String releaseDate = movieObject.getReleaseDate();
        double voteAverage = movieObject.getVoteAverage();
        String overview = movieObject.getOverview();

        if (!TextUtils.isEmpty(title)) {
            background.setContentDescription(title);
            posterImage.setContentDescription(title);
            titleTv.setText(title);
        }

        Drawable placeholderHorizontal =
                ContextCompat.getDrawable(this, R.drawable.image_not_available_horizontal);
        Picasso.with(this)
                .load(movieBackdrop)
                .placeholder(placeholderHorizontal)
                .error(placeholderHorizontal)
                .into(background);

        Drawable placeholder =
                ContextCompat.getDrawable(this, R.drawable.image_not_available_vertical);
        Picasso.with(this)
                .load(moviePoster)
                .placeholder(placeholder)
                .error(placeholder)
                .into(posterImage);

        if (!TextUtils.isEmpty(releaseDate)) {
            releaseDateTv.setText(releaseDate.replace("-", "."));
        }

        if (voteAverage != 0) {
            voteAverageTv.setText(getString(R.string.total_rating, voteAverage + ""));
        }

        if (!TextUtils.isEmpty(title)) {
            overviewTv.setText(overview);
        }

        if (version >= MIN_VERSION && version < MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
            titleInImage = findViewById(R.id.title_in_image);
            if (!TextUtils.isEmpty(title)) {
                titleInImage.setText(title);
            }
            collapsingToolbarLayout.setTitle(" ");

        } else if (version >= MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
            constraintLayout = findViewById(R.id.constraint_layout);
            floatImage = findViewById(R.id.float_image);
            if (!TextUtils.isEmpty(title)) {
                floatImage.setContentDescription(title);
            }
            Picasso.with(this)
                    .load(moviePoster)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(floatImage);
        }
    }

    private void settingMarginsValues() {
        float density = DisplayMetricsUtils.getDensity();
        smallMargin = (int) (NORMAL_MARGIN * density);
        bigMargin = (int) (MARGIN_WITH_APPBAR * density);
        intermediateMargin = (int) (MARGIN_WITHOUT_APPBAR * density);
    }

    private final AppBarLayout.OnOffsetChangedListener appBarListener =
            new AppBarLayout.OnOffsetChangedListener() {
        int scrollRange = -1;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            scrollRange = appBarLayout.getTotalScrollRange();

            if (scrollRange + verticalOffset <= 5) {
                //Collapsed AppBar
                collapsingToolbarLayout.setTitle(title);
                toolbar.setBackgroundColor(Color.TRANSPARENT);

                if (version >= MIN_VERSION && version < MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
                    titleInImage.setVisibility(View.INVISIBLE);
                } else {
                    //It's necessary to set the constraints here too because if we scroll to this
                    //position and then rotate the screen, the overview text doesn't appear, the
                    //float image is still visible and release date and vote average are shown in
                    //incorrect positions
                    settingConstrainsWithoutFloatImage(appBarLayout);
                }
            } else if ((scrollRange + verticalOffset) <= (scrollRange - 20) 
                    && (scrollRange + verticalOffset) >= 0) {
                // In between
                // (necessary because while moving from extended to collapsed,
                // overview would be underneath the poster)
                if (version >= MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
                    collapsingToolbarLayout.setTitle(" ");
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                    settingConstrainsWithoutFloatImage(appBarLayout);
                }
            } else {
                //Extended appBar
                collapsingToolbarLayout.setTitle(" ");
                toolbar.setBackground(ContextCompat.getDrawable(
                                DetailActivity.this, R.drawable.gradient_bg));

                if (version >= MIN_VERSION && version < MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
                    titleInImage.setVisibility(View.VISIBLE);
                } else {
                    floatImage.setVisibility(View.VISIBLE);
                    posterImage.setVisibility(View.INVISIBLE);

                    appBarLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ConstraintSet constraintSet2 = new ConstraintSet();
                            constraintSet2.clone(constraintLayout);
                            constraintSet2.connect(R.id.overview, ConstraintSet.TOP, 
                                    R.id.vote_average, ConstraintSet.BOTTOM);
                            constraintSet2.connect(R.id.overview, ConstraintSet.START, 
                                    R.id.constraint_layout, ConstraintSet.START);

                            constraintSet2.connect(R.id.release_date, ConstraintSet.TOP, 
                                    R.id.title, ConstraintSet.BOTTOM);
                            constraintSet2.connect(R.id.release_date, ConstraintSet.START, 
                                    R.id.vote_average, ConstraintSet.END);

                            constraintSet2.setMargin(R.id.title, ConstraintSet.TOP, 
                                    intermediateMargin);
                            constraintSet2.setMargin(R.id.release_date, ConstraintSet.START,
                                   bigMargin);

                            constraintSet2.applyTo(constraintLayout);

                            changeMarginsParams(voteAverageTv, smallMargin, smallMargin);
                            changeMarginsParams(releaseDateTv, bigMargin, smallMargin);
                            changeMarginsParams(overviewTv, smallMargin, bigMargin);
                        }
                    },DELAY_TIME);
                }
            }
        }
    };

    private void changeMarginsParams(View v, int marginStart, int marginTop) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(marginStart, marginTop, smallMargin, smallMargin);
        v.setLayoutParams(params);
    }

    private void settingConstrainsWithoutFloatImage(AppBarLayout appBarLayout) {
        floatImage.setVisibility(View.INVISIBLE);
        posterImage.setVisibility(View.VISIBLE);

        appBarLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.overview, ConstraintSet.TOP,
                        R.id.poster, ConstraintSet.BOTTOM);
                constraintSet.connect(R.id.overview, ConstraintSet.START,
                        R.id.constraint_layout, ConstraintSet.START);

                constraintSet.connect(R.id.release_date, ConstraintSet.TOP,
                        R.id.vote_average, ConstraintSet.BOTTOM);
                constraintSet.connect(R.id.release_date, ConstraintSet.START,
                        R.id.vote_average, ConstraintSet.START);

                constraintSet.setHorizontalBias(R.id.release_date, 0);
                constraintSet.setMargin(R.id.release_date, ConstraintSet.START, 0);
                constraintSet.setMargin(R.id.release_date, ConstraintSet.TOP, bigMargin);

                constraintSet.applyTo(constraintLayout);

                changeMarginsParams(titleTv, smallMargin, bigMargin);
                changeMarginsParams(voteAverageTv, smallMargin, bigMargin);
                changeMarginsParams(overviewTv, smallMargin, 0);
            }
        },DELAY_TIME);
    }
}

package com.sofialopes.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.HelperClasses.DisplayMetricsUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.AdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<MovieObject>> {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView errorText;

    private MoviesAdapter adapter;

    public static final String INTENT_EXTRA_MOVIE = "movieDetails";
    private static boolean sPopularSelected = true;

    private List<MovieObject> moviePopular = new ArrayList<>();
    private List<MovieObject> movieTopRated = new ArrayList<>();

    private LoaderManager loaderManager;
    private static final int LOADER_POPULAR = 25;
    private static final int LOADER_HIGH_RATE = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        errorText = findViewById(R.id.error_message);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progress_bar);

        int noOfColumns = DisplayMetricsUtils.getNumberOfColumns();
        GridLayoutManager layoutManager = new GridLayoutManager(this, noOfColumns);
        recyclerView.setLayoutManager(layoutManager);

        if (sPopularSelected) {
            adapter = new MoviesAdapter(this, moviePopular, this);
        } else {
            adapter = new MoviesAdapter(this, movieTopRated, this);
        }
        recyclerView.setAdapter(adapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        loaderManager = getSupportLoaderManager();
        if (isOnline()) {
            networkAvailable();

            if (sPopularSelected) {
                Loader<ArrayList<MovieObject>> loaderPopular =
                        loaderManager.getLoader(LOADER_POPULAR);
                if (loaderPopular == null) {
                    loaderManager.initLoader(LOADER_POPULAR, null, this);
                }
            } else {
                Loader<ArrayList<MovieObject>> loaderHighRate =
                        loaderManager.getLoader(LOADER_HIGH_RATE);
                if (loaderHighRate == null) {
                    loaderManager.initLoader(LOADER_HIGH_RATE, null, this);
                }
            }
        } else {
            noNetworkAvailable();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isOnline()) {
                    networkAvailable();

                    if (parent.getItemAtPosition(position).toString()
                            .equals(getString(R.string.popular))) {
                        sPopularSelected = true;
                        loaderManager.initLoader(LOADER_POPULAR, null,
                                MainActivity.this);

                    } else {
                        sPopularSelected = false;
                        loaderManager.initLoader(LOADER_HIGH_RATE, null,
                                MainActivity.this);
                    }
                } else {
                    noNetworkAvailable();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return true;
    }

    @Override
    public Loader<List<MovieObject>> onCreateLoader(int id, Bundle args) {
        loadingMovies();
        if (id == LOADER_POPULAR) {
            return new LoaderMovies(this, true);
        } else {
            return new LoaderMovies(this, false);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<MovieObject>> loader, List<MovieObject> data) {
        if (data == null || data.isEmpty()) {
            noNetworkAvailable();
            errorText.setText(getString(R.string.error_network_or_json));
            return;
        }

        if (loader.getId() == LOADER_POPULAR) {
            moviePopular = data;
            adapter = new MoviesAdapter(this, moviePopular, this);
            recyclerView.setAdapter(adapter);
        } else {
            movieTopRated = data;
            adapter = new MoviesAdapter(this, movieTopRated, this);
            recyclerView.setAdapter(adapter);
        }
        loadingFinished();
    }

    @Override
    public void onLoaderReset(Loader<List<MovieObject>> loader) {
    }

    @Override
    public void onClick(MovieObject data) {
        Intent goToDetails = new Intent(MainActivity.this, DetailActivity.class);
        goToDetails.putExtra(INTENT_EXTRA_MOVIE, data);
        startActivity(goToDetails);
    }

    private void loadingMovies() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void loadingFinished() {
        progressBar.setVisibility(GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void noNetworkAvailable() {
        recyclerView.setVisibility(GONE);
        progressBar.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
    }

    private void networkAvailable() {
        errorText.setVisibility(GONE);
    }
}

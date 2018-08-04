package com.sofialopes.android.popularmoviesapp;


import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.sofialopes.android.popularmoviesapp.HelperClasses.JsonUtils;
import com.sofialopes.android.popularmoviesapp.HelperClasses.NetworkUtils;

import java.net.URL;
import java.util.List;

/**
 *
 * Created by Sofia on 2/17/2018.
 */

public class LoaderMovies extends AsyncTaskLoader<List<MovieObject>> {

    private List<MovieObject> movieList = null;
    private final boolean popularOn;

    private static final String MOVIES_SCHEME = "https";
    private static final String MOVIES_AUTHORITY = "api.themoviedb.org";
    private static final String MOVIES_COMMON_PATH = "3/movie";

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    private static final String KEY_LABEL = "api_key";
    private static final String API_KEY = "";//todo: put api key here

    public LoaderMovies(Context context, boolean popularSelected) {
        super(context);
        popularOn = popularSelected;
    }

    @Override
    protected void onStartLoading() {
        if (movieList != null) {
            deliverResult(movieList);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<MovieObject> loadInBackground() {

        URL url;
        Uri.Builder builder = new Uri.Builder();
        if (popularOn) {
            builder.scheme(MOVIES_SCHEME)
                    .authority(MOVIES_AUTHORITY)
                    .appendEncodedPath(MOVIES_COMMON_PATH)
                    .appendPath(POPULAR)
                    .appendQueryParameter(KEY_LABEL, API_KEY);

            String popularString = builder.build().toString();
            url = NetworkUtils.buildUrl(popularString);
        } else {
            builder.scheme(MOVIES_SCHEME)
                    .authority(MOVIES_AUTHORITY)
                    .appendEncodedPath(MOVIES_COMMON_PATH)
                    .appendPath(TOP_RATED)
                    .appendQueryParameter(KEY_LABEL, API_KEY);

            String topRatedString = builder.build().toString();
            url = NetworkUtils.buildUrl(topRatedString);
        }

        if (url == null) {
            return null;
        }

        String jsonResponse = NetworkUtils.connectToMovieDb(url);

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        movieList = JsonUtils.extractMovieData(jsonResponse);
        return movieList;
    }

    public void deliverResult(List<MovieObject> data) {
        movieList = data;
        super.deliverResult(data);
    }
}

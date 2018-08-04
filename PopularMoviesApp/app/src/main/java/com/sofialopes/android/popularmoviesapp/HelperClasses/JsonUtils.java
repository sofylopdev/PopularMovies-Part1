package com.sofialopes.android.popularmoviesapp.HelperClasses;

import android.net.Uri;
import android.util.Log;

import com.sofialopes.android.popularmoviesapp.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sofia on 2/17/2018.
 */

public class JsonUtils {

    private static final String TAG_JSON = JsonUtils.class.getName();

    private static final String BASE_IMAGE_URI = "http://image.tmdb.org";
    private static final String COMMON_IMAGES_PATH = "t/p";
    private static final String POSTER_SIZE = "w185";
    private static final String BACKDROP_SIZE = "w342";

    private static final String RESULTS_JSON = "results";
    private static final String TITLE_JSON = "title";
    private static final String RELEASE_DATE_JSON = "release_date";
    private static final String POSTER_JSON = "poster_path";
    private static final String VOTE_AVG_JSON = "vote_average";
    private static final String OVERVIEW_JSON = "overview";
    private static final String BACKDROP_JSON = "backdrop_path";

    private static String uriParser(String imageSize, String finalPath) {
        Uri builtUri = Uri.parse(BASE_IMAGE_URI)
                .buildUpon()
                .appendEncodedPath(COMMON_IMAGES_PATH)
                .appendPath(imageSize)
                .appendPath(finalPath)
                .build();
        return builtUri.toString();
    }

    private JsonUtils() {
    }

    public static List<MovieObject> extractMovieData(String movieJSON) {

        List<MovieObject> movieObjectList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            JSONArray resultsArray = baseJsonResponse.optJSONArray(RESULTS_JSON);

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentMovie = resultsArray.optJSONObject(i);

                String title = currentMovie.optString(TITLE_JSON);
                String releaseDate = currentMovie.optString(RELEASE_DATE_JSON);
                String posterPath = currentMovie.optString(POSTER_JSON);
                double voteAverage = currentMovie.getDouble(VOTE_AVG_JSON);
                String overview = currentMovie.optString(OVERVIEW_JSON);
                String backdropPath = currentMovie.optString(BACKDROP_JSON);

                String poster = uriParser(POSTER_SIZE, posterPath.split("/")[1]);
                String backdrop = uriParser(BACKDROP_SIZE, backdropPath.split("/")[1]);

                movieObjectList.add(
                        new MovieObject(title, releaseDate, poster, voteAverage, overview, backdrop));
            }

        } catch (JSONException e) {
            Log.e(TAG_JSON, "Problem parsing the JSON results: ", e);
        }
        return movieObjectList;
    }
}

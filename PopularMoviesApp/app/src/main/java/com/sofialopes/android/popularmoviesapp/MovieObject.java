package com.sofialopes.android.popularmoviesapp;

import java.io.Serializable;

/**
 * Created by Sofia on 2/17/2018.
 *
 */

public class MovieObject implements Serializable {
    private String title;
    private String releaseDate;
    private String moviePosterPath;
    private double voteAverage = 0;
    private String overview;
    private String backdropImage;

    public MovieObject(String title, String releaseDate, String moviePosterPath,
                       double voteAverage, String overview, String backdrop) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.moviePosterPath = moviePosterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.backdropImage = backdrop;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getBackdropImage() {
        return backdropImage;
    }
}

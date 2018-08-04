package com.sofialopes.android.popularmoviesapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sofialopes.android.popularmoviesapp.HelperClasses.DisplayMetricsUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sofia on 2/22/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final Context context;
    private static List<MovieObject> movieList = new ArrayList<>();
    private static AdapterOnClickHandler mClickOnMovie;

    public interface AdapterOnClickHandler {
        void onClick(MovieObject data);
    }

    public MoviesAdapter(Context ctx, List<MovieObject> list, AdapterOnClickHandler clickHandler) {
        this.context = ctx;
        movieList = list;
        mClickOnMovie = clickHandler;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {

        int screenWidth = DisplayMetricsUtils.getWidthPixels();
        int noOfColumns = DisplayMetricsUtils.getNumberOfColumns();

        int spacing = 2 * (context.getResources().getDimensionPixelOffset(R.dimen.frame_spacing));
        int imageWidth = ((screenWidth) / (noOfColumns)) - spacing;
        int imageHeight = (int) ((imageWidth) * 1.5);

        //set minimum width and height uses pixels, not dp's
        holder.posterImage.setMinimumWidth(imageWidth);
        holder.posterImage.setMinimumHeight(imageHeight);

        MovieObject movieObject = movieList.get(position);
        String title = movieObject.getTitle();
        if (!TextUtils.isEmpty(title))
            holder.posterImage.setContentDescription(title);

        Drawable placeholder =
                ContextCompat.getDrawable(context, R.drawable.image_not_available_vertical);
        if (!TextUtils.isEmpty(movieObject.getMoviePosterPath())) {
            String imageUrl = movieObject.getMoviePosterPath();
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(holder.posterImage);
        }
    }

    @Override
    public int getItemCount() {
        if (movieList == null || movieList.isEmpty()) {
            return 0;
        }
        return movieList.size();
    }

    public static class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

       final ImageView posterImage;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickOnMovie.onClick(movieList.get(getAdapterPosition()));
        }
    }
}

package com.arctouch.codechallenge.movie;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.base.BaseActivity;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.arctouch.codechallenge.util.MovieKeys;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MovieActivity extends BaseActivity {

    private int idMovie = 0;
    private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();
    private Movie movie;
    private ProgressBar progressBar;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.backDropImageView) ImageView backdrop;
    @BindView(R.id.moviePosterImageView) ImageView poster;
    @BindView(R.id.movieNameTextView) TextView name;
    @BindView(R.id.overviewTextView) TextView overview;
    @BindView(R.id.genderTextView) TextView gender;
    @BindView(R.id.releaseTextView) TextView release;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.idMovie = (int) getIntent().getExtras().get(MovieKeys.MOVIE_ID);

        this.progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        api.movie((long) idMovie, TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    this.movie = response;
                    bindDataView();
                    progressBar.setVisibility(View.GONE);
                });

    }

    public void bindDataView() {

        if (this.movie != null) {
            toolbar.setTitle(movie.title);
            name.setText(movie.title);
            overview.setText(movie.overview);
            gender.setText(movie.genres.toString());
            release.setText(movie.releaseDate);

            String backDropPath = movie.backdropPath;
            if (TextUtils.isEmpty(backDropPath) == false) {
                Glide.with(backdrop)
                        .load(movieImageUrlBuilder.buildPosterUrl(backDropPath))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(backdrop);
            }

            String posterPath = movie.posterPath;
            if (TextUtils.isEmpty(posterPath) == false) {
                Glide.with(poster)
                        .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(poster);
            }


        }
    }
}

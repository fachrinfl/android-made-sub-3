package com.fachrinfl.movie.movie.view.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fachrinfl.movie.R;
import com.fachrinfl.movie.movie.adapter.MovieAdapter;
import com.fachrinfl.movie.movie.model.Movie;
import com.fachrinfl.movie.movie.viewmodel.MovieViewModel;
import com.fachrinfl.movie.utilities.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment {

    private ArrayList<Movie> movies = new ArrayList<>();
    private MovieViewModel movieViewModel;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPopularMovie();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rvMovie);
        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
    }

    private void getPopularMovie() {
        movieViewModel.getPopularMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> moviesList) {
                movies = (ArrayList<Movie>) moviesList;
                initRv();
            }
        });
    }

    private void initRv() {
        movieAdapter = new MovieAdapter(getContext(), movies);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }

        int spanCount = 2; // 2 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieViewModel.clear();
    }
}

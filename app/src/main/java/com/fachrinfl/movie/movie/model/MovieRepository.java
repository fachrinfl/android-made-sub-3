package com.fachrinfl.movie.movie.model;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.widget.Toast;

import com.fachrinfl.movie.R;
import com.fachrinfl.movie.movie.service.MovieDataServices;
import com.fachrinfl.movie.movie.service.RetrofitMovieInstance;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieRepository {
    private Application application;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Movie>> movieLiveData = new MutableLiveData<>();
    private ArrayList<Movie> movies;
    private Observable<MovieResponse> movieResponseObservable;

    public MovieRepository(Application application) {
        this.application = application;
    }

    public MutableLiveData<List<Movie>> getPopularMovieLiveData() {
        Toast.makeText(
                application.getApplicationContext(),
                "Request Load Data",
                Toast.LENGTH_LONG
        ).show();
        movies = new ArrayList<>();
        MovieDataServices getMovieDataService = RetrofitMovieInstance.getService();
        movieResponseObservable = getMovieDataService
                .getPopularMovie(application.getApplicationContext()
                        .getString(R.string.api_key));

        compositeDisposable.add(
                movieResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<MovieResponse, Observable<Movie>>() {
                    @Override
                    public Observable<Movie> apply(MovieResponse movieResponse) throws Exception {
                        return Observable.fromArray(movieResponse.getResults().toArray(new Movie[0]));
                    }
                })
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(Movie movie) {
                        movies.add(movie);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(
                                application.getApplicationContext(),
                                "Failed Load Data",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onComplete() {
                        movieLiveData.postValue(movies);
                        Toast.makeText(
                                application.getApplicationContext(),
                                "Success Load Data",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }));
        return movieLiveData;
    }

    public void clear() {
        compositeDisposable.clear();
    }
}

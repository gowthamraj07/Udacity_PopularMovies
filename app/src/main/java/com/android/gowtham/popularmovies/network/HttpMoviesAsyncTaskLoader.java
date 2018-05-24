package com.android.gowtham.popularmovies.network;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.gowtham.popularmovies.dto.MovieDto;
import com.android.gowtham.popularmovies.utils.MovieConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HttpMoviesAsyncTaskLoader extends AsyncTaskLoader<List<MovieDto>> {

    private final String sortBy;
    private String TAG = HttpMoviesAsyncTaskLoader.class.getSimpleName();

    public HttpMoviesAsyncTaskLoader(Context context, String sortBy) {
        super(context);
        this.sortBy = sortBy;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i(getClass().getName(), MovieConstant.STARTED_DOWNLOADING);
    }

    @Override
    public List<MovieDto> loadInBackground() {
        Uri urlToDownload = getUrlToDownload();
        try {
            String strUrl = urlToDownload.toString();
            URL url = new URL(strUrl);
            Log.i(TAG, url.toString());
            String content = getContent(url);
            return getMovieDtoListFromString(content);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getContent(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.i(getClass().getName(), MovieConstant.FINISHED_DOWNLOADING);
    }

    private List<MovieDto> getMovieDtoListFromString(String content) throws JSONException {
        JSONObject obj = new JSONObject(content);
        JSONArray results = obj.getJSONArray(MovieConstant.RESULTS);
        List<MovieDto> dtoList = new ArrayList<>();

        for(int index=0; index < results.length(); index++) {
            JSONObject jsonObject = results.getJSONObject(index);
            long movieId = jsonObject.getLong(MovieConstant.MOVIE_ID);
            String title = jsonObject.getString(MovieConstant.TITLE);
            String posterPath = jsonObject.getString(MovieConstant.POSTER_PATH);
            String overview = jsonObject.getString(MovieConstant.OVERVIEW);
            String voteAverage = jsonObject.getString(MovieConstant.VOTE_AVERAGE);
            String releaseDate = jsonObject.getString(MovieConstant.RELEASE_DATE);

            Log.i(getClass().getName(), posterPath);

            dtoList.add(new MovieDto(movieId, title, posterPath, overview, voteAverage, releaseDate));
        }

        return dtoList;
    }

    private Uri getUrlToDownload() {
        return new Uri.Builder().scheme(MovieConstant.HTTPS).path(MovieConstant.BASE_PATH + getSortByUrl())
                .appendQueryParameter(MovieConstant.API_KEY, MovieConstant.API_KEY_VALUE)
                .appendQueryParameter(MovieConstant.SORT_BY, MovieConstant.POPULARITY_DESC).build();
    }

    private String getSortByUrl() {
        return sortBy.equals(MovieConstant.SORT_BY_POPULARITY)? MovieConstant.SORY_BY_POPULARITY: MovieConstant.SORY_BY_HISHEST_RATE;
    }
}

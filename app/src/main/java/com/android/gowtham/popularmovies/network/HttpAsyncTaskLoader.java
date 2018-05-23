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

public class HttpAsyncTaskLoader extends AsyncTaskLoader<List<MovieDto>> {

    private static final String BASE_PATH = "api.themoviedb.org/3";
    private static final String SORY_BY_POPULARITY = "/movie/popular";
    private static final String SORY_BY_HISHEST_RATE = "/movie/top_rated";
    private static final String STARTED_DOWNLOADING = "Started downloading";
    private static final String FINISHED_DOWNLOADING = "Finished downloading";
    private static final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String HTTPS = "https";
    private static final String API_KEY = "api_key";
    private static final String SORT_BY = "sort_by";
    private static final String POPULARITY_DESC = "popularity.desc";
    private final String sortBy;
    private String TAG = HttpAsyncTaskLoader.class.getSimpleName();

    public HttpAsyncTaskLoader(Context context, String sortBy) {
        super(context);
        this.sortBy = sortBy;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i(getClass().getName(), STARTED_DOWNLOADING);
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
        Log.i(getClass().getName(), FINISHED_DOWNLOADING);
    }

    private List<MovieDto> getMovieDtoListFromString(String content) throws JSONException {
        JSONObject obj = new JSONObject(content);
        JSONArray results = obj.getJSONArray(RESULTS);
        List<MovieDto> dtoList = new ArrayList<>();

        for(int index=0; index < results.length(); index++) {
            JSONObject jsonObject = results.getJSONObject(index);
            long movieId = jsonObject.getLong(MOVIE_ID);
            String title = jsonObject.getString(TITLE);
            String posterPath = jsonObject.getString(POSTER_PATH);
            String overview = jsonObject.getString(OVERVIEW);
            String voteAverage = jsonObject.getString(VOTE_AVERAGE);
            String releaseDate = jsonObject.getString(RELEASE_DATE);

            Log.i(getClass().getName(), posterPath);

            dtoList.add(new MovieDto(movieId, title, posterPath, overview, voteAverage, releaseDate));
        }

        return dtoList;
    }

    private Uri getUrlToDownload() {
        return new Uri.Builder().scheme(HTTPS).path(BASE_PATH + getSortByUrl())
                .appendQueryParameter(API_KEY, MovieConstant.API_KEY_VALUE)
                .appendQueryParameter(SORT_BY, POPULARITY_DESC).build();
    }

    private String getSortByUrl() {
        return sortBy.equals(MovieConstant.SORT_BY_POPULARITY)?SORY_BY_POPULARITY:SORY_BY_HISHEST_RATE;
    }
}

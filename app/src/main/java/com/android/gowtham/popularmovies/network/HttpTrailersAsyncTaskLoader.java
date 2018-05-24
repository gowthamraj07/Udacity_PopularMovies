package com.android.gowtham.popularmovies.network;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.gowtham.popularmovies.dto.TrailerDto;
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

public class HttpTrailersAsyncTaskLoader extends AsyncTaskLoader<List<TrailerDto>> {


    private final long movieId;
    private String TAG = HttpTrailersAsyncTaskLoader.class.getSimpleName();

    public HttpTrailersAsyncTaskLoader(Context context, long movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i(getClass().getName(), MovieConstant.STARTED_DOWNLOADING);
    }

    @Override
    public List<TrailerDto> loadInBackground() {
        Uri urlToDownload = getUrlToDownload(movieId);
        try {
            String strUrl = urlToDownload.toString();
            URL url = new URL(strUrl);
            Log.i(TAG, url.toString());
            String content = getContent(url);
            return getTrailerListFromString(content);
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

    private List<TrailerDto> getTrailerListFromString(String content) throws JSONException {
        JSONObject obj = new JSONObject(content);
        JSONArray results = obj.getJSONArray(MovieConstant.RESULTS);
        List<TrailerDto> dtoList = new ArrayList<>();

        for(int index=0; index < results.length(); index++) {
            JSONObject jsonObject = results.getJSONObject(index);
            String trailerId = jsonObject.getString("id");
            String key = jsonObject.getString(MovieConstant.KEY);
            String type = jsonObject.getString(MovieConstant.TYPE);
            if("Trailer".equalsIgnoreCase(type)) {
                dtoList.add(new TrailerDto(trailerId, key));
            }
        }

        return dtoList;
    }

    private Uri getUrlToDownload(long movieId) {
        return new Uri.Builder().scheme(MovieConstant.HTTPS).path(MovieConstant.BASE_PATH + "/movie/"+movieId+"/videos")
                .appendQueryParameter(MovieConstant.API_KEY, MovieConstant.API_KEY_VALUE)
                .build();
    }

}

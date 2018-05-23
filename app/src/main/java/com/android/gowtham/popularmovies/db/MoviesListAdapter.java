package com.android.gowtham.popularmovies.db;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.android.gowtham.popularmovies.utils.MovieConstant;
import com.squareup.picasso.Picasso;

public class MoviesListAdapter extends CursorAdapter {

    private final View.OnClickListener listener;

    public MoviesListAdapter(Context context, Cursor c, View.OnClickListener listener) {
        super(context, c);
        this.listener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView ivThumbnails = new ImageView(context);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ivThumbnails.setLayoutParams(layoutParams);
        ivThumbnails.setPadding(4,4,4,4);

        ivThumbnails.setOnClickListener(listener);

        return ivThumbnails;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(! (view instanceof ImageView)) {
            return;
        }

        ImageView ivThumbnails = (ImageView) view;
        String id = cursor.getString(cursor.getColumnIndex(MoviesDBContract._ID));
        String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MoviesDBContract.THUMBNAIL_URL_COLUMN));
        ivThumbnails.setTag(id);
        Picasso.with(context).load(getAbsolutePath(thumbnailUrl)).into(ivThumbnails);
    }

    @NonNull
    private String getAbsolutePath(String thumbnailUrl) {
        Uri build = new Uri.Builder()
                .scheme("http")
                .path(MovieConstant.BASE_URL)
                .appendPath("w500")
                .appendPath(thumbnailUrl.substring(1))
                .build();
        String path = build.toString();
        Log.i(getClass().getName(), path);
        return path;
    }
}

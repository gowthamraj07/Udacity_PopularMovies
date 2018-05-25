package com.android.gowtham.popularmovies.db;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.gowtham.popularmovies.utils.MovieConstant;
import com.squareup.picasso.Picasso;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.ViewHolder> {

    private final Context context;
    private final Cursor cursor;
    private final View.OnClickListener listener;

    public MoviesListAdapter(Context context, Cursor cursor, View.OnClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView ivThumbnails = new ImageView(context);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivThumbnails.setLayoutParams(layoutParams);
        ivThumbnails.setPadding(4,4,4,4);

        ivThumbnails.setOnClickListener(listener);

        return new ViewHolder(ivThumbnails);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(cursor.moveToPosition(position)) {
            holder.bindData(cursor);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        void bindData(Cursor cursor) {
            if(! (itemView instanceof ImageView)) {
                return;
            }

            ImageView ivThumbnails = (ImageView) itemView;
            String id = cursor.getString(cursor.getColumnIndex(MoviesDBContract.MOVIE_ID));
            String thumbnailUrl = cursor.getString(cursor.getColumnIndex(MoviesDBContract.THUMBNAIL_URL_COLUMN));
            ivThumbnails.setTag(id);
            Picasso.with(context).load(getAbsolutePath(thumbnailUrl)).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_menu_report_image).into(ivThumbnails);
        }
    }
}

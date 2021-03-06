package com.android.gowtham.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.gowtham.popularmovies.R;
import com.android.gowtham.popularmovies.dto.TrailerDto;

import java.util.List;

import static android.net.Uri.parse;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static final String VND_YOUTUBE = "vnd.youtube:";
    private static final String HTTP_WWW_YOUTUBE_COM_WATCH_V = "http://www.youtube.com/watch?v=";

    private Context applicationContext;
    private List<TrailerDto> trailerList;
    private View.OnClickListener mOnClickListener = new TrailerClickListener();

    public TrailerAdapter(Context applicationContext, List<TrailerDto> trailerList) {
        this.applicationContext = applicationContext;
        this.trailerList = trailerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(applicationContext).inflate(R.layout.trailer_item_layout, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindTrailer(trailerList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTrailer;
        private View itemView;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            tvTrailer = itemView.findViewById(R.id.tvTrailer);
        }

        void bindTrailer(TrailerDto trailerDto, int position) {
            itemView.setTag(trailerDto);
            String text = applicationContext.getString(R.string.trailer_string) + (position + 1);
            tvTrailer.setText(text);
        }
    }

    private class TrailerClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (!(tag instanceof TrailerDto)) {
                return;
            }

            String key = ((TrailerDto) tag).getKey();
            Intent appIntent = new Intent(Intent.ACTION_VIEW, parse(VND_YOUTUBE + key));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    parse(HTTP_WWW_YOUTUBE_COM_WATCH_V + key));
            try {
                applicationContext.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                applicationContext.startActivity(webIntent);
            }
        }
    }
}

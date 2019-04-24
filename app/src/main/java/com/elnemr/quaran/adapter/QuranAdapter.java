package com.elnemr.quaran.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elnemr.quaran.R;
import com.elnemr.quaran.model.QuranModel;
import com.elnemr.quaran.playclicklistener.PlayClickListener;
import com.elnemr.quaran.presenter.MainActivityPresenter;

import java.util.List;

public class QuranAdapter extends RecyclerView.Adapter<QuranAdapter.MyViewHolder> {

    private List<QuranModel.Api> moviesList;
    PlayClickListener playClickListener;
    Context context;
    int play = 1, stop = 0;
    View dialogView;
    Dialog dialog;
    MainActivityPresenter mainActivityPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView soraName, sheikhName;
        public ImageView stop_audio_in_background, start_audio_in_background, pause_audio_in_background;
        LinearLayout itemView, controller;
ProgressBar play_audio_in_background_service_progressbar;
        public MyViewHolder(View view) {
            super(view);
            soraName = view.findViewById(R.id.soraName);
            sheikhName = view.findViewById(R.id.sheikhName);
            itemView = view.findViewById(R.id.itemView);
            controller = view.findViewById(R.id.controller);
            stop_audio_in_background = view.findViewById(R.id.stop_audio_in_background);
            start_audio_in_background = view.findViewById(R.id.start_audio_in_background);
            pause_audio_in_background = view.findViewById(R.id.pause_audio_in_background);
            play_audio_in_background_service_progressbar =
                    view.findViewById(R.id.play_audio_in_background_service_progressbar);
        }
    }

    public QuranAdapter(List<QuranModel.Api> moviesList, Context context1) {
        this.moviesList = moviesList;
        this.context = context1;
        playClickListener = (PlayClickListener) context;
        mainActivityPresenter = new MainActivityPresenter(context);
    }

    @Override
    public QuranAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quran_row, parent, false);
        return new QuranAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuranAdapter.MyViewHolder holder, final int position) {
        final QuranModel.Api movie = moviesList.get(position);
        holder.soraName.setText((movie).getSora());
        holder.sheikhName.setText((movie).getReaderName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.controller.setVisibility(View.VISIBLE);
            }
        });

//        holder.playIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogView = View.inflate(context, R.layout.play_controllers, null);
//                ImageView stop_audio_in_background, start_audio_in_background, pause_audio_in_background;
//
//                dialog = new Dialog(context, R.style.MyAlertDialogStyle);
//
//                stop_audio_in_background = dialogView.findViewById(R.id.stop_audio_in_background);
//                start_audio_in_background = dialogView.findViewById(R.id.start_audio_in_background);
//                pause_audio_in_background = dialogView.findViewById(R.id.pause_audio_in_background);
//
//                Window window = dialog.getWindow();
//                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//                dialog.setContentView(dialogView);
//                window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//
//
//
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                dialog.show();
//
//            }
//        });


        holder.stop_audio_in_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // playClickListener.onItemClick(position, movie.getLink(), "stop");
                mainActivityPresenter.play();
            }
        });
        holder.start_audio_in_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickListener.onItemClick(position, movie.getLink(), "start");
            }
        });
        holder.pause_audio_in_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickListener.onItemClick(position, movie.getLink(), "pause");
            }
        });
    }


    @Override
    public int getItemCount() {
        //    return moviesList.size();
        return moviesList == null ? 0 : moviesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
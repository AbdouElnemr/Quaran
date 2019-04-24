package com.elnemr.quaran.presenter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.elnemr.quaran.R;
import com.elnemr.quaran.model.QuranModel;
import com.elnemr.quaran.network.ApiClient;
import com.elnemr.quaran.network.ApiService;
import com.elnemr.quaran.playclicklistener.PlaybackInfoListener;
import com.elnemr.quaran.playclicklistener.PlayerAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.elnemr.quaran.playclicklistener.MediaPlayerHolder.PLAYBACK_POSITION_REFRESH_INTERVAL_MS;

public class MainActivityPresenter implements PlayerAdapter {
    Context mContext;
    ApiService apiService;
    String Result;
    KProgressHUD hud;
    OnComplete onComplete;
    QuranModel quranModel;
    List<String> names;

     private MediaPlayer mMediaPlayer;
    private int mResourceId;
    private PlaybackInfoListener mPlaybackInfoListener;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;

    public MainActivityPresenter(Context mContext) {
        this.mContext = mContext;
        apiService = ApiClient.getClient(mContext).create(ApiService.class);
        onComplete = (OnComplete) mContext;
        hud = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        names = new ArrayList<>();

        names.add(mContext.getString(R.string.chooseSh));
        names.add(mContext.getString(R.string.mashary));
        names.add(mContext.getString(R.string.maher));
        names.add(mContext.getString(R.string.ajamy));
        names.add(mContext.getString(R.string.ghamdi));
        names.add(mContext.getString(R.string.sodis));
        names.add(mContext.getString(R.string.gbril));
        names.add(mContext.getString(R.string.abdelbaset));
        names.add(mContext.getString(R.string.minshawy));
        names.add(mContext.getString(R.string.abbad));
        names.add(mContext.getString(R.string.wadi3));
        names.add(mContext.getString(R.string.huzifi));
        names.add(mContext.getString(R.string.nasser));
    }

    public void getQuran(int sheKh) {
//        HashMap<String, Integer>d = new HashMap<>();
//        d.put("reader_id", sheKh);
        apiService.getQuran(sheKh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<QuranModel.Api>>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onSuccess(List<QuranModel.Api> responed) {
                        onComplete.onComplete(responed);
                        hud.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("E", "onError: " + e.getLocalizedMessage());
                        Log.e("ED", "onError: " + e.getMessage());
                        Toast.makeText(mContext, "Network Error Please Check Network", Toast.LENGTH_LONG).show();
                        hud.dismiss();
                    }
                });
    }

    public void setSheikh() {
        onComplete.setSheikh(names);
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopUpdatingCallbackWithPosition(true);
                    logToUI("MediaPlayer playback completed");
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                        mPlaybackInfoListener.onPlaybackCompleted();
                    }
                }
            });
            logToUI("mMediaPlayer = new MediaPlayer()");
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loadMedia(int resourceId) {
        mResourceId = resourceId;

        initializeMediaPlayer();

        AssetFileDescriptor assetFileDescriptor =
                mContext.getResources().openRawResourceFd(mResourceId);
        try {
            logToUI("load() {1. setDataSource}");
            mMediaPlayer.setDataSource(assetFileDescriptor);
        } catch (Exception e) {
            logToUI(e.toString());
        }

        try {
            logToUI("load() {2. prepare}");
            mMediaPlayer.prepare();
        } catch (Exception e) {
            logToUI(e.toString());
        }

        initializeProgressCallback();
        logToUI("initializeProgressCallback()");
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            logToUI("release() and mMediaPlayer = null");
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            logToUI(String.format("playbackStart() %s",
                    mContext.getResources().getResourceEntryName(mResourceId)));
            mMediaPlayer.start();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PLAYING);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            logToUI("playbackReset()");
            mMediaPlayer.reset();
            loadMedia(mResourceId);
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.RESET);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PAUSED);
            }
            logToUI("playbackPause()");
        }
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            logToUI(String.format("seekTo() %d ms", position));
            mMediaPlayer.seekTo(position);
        }
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }

    private void updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void initializeProgressCallback() {
        final int duration = mMediaPlayer.getDuration();
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
            logToUI(String.format("firing setPlaybackDuration(%d sec)",
                    TimeUnit.MILLISECONDS.toSeconds(duration)));
            logToUI("firing setPlaybackPosition(0)");
        }
    }

    private void logToUI(String message) {
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onLogUpdated(message);
        }
    }
    public interface OnComplete {
        void onComplete(List<QuranModel.Api> quran);
        void setSheikh(List<String> sheikh);
    }
}
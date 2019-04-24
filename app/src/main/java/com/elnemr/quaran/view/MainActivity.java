package com.elnemr.quaran.view;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elnemr.quaran.R;
import com.elnemr.quaran.adapter.QuranAdapter;
import com.elnemr.quaran.audioplay.AudioServiceBinder;
import com.elnemr.quaran.model.QuranModel;
import com.elnemr.quaran.playclicklistener.PlayClickListener;
import com.elnemr.quaran.presenter.MainActivityPresenter;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityPresenter.OnComplete,
        PlayClickListener {
    RecyclerView recycler_view;
    MaterialSpinner shiekhsName;
    MainActivityPresenter mainActivityPresenter;
    // String[] shekhNames;
    int reader_id = 1;
    List<String> shekhNames;

    private AudioServiceBinder audioServiceBinder = null;

    private Handler audioProgressUpdateHandler = null;

    // Show played audio progress.
    private ProgressBar backgroundAudioProgress;

    private TextView audioFileUrlTextView;

    // This service connection object is the bridge between activity and background service.
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Cast and assign background service's onBind method returned iBander object.
            audioServiceBinder = (AudioServiceBinder) iBinder;
            Log.e(" initiated", "onServiceConnected: "+ "initiated" );
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    void initViews() {
         mainActivityPresenter = new MainActivityPresenter(this);
        recycler_view = findViewById(R.id.recycler_view);
        shiekhsName = findViewById(R.id.sheikhName);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        mainActivityPresenter.getQuran(reader_id);
        mainActivityPresenter.setSheikh();
        shiekhsName.setItems(shekhNames);
        shiekhsName.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                int pos = shekhNames.indexOf(item);
                Log.e("pos", "onItemSelected: " + pos);
                Log.e("item", "onItemSelected: " + item);
                Log.e("id", "onItemSelected: " + id);
                if (pos != 0) {
                    Log.e("pos in ", "onItemSelected: " + pos);
                    Log.e("item in ", "onItemSelected: " + item);
                    reader_id = pos;
                    Log.e("reader id", "onItemSelected: " + reader_id);
                    mainActivityPresenter.getQuran(reader_id);
                }
            }
        });
    }

    @Override
    public void onComplete(List<QuranModel.Api> quran) {
        QuranModel.Api aa = (QuranModel.Api) quran;
        QuranAdapter quranAdapter = new QuranAdapter(quran, MainActivity.this);
        quranAdapter.setHasStableIds(true);
        recycler_view.setAdapter(quranAdapter);
    }

    @Override
    public void setSheikh(List<String> sheikh) {
        shekhNames = sheikh;
        Log.e("shekhNames", "setSheikh: " + shekhNames.size());
    }

    @Override
    public void onItemClick(int position, String url, String action) {
        if (action.equalsIgnoreCase("start")) {

            try {
                Log.e("hhghghg", "onItemClick: "+ url );
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();

                Toast.makeText(getApplicationContext(), "Start play web audio file.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equalsIgnoreCase("pause")) {
            audioServiceBinder.pauseAudio();
            Toast.makeText(getApplicationContext(), "Play web audio file is paused.", Toast.LENGTH_LONG).show();
        } else if (action.equalsIgnoreCase("stop")) {
            audioServiceBinder.stopAudio();
            backgroundAudioProgress.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Stop play web audio file.", Toast.LENGTH_LONG).show();
        }
    }
}
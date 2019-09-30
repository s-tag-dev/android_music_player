package ir.s_tag.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.s_tag.musicplayer.CustomViews.MusicPlayer;
import ir.s_tag.musicplayer.DataModel.SongsDataModel;
import ir.s_tag.musicplayer.Interfaces.IPhoneVolumeChange;
import ir.s_tag.musicplayer.Interfaces.ITimeSet;
import ir.s_tag.musicplayer.Interfaces.IVolSet;

public class MainActivity extends AppCompatActivity {
    MusicPlayer player;
    AudioManager audioManager;
    SettingsContentObserver mSettingsContentObserver;
    Cursor cursor;
    MediaPlayer mp;
    List<SongsDataModel> songs = new ArrayList<>();
    ConstraintLayout play_btn;
    SongsDataModel currentSong ;
    TextView title , author;
    Timer timer;
    ImageView play_ic , cover_image;

    ImageButton next_song , previous_song;

    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getMusics();
//        for (int i = 0; i < songs.size(); i++) {
//            Log.i(MyApplication.TAG, "onCreate: Songs : dir : "+ songs.get(i).getDir()+ " || name : "+songs.get(i).getTitle());
//        }
        setSong(currentIndex);


        preparePlayer();

        uiIntracts();

    }

    private void uiIntracts() {

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp.isPlaying()){
                    mp.pause();
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                    }
                    play_ic.setVisibility(View.VISIBLE);
                }else{
                    mp.start();
                    play_ic.setVisibility(View.GONE);
//                    PlaySong();
                    setTimer();
                }
            }
        });

        next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex++;

                if(currentIndex > songs.size() - 1 ){
                    currentIndex = 0;
                }
                PlaySong();
//                setTimer();
            }
        });
        previous_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex--;
                if(currentIndex < 0 ){
                    currentIndex = songs.size() - 1;
                }
                PlaySong();
//                setTimer();
            }
        });

    }

    void PlaySong(){
        Log.i(MyApplication.TAG, "PlaySong:MediaPlayer Play!");
        setSong(currentIndex);
        timer = null;
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.i(MyApplication.TAG, "onError: Errororoorrorr!!!!!!!!!!!!!!!!!");
                setSong(currentIndex);

                return false;
            }
        });
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.start();
                setTimer();
                Log.i(MyApplication.TAG, "onPrepared: MediaPlayer Playing here!");
            }
        });
        play_ic.setVisibility(View.GONE);
//        if(timer == null){
//        setTimer();
        Log.i(MyApplication.TAG, "PlaySong: MediaPlayer Playing!!!!!");
//        }
    }

    private void preparePlayer() {
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / 15;

        player.setVolume(vol);
        player.setEventListener(new IVolSet() {
            @Override
            public void onVolSet(int percent) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,percent * 15 / 100,0);
            }
        }, new ITimeSet() {
            @Override
            public void onTimeSet(int percent) {
                mp.seekTo(percent * mp.getDuration() / 100);
                player.setTime(mp.getCurrentPosition() * 100 / mp.getDuration() ,  getTime());
            }
        });
    }

    private void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mp != null && mp.isPlaying()) {
                            player.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mp.isPlaying()){
                                        player.setTime(mp.getCurrentPosition() * 100 / mp.getDuration() ,  getTime());
                                        Log.i(MyApplication.TAG, "run: pos : "  + mp.getCurrentPosition());
                                        Log.i(MyApplication.TAG, "run: dur : "  + mp.getDuration());
                                        if(mp.getCurrentPosition() / 1000 + 2  >= mp.getDuration() / 1000){
                                            currentIndex++;
                                            if(currentIndex > songs.size() - 1 ){
                                                currentIndex = 0;
                                            }
                                            PlaySong();
                                        }
                                    }
                                }
                            });
                        } else {
                            if(timer != null){
                                timer.cancel();
                                timer.purge();
                            }
                        }
                    }
                });
            }
        }, 0, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }


    void init(){

        mSettingsContentObserver = new SettingsContentObserver(this, new Handler(), new IPhoneVolumeChange() {
            @Override
            public void volumeChangeListener() {
                int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / 15;
//                Log.i(MyApplication.TAG, "volumeChangeListener: Working!" + vol);
                player.setVolume(vol);
            }
        });
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );


        audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        player = findViewById(R.id.player);
        author = findViewById(R.id.author);
        title = findViewById(R.id.title);
        play_ic = findViewById(R.id.play_ic);
        play_btn = findViewById(R.id.play_btn);
        next_song = findViewById(R.id.next_song );
        previous_song = findViewById(R.id.previous_song);
        cover_image = findViewById(R.id.cover_image);
        mp = new MediaPlayer();
    }

    void getMusics(){
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);


        while(cursor.moveToNext()){
            SongsDataModel song = new SongsDataModel(cursor.getInt(0),cursor.getString(4).replace(".flac" , "") ,cursor.getString(3) ,cursor.getString(1) ,cursor.getString(4) );
//            if(cursor.getString(3).endsWith(".mp3")){
//            }
            songs.add(song);
//            Log.i(MyApplication.TAG, "getMusics: +"+cursor.getString(0) + "||" + cursor.getString(1) + "||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" +  cursor.getString(4) + "||" +  cursor.getString(5));
//            songs.add();
        }
    }

    void setSong(int index){
        if(songs.size() == 0){
            Toast.makeText(this, "No Song Found!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mp != null){
            mp.pause();
            mp.stop();
        }
        mp = new MediaPlayer();
        currentIndex = index;
        currentSong = songs.get(index);
        title.setText(currentSong.getTitle());
        author.setText(currentSong.getAuthor());
        try {
            if(mp.isPlaying()){
                mp.pause();
                mp.stop();
            }
            mp.setDataSource(currentSong.getDir());
            mp.prepareAsync();

        } catch (Exception e) {
//            e.printStackTrace();
            Toast.makeText(this, "Sound File Is Corrupted!", Toast.LENGTH_SHORT).show();
            mp = new MediaPlayer();
        }
    }



    String getTime(){
        Date date = new Date((long)(mp.getCurrentPosition() - (1800 * 1000)));
        String formattedDate = new SimpleDateFormat("mm:ss").format(date);
        return formattedDate;
    }

}

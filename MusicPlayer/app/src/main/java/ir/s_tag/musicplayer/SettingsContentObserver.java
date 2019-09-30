package ir.s_tag.musicplayer;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import ir.s_tag.musicplayer.Interfaces.IPhoneVolumeChange;

import static ir.s_tag.musicplayer.MyApplication.TAG;

public class SettingsContentObserver extends ContentObserver {
    int previousVolume;
    Context context;
    private final IPhoneVolumeChange iPhoneVolumeChange;

    public SettingsContentObserver(Context c, Handler handler , IPhoneVolumeChange iPhoneVolumeChange) {
        super(handler);
        context=c;
        this.iPhoneVolumeChange = iPhoneVolumeChange;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta=previousVolume-currentVolume;

        iPhoneVolumeChange.volumeChangeListener();

    }
}
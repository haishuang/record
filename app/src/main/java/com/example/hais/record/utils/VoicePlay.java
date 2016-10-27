package com.example.hais.record.utils;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by Huang hai-sen on 2016/10/26.
 */

public class VoicePlay {
    private static VoicePlay instance = null;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    /**
     * 上一个播放的路径
     */
    private String lastPath;

    private VoicePlay() {
        mediaPlayer = new MediaPlayer();
    }

    public static VoicePlay getInstance() {
        if (instance == null) {
            synchronized (VoicePlay.class) {
                if (instance == null)
                    instance = new VoicePlay();
            }
        }
        return instance;
    }

    public void play(String path) {
        if (isPlaying) {
            stop();
            if (!path.equals(lastPath)) {
                playing(path);
            }
        }else {
            playing(path);
        }

    }

    private void playing(String path)  {
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.start();
            isPlaying=true;
            lastPath=path;
        }catch (Exception e){

        }

    }

    public void stop() {

    }
}

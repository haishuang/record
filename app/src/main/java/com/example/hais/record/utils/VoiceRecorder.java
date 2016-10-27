package com.example.hais.record.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;


import java.io.File;
import java.io.IOException;

public class VoiceRecorder {
    MediaRecorder recorder;

    static final String EXTENSION = ".amr";

    private boolean isRecording = false;
    private String voiceFilePath = null;
    private String voiceFileName = null;
    private File file;

    private Handler handler;

    public VoiceRecorder(Handler handler) {
        this.handler = handler;
    }

    /**
     * start recording to the file
     */
    public String startRecording() {
        file = null;
        try {

            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioChannels(1); // MONO
            recorder.setAudioSamplingRate(8000); // 8000Hz
            recorder.setAudioEncodingBitRate(64); // seems if change this to
                                                    // 128, still got same file
                                                    // size
            voiceFileName = getVoiceFileName(String.valueOf(System.currentTimeMillis()));
            voiceFilePath = Environment.getExternalStorageDirectory() +"/zhish/record/"+ voiceFileName;
            checkFile(voiceFilePath);
            file = new File(voiceFilePath);

            recorder.setOutputFile(file.getAbsolutePath());
            recorder.prepare();
            isRecording = true;
            recorder.start();
        } catch (IOException e) {

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int time = 1;
                    while (isRecording) {
                        android.os.Message msg = new android.os.Message();
                        msg.what = recorder.getMaxAmplitude() * 13 / 0x7FFF;
                        msg.arg1=time;
                        handler.sendMessage(msg);
                        SystemClock.sleep(1000);
                        time++;
                    }
                } catch (Exception e) {

                }
            }
        }).start();

        Log.e("voice", "start voice recording to file:" + file.getAbsolutePath());
        return file == null ? null : file.getAbsolutePath();
    }

    public static void checkFile(String filePath) {
        File file1 = new File(filePath.substring(0, filePath.lastIndexOf("/")));
        if (!file1.exists()) {
            try {
                file1.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * stop the recoding
     * 
     * @return seconds of the voice recorded
     */

    public void discardRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                if (file != null && file.exists() && !file.isDirectory()) {
                   // file.delete();
                }
            } catch (IllegalStateException e) {
            } catch (RuntimeException e){}
            isRecording = false;
        }
    }
//
//    public int stopRecoding() {
//        if(recorder != null){
//            isRecording = false;
//            recorder.stop();
//            recorder.release();
//            recorder = null;
//
//            if(file == null || !file.exists() || !file.isFile()){
//                return EMError.FILE_INVALID;
//            }
//            if (file.length() == 0) {
//                file.delete();
//                return EMError.FILE_INVALID;
//            }
//            int seconds = (int) (new Date().getTime() - startTime) / 1000;
//            EMLog.d("voice", "voice recording finished. seconds:" + seconds + " file length:" + file.length());
//            return seconds;
//        }
//        return 0;
//    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }

    private String getVoiceFileName(String uid) {
        Time now = new Time();
        now.setToNow();
        return uid + now.toString().substring(0, 15) + EXTENSION;
    }

    public boolean isRecording() {
        return isRecording;
    }

    
    public String getVoiceFilePath() {
        return voiceFilePath;
    }
    
    public String getVoiceFileName() {
        return voiceFileName;
    }
}

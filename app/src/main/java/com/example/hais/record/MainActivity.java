package com.example.hais.record;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.Voice;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hais.record.utils.VoiceRecorder;
import com.example.hais.record.utils.SDUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private TextView tvRecord;
    private ImageView ivSpeak;
    private TextView tvPrompt;
    private TextView tvTime;
    private ListView lv;
    private Context mContext;

    private BaseAdapter adapter;
    private VoiceRecorder voiceRecorder;

    private List<String> list = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int s = msg.arg1 % 60;
            String text = "";
            switch (s%7) {//实际开发使用动画代替
                case 0:
                    text = " . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " .";
                    break;
                case 1:
                    text = " .  . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " .  . ";
                    break;
                case 2:
                    text = " . . . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " . . .";
                    break;
                case 3:
                    text = ". . . . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " . . . .";
                    break;
                case 6:
                    text = " . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " .";
                    break;
                case 5:
                    text = " . . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " . . ";
                    break;
                case 4:
                    text = " . . . " + msg.arg1 / 60 + ":" + (s < 10 ? "0" + s : s) + " . . .";
                    break;
                default:break;
            }

            tvTime.setText(text);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        tvRecord = (TextView) findViewById(R.id.tv_record);
        ivSpeak = (ImageView) findViewById(R.id.iv_speak);
        tvPrompt = (TextView) findViewById(R.id.tv_prompt);
        tvTime = (TextView) findViewById(R.id.tv_time);
        lv = (ListView) findViewById(R.id.lv);
        setAdapter();
        setSpeakBtn(false);

        voiceRecorder = new VoiceRecorder(handler);

        ivSpeak.setOnClickListener(new RecordPlayClickListener(mContext, ivSpeak, "/storage/emulated/0/zhish/fy.mp3"));

        ivSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setSpeakBtn(true);
                        startRecord();
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        setSpeakBtn(false);
                        adapter.notifyDataSetChanged();
                        voiceRecorder.discardRecording();
                        return true;
                }
                return false;
            }
        });
    }

    private void setAdapter() {
        list.add("/storage/emulated/0/zhish/147753526827520161027T102748.mp3");
        getFileList();
        lv.setAdapter(adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                if(i%2==0){
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_chatting_text_left,null);

                }else {
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_chatting_text_right,null);
                }
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_chat_msg);
                imageView.setOnClickListener(new RecordPlayClickListener(mContext, imageView, list.get(i)));
                return view;
            }
        });
    }



    private void startRecord() {
        //1.判断sd卡是否存在
        if (!SDUtils.isSDCardEnable()) {
            Toast.makeText(mContext, "录音需要sd卡支持", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //滴的一声
            new RecordPlayClickListener(mContext).startPlayRecord("/storage/emulated/0/zhish/fy.mp3");
            Thread.sleep(100);
            //开始录音
            list.add(voiceRecorder.startRecording());
        } catch (Exception e) {
            Log.e(TAG, "录音异常：" + e.getMessage());
        }
    }

    private void setSpeakBtn(boolean isSpeack) {
        if (isSpeack) {
            tvTime.setVisibility(View.VISIBLE);
            tvPrompt.setVisibility(View.GONE);
        } else {
            tvTime.setVisibility(View.GONE);
            tvPrompt.setVisibility(View.VISIBLE);
        }
    }

    public void getFileList() {
        list.addAll(getFile("/storage/emulated/0/zhish/record/"));
    }

    private List<String>  getFile(String path){
        List<String> fs = new ArrayList<>();
        File file = new File(path);
        File[] array = file.listFiles();

        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                fs.add(array[i].getPath());
            }
        }
        return fs;
    }
}

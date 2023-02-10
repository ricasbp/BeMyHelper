package com.example.bemyhelper;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicLong;

public class HelpRequestListener implements Runnable {


    private OnDataCheck onDataCheck;
    private int time;
    private AtomicLong startTime;
    private long millis;
    private int seconds;
    private int minutes;
    private int timeout;
    private OnRequestTimeout onRequestTimeout;
    private TextView textView;
    private Handler timerHandler;

    private boolean started;
    private boolean finished;

    public HelpRequestListener(OnDataCheck onDataCheck, int time){
        this.startTime = new AtomicLong();
        this.onDataCheck = onDataCheck;
        this.time = time;
        this.startTime = new AtomicLong();
        this.timerHandler = new Handler();
        this.started = false;
        this.finished = false;
    }

    public HelpRequestListener(OnDataCheck onDataChange, int time, TextView textView, int timeout){
        this(onDataChange, time);
        this.timeout = timeout;
        this.textView = textView;
    }

    @Override
    public void run() {

        if(!finished){

            if(!started){
                this.startTime.set(System.currentTimeMillis());
                started = true;
            }

            this.millis = System.currentTimeMillis() - startTime.get();
            this.seconds = (int) (millis / 1000);
            this.minutes= seconds / 60;
            this.seconds = seconds % 60;

            Log.d("Time", this.seconds  + "");

            textView.setText(this.getFormatTime());

            if(this.GetSeconds() >= timeout){
                if(this.onRequestTimeout != null){
                    this.onRequestTimeout.apply();
                }

            }

            if(this.GetSeconds() % time == 0){
                onDataCheck.apply(() -> {
                    stop();
                });
            }


            timerHandler.postDelayed(this, 500);
        }
    }

    public int GetSeconds(){
        return minutes * 60 + seconds;
    }

    public String getFormatTime(){
        return String.format("%d:%02d", minutes, seconds);
    }

    public void AddOnRequestTimeout(OnRequestTimeout onRequestTimeout) {
        this.onRequestTimeout = onRequestTimeout;
    }

    public void start() {
        timerHandler.removeCallbacks(this);
        timerHandler.postDelayed(this, 0);
    }

    public void stop(){
        finished = true;
        timerHandler.removeCallbacks(this);
    }

    interface OnRequestTimeout{
        void apply();
    }

    interface OnDataChange{
        void apply();
    }

    interface OnDataCheck {
        void apply(OnDataChange onDataChange);
    }

}

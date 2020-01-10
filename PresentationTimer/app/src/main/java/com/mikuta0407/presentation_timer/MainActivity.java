package com.mikuta0407.presentation_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    boolean run = false; //タイマーが動作中かそうじゃないかを記録
    boolean finished = true; //一時停止状態か、終了/キャンセルで止まったのかを記録。
    boolean paused = false;
    int mode = 1;
    boolean rotated = false;

    private TextView timerText; //数字表示部のTextView
    private TextView pastText; //経過時間のTextView
    private ProgressBar timeProgressBar; //プログレスバー
    private FloatingActionButton start_pause;
    private FloatingActionButton cancel;
    private CountDownTimer countDown; //カウントダウンクラス
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss.S", java.util.Locale.JAPANESE); //データフォーマット
    private RadioGroup ptime_radiobox;
    private RadioGroup qtime_radiobox;

    public long ptime; //発表時間設定を記録
    public long qtime; //質問時間設定を記録
    private long leftTime; //残り時間を記録


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // いろいろ定義たいむ
        start_pause = findViewById(R.id.start_pause);
        cancel = findViewById(R.id.cancel);

        ptime_radiobox = (RadioGroup)findViewById(R.id.ptime_radiobox);
        qtime_radiobox = (RadioGroup)findViewById(R.id.qtime_radiobox);
        timerText = findViewById(R.id.disp_time);
        timerText.setText(dataFormat.format(0));
        pastText = findViewById(R.id.past_time);
        pastText.setText(dataFormat.format(0));
        timeProgressBar = findViewById(R.id.progressBar);
            timeProgressBar.setProgress(100);

        // 回転時状態復元
        // savedInstanceStateがnullでないときは、Activityが再作成されたと判断、状態を復元
        if (savedInstanceState!=null) {
            // まず動作状態を復元
            run = savedInstanceState.getBoolean("runstatus");
            finished = savedInstanceState.getBoolean("finishedstatus");
            paused = savedInstanceState.getBoolean("pausedstatus");
            mode = savedInstanceState.getInt("modestatus");
            rotated = savedInstanceState.getBoolean("rotatedstatus");

            //数値系
            ptime = savedInstanceState.getLong("ptimestatus");
            qtime = savedInstanceState.getLong("qtimestatus");
            leftTime = savedInstanceState.getLong("leftTimestatus");

            //UI系
            // スタート・ストップボタンの画像状態を復元
            if (run) { // 動作中なら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更.
                startTimer(); //タイマー続行
            } else {   // 動いてなかったら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
            }

            // ラジオボタン
            // 発表時間
            if (ptime == 10000) {
                ptime_radiobox.check(R.id.ptime10);
            } else if (ptime == 1200000) {
                ptime_radiobox.check(R.id.ptime20);
            } else if (ptime == 1800000) {
                ptime_radiobox.check(R.id.ptime30);
            }
            // 質問時間
            if (qtime == 15000) {
                qtime_radiobox.check(R.id.qtime5);
            } else if (qtime == 600000) {
                qtime_radiobox.check(R.id.qtime10);
            }
        }


        // 以下メイン処理

        // スタート・ストップボタンが押されたら
        start_pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View spClick) {
                if (run) { //動作中なら
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                    pauseTimer();
                } else {        // 動いてなかったら
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更

                    if (finished == true){ //完了後または初期状態の場合
                        // 発表時間設定の選択を取得
                        int ptimeId = ptime_radiobox.getCheckedRadioButtonId();
                        // ptimeに時間設定。(ms)
                        if (ptimeId == R.id.ptime10) {
                            //ptime = 600000;  //10*60*1000 ms
                            ptime = 10000;
                        } else if (ptimeId == R.id.ptime20) {
                            ptime = 1200000; //20*60*1000 ms
                        } else if (ptimeId == R.id.ptime30) {
                            ptime = 1800000; //30*60*1000 ms
                        }


                        // 質問時間設定の選択を取得

                        int qtimeId = qtime_radiobox.getCheckedRadioButtonId();
                        // qtimeに時間設定。(ms)
                        if (qtimeId == R.id.qtime5) {
                            //qtime = 300000;  //5*60*1000 ms
                            qtime = 15000;
                        } else if (qtimeId == R.id.qtime10) {
                            qtime = 600000;  //10*60*1000 ms
                        }

                    }
                    //ptime_radiobox.setEnabled(false);
                    ptime_radiobox.setEnabled(false);
                    startTimer(); // タイマースタート
                }
            }
        });

        // キャンセルボタンが押されたら
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetTimer();
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                ptime_radiobox.setEnabled(true);
                String resetFormatted = String.format(java.util.Locale.JAPANESE, "%02d:%02d.%01d", 0, 0, 0);
                timerText.setText(resetFormatted);
                pastText.setText(resetFormatted);
            }
        });
    }


    private void startTimer() {  // タイマー実行

        run = true;
        if (paused == true){ //一時停止復帰
            paused = false;
            Log.i("デバッグ", "一時停止から復帰しました");
        } else if (rotated){
            rotated = false;
        } else { // ノーマル起動
            if (mode == 1) {
                leftTime = ptime;
            } else if (mode == 2) {
                leftTime = qtime;
            }
        }
            countDown = new CountDownTimer(leftTime,10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    leftTime = millisUntilFinished;
                    updateCountDownText();
                }

                @Override
                public void onFinish() {
                    if (mode == 1) { //まだ質問に入ってなかったら(発表が終わったら)
                        mode = 2; //発表は完了
                        Log.i("デバッグ", "発表時間が終わりました。");
                        leftTime = qtime;
                        updateCountDownText();
                        startTimer(); //質問時間スタート
                        updateCountDownText();
                    } else { //質問も終わったら
                        Log.i("デバッグ", "質問時間も終わりました");
                        resetTimer();
                    }
                }
            }.start();
        }



    private void pauseTimer(){  // 一時停止
        countDown.cancel();
        paused = true;
        run = false;
        finished = false;
    }

    private void resetTimer(){  // リセット
        countDown.cancel();
        leftTime = ptime;
        updateCountDownText();
        finished = true;
        paused = false;
        mode = 1;
        run = false;
        timeProgressBar.setProgress(100);
    }

    private void updateCountDownText(){ // 時刻の表示
        int minutes = (int)(leftTime/1000)/60;
        int seconds = (int)(leftTime/1000)%60;
        int ms = (int)((leftTime%1000)/100);

        int pastminutes;
        int pastseconds;
        int pastms;

        int timeprogress;

        if (mode == 1) { //はぴょう時間なら
            pastminutes = (int)((ptime-leftTime+100) / 1000)/60;
            pastseconds = (int)((ptime-leftTime) / 1000)%60;
            pastms = (int)((ptime-leftTime+100) %1000/100);
            timeprogress = ((int) (((float) leftTime / (float) ptime) * 100));
        } else {
            pastminutes = (int)((qtime-leftTime+100) / 1000)/60;
            pastseconds = (int)((qtime-leftTime) / 1000)%60;
            pastms = (int)((qtime-leftTime+100) %1000/100);
            timeprogress = ((int) (((float) leftTime / (float) qtime) * 100));
        }

        String timerLeftFormatted = String.format(java.util.Locale.JAPANESE, "%02d:%02d.%01d", minutes, seconds, ms);
        timerText.setText(timerLeftFormatted);
        String timerPastFormatted = String.format(java.util.Locale.JAPANESE, "%02d:%02d.%01d", pastminutes, pastseconds, pastms);
        pastText.setText(timerPastFormatted);
        timeProgressBar.setProgress(timeprogress);
    }


    // 状態セーブ
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //boolarn系
        outState.putBoolean("runstatus", run);
        outState.putBoolean("finishedstatus", finished);
        outState.putBoolean("pausedstatus", paused);
        outState.putInt("modestatus", mode);


        //数値系
        outState.putLong("ptimestatus", ptime);
        outState.putLong("qtimestatus", qtime);
        outState.putLong("leftTimestatus", leftTime);

        rotated = true;
        outState.putBoolean("rotatedstatus", rotated);
    }

}

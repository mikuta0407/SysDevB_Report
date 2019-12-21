package com.mikuta0407.PresentationTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    boolean run = false; //タイマーが動作中かそうじゃないかを記録
    boolean finished = true; //一時停止状態か、終了/キャンセルで止まったのかを記録。
    boolean paused = false;
    int mode = 1;

    private TextView timerText; //数字表示部のTextView
    private ProgressBar timeProgressBar; //プログレスバー
    private FloatingActionButton start_pause;
    private FloatingActionButton cancel;
    private CountDownTimer pCountDown; //発表カウントダウンクラス
    private CountDownTimer qCountDown; //質問カウントダウンクラス
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss", java.util.Locale.JAPANESE); //データフォーマット

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
        timerText = findViewById(R.id.disp_time);
        timerText.setText(dataFormat.format(0));
        timeProgressBar = findViewById(R.id.progressBar);

        // 回転時状態復元
        // savedInstanceStateがnullでないときは、Activityが再作成されたと判断、状態を復元
        if (savedInstanceState!=null) {
            // まず動作状態を復元
            run = savedInstanceState.getBoolean("runstatus");
            finished = savedInstanceState.getBoolean("finishedstatus");
            //paused;
            //mode;

            //数値系
            ptime = savedInstanceState.getLong("ptimestatus");
            qtime = savedInstanceState.getLong("qtimestatus");
            leftTime = savedInstanceState.getLong("leftTimestatus");

            // スタート・ストップボタンの画像状態を復元
            if (run) { // 動作中なら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更.
                if (mode == 1) {
                    pStartTimer(); //タイマー続行
                } else if (mode == 2) {
                    qStartTimer();
                }
            } else {   // 動いてなかったら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
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
                        RadioGroup ptime_radiobox = (RadioGroup)findViewById(R.id.ptime_radiobox);
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
                        RadioGroup qtime_radiobox = (RadioGroup)findViewById(R.id.qtime_radiobox);
                        int qtimeId = qtime_radiobox.getCheckedRadioButtonId();
                        // qtimeに時間設定。(ms)
                        if (qtimeId == R.id.qtime10) {
                            //qtime = 300000;  //5*60*1000 ms
                            qtime = 15000;
                        } else if (qtimeId == R.id.qtime10) {
                            qtime = 600000;  //10*60*1000 ms
                        }

                    }

                    if (mode ==1){
                        pStartTimer();
                    }
                }
            }
        });

        // キャンセルボタンが押されたら
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetTimer();
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
            }
        });
    }


    private void pStartTimer() {  // 質問タイマー実行
        run = true;
        if (paused == true){
            paused = false;
            Log.i("デバッグ", "一時停止から復帰しました");
        } else {
            leftTime = ptime;
        }

        pCountDown = new CountDownTimer(leftTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mode = 2; //発表は完了
                Log.i("デバッグ", "発表時間が終わりました。");
                leftTime = qtime;
                qStartTimer(); //質問時間スタート
                //} else { //質問も終わったら
                //    Log.i("デバッグ", "質問時間も終わりました");
                //    resetTimer();

            }
        }.start();
    }

    private void qStartTimer() {  // 質問タイマー実行
        run = true;
        if (paused == true){
            paused = false;
            Log.i("デバッグ", "一時停止から復帰しました");
        } else {
            leftTime = qtime;
        }

        qCountDown = new CountDownTimer(leftTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //質問も終わったら
                Log.i("デバッグ", "質問時間も終わりました");
                resetTimer();
            }
        }.start();
    }


    private void pauseTimer(){  // 一時停止
        if (mode == 1){
            pCountDown.cancel();
        } else {
            qCountDown.cancel();
        }

        paused = true;
        run = false;
        finished = false;
    }

    private void resetTimer(){  // リセット
        pCountDown.cancel();
        qCountDown.cancel();
        leftTime = ptime;
        updateCountDownText();
        finished = true;
        paused = false;
        mode = 1;
        run = false;
    }

    private void updateCountDownText(){ // 時刻の表示
        int minutes = (int)(leftTime/1000)/60;
        int seconds = (int)(leftTime/1000)%60;
        int timeprogress;
        String timerLeftFormatted = String.format(java.util.Locale.JAPANESE, "%02d:%02d", minutes, seconds);
        timerText.setText(timerLeftFormatted);
        if (mode == 1) { //はぴょう時間なら
            timeprogress = ((int) (((float) leftTime / (float) ptime) * 100));
        } else {
            timeprogress = ((int) (((float) leftTime / (float) qtime) * 100));
        }
        timeProgressBar.setProgress(timeprogress);
    }


    // 状態セーブ
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //boolarn系
        outState.putBoolean("runstatus", run);
        outState.putBoolean("finishedstatus", finished);
        //paused
        //mode

        //数値系
        outState.putLong("ptimestatus", ptime);
        outState.putLong("qtimestatus", qtime);
        outState.putLong("leftTimestatus", leftTime);
    }

}

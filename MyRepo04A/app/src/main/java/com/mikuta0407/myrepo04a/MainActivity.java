package com.mikuta0407.myrepo04a;

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
    boolean pfinished = false; //発表時間が終わったかどうか
    boolean question = false; //質問タイムかどうかの判定

    private TextView timerText; //数字表示部のTextView
    private ProgressBar timeProgressBar; //プログレスバー
    private FloatingActionButton start_pause;
    private FloatingActionButton cancel;
    private CountDownTimer countDown; //カウントダウンクラス
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
            question = savedInstanceState.getBoolean("questionstatus");
            pfinished = savedInstanceState.getBoolean("pfinishedstatus");

            //数値系
            ptime = savedInstanceState.getLong("ptimestatus");
            qtime = savedInstanceState.getLong("qtimestatus");
            leftTime = savedInstanceState.getLong("leftTimestatus");

            // スタート・ストップボタンの画像状態を復元
            if (run) { // 動作中なら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更.
                startTimer(); //タイマー続行
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
                    if (finished == true){
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

                    startTimer();
                }
            }
        });

        // キャンセルボタンが押されたら
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetTimer();
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                run = false;
                finished = true;
                question = false;
                pfinished = false;
            }
        });
    }


    private void startTimer() {  // タイマー実行
        if ((pfinished == false) && (question == false)){ //まだ発表時間が終わってなくて、
            if (!run) { //動作中じゃなかったら(回転後じゃなくて純粋なオンだったら)
                run = true;
                leftTime = ptime;
            }
        } else if ((pfinished == true) && (question == false)){ // 発表が終わってて回転後じゃなくてonFinishから呼ばれたら
                question = true;
                leftTime = qtime;
        }
            countDown = new CountDownTimer(leftTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                if (question == false) { //まだ質問に入ってなかったら(発表が終わったら)
                    pfinished = true; //発表は完了
                    startTimer(); //質問時間スタート
                } else { //質問も終わったら
                    run = false; //動作じゃないよ
                    finished = true; //おわったよ
                    question = false; //質問も終わったよ
                    pfinished = false;  //状態戻すよ
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                }
            }
        }.start();

    }

    private void pauseTimer(){  // 一時停止
        countDown.cancel();
        run = false;
        finished = false;
    }

    private void resetTimer(){  // リセット
        leftTime = ptime;
        countDown.cancel();
        updateCountDownText();
        finished = true;
        question = false;
    }

    private void updateCountDownText(){ // 時刻の表示
        int minutes = (int)(leftTime/1000)/60;
        int seconds = (int)(leftTime/1000)%60;
        int timeprogress;
        String timerLeftFormatted = String.format(java.util.Locale.JAPANESE, "%02d:%02d", minutes, seconds);
        timerText.setText(timerLeftFormatted);
        if (question == false) {
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
        outState.putBoolean("questionstatus", question);
        outState.putBoolean("pfinishedstatus", pfinished);

        //数値系
        outState.putLong("ptimestatus", ptime);
        outState.putLong("qtimestatus", qtime);
        outState.putLong("leftTimestatus", leftTime);
    }

}

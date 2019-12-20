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

    private TextView timerText; //数字表示部のTextView
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss", java.util.Locale.JAPANESE); //データフォーマット
    public long ptime; //発表時間設定を記録
    private long ptimeLeft; //残り時間を記録
    public long qtime;
    private long qtimeLeft;
    public long timetmp = 0;
    public long interval = 0;
    private ProgressBar timeProgressBar;
    private CountDownTimer countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // いろいろ宣言たいむ
        final FloatingActionButton start_pause = findViewById(R.id.start_pause);
        final FloatingActionButton cancel = findViewById(R.id.cancel);
        timerText = findViewById(R.id.disp_time);
        timerText.setText(dataFormat.format(0));
        timeProgressBar = findViewById(R.id.progressBar);

        // savedInstanceStateがnullでないときは、Activityが再作成されたと判断、状態を復元
        if (savedInstanceState!=null) {
            // まず動作状態を復元
            run = savedInstanceState.getBoolean("runstatus");

            
            // スタート・ストップボタンの画像状態を復元
            if (run) { // 動作中なら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更.
            } else {   // 動いてなかったら
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
            }
        }




        // スタート・ストップボタンが押されたら
        start_pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View spClick) {
                if (run) { //動作中なら
                    run = false; //動作をオフにして
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                    pauseTimer();
                    //floatingActionButton.
                } else {        // 動いてなかったら
                    run = true; // オンにして
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更
                    if (finished == true){
                        // 発表時間設定の選択を取得
                        RadioGroup ptime_radiobox = (RadioGroup)findViewById(R.id.ptime_radiobox);
                        int ptimeId = ptime_radiobox.getCheckedRadioButtonId();
                        // ptimeに時間設定。(ms)
                        if (ptimeId == R.id.ptime10) {
                            ptime = 600000;  //10*60*1000 ms
                        } else if (ptimeId == R.id.ptime20) {
                            ptime = 1200000; //20*60*1000 ms
                        } else if (ptimeId == R.id.ptime30) {
                            ptime = 1800000; //30*60*1000 ms
                        }
                        ptimeLeft = ptime;

                        // 質問時間設定の選択を取得
                        RadioGroup qtime_radiobox = (RadioGroup)findViewById(R.id.qtime_radiobox);
                        int qtimeId = qtime_radiobox.getCheckedRadioButtonId();
                        // qtimeに時間設定。(ms)
                        if (qtimeId == R.id.qtime10) {
                            qtime = 300000;  //5*60*1000 ms
                        } else if (qtimeId == R.id.qtime10) {
                            qtime = 600000;  //10*60*1000 ms
                        }
                        qtimeLeft = qtime;
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
            }
        });
    }


    private void startTimer() {  // タイマー実行
        countDown = new CountDownTimer(ptimeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ptimeLeft = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                run = false;

            }
        }.start();

        run = true;

    }

    private void pauseTimer(){  // 一時停止
        countDown.cancel();
        run = false;
        finished = false;
    }

    private void resetTimer(){  // リセット
        ptimeLeft = ptime;
        countDown.cancel();
        updateCountDownText();
        finished = true;

    }

    private void updateCountDownText(){ // 時刻の表示
        int minutes = (int)(ptimeLeft/1000)/60;
        int seconds = (int)(ptimeLeft/1000)%60;
        String timerLeftFormatted = String.format(java.util.Locale.JAPANESE, "%02d:%02d", minutes, seconds);
        timerText.setText(timerLeftFormatted);
        int timeprogress = ( (int)(((float)ptimeLeft/(float)ptime)*100) );
        timeProgressBar.setProgress(timeprogress);
        Log.i("時間", "Progress: " + ( (int)(((float)ptimeLeft/(float)ptime)*100) ) );
    }


    // 状態セーブ
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //メンバ変数runを保存
        outState.putBoolean("runstatus", run);
        //ptimeとqtime
        outState.putLong("ptimestatus", ptime);
        outState.putLong("qtimestatus", qtime);
    }

}

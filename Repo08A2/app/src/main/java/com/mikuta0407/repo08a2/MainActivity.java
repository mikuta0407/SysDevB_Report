package com.mikuta0407.repo08a2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private SurfaceTimerView mSfTV;

    public static ProgressBar timeProgressBar; //プログレスバー
    private FloatingActionButton start_pause;
    private FloatingActionButton cancel;
    private RadioGroup ptime_radiobox;


    public long time; //発表時間設定を記録
    private long leftTime; //残り時間を記録

    private boolean pause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // いろいろ定義たいむ
        start_pause = findViewById(R.id.start_pause);
        cancel = findViewById(R.id.cancel);

        ptime_radiobox = (RadioGroup)findViewById(R.id.ptime_radiobox);

        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mSfTV = new SurfaceTimerView(this, mSurfaceView);
        timeProgressBar = findViewById(R.id.progressBar);
            timeProgressBar.setProgress(100);

        // 以下メイン処理

        // スタート・ストップボタンが押されたら
        start_pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View spClick) {
                if (!mSfTV.mTimerPause) { //動作中なら
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                    mSfTV.pause();
                } else {        // 停止/一時停止中なら
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更
                    if (mSfTV.mTimerStop) { //完了後または初期状態の場合
                        // 発表時間設定の選択を取得
                        int ptimeId = ptime_radiobox.getCheckedRadioButtonId();
                        // ptimeに時間設定。(ms)
                        if (ptimeId == R.id.ptime10) {
                            //ptime = 600000;  //10*60*1000 ms
                            mSfTV.time = 10000;
                        } else if (ptimeId == R.id.ptime20) {
                            mSfTV.time = 1200000; //20*60*1000 ms
                        } else if (ptimeId == R.id.ptime30) {
                            mSfTV.time = 1800000; //30*60*1000 ms
                        }
                        mSfTV.start();
                    } else { //一時停止なら
                        mSfTV.restart();
                    }
                }

            }

        });

        // キャンセルボタンが押されたら
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mSfTV.reset();
                start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
            }
        });
    }


}

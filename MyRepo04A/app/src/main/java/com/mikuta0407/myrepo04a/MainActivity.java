package com.mikuta0407.myrepo04a;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class MainActivity extends AppCompatActivity {

    boolean run = false;
    int ptime;
    int qtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // いろいろ宣言たいむ
        final FloatingActionButton start_pause = findViewById(R.id.start_pause);

        if (savedInstanceState!=null) {
            // savedInstanceStateがnullでないときは
            // オブジェクトが再作成されたと判断
            // 状態を復元

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
                    //floatingActionButton.
                } else {        // 動いてなかったら
                    run = true; // オンにして
                    start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //一時停止ボタンに変更
                }
            }
        });

        // 発表時間設定の選択を取得
        RadioGroup ptime_radiobox = (RadioGroup)findViewById(R.id.ptime_radiobox);
        int ptimeId = ptime_radiobox.getCheckedRadioButtonId();

        if (ptimeId == R.id.ptime10) {
            ptime = 10;
        } else if (ptimeId == R.id.ptime20) {
            ptime = 20;
        } else if (ptimeId == R.id.ptime30) {
            ptime = 30;
        }

        // 質問時間設定の選択を取得
        RadioGroup qtime_radiobox = (RadioGroup)findViewById(R.id.qtime_radiobox);
        int qtimeId = qtime_radiobox.getCheckedRadioButtonId();

        if (qtimeId == R.id.qtime10) {
            qtime = 5;
        } else if (qtimeId == R.id.qtime10) {
            qtime = 10;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //メンバ変数runを保存
        outState.putBoolean("runstatus", run);
        //ptimeとqtime
        outState.putInt("ptimestatus", ptime);
        outState.putInt("qtimestatus", qtime);
    }
}

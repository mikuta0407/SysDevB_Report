package com.mikuta0407.myrepo04a;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class MainActivity extends AppCompatActivity {

    boolean run = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // いろいろ宣言たいむ
        final FloatingActionButton start_pause = findViewById(R.id.start_pause);

        if (savedInstanceState!=null) {
            //savedInstanceStateがnullでないときは
            //オブジェクトが再作成されたと判断
            //カウンターの値を復元
            run = savedInstanceState.getBoolean("runstatus");
        }

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //メンバ変数mCounterを退避
        outState.putBoolean("runstatus", run);
    }
}

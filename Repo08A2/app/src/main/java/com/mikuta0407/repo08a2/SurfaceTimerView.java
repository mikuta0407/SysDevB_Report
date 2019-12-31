package com.mikuta0407.repo08a2;

/**
 * 　SurfaceView によるタイマーアプリ
 * 高速描画できるSurfaceViewをもとに、ジェスチャーイベントを組み込んでいる
 * 特に、Viewのタッチイベント以上の機能のある　ジェスチャー機能（例えばフリック）
 * を使用している。
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class SurfaceTimerView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Thread mThread;
    private boolean mAttached;
    private GestureDetector mGestureDetector;

    public boolean mTimerPause;
    public boolean mTimerStop;

    // 開始時刻、終了時刻、残時間の変数
    private long startTime;
    private long endTime;
    private long remainingTime;

    // 発表時間用変数
    public long time;  // 分

    //Paint用インスタンス
    Paint paint;
    //　一度だけチャイムを鳴らすために、鳴らしたかどうかの状態を表す変数を用意する。
    boolean rang = false;

    //コンストラクタ
    public SurfaceTimerView(Context context, SurfaceView mSurfaceView) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);

        //描画設定用のPaintクラスのインスタンス生成
        paint = new Paint();
    }

    // 画面変更時の処理
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // アプリ開始時の処理
    public void surfaceCreated(SurfaceHolder holder) {
        //タイマー機能の有効／無効
        mTimerStop = true;
        //スレッド動作の有効／無効
        mAttached = true;
        // 描画スレッドを生成、起動する
        mThread = new Thread(this);
        mThread.start();  // run()メソッドが1回実行される
        this.startScreen();//アプリ使用法解説の初期画面表示（右フリックで開始）
    }

    // アプリ終了時の処理
    public void surfaceDestroyed(SurfaceHolder holder) {
        // スレッドを終了させる
        mAttached = false;
        // スレッド終了待ち
        while (mThread.isAlive());
    }

    // 開始時の、「操作方法解説画面」の描画
    private void startScreen() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            try {
                canvas.drawColor(Color.WHITE); // 背景を白く塗りつぶす
                paint.setAntiAlias(true);
                paint.setTextSize(50.f);
                paint.setColor(Color.rgb(50, 50, 50));
                canvas.drawText("00:00.00",0,0, paint);

            } finally {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // タイマー開始後の描画処理
    private void doDraw() {
        //残時間(ミリ秒）の計算
        remainingTime = endTime - System.currentTimeMillis();
        String mm = Integer.toString((int)(remainingTime/1000)/60);
        String ss = Integer.toString((int)(remainingTime/1000)%60);
        String tmpff = Integer.toString((int)(remainingTime));
        String ff = tmpff.substring(tmpff.length() - 3);

        paint.setTextSize(400);
        paint.setColor(Color.rgb(200, 0, 200));
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            try {
                canvas.drawColor(Color.WHITE);// 背景を白く塗りつぶす
                //残り時間の描画
                paint.setColor(Color.rgb(200, 200, 0));
                paint.setAntiAlias(true);
                paint.setTextSize(150.f);
                canvas.drawText("" + mm + ":" + ss + "." + ff, 0, 0, paint);
            } finally {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
        int timeprogress = (int)(((float)(remainingTime)) / ((float)(time))  * 100);
        MainActivity.timeProgressBar.setProgress(timeprogress);
    }

    // 描画スレッド実行コード
    public void run() {
        while (mAttached) {
            if (mTimerStop || mTimerPause) {
                //タイマー停止時は処理を飛ばす
                continue;
            }
            // タイマーが動作しているときの描画処理
            doDraw();
            try {
                // 10ミリ秒 スリープする　
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // タイマー停止操作
    public void stop() {
        //タイマー動作を停止
        mTimerStop = true;
        mTimerPause = false;

    }
    public void pause() {
        //タイマー動作を一時停止
        mTimerPause = true;
    }

    //最初からタイマーをスタート
    public void start(){
        startTime = System.currentTimeMillis();
        endTime = startTime + time;
        mTimerStop = false;
        mTimerPause = false;
    }

    // タイマー再開
    public void restart() {
        startTime = System.currentTimeMillis();
        endTime = startTime + remainingTime;
        mTimerStop = false;
        mTimerPause = false;

    }
}

package com.mikuta0407.repo08a2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class SurfaceTimerView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    // SurfaceViewer関連
    private SurfaceHolder mHolder;
    private Thread mThread;
    private boolean mAttached;

    // タイマーの状態管理
    public boolean mTimerPause = true;
    public boolean mTimerStop = true;
    public boolean reset = false;
    public boolean finish = false;

    // 開始時刻、終了時刻、残時間の変数
    private long startTime;
    private long endTime;
    private long remainingTime;

    // 発表時間用変数
    public long time;  // msで

    // Paint用インスタンス
    Paint paint;

    // コンストラクタ
    public SurfaceTimerView(Context context, SurfaceView sv) {
        super(context);
        mHolder = sv.getHolder();
        mHolder.addCallback(this);

        // 描画設定用のPaintクラスのインスタンス生成
        paint = new Paint();
        Log.i("デバッグ", "SurfaceTimerViewのコンストラクタが起動");
        surfaceCreated(mHolder);
        int width = sv.getWidth();
        int height = sv.getHeight();
        Log.i("デバッグ", "Width= " + width);
        Log.i("デバッグ", "Height= " + height);
    }

    // 画面変更時の処理
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    // アプリ開始時の処理
    public void surfaceCreated(SurfaceHolder Holder) {
        Log.i("デバッグ", "surfaceCreated");

        // スレッド動作の有効／無効
        mAttached = true;

        // 描画スレッドを生成、起動する
        mThread = new Thread(this);
        mThread.start();  // run()メソッドが1回実行される
        this.startScreen(); // 00:00.0と表示
    }

    // アプリ終了時の処理
    public void surfaceDestroyed(SurfaceHolder holder) {
        // スレッドを終了させる
        mAttached = false;
        // スレッド終了待ち
        while (mThread.isAlive());
    }

    // 00:00.0を表示
    private void startScreen() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            try {
                canvas.drawColor(Color.parseColor("#2E2E2E"));// 背景を白く塗りつぶす
                //getHolder().setFormat(PixelFormat.TRANSLUCENT);
                //setZOrderOnTop(true);

                paint.setAntiAlias(true);
                paint.setTextSize(130.f);
                paint.setColor(Color.rgb(255, 255, 255));
                canvas.drawText("00:00.000",15,155, paint);

            } finally {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // タイマー開始後の描画処理
    private void doDraw() {

        //残時間(ミリ秒）→ 表示用数値の計算
        remainingTime = endTime - System.currentTimeMillis();

        int mm = ((int) (remainingTime / 1000) / 60);
        int ss = ((int) (remainingTime / 1000) % 60);
        int ff = (int)(remainingTime % 1000);
        if (remainingTime <= 0){
            ff = 000;
            finish = true;
        }

        paint.setTextSize(40);
        paint.setColor(Color.rgb(200, 0, 200));
        Canvas canvas = mHolder.lockCanvas();

        if (canvas != null) {
            try {
                canvas.drawColor(Color.parseColor("#2E2E2E"));// 背景を白く塗りつぶす
                //getHolder().setFormat(PixelFormat.TRANSLUCENT);
                //setZOrderOnTop(true);

                //残り時間の描画
                paint.setColor(Color.rgb(255, 255, 255));
                paint.setAntiAlias(true);
                paint.setTextSize(130.f);
                //String.format("%1$02d", 1);
                canvas.drawText("" + (String.format("%1$02d",mm)) + ":" + (String.format("%1$02d",ss)) + "." + (String.format("%1$03d",ff)), 15, 155, paint);
            } finally {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }

        // プログレスバーの更新
        int timeprogress = (int)(((float)(remainingTime)) / ((float)(time))  * 100);
        MainActivity.timeProgressBar.setProgress(timeprogress);

    }

    @Override
    // 描画スレッド実行コード
    public void run() {
        while (mAttached) {
            // 初期状態なら
            if (mTimerStop && mTimerPause) {
                // プログレスバーを0状態に(100から減っていくようにしているので、初期状態が100
                MainActivity.timeProgressBar.setProgress(100);
                if (reset) { // リセットボタンが押されていたら
                    remainingTime = 0; // 時間を0に
                    reset = false;
                    MainActivity.timeProgressBar.setProgress(100);
                    mTimerStop = true;
                    mTimerPause = true;
                }
                continue;
            } else if (!mTimerStop && mTimerPause) {
                //タイマー一時停止時は処理を何もしない
                continue;
            } else if (finish) {
                // 普通にカウントダウンしていって0になったら
                mTimerStop = true;
                mTimerPause = true;
                //ring();
                finish=false;
                MainActivity.start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play)); //再生ボタンに変更
                continue;
            }

            if (reset) { // リセットボタンが押されていたら
                remainingTime = 0; // 時間を0に
                reset = false;
                MainActivity.timeProgressBar.setProgress(100);
                mTimerStop = true;
                mTimerPause = true;
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

    public void reset() {
        reset = true;
        MainActivity.timeProgressBar.setProgress(100);
    }
}

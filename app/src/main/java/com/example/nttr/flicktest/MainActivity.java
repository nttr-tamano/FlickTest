package com.example.nttr.flicktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // テキストビュー
    private TextView textView1;
    private TextView textView2;

    // X軸最低スワイプ距離
    private static final int SWIPE_MIN_DISTANCE = 50;

    // X軸最低スワイプスピード
    private static final int SWIPE_THRESHOLD_VELOCITY = 10;     //200;

    // Y軸の移動距離　これ以上なら横移動を判定しない
    private static final int SWIPE_MAX_OFF_PATH = 1500;         //250;

    // タッチイベントを処理するためのインタフェース
    private GestureDetector mGestureDetector;

    // 統計情報
    private int mSwipeCount = 0;
    private float mSwipeSpeedAverageX = 0.0f;
    private float mSwipeSpeedAverageY = 0.0f;
    private int mSwipeSpeedMaxX = 0;
    private int mSwipeSpeedMaxY = 0;

    private String crlf = null;     // 改行

    private final float SLOPE_RATE = 1.2f;     // 斜めと判定されないための比率

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // タッチイベントのインスタンスを生成
        mGestureDetector = new GestureDetector(this, mOnGestureListener);
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);

        crlf = System.getProperty("line.separator");    // 改行の取得

    }

    // タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // タッチイベントのリスナー
    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        // フリックイベント
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

            try {

                // 移動距離・スピードを出力
                float distance_x = Math.abs((event1.getX() - event2.getX()));
                float velocity_x = Math.abs(velocityX);
                float distance_y = Math.abs((event1.getY() - event2.getY()));
                float velocity_y = Math.abs(velocityY);
                String strX = null;
                String strY = null;

                // 最大
                mSwipeSpeedMaxX = Math.max(mSwipeSpeedMaxX, (int)velocity_x);
                mSwipeSpeedMaxY = Math.max(mSwipeSpeedMaxY, (int)velocity_y);
                // 平均
                mSwipeSpeedAverageX = (mSwipeSpeedAverageX * mSwipeCount + velocity_x) / (mSwipeCount + 1);
                mSwipeSpeedAverageY = (mSwipeSpeedAverageY * mSwipeCount + velocity_y) / (mSwipeCount + 1);
                // 計測回数をインクリメント
                mSwipeCount++;

                textView1.setText("横の移動距離:" + distance_x + " 横の移動スピード:" + velocity_x
                        + crlf + "縦の移動距離:" + distance_y + " 縦の移動スピード:" + velocity_y
                        + crlf
                        + crlf + "計測回数:" + mSwipeCount
                        + crlf + "横の最大スピード:" + mSwipeSpeedMaxX + " 横の平均スピード:" + mSwipeSpeedAverageX
                        + crlf + "縦の最大スピード:" + mSwipeSpeedMaxY + " 縦の平均スピード:" + mSwipeSpeedAverageY
                );

                // 移動距離が大きすぎる場合
                if ((Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH)
                        || (Math.abs(event1.getX() - event2.getX()) > SWIPE_MAX_OFF_PATH)) {
                    textView2.setText("縦または横の移動距離が大きすぎ");
                }

                textView2.setText("");
                // 開始位置から終了位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                if  (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    textView2.setText("右から左");
                    strX = "左";

                }
                // 終了位置から開始位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    textView2.setText("左から右");
                    strX = "右";
                }

                // Y軸の移動速度が指定値より大きい
                if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    textView2.setText(textView2.getText() + " 且つ 下から上");
                    strY = "上";
                }
                else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    textView2.setText(textView2.getText() + " 且つ 上から下");
                    strY = "下";
                }

                // 方向（上記の合成？）
                // event1:移動開始 event2:移動終了
                // X軸は、右が大きい。Y軸は下が大きい。
                if (distance_x > distance_y * SLOPE_RATE) {
                    // X方向が大きい
                    textView2.setText(textView2.getText() + " どちらかといえば、" + strX + "方向");
                } else if (distance_x * SLOPE_RATE < distance_y) {
                    // Y方向が大きい
                    textView2.setText(textView2.getText() + " どちらかといえば、" + strY + "方向");
                } else {
                    // ほぼ等しい
                    textView2.setText(textView2.getText() + " ほぼ、斜め" + strX + strY + "方向");
                }

            } catch (Exception e) {
                // TODO
            }

            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

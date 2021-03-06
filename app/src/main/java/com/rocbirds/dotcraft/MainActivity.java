package com.rocbirds.dotcraft;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final ImageView[] containerViewArr = new ImageView[9];
    private final ImageView[] dotViewArr = new ImageView[9];
    private TextView levelMessage;
    private TextView pointMessage;
    private Level level;
    private int levelNum=3;
//    private boolean first ;
    private ImageView backupDot;

    private float lastMotionX;
    private float lastMotionY;
    private int touchIndex;

    private static final int STATE_IDLE = 0;
    private static final int STATE_WAITING_DRAG = 1;
    private static final int STATE_HORIZONTAL_DRAG = 2;
    private static final int STATE_VERTICAL_DRAG = 3;
    private int state = STATE_IDLE;

    private int touchSlop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        initDotViews();
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context .MODE_PRIVATE);
        levelNum=sharedPreferences.getInt("levelNum",3);
        Log.d("Create","levelNum is "+levelNum);
        initContainerViews();
        levelMessage=findViewById(R.id.levelMessage);
        pointMessage=findViewById(R.id.pointMessage);
        if (levelNum==3){
            initLevel();
        }else {
           makeLevel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("levelNum");
        editor.putInt("levelNum",levelNum);
        editor.apply();
        Log.d("Destroy","the activity is Destroy,levelNum="+levelNum);
    }

    private void initDotViews() {
        dotViewArr[0] = findViewById(R.id.dot0);
        dotViewArr[1] = findViewById(R.id.dot1);
        dotViewArr[2] = findViewById(R.id.dot2);
        dotViewArr[3] = findViewById(R.id.dot3);
        dotViewArr[4] = findViewById(R.id.dot4);
        dotViewArr[5] = findViewById(R.id.dot5);
        dotViewArr[6] = findViewById(R.id.dot6);
        dotViewArr[7] = findViewById(R.id.dot7);
        dotViewArr[8] = findViewById(R.id.dot8);
        backupDot = findViewById(R.id.backup_dot);
    }

    private void initContainerViews() {
        containerViewArr[0] = findViewById(R.id.container0);
        containerViewArr[1] = findViewById(R.id.container1);
        containerViewArr[2] = findViewById(R.id.container2);
        containerViewArr[3] = findViewById(R.id.container3);
        containerViewArr[4] = findViewById(R.id.container4);
        containerViewArr[5] = findViewById(R.id.container5);
        containerViewArr[6] = findViewById(R.id.container6);
        containerViewArr[7] = findViewById(R.id.container7);
        containerViewArr[8] = findViewById(R.id.container8);
    }

    private  void  makeLevel(){
        level =new RandomLevel(levelNum);
        while (LevelUtils.hasSuccess(level)) {
            Log.d("makeLevel","a lucky case");
            level=new RandomLevel(levelNum);
        }
        refreshView();
    }
    private void initLevel() {
        level = new Level1();
//        levelNum=3;
        refreshView();
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void refreshView() {

        levelMessage.setText("now level is "+(levelNum-3));
        pointMessage.setText("now point number is"+levelNum);
        int[] containerArr = level.getContainerArray();
        int[] dotArr = level.getDotArray();
        for (int i = 0; i < 9; i++) {
            if (containerArr[i] == 1) {
                Log.v("Container", String.valueOf(i)+"___"+levelNum);
                containerViewArr[i].setImageResource(R.drawable.shape_ring_white);
            } else {
                containerViewArr[i].setImageResource(0);
            }
        }
        for (int i = 0; i < 9; i++) {
            if (dotArr[i] == 1) {
                Log.v("DOT", String.valueOf(i)+"___"+levelNum);
                dotViewArr[i].setImageResource(R.drawable.shape_dot_white);
            } else {
                dotViewArr[i].setImageResource(R.drawable.shape_dot_black);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // ????????????
            case MotionEvent.ACTION_DOWN: {
                state = STATE_IDLE;
                lastMotionX = event.getRawX();
                lastMotionY = event.getRawY();
                touchIndex = getTouchViewIndex(lastMotionX, lastMotionY);
                if (touchIndex != -1) {
                    state = STATE_WAITING_DRAG;
                }
                break;
            }
            // ????????????
            case MotionEvent.ACTION_MOVE: {
                float deltaX = event.getRawX() - lastMotionX;
                float deltaY = event.getRawY() - lastMotionY;
                if (state == STATE_WAITING_DRAG) {
                    // ???????????????????????????????????????
                    if (Math.abs(deltaX) >= touchSlop || Math.abs(deltaY) >= touchSlop) {
                        state = Math.abs(deltaX) > Math.abs(deltaY) ? STATE_HORIZONTAL_DRAG : STATE_VERTICAL_DRAG;
                    }
                }
                if (state == STATE_HORIZONTAL_DRAG) {
                    // ????????????
                    horizontalDragging(touchIndex / 3, deltaX);
                } else if (state == STATE_VERTICAL_DRAG) {
                    // ????????????
                    verticalDragging(touchIndex % 3, deltaY);
                }
                lastMotionX = event.getRawX();
                lastMotionY = event.getRawY();
                break;
            }
            // ????????????
            case MotionEvent.ACTION_UP: {
                if (state == STATE_HORIZONTAL_DRAG) {
                    // ????????????
                    horizontalDragEnd(touchIndex / 3);
                } else if (state == STATE_VERTICAL_DRAG) {
                    // ????????????
                    verticalDragEnd(touchIndex % 3);
                }
                touchIndex = -1;
                state = STATE_IDLE;
                break;
            }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getTouchViewIndex(float x, float y) {
        for (int i = 0; i < 9; i++) {
            ImageView dotView = dotViewArr[i];
            int[] location = new int[2];
            dotView.getLocationOnScreen(location);
            int left = location[0];
            int top = location[1];
            int right = left + dotView.getWidth();
            int bottom = top + dotView.getHeight();
            if (x >= left && x <= right && y >= top && y <= bottom) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ????????????????????????????????????
     */
    private void horizontalDragging(int rowIndex, float delta) {
        ImageView leftDot = dotViewArr[rowIndex * 3];
        ImageView middleDot = dotViewArr[rowIndex * 3 + 1];
        ImageView rightDot = dotViewArr[rowIndex * 3 + 2];
        float translationX = getValidTranslation(leftDot.getTranslationX() + delta);
        leftDot.setTranslationX(translationX);
        middleDot.setTranslationX(translationX);
        rightDot.setTranslationX(translationX);
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        if (translationX > 0) {
            // ????????????backup???????????????
            backupDot.setTranslationX(translationX - backupDot.getWidth());
            backupDot.setImageDrawable(rightDot.getDrawable());
        } else {
            // ????????????backup???????????????
            backupDot.setTranslationX(backupDot.getWidth() * 3 + translationX);
            backupDot.setImageDrawable(leftDot.getDrawable());
        }
        backupDot.setTranslationY(backupDot.getHeight() * rowIndex);
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void horizontalDragEnd(int rowIndex) {
        ImageView leftDot = dotViewArr[rowIndex * 3];
        ImageView middleDot = dotViewArr[rowIndex * 3 + 1];
        ImageView rightDot = dotViewArr[rowIndex * 3 + 2];
        float targetTranslationX = leftDot.getTranslationX();
        leftDot.setTranslationX(0.0f);
        middleDot.setTranslationX(0.0f);
        rightDot.setTranslationX(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (Math.abs(targetTranslationX) < backupDot.getWidth() * 1.0f / 2) {
            return;
        }
        boolean toRight = targetTranslationX > backupDot.getWidth() * 1.0f / 2;
        LevelUtils.horizontalDragLevel(level, toRight, rowIndex);
        refreshView();
        if (LevelUtils.hasSuccess(level)) {
            congratulations();
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void verticalDragging(int columnIndex, float delta) {
        ImageView topDot = dotViewArr[columnIndex];
        ImageView middleDot = dotViewArr[columnIndex + 3];
        ImageView bottomDot = dotViewArr[columnIndex + 6];
        float translationY = getValidTranslation(topDot.getTranslationY() + delta);
        topDot.setTranslationY(translationY);
        middleDot.setTranslationY(translationY);
        bottomDot.setTranslationY(translationY);
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        backupDot.setTranslationX(backupDot.getWidth() * columnIndex);
        if (translationY > 0) {
            // ????????????backup???????????????
            backupDot.setTranslationY(translationY - backupDot.getHeight());
            backupDot.setImageDrawable(bottomDot.getDrawable());
        } else {
            // ????????????backup???????????????
            backupDot.setTranslationY(backupDot.getHeight() * 3 + translationY);
            backupDot.setImageDrawable(topDot.getDrawable());
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void verticalDragEnd(int columnIndex) {
        ImageView topDot = dotViewArr[columnIndex];
        ImageView middleDot = dotViewArr[columnIndex + 3];
        ImageView bottomDot = dotViewArr[columnIndex + 6];
        float targetTranslationY = topDot.getTranslationY();
        topDot.setTranslationY(0.0f);
        middleDot.setTranslationY(0.0f);
        bottomDot.setTranslationY(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (Math.abs(targetTranslationY) < backupDot.getWidth() * 1.0f / 2) {
            return;
        }
        boolean toTop = targetTranslationY < backupDot.getWidth() * -1.0f / 2;
        LevelUtils.verticalDragLevel(level, toTop, columnIndex);
        refreshView();
        if (LevelUtils.hasSuccess(level)) {
            congratulations();
        }
    }

    /**
     * ??????????????????????????????
     */
    private float getValidTranslation(float translation) {
        return Math.max(backupDot.getWidth() * -1, Math.min(translation, backupDot.getWidth()));
    }

    private void congratulations() {
        levelNum++;
        if (levelNum==9) {
            Toast.makeText(this, "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            levelNum=3;
//            initLevel();
        }else {
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
        }
        makeLevel();
    }

}
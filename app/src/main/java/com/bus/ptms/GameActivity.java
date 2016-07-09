package com.bus.ptms;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends AppCompatActivity{

    ViewPager pager;
    MediaPlayer mPlayer;
    int life = 5;
    int correct =0;
    long startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pager = (ViewPager)findViewById(R.id.viewPager);

        pager.setAdapter(new GamePagerAdapter(getSupportFragmentManager(),getApplicationContext(),getQuestionCount()));
        pager.getAdapter().notifyDataSetChanged();
        startTime = System.currentTimeMillis() / 1000L;
    }
    private int getQuestionCount(){
        // get questions count from database
        SQLiteHelper helper = new SQLiteHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM QuestionsLog";
        Cursor cursor = db.rawQuery(sql,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    private void initializePlayer(){
        // play bgm music
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameplay);
        mPlayer.setLooping(true);
        mPlayer.start();
    }
    public void triggerGameOver(boolean isWin){
        // triggered when turn end
        saveChanges();
        Intent intent= new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("correct",correct);
        bundle.putBoolean("isWin",isWin);
        intent.setClass(GameActivity.this,ResultActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
    @Override
    public void onPause() {
        // paused
        super.onPause();
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }
    @Override
    public void onResume() {
        // resumed
        super.onResume();
        if (mPlayer == null) {
            initializePlayer();
        }
        else{
            mPlayer.start();
        }
    }
    public void addCorrect(){
        // triggered when users inserted correct answer
        correct++;
    }
    public void onMove() {
        // should move on to next page of pager
        if(pager.getCurrentItem()+1 == pager.getAdapter().getCount()) {
            triggerGameOver(true);
            finish();
        }
        else
            pager.setCurrentItem(pager.getCurrentItem()+1,true);
    }

    private void saveChanges(){
        // save details into database
       long duration =  (System.currentTimeMillis() / 1000L) - startTime;
        SQLiteDatabase db = new SQLiteHelper(getApplicationContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_UNIX", startTime);
        values.put("_DURATION", duration);
        values.put("_CORRECTCOUNT", correct);
        long newRowId = db.insert(
                "GamesLog",
                null,
                values);
    }
    public int getLife(){
        return life;
    }
    // return current life amount

    public void setLife(int life){
        this.life = life;
    }
    // set new life amount
}

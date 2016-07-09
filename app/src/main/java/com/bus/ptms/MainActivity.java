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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mPlayer;
    Boolean isDatabaseEmpty = true;
    Boolean isSettingUp = false;
    SQLiteHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView notifyText = (TextView)findViewById(R.id.startNotify);
        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDatabaseEmpty){
                    pullQuestion();
                }
                else {
                    if(!isSettingUp) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, GameActivity.class);
                        //intent.setClass(MainActivity.this, ResultActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        try {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            notifyText.startAnimation(anim); // Press Start Blink Animation
            initializePlayer();
            initializeDatabase();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void initializePlayer(){
        // intro music
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.intro);
        mPlayer.setLooping(true);
        mPlayer.start();
    }
    private void initializeDatabase(){
        // database init , clear auto increment
        helper = new SQLiteHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM QuestionsLog");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'QuestionsLog'");
        db.close();
    }
    private void insertIntoDatabase(JSONObject res){
        // insert questions to database
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            JSONArray arr = res.getJSONArray("Questions");
            for(int i=0;i<arr.length();i++){
                JSONObject obj = arr.getJSONObject(i);
                String question = obj.getString("question");
                String answer = obj.getString("answer");
                ContentValues values = new ContentValues();
                values.put("_QUESTION", question);
                values.put("_ANSWER", answer);
                long newRowId = db.insert(
                        "QuestionsLog",
                        null,
                        values);
            }
            db.close();
            Toast.makeText(getApplicationContext(),"Ready to play",Toast.LENGTH_SHORT).show();
            isSettingUp = false;
            isDatabaseEmpty = false;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void pullQuestion(){
        // pull json from server
        isSettingUp = true;
        Toast.makeText(getApplicationContext(),"Setting up questions..",Toast.LENGTH_SHORT).show();
        Ion.with(getApplicationContext())
                .load("http://itdmoodle.hung0530.com/ptms/questions_ws.php")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(e!= null)
                            e.printStackTrace();
                        else {
                            try {
                                JSONObject obj = new JSONObject(result.toString());
                                insertIntoDatabase(obj);
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
    @Override
    public void onPause() {
        // pause music while not focus
        super.onPause();
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }
    @Override
    public void onResume() {
        // resume music
        super.onResume();
        if (mPlayer == null) {
            initializePlayer();
        }
        else{
            mPlayer.start();
        }
    }

}

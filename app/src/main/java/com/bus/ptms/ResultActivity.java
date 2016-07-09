package com.bus.ptms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResultActivity extends AppCompatActivity {
    GamePlayBarChart barChart;
    TextView timeSpendText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle bundle = getIntent().getExtras();
        barChart = (GamePlayBarChart) findViewById(R.id.bar);
        timeSpendText = (TextView)findViewById(R.id.timeSpentText);
        TextView correctText = (TextView)findViewById(R.id.correctText);
        TextView statusText = (TextView)findViewById(R.id.status);
        boolean isWon = bundle.getBoolean("isWin");
        int correct = bundle.getInt("correct");
        FrameLayout replayBT = (FrameLayout)findViewById(R.id.replyBT);
        replayBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ResultActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FrameLayout finishBT = (FrameLayout)findViewById(R.id.finishBT);
        finishBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.e("iswon",isWon+"");
        statusText.setText(isWon ? "You Won!" : "You Lose!");
        correctText.setText("Correct: "+correct);
        getGamePlayData();
    }
    private void getGamePlayData(){
        SQLiteDatabase db = new SQLiteHelper(getApplicationContext()).getReadableDatabase();
        String sql = "SELECT * FROM GamesLog ORDER BY _id DESC LIMIT 5";
        Cursor cursor = db.rawQuery(sql,null);
        List<Bundle> list = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            int correct = cursor.getInt(cursor.getColumnIndex("_CORRECTCOUNT"));
            long duration = cursor.getInt(cursor.getColumnIndex("_DURATION"));
            long playUnix = cursor.getInt(cursor.getColumnIndex("_UNIX"));
            Date df = new Date(playUnix*1000);
            String date = new SimpleDateFormat("MM/dd").format(df);
            Bundle b = new Bundle();
            b.putInt("correct",correct);
            b.putString("duration",getTimeDuration(duration*1000));
            b.putString("date",date);
            list.add(b);
            cursor.moveToNext();
        }
        timeSpendText.setText("Time spent:" + list.get(0).getString("duration"));
        barChart.setSource(list);
        db.close();
    }
    private String getTimeDuration(long millis){
        return String.format("%dm:%ds",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}

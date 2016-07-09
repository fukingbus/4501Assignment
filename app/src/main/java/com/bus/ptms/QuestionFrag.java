package com.bus.ptms;


import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFrag extends Fragment implements View.OnClickListener{
    SoundPoolHelper sPool;
    TextView answerField;
    TextView lifeText;
    TextView countdownText;
    TextView questText;
    int questionID;
    String correctAns;
    String question;
    Boolean isStarted = false;
    Boolean isRoundEnd = false;
    CountDownTimer timer;
    public QuestionFrag() {
        // Required empty public constructor
    }
    /**
        The fragment which would be the game controller/ ui/ main stuff
        created with the viewpager
    * */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_question, container, false);
        questionID = getArguments().getInt("pos");

        // sound pool ( for sound effects )
        sPool = new SoundPoolHelper(getContext());

        // num pads
        TextView btn0 = (TextView)root.findViewById(R.id.btn0);
        TextView btn1 = (TextView)root.findViewById(R.id.btn1);
        TextView btn2 = (TextView)root.findViewById(R.id.btn2);
        TextView btn3 = (TextView)root.findViewById(R.id.btn3);
        TextView btn4 = (TextView)root.findViewById(R.id.btn4);
        TextView btn5 = (TextView)root.findViewById(R.id.btn5);
        TextView btn6 = (TextView)root.findViewById(R.id.btn6);
        TextView btn7 = (TextView)root.findViewById(R.id.btn7);
        TextView btn8 = (TextView)root.findViewById(R.id.btn8);
        TextView btn9 = (TextView)root.findViewById(R.id.btn9);

        // two extra action buttons
        FrameLayout btnSubmit = (FrameLayout)root.findViewById(R.id.btnSubmit);
        FrameLayout btnClear = (FrameLayout)root.findViewById(R.id.btnClear);

        lifeText = (TextView)root.findViewById(R.id.lifeView);
         countdownText = (TextView)root.findViewById(R.id.countdownText);
         questText = (TextView)root.findViewById(R.id.questionText);
        answerField = (TextView)root.findViewById(R.id.answerField);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        questText.setOnClickListener(this);

        appendQuestion();
        return root;
    }
    private int getLife(){
        // get the life amount
        return ((GameActivity)getActivity()).getLife();
    }
    private void setLife(int life){
        // set the life amount
        ((GameActivity)getActivity()).setLife(life);
        lifeText.setText("LIFE: "+life);
        if(life<3){
            lifeText.setTextColor(getResources().getColor(R.color.colorHoloRed));
            if(life<=0) // game over
                ((GameActivity)getActivity()).triggerGameOver(false);
        }
    }
    private void appendQuestion(){
        //get question from database which matches the question number
        SQLiteHelper helper = new SQLiteHelper(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM QuestionsLog WHERE _id='"+(questionID+1)+"'";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        question = cursor.getString(cursor.getColumnIndex("_QUESTION"));
        correctAns = cursor.getString(cursor.getColumnIndex("_ANSWER"));
        cursor.close();
        db.close();
    }
    private void appendAnswer(String num){
        // triggered by user pressing the num pad
        if(isStarted && !isRoundEnd)
            answerField.setText(answerField.getText().toString()+num);
    }
    private void clearAnswer(){
        // clean
        answerField.setText("");
    }
    private void beginRound(){
        // after user pressed the 'begin' , the round begins
        lifeText.setText("LIFE: "+getLife());
        lifeText.setVisibility(View.VISIBLE);
        if(getLife()<3)
            lifeText.setTextColor(getResources().getColor(R.color.colorHoloRed));

        isStarted = true;
        clearAnswer();
        // count down timer to determine the user can only able to answer in 20 seconds
        timer = new CountDownTimer(20*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int sec = (int)(millisUntilFinished / 1000);
                if(sec < 10){
                    countdownText.setTextColor(getResources().getColor(R.color.colorHoloRed));
                    sPool.playSound("time_blip");
                }
                countdownText.setText(sec+"");
            }

            public void onFinish() {
                countdownText.setText("TIME OUT");
                triggerGameOver();
            }
        }.start();
        flipTextView(questText);
        questText.setText(question);
    }
    private void triggerGameOver(){
        // when game(round) over (timeout)
        isRoundEnd = true;
        sPool.playSound("game_over");
        setLife(getLife()-1);
        triggerReadyToNext();
    }
    private void triggerReadyToNext(){
        //display 'next round' text
        flipTextView(questText);
        questText.setText("NEXT ROUND");
    }
    private void moveToNext(){
        // pager should move to next page
        ((GameActivity)getActivity()).onMove();
    }
    private void triggerRoundEnd(){
        // when round ends
        if(answerField.getText().length()!=0) {
            isRoundEnd = true;
            timer.cancel();
            if (answerField.getText().toString().equals(correctAns)) {
                //answer correct
                answerField.setTextColor(getResources().getColor(R.color.colorHoloGreen));
                answerField.setText("CORRECT!");
                sPool.playSound("correct");
                ((GameActivity)getActivity()).addCorrect();
            } else {
                //answer wrong
                answerField.setTextColor(getResources().getColor(R.color.colorHoloRed));
                answerField.setText("WRONG!");
                sPool.playSound("wrong");
                setLife(getLife() - 1);
            }
            triggerReadyToNext();
        }
    }
    private void flipTextView(TextView view){
        // animation to flip the textview
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationX", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }
    @Override
    public void onClick(View v) {
        sPool.playSound("click");
        switch (v.getId()){
            case R.id.btn0:
                appendAnswer("0");
                break;
            case R.id.btn1:
                appendAnswer("1");
                break;
            case R.id.btn2:
                appendAnswer("2");
                break;
            case R.id.btn3:
                appendAnswer("3");
                break;
            case R.id.btn4:
                appendAnswer("4");
                break;
            case R.id.btn5:
                appendAnswer("5");
                break;
            case R.id.btn6:
                appendAnswer("6");
                break;
            case R.id.btn7:
                appendAnswer("7");
                break;
            case R.id.btn8:
                appendAnswer("8");
                break;
            case R.id.btn9:
                appendAnswer("9");
                break;
            case R.id.btnSubmit:
                if(isStarted && !isRoundEnd)
                    triggerRoundEnd();
                break;
            case R.id.btnClear:
                if(isStarted && !isRoundEnd)
                    clearAnswer();
                break;
            case R.id.questionText:
                if(!isStarted)
                    beginRound();
                if(isStarted && isRoundEnd)
                    moveToNext();
                break;
        }
    }

}

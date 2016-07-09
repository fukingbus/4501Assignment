package com.bus.ptms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xeonyan on 9/7/2016.
 */
public class SQLiteHelper  extends SQLiteOpenHelper {

    private final static int _DBVersion = 1;
    private final static String _DBName = "assignment.db";
    private final static String _QuestionTableName = "QuestionsLog";
    private final static String _InGameTableName = "GamesLog";
    public SQLiteHelper(Context context) {
        super(context, _DBName, null, _DBVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // create two tables
        String SQL = "CREATE TABLE IF NOT EXISTS " + _QuestionTableName + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_QUESTION TEXT, " +
                "_ANSWER TEXT" +
                ");";
        db.execSQL(SQL);
        SQL = "CREATE TABLE IF NOT EXISTS " + _InGameTableName + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "_UNIX TEXT, " +
                "_DURATION TEXT," +
                "_CORRECTCOUNT INT" +
                ");";
        db.execSQL(SQL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
         String SQL = "DROP TABLE " + _QuestionTableName;
        db.execSQL(SQL);
        SQL = "DROP TABLE " + _InGameTableName;
        db.execSQL(SQL);
    }

}

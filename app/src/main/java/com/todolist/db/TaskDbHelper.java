package com.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.TITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.STAGE + " INTEGER NOT NULL, " +
                TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + " INTEGER, " +
                "FOREIGN KEY(" + TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + ") REFERENCES " +
                CategoryContract.CategoryEntry.TABLE_NAME + "(" +
                CategoryContract.CategoryEntry._ID + "));";

        Log.d(this.getClass().getName(), "Kasia -> Create table string: " + createTable);

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().getName(), "Kasia -> Drop table");
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}
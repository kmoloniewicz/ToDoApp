package com.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CategoryDbHelper extends SQLiteOpenHelper {

    public CategoryDbHelper(Context context) {
        super(context, CategoryContract.DB_NAME, null, CategoryContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + CategoryContract.CategoryEntry.TABLE_NAME + " ( " +
                CategoryContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryContract.CategoryEntry.TITLE + " TEXT NOT NULL UNIQUE);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryContract.CategoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
package com.todolist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import com.todolist.db.CategoryContract;
import com.todolist.db.CategoryDbHelper;

public class CategoryDAOImpl implements CategoryDAO {

    public int findCategoryIdByName(Context context, String categoryName) {
        int result = -1;
        final SQLiteDatabase db = new CategoryDbHelper(context).getWritableDatabase();
        final Cursor cursor = db.query(CategoryContract.CategoryEntry.TABLE_NAME,
                new String[]{CategoryContract.CategoryEntry._ID},
                CategoryContract.CategoryEntry.TITLE + " like ?", new String[] {categoryName}, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(CategoryContract.CategoryEntry._ID);
            result = cursor.getInt(columnIndex);
        }
        cursor.close();
        db.close();

        return result;
    }

    public int findExistingCategoryIdByName(final Context context, final String categoryName) throws SQLException {
        final int categoryId = findCategoryIdByName(context, categoryName);
        if (categoryId == -1) {
            Log.d(this.getClass().getName(), "Category doesn't exist in database. Category name: " + categoryName);
            throw new SQLException("No Category has been found for this name: " + categoryName);
        }

        return categoryId;
    }

    public void deleteCategory(final Context context, final String categoryItemName) {
        final SQLiteDatabase db = new CategoryDbHelper(context).getWritableDatabase();
        db.delete(CategoryContract.CategoryEntry.TABLE_NAME,
                CategoryContract.CategoryEntry.TITLE + " = ?",
                new String[]{categoryItemName});
        db.close();
    }

    public void saveCategory(final Context context, final String categoryName) {
        final SQLiteDatabase db = new CategoryDbHelper(context).getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(CategoryContract.CategoryEntry.TITLE, categoryName);
        db.insertWithOnConflict(CategoryContract.CategoryEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public ArrayList<String> getAllExistingCategoryNames(final Context context) {
        final SQLiteDatabase db = new CategoryDbHelper(context).getReadableDatabase();
        final ArrayList<String> categoryList = new ArrayList<String>();
        final Cursor cursor = db.query(CategoryContract.CategoryEntry.TABLE_NAME,
                new String[]{CategoryContract.CategoryEntry._ID, CategoryContract.CategoryEntry.TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(CategoryContract.CategoryEntry.TITLE);
            categoryList.add(cursor.getString(idx));
        }

        cursor.close();
        db.close();
        return categoryList;
    }

    @Override
    public void updateCategoryName(Context context, String newCategoryName, String previousCategoryName) {
        SQLiteDatabase db = new CategoryDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryContract.CategoryEntry.TITLE, newCategoryName);

        db.updateWithOnConflict(CategoryContract.CategoryEntry.TABLE_NAME,
                values,
                CategoryContract.CategoryEntry.TITLE + " = ? ",
                new String[] {previousCategoryName},
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
}

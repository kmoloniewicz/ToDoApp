package com.todolist.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CategoryDAO {
    public int findCategoryIdByName(final Context context, final String categoryName);
    public int findExistingCategoryIdByName(final Context context, final String categoryName) throws SQLException;
    public void deleteCategory(final Context context, final String categoryItemName);
    public void saveCategory(final Context context, final String categoryName);
    public ArrayList<String> getAllExistingCategoryNames(final Context context);
    public void updateCategoryName(Context context, String newCategoryName, String previousCategoryName);
}

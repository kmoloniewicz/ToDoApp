package com.todolist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.todolist.beans.Task;
import com.todolist.db.CategoryContract;
import com.todolist.db.TaskContract;
import com.todolist.db.TaskDbHelper;

public class TaskDAOImpl implements TaskDAO {
    private static final String DONE_TASK_STATE = "1";

    public List<String> getAllExistingTaskNames(final Context context, final int categoryId) {
        final List<String> taskList = new ArrayList<String>();
        final SQLiteDatabase db = new TaskDbHelper(context).getReadableDatabase();
        final Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.TITLE},
                TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + "=?",
                new String[] {String.valueOf(categoryId)}, null, null, null);

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.TITLE);
            taskList.add(cursor.getString(idx));
        }

        cursor.close();
        db.close();
        return taskList;
    }

    @Override
    public List<Task> getAllExistingTasks(Context context, int categoryId) {
        final List<Task> taskList = new ArrayList<Task>();
        final SQLiteDatabase db = new TaskDbHelper(context).getReadableDatabase();
        final Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.TITLE, TaskContract.TaskEntry.STAGE},
                TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + "=?",
                new String[] {String.valueOf(categoryId)}, null, null, null);

        while (cursor.moveToNext()) {
            Task task = new Task();

            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.TITLE);
            task.setTaskName(cursor.getString(idx));

            idx = cursor.getColumnIndex(TaskContract.TaskEntry.STAGE);
            boolean taskDone = cursor.getInt(idx) == 1 ? true : false;
            task.setTaskDone(taskDone);

            taskList.add(task);
        }

        cursor.close();
        db.close();
        return taskList;
    }

    @Override
    public void deleteTask(Context context, String taskItemName, final int categoryId) {
        SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry.TITLE + " = ? and " + TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + " = ?",
                new String[]{taskItemName, String.valueOf(categoryId)});
        db.close();
    }

    @Override
    public void saveTask(final Context context, final String taskName, final int categoryId) {
        SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.TITLE, taskName);
        values.put(TaskContract.TaskEntry.STAGE, 0);
        values.put(TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY, categoryId);

        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public int findTaskIdByNameForCategory(final Context context, final String taskName, final int categoryId) {
        int result = -1;
        final SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        final Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID},
                TaskContract.TaskEntry.TITLE + " like ? and " + TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + "=?",
                new String[] {taskName, String.valueOf(categoryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(CategoryContract.CategoryEntry._ID);
            result = cursor.getInt(columnIndex);
        }
        cursor.close();
        db.close();

        return result;
    }

    @Override
    public void deleteAllTasksForCategory(Context context, int categoryId) {
        final SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE_NAME,
                TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + " = ?",
                new String[]{String.valueOf(categoryId)});
        db.close();
    }

    @Override
    public void markTaskAsDone(final Context context, final String taskItemName, final int categoryId, final boolean taskStage) {
        SQLiteDatabase db = new TaskDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        final int stageToSave = taskStage ? 1 : 0;
        values.put(TaskContract.TaskEntry.STAGE, stageToSave);

        db.updateWithOnConflict(TaskContract.TaskEntry.TABLE_NAME,
                values,
                TaskContract.TaskEntry.TITLE + " = ? and " +
                        TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + " = ?",
                new String[] {taskItemName, String.valueOf(categoryId)},
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    @Override
    public List<String> getAllTasksForCategoryMarkedAsDone(Context context, int categoryId) {
        final List<String> taskList = new ArrayList<String>();
        final SQLiteDatabase db = new TaskDbHelper(context).getReadableDatabase();
        final Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry.TITLE},
                TaskContract.TaskEntry.CATEGORY_FOREIGN_KEY + " = ? and " + TaskContract.TaskEntry.STAGE + " = ?",
                new String[] {String.valueOf(categoryId), DONE_TASK_STATE}, null, null, null);

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.TITLE);
            taskList.add(cursor.getString(idx));
        }

        cursor.close();
        db.close();
        return taskList;
    }
}

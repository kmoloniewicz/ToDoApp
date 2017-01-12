package com.todolist.dao;

import android.content.Context;

import java.util.List;

import com.todolist.beans.Task;

public interface TaskDAO {
    public List<String> getAllExistingTaskNames(final Context context, final int categoryId);
    public List<Task> getAllExistingTasks(final Context context, final int categoryId);
    public void deleteTask(final Context context, final String taskItemName, final int categoryId);
    public void saveTask(final Context context, final String taskName, final int categoryId);
    public int findTaskIdByNameForCategory(final Context context, final String taskName, final int categoryId);
    public void deleteAllTasksForCategory(final Context context, final int categoryId);
    public void markTaskAsDone(final Context context, final String taskItemName, final int categoryId, final boolean taskStage);
    public List<String> getAllTasksForCategoryMarkedAsDone(final Context context, final int categoryId);
}

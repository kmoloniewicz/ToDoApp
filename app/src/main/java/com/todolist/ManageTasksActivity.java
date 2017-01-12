package com.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.todolist.beans.Task;
import com.todolist.dao.CategoryDAO;
import com.todolist.dao.CategoryDAOImpl;
import com.todolist.dao.TaskDAO;
import com.todolist.dao.TaskDAOImpl;
import com.todolist.db.CategoryContract;
import com.todolist.db.CategoryDbHelper;
import com.todolist.db.TaskDbHelper;

import com.todolist.R;

public class ManageTasksActivity extends AppCompatActivity {
    private static final String MANAGE_TASKS_SUBTITLE = " - Zadania";
    private int tasksCategoryId;
    private TaskDbHelper taskDbHelper;
    private CategoryDbHelper categoryDbHelper;
    private ListView taskListview;
    private ArrayAdapter<Task> mAdapter;
    private ActionMode mActionMode;
    private String taskItemName;
    private String categoryName;
    private CategoryDAO categoryDAO;
    private TaskDAO taskDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        categoryName = intent.getStringExtra(CategoryContract.CATEGORY_NAME_PARAM);

        setContentView(R.layout.tasks_view);
        categoryDAO = new CategoryDAOImpl();
        taskDAO = new TaskDAOImpl();
        categoryDbHelper = new CategoryDbHelper(this);
        taskDbHelper = new TaskDbHelper(this);
        updateUI();
        setTitle(getTitle() + MANAGE_TASKS_SUBTITLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateUI() {
        taskListview = (ListView) findViewById(R.id.tasks_list);
        final int categoryId = categoryDAO.findCategoryIdByName(ManageTasksActivity.this, categoryName);
        final List<Task> taskList = taskDAO.getAllExistingTasks(this, categoryId);

        if (mAdapter == null) {
            mAdapter = new TaskAdapter(this, R.layout.todo_item,
                    R.id.textView,
                    taskList);

            taskListview.setAdapter(mAdapter);
            taskListview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            taskListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                    taskItemName = ((TextView)((LinearLayout) taskListview.getChildAt(i)).getChildAt(0)).getText().toString();
                    if (mActionMode != null) {
                        return false;
                    }

                    mActionMode = ManageTasksActivity.this.startActionMode(new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater inflater = mode.getMenuInflater();
                            inflater.inflate(R.menu.task_menu_contextual, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.id_task_do_undo:
                                    Log.d(this.getClass().getName(), item.getTitle() + " Edit clicked from contextual menu - contextual action mode - dziala");

                                    final int categoryId = categoryDAO.findCategoryIdByName(ManageTasksActivity.this, categoryName);
                                    final boolean newTaskStatus = changeTaskStatusInAdapter(taskItemName);
                                    taskDAO.markTaskAsDone(ManageTasksActivity.this, taskItemName, categoryId, newTaskStatus);
                                    strikeThroughDoneTasks();

                                    mode.finish();
                                    return true;
                                case R.id.id_task_delete:
                                    Log.d(this.getClass().getName(), item.getTitle() + " Delete clicked from contextual menu - contextual action mode - dziala");
                                    deleteTask();
                                    strikeThroughDoneTasks();
                                    mode.finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            mActionMode = null;
                        }
                    });

                    return true;
                }
            });
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        strikeThroughDoneTasks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskText = new EditText(this);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Dodaj zadanie")
                        .setView(taskText)
                        .setNegativeButton("Powrót", null)
                        .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String taskName = String.valueOf(taskText.getText());
                                final int categoryId = categoryDAO.findCategoryIdByName(ManageTasksActivity.this, categoryName);

                                if (taskExists(taskName, categoryId) || taskName.trim().isEmpty()) {
                                    final AlertDialog informationDialog = new AlertDialog.Builder(ManageTasksActivity.this).setTitle("Zadanie już istnieje, bądź jest puste. Nie zostanie utworzone!")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //do nothing here
                                                }
                                            }).create();
                                    informationDialog.show();
                                } else {
                                    taskDAO.saveTask(ManageTasksActivity.this, taskName, categoryId);
                                    mAdapter.add(new Task(taskName));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .create();
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean taskExists(final String taskName, final int categoryId) {
        int taskId = taskDAO.findTaskIdByNameForCategory(ManageTasksActivity.this, taskName, categoryId);
        final boolean result = taskId != -1 ? true : false;

        return result;
    }

    private void deleteTask() {
        final int categoryId = categoryDAO.findCategoryIdByName(ManageTasksActivity.this, categoryName);
        taskDAO.deleteTask(this, taskItemName, categoryId);
        removeTaskWithNameFromAdapter(taskItemName);
        mAdapter.notifyDataSetChanged();
    }

    public void removeTaskWithNameFromAdapter(final String taskItemName) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Task task = mAdapter.getItem(i);
            if(task.getTaskName().equals(taskItemName)) {
                mAdapter.remove(task);
                break;
            }
        }
    }

    public boolean changeTaskStatusInAdapter(final String taskItemName) {
        boolean taskStatus = false;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Task task = mAdapter.getItem(i);
            if(task.getTaskName().equals(taskItemName)) {
                taskStatus = !task.isTaskDone();
                task.setTaskDone(taskStatus);
                break;
            }
        }

        return taskStatus;
    }

    private void setStrikeThroughTextForView(View view) {
        if (view instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) view;

            if (linearLayout.getChildCount() > 0) {
                final TextView textView = (TextView) linearLayout.getChildAt(0);
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    private void strikeThroughDoneTasks() {
        for (int i = 0; i < taskListview.getAdapter().getCount(); i++) {
            taskListview.getAdapter().getView(i, null, null);
        }
        ((TaskAdapter)taskListview.getAdapter()).notifyDataSetChanged();
    }
}

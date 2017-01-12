package com.todolist;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

import com.todolist.dao.CategoryDAO;
import com.todolist.dao.CategoryDAOImpl;
import com.todolist.dao.TaskDAO;
import com.todolist.dao.TaskDAOImpl;
import com.todolist.db.CategoryContract;
import com.todolist.db.CategoryDbHelper;

import com.todolist.R;

public class MainActivity extends AppCompatActivity {

    private static final String CATEGORY_SUBTITLE = " - Kategorie";
    private CategoryDbHelper categoryDbHelper;
    private ListView categoryListview;
    private ArrayAdapter<String> mAdapter;
    private ActionMode mActionMode;
    private String categoryItemName;
    private CategoryDAO categoryDAO;
    private TaskDAO taskDAO;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            final MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_contextual, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.id_edit:
                    Log.d(this.getClass().getName(), item.getTitle() + " Edit clicked from contextual menu - contextual action mode - dziala");

                    final EditText categoryText = new EditText(MainActivity.this);
                    categoryText.setText(categoryItemName);
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Edytuj kategorię")
                            .setView(categoryText)
                            .setNegativeButton("Powrót", null)
                            .setPositiveButton("Zmień", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String newCategoryName = String.valueOf(categoryText.getText());
                                    if (categoryExists(newCategoryName)) {
                                        final AlertDialog informationDialog = new AlertDialog.Builder(MainActivity.this).setTitle("Kategoria już istnieje, nie zostanie utworzona!")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //do nothing here
                                                    }
                                                }).create();
                                        informationDialog.show();
                                    } else {
                                        updateCategory(MainActivity.this, newCategoryName, categoryItemName);
                                    }
                                }
                            })
                            .create();
                    alertDialog.show();

                    mode.finish();
                    return true;
                case R.id.id_delete:
                    Log.d(this.getClass().getName(), item.getTitle() + " Delete clicked from contextual menu - contextual action mode - dziala");
                    deleteCategory(MainActivity.this, categoryItemName);
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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categoryDAO = new CategoryDAOImpl();
        taskDAO = new TaskDAOImpl();

        updateUI();
        setTitle(getTitle() + CATEGORY_SUBTITLE);

    }

    private void updateUI() {
        categoryListview = (ListView) findViewById(R.id.category_list);
        final ArrayList<String> categoryList = categoryDAO.getAllExistingCategoryNames(this);

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.todo_item,
                    R.id.textView,
                    categoryList);
            categoryListview.setAdapter(mAdapter);

            categoryListview.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            categoryListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    categoryItemName = ((TextView)((LinearLayout) categoryListview.getChildAt(i)).getChildAt(0)).getText().toString();
                    if (mActionMode != null) {
                        return false;
                    }
                    mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
                    return true;
                }
            });
            categoryListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(view.getContext(), ManageTasksActivity.class);
                    categoryItemName = ((TextView)((LinearLayout) view).getChildAt(0)).getText().toString();
                    intent.putExtra(CategoryContract.CATEGORY_NAME_PARAM, categoryItemName);

                    Log.d(this.getClass().getName(), "Screen category -> task, category_name_value: " + categoryItemName);
                    startActivity(intent);
                }
            });
        } else {
            mAdapter.clear();
            mAdapter.addAll(categoryList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_category:
                final EditText categoryText = new EditText(this);
                final AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Dodaj kategorię")
                        .setView(categoryText)
                        .setNegativeButton("Powrót", null)
                        .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String categoryName = String.valueOf(categoryText.getText());
                                if (categoryExists(categoryName)) {
                                    final AlertDialog informationDialog = new AlertDialog.Builder(MainActivity.this).setTitle("Kategoria już istnieje, nie zostanie utworzona!")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //do nothing here
                                                }
                                            }).create();
                                    informationDialog.show();
                                } else {
                                    saveCategory(MainActivity.this, categoryName);
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

    private void updateCategory(final Context context, final String newCategoryName,
                                final String previousCategoryName) {
        categoryDAO.updateCategoryName(context, newCategoryName, previousCategoryName);
        mAdapter.remove(previousCategoryName);
        mAdapter.add(newCategoryName);
        mAdapter.notifyDataSetChanged();
    }

    private boolean categoryExists(String categoryName) {
        int categoryId = categoryDAO.findCategoryIdByName(MainActivity.this, categoryName);
        final boolean result = categoryId != -1 ? true : false;

        return result;
    }

    public void saveCategory(final Context context, final String categoryName) {
        categoryDAO.saveCategory(context, categoryName);
        mAdapter.add(categoryName);
        mAdapter.notifyDataSetChanged();
    }

    private void deleteCategory(final Context context, final String categoryItemName) {
        final int categoryId = categoryDAO.findCategoryIdByName(this, categoryItemName);
        taskDAO.deleteAllTasksForCategory(this, categoryId);
        categoryDAO.deleteCategory(context, categoryItemName);
        mAdapter.remove(categoryItemName);
        mAdapter.notifyDataSetChanged();
    }
}

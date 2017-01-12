package com.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.todolist.beans.Task;

import java.util.List;

import com.todolist.R;

public class TaskAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Object> mTasks;

    public TaskAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
        this.mContext = context;
        this.mTasks = objects;
    }

    public TaskAdapter(Context context, List objects) {
        super(context, R.layout.task_item, objects);
        this.mContext = context;
        this.mTasks = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            rowView = mLayoutInflater.inflate(R.layout.todo_item, null);
        }

        final Task task = (Task) mTasks.get(position);
        final TextView tv = (TextView) rowView.findViewById(R.id.textView);
        tv.setText(task.getTaskName());
        if (task.isTaskDone()) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv.setPaintFlags(0);
        }

        return rowView;
    }
}

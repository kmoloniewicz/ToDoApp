package com.todolist.beans;

import java.io.Serializable;

public class Task implements Serializable {
    private String taskName;
    private boolean taskDone;

    public Task() {
    }

    public Task(final String taskName) {
        this.taskName = taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDone(boolean taskDone) {
        this.taskDone = taskDone;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isTaskDone() {
        return taskDone;
    }
}

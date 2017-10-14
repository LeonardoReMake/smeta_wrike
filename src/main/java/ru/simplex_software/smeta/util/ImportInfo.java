package ru.simplex_software.smeta.util;

import ru.simplex_software.smeta.model.Task;

import java.util.ArrayList;
import java.util.List;

/** Класс, представляющий информацию о статусе очередного импорта задач. **/
public class ImportInfo {

    public static class NotParsedTask {

        private Task task;

        private String message;

        public NotParsedTask(Task task, String message) {
            this.task = task;
            this.message = message;
        }

        public Task getTask() {
            return task;
        }

        public String getMessage() {
            return message;
        }
    }

    private int importedTaskCount = 0;

    private int notParsedTaskCount = 0;

    private List<Task> notParsedTasks = new ArrayList<>();

    private List<String> taskLogMessages = new ArrayList<>();

    private List<NotParsedTask> notParsedTaskList = new ArrayList<>();

    public int getImportedTaskCount() {
        return importedTaskCount;
    }

    public void setImportedTaskCount(int importedTaskCount) {
        this.importedTaskCount = importedTaskCount;
    }

    public int getNotParsedTaskCount() {
        return notParsedTaskCount;
    }

    public void setNotParsedTaskCount(int notParsedTaskCount) {
        this.notParsedTaskCount = notParsedTaskCount;
    }

    public List<Task> getNotParsedTasks() {
        return notParsedTasks;
    }

    public void setNotParsedTasks(List<Task> notParsedTasks) {
        this.notParsedTasks = notParsedTasks;
    }

    public List<String> getTaskLogMessages() {
        return taskLogMessages;
    }

    public void setTaskLogMessages(List<String> taskLogMessages) {
        this.taskLogMessages = taskLogMessages;
    }

    public List<NotParsedTask> getNotParsedTaskList() {
        return notParsedTaskList;
    }

    public void setNotParsedTaskList(List<NotParsedTask> notParsedTaskList) {
        this.notParsedTaskList = notParsedTaskList;
    }

    public void addNotParsedTask(Task task, String message) {
        notParsedTaskList.add(new NotParsedTask(task, message));
        notParsedTaskCount++;
    }
}

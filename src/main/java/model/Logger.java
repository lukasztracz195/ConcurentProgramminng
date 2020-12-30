package model;

import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Logger {

    private List<String> logs;

    private static Logger instance;
    private ListView<String> listViewOnLogs;

    private Logger() {
        logs = new CopyOnWriteArrayList<>();
    }


    public synchronized static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public synchronized List<String> getLogs() {
        return logs;
    }


    public void log(String log) {
        logs.add(log);
        if (listViewOnLogs != null) {
            Platform.runLater(() -> {
                Set<String> hashSetOnLogs = new HashSet<>(listViewOnLogs.getItems());
                Set<String> newLogs = new HashSet<>(Logger.getInstance().getLogs());
                newLogs.removeAll(hashSetOnLogs);
                listViewOnLogs.getItems().addAll(newLogs);
            });
        }
    }

    public void setListView(ListView<String> listView) {
        this.listViewOnLogs = listView;
    }
}

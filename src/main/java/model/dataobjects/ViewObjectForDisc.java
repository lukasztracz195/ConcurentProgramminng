package model.dataobjects;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ViewObjectForDisc {

    private ListView<File> listSavedOfFiles;
    private ProgressBar progressBar;
    private Label labelClientId;
    private Label labelFileId;
    private Label labelTypeOfFile;
}

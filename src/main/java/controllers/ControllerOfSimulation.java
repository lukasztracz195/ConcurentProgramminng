package controllers;

import controllers.parsers.IntegerParser;
import controllers.validators.ProbabilitySizeOfFilesValidator;
import controllers.validators.StringToDoubleValidator;
import controllers.validators.StringToIntegerValidator;
import controllers.validators.StringValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Logger;
import model.Simulation;
import model.Simulator;
import model.algorithms.AlgorithmWage;
import model.config.ConfigGenerator;
import model.dataobjects.File;
import model.dataobjects.ViewObjectForDisc;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ControllerOfSimulation implements Initializable {

    private int NUMBER_OF_DISCS = 5;
    private Simulator simulation;

    @FXML
    private ListView<File> discZeroListSavedOfFiles;
    @FXML
    private ListView<File> discOneListSavedOfFiles;
    @FXML
    private ListView<File> discTwoListSavedOfFiles;
    @FXML
    private ListView<File> discThreeListSavedOfFiles;
    @FXML
    private ListView<File> discFourListSavedOfFiles;
    @FXML
    private ListView<String> listViewOnLogs;
    @FXML
    private Label labelErrorMessage;
    @FXML
    private Label labelClientIdForDiscZero;
    @FXML
    private Label labelFileIdForDiscZero;
    @FXML
    private Label labelTypeOfFileForDiscZero;

    @FXML
    private Label labelClientIdForDiscOne;
    @FXML
    private Label labelFileIdForDiscOne;
    @FXML
    private Label labelTypeOfFileForDiscOne;

    @FXML
    private Label labelClientIdForDiscTwo;
    @FXML
    private Label labelFileIdForDiscTwo;
    @FXML
    private Label labelTypeOfFileForDiscTwo;

    @FXML
    private Label labelClientIdForDiscThree;
    @FXML
    private Label labelFileIdForDiscThree;
    @FXML
    private Label labelTypeOfFileForDiscThree;

    @FXML
    private Label labelClientIdForDiscFour;
    @FXML
    private Label labelFileIdForDiscFour;
    @FXML
    private Label labelTypeOfFileForDiscFour;

    @FXML
    private ProgressBar progressBarDiscZero;
    @FXML
    private ProgressBar progressBarDiscOne;
    @FXML
    private ProgressBar progressBarDiscTwo;
    @FXML
    private ProgressBar progressBarDiscThree;
    @FXML
    private ProgressBar progressBarDiscFour;

    @FXML
    private TextField inputfieldOnProbabilityradnomHavyFile;
    @FXML
    private TextField inputfieldOnProbabilityradnomMediumFile;
    @FXML
    private TextField inputfieldOnProbabilityradnomLightFile;
    @FXML
    private TextField inputfieldOnMaxSizeOfFile;
    @FXML
    private TextField inputfieldOnMaxNumberOfFilesForClient;
    @FXML
    private TextField inputfieldOnMaxNumberOfClientsGeneratedInTheSameTime;
    @FXML
    private TextField inputfieldOnNumberOfTimeUnitsToDurationGenrateProcess;
    @FXML
    private TextField inputfieldOnNumberOfTimeUnitsToDurationOfPauseInGenrateProcess;

    @FXML
    private ChoiceBox<TimeUnit> choiceBoxOnTimeUnitsForDurationGenerateProcess;
    @FXML
    private ChoiceBox<TimeUnit> choiceBoxOnTimeUnitsForDurationOfPauseInGenerateProcess;

    @FXML
    private Button buttonStartSimulation;
    @FXML
    private Button buttonStopSimulation;
    @FXML
    private Label labelOnTimeSimulation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choiceBoxOnTimeUnitsForDurationGenerateProcess.getItems().addAll(TimeUnit.values());
        choiceBoxOnTimeUnitsForDurationGenerateProcess.setValue(TimeUnit.SECONDS);
        choiceBoxOnTimeUnitsForDurationOfPauseInGenerateProcess.getItems().addAll(TimeUnit.values());
        choiceBoxOnTimeUnitsForDurationOfPauseInGenerateProcess.setValue(TimeUnit.SECONDS);
        Logger.getInstance().setListView(listViewOnLogs);
        buttonStopSimulation.setDisable(true);
    }

    @FXML
    public void startSimulation() {
        if (validateParametersOfSimulation()) {
            ConfigGenerator configGenerator = prepareConfigGenerator();
            simulation = new Simulator(configGenerator, NUMBER_OF_DISCS, new AlgorithmWage(),
                    prepareViewObjectsForDiscs(), labelOnTimeSimulation);
            simulation.setStart(buttonStartSimulation);
            simulation.setStop(buttonStopSimulation);
            simulation.run();
        }

    }

    @FXML
    public void stopSimulation(){
        if(!simulation.isStopped()){
            simulation.stop();
        }
    }

    private ConfigGenerator prepareConfigGenerator() {
        return ConfigGenerator.builder()
                .howManyFilesCanBeHaveClient(DataParser.parseDataFromTextFieldToInt(inputfieldOnMaxNumberOfFilesForClient))
                .howManyClientsCanGenerate(DataParser.parseDataFromTextFieldToInt(inputfieldOnMaxNumberOfClientsGeneratedInTheSameTime))
                .maxSizeOfFile(DataParser.parseDataFromTextFieldToInt(inputfieldOnMaxSizeOfFile))
                .probabilityRandomHeavyFile(DataParser.parseDataFromTextFieldToInt(inputfieldOnProbabilityradnomHavyFile))
                .probabilityRandomMediumFile(DataParser.parseDataFromTextFieldToInt(inputfieldOnProbabilityradnomMediumFile))
                .probabilityRandomLightFile(DataParser.parseDataFromTextFieldToInt(inputfieldOnProbabilityradnomLightFile))
                .timeUnitForDuration(choiceBoxOnTimeUnitsForDurationGenerateProcess.getValue())
                .thresholdBetweenLightAndMediumFile(DataParser.parseDataFromTextFieldToInt(inputfieldOnMaxSizeOfFile) / 4)
                .thresholdBetweenMediumAndHeavyFile(DataParser.parseDataFromTextFieldToInt(inputfieldOnMaxSizeOfFile) -
                        DataParser.parseDataFromTextFieldToInt(inputfieldOnMaxSizeOfFile) / 4)
                .timeDuration(DataParser.parseDataFromTextFieldToInt(inputfieldOnNumberOfTimeUnitsToDurationGenrateProcess))
                .timeUnitForPause(choiceBoxOnTimeUnitsForDurationOfPauseInGenerateProcess.getValue())
                .timePauseDuration(DataParser.parseDataFromTextFieldToInt(inputfieldOnNumberOfTimeUnitsToDurationOfPauseInGenrateProcess))
                .build();
    }

    private Map<Integer, ViewObjectForDisc> prepareViewObjectsForDiscs() {
        Map<Integer, ViewObjectForDisc> map = new HashMap<>();
        List<ViewObjectForDisc> viewObjectForDiscList = new ArrayList<>();
        viewObjectForDiscList.add(ViewObjectForDisc.builder()
                .labelClientId(labelClientIdForDiscZero)
                .labelFileId(labelFileIdForDiscZero)
                .labelTypeOfFile(labelTypeOfFileForDiscZero)
                .listSavedOfFiles(discZeroListSavedOfFiles)
                .progressBar(progressBarDiscZero)
                .build());

        viewObjectForDiscList.add(ViewObjectForDisc.builder()
                .labelClientId(labelClientIdForDiscOne)
                .labelFileId(labelFileIdForDiscOne)
                .labelTypeOfFile(labelTypeOfFileForDiscOne)
                .listSavedOfFiles(discOneListSavedOfFiles)
                .progressBar(progressBarDiscOne)
                .build());

        viewObjectForDiscList.add(ViewObjectForDisc.builder()
                .labelClientId(labelClientIdForDiscTwo)
                .labelFileId(labelFileIdForDiscTwo)
                .labelTypeOfFile(labelTypeOfFileForDiscTwo)
                .listSavedOfFiles(discTwoListSavedOfFiles)
                .progressBar(progressBarDiscTwo)
                .build());

        viewObjectForDiscList.add(ViewObjectForDisc.builder()
                .labelClientId(labelClientIdForDiscThree)
                .labelFileId(labelFileIdForDiscThree)
                .labelTypeOfFile(labelTypeOfFileForDiscThree)
                .listSavedOfFiles(discThreeListSavedOfFiles)
                .progressBar(progressBarDiscThree)
                .build());

        viewObjectForDiscList.add(ViewObjectForDisc.builder()
                .labelClientId(labelClientIdForDiscFour)
                .labelFileId(labelFileIdForDiscFour)
                .labelTypeOfFile(labelTypeOfFileForDiscFour)
                .listSavedOfFiles(discFourListSavedOfFiles)
                .progressBar(progressBarDiscFour)
                .build());

        for (int index = 0; index < NUMBER_OF_DISCS; index++) {
            map.put(index, viewObjectForDiscList.get(index));
        }
        return map;
    }

    private boolean validateParametersOfSimulation() {
        Set<Boolean> stepsOfFalidation = new HashSet<>();
        stepsOfFalidation.add(validateProbabilityOfSizeOfFiles());
        stepsOfFalidation.add(validateIntegerInputControls());
        return !stepsOfFalidation.contains(false);
    }

    private boolean validateProbabilityOfSizeOfFiles() {
        List<TextField> probabilityInputControls = Arrays.asList(inputfieldOnProbabilityradnomLightFile,
                inputfieldOnProbabilityradnomMediumFile, inputfieldOnProbabilityradnomHavyFile);
        List<Integer> probabilityOfValues = new ArrayList<>();
        for (TextField inputControl : probabilityInputControls) {
            StringValidator validator = StringToDoubleValidator.of(inputControl.getText());
            validator.validate();
            if (!validator.isValid()) {
                setTextError(validator.getErrorMessage());
                return false;
            }
            probabilityOfValues.add(IntegerParser.parse(inputControl.getText()).orElse(0));
        }
        ProbabilitySizeOfFilesValidator validator = ProbabilitySizeOfFilesValidator.of(probabilityOfValues);
        if (!validator.isValid()) {
            setTextError(validator.getErrorMessage());
            return false;
        }
        clearTestError();
        return true;
    }

    private boolean validateIntegerInputControls() {
        List<TextField> intInputControls = Arrays.asList(inputfieldOnMaxNumberOfClientsGeneratedInTheSameTime,
                inputfieldOnMaxNumberOfFilesForClient, inputfieldOnNumberOfTimeUnitsToDurationGenrateProcess, inputfieldOnNumberOfTimeUnitsToDurationOfPauseInGenrateProcess);
        List<Integer> integerParameters = new ArrayList<>();
        for (TextField inputControl : intInputControls) {
            StringToIntegerValidator validator = StringToIntegerValidator.of(inputControl.getText());
            validator.validate();
            if (!validator.isValid()) {
                setTextError(validator.getErrorMessage());
                return false;
            }
            integerParameters.add(IntegerParser.parse(inputControl.getText()).orElse(0));
        }
        if (integerParameters.stream().anyMatch(f -> f.doubleValue() < 1)) {
            setTextError("One from integer parameters is lower than 1");
            return false;
        }
        return true;
    }

    private void setTextError(String message) {
        labelErrorMessage.setText(message);
    }

    private void clearTestError() {
        labelErrorMessage.setText("");
    }

}

package com.ssau.pmi.controllers;

import com.ssau.pmi.exceptions.ValidateParametersException;
import com.ssau.pmi.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML
    public AnchorPane mainPane;
    @FXML
    private TextField textFieldR;
    @FXML
    private TextField textFieldL;
    @FXML
    private TextField textFieldN;
    @FXML
    private TextField textFieldLambda;
    @FXML
    private TextField textFieldStepR;
    @FXML
    private TextField textFieldFixedValue;
    @FXML
    private TextField textFieldStepZ;
    @FXML
    private RadioButton radioButtonZ;
    @FXML
    private RadioButton radioButtonR;
    @FXML
    private RadioButton radioButtonCN;
    @FXML
    private RadioButton radioButtonImplicit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = new Image("images/gradient.jpeg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(1000, 600, true, true, true, true));
        Background background = new Background(backgroundImage);
        mainPane.setBackground(
                background
        );

        ToggleGroup toggleGroupForRZ = new ToggleGroup();
        radioButtonZ.setToggleGroup(toggleGroupForRZ);
        radioButtonR.setToggleGroup(toggleGroupForRZ);

        ToggleGroup toggleGroupForSchemes = new ToggleGroup();
        radioButtonCN.setToggleGroup(toggleGroupForSchemes);
        radioButtonImplicit.setToggleGroup(toggleGroupForSchemes);
    }

    @FXML
    public void buildButtonOnAction(ActionEvent actionEvent) {
        try {
            SchemeParameters schemeParameters = validateParameters();
            buildScheme(schemeParameters);
        } catch (ValidateParametersException ignored) {
        }
    }

    private SchemeParameters validateParameters() throws ValidateParametersException {
        SchemeParameters schemeParameters;
        boolean isValidParameters = true;
        StringBuilder errorMessage = new StringBuilder();
        try {
            String inputFieldR = textFieldR.getText();
            String inputFieldL = textFieldL.getText();
            String inputFieldN = textFieldN.getText();
            String inputFieldLambda = textFieldLambda.getText();
            String inputFixedValue = textFieldFixedValue.getText();
            String inputFieldStepR = textFieldStepR.getText();
            String inputFieldStepZ = textFieldStepZ.getText();

            List<Double> fixedValues = Arrays.stream(inputFixedValue.split("\\s+"))
                    .map(Double::parseDouble)
                    .distinct()
                    .collect(Collectors.toList());

            schemeParameters =
                    SchemeParameters.builder()
                            .R(Double.parseDouble(inputFieldR))
                            .L(Double.parseDouble(inputFieldL))
                            .N(Double.parseDouble(inputFieldN))
                            .lambda(Double.parseDouble(inputFieldLambda))
                            .fixedValues(fixedValues)
                            .stepR(Integer.parseInt(inputFieldStepR))
                            .stepZ(Integer.parseInt(inputFieldStepZ))
                            .variable(getSelectedVariable())
                            .schemeType(getSelectedSchemeType())
                            .build();

            if (schemeParameters.getR() <= 0) {
                isValidParameters = false;
                String msg = "Параметр R должен быть больше 0\n";
                errorMessage.append(msg);
            }

            if (schemeParameters.getL() <= 0) {
                isValidParameters = false;
                String msg = "Параметр L должен быть больше 0\n";
                errorMessage.append(msg);
            }

            if (schemeParameters.getN() <= 0) {
                isValidParameters = false;
                String msg = "Параметр n должен быть больше 0\n";
                errorMessage.append(msg);
            }

            if (schemeParameters.getLambda() <= 0) {
                isValidParameters = false;
                String msg = "Параметр λ должен быть больше или равен 0\n";
                errorMessage.append(msg);
            }

            if (schemeParameters.getStepR() <= 0) {
                isValidParameters = false;
                String msg = "Некорректный шаг по r\n";
                errorMessage.append(msg);
            }

            if (schemeParameters.getStepZ() <= 0) {
                isValidParameters = false;
                String msg = "Некорректный шаг по z\n";
                errorMessage.append(msg);
            }

            if(!isValidParameters) {
                showAlertWithError(errorMessage.toString());
                throw new ValidateParametersException();
            }

            if (schemeParameters.getFixedValues().size() > 10 || schemeParameters.getFixedValues().isEmpty()) {
                isValidParameters = false;
                String msg = "Количество фиксируемых значений не должно превышать 10\n";
                errorMessage.append(msg);
            }

            Predicate<Double> predicate = x -> x < 0;
            SchemeParameters finalSchemeParameters = schemeParameters;
            if (schemeParameters.getVariable() == Variable.Z) {
                predicate = predicate.or(x -> x > finalSchemeParameters.getL());
            }
            else {
                predicate = predicate.or(x -> x > finalSchemeParameters.getR());
            }

            for (Double fixedValue : schemeParameters.getFixedValues()) {
                if (predicate.test(fixedValue)) {
                    isValidParameters = false;
                    String msg = "Некорректное фиксируемое значение\n";
                    errorMessage.append(msg);
                    break;
                }
            }

            if(!isValidParameters) {
                showAlertWithError(errorMessage.toString());
                throw new ValidateParametersException();
            }

        } catch (NumberFormatException e) {
            String msg = "Введены некорректные данные\n";
            errorMessage.append(msg);
            showAlertWithError(errorMessage.toString());
            throw new ValidateParametersException();
        }
        return schemeParameters;
    }

    private void buildScheme(SchemeParameters schemeParameters) {
        var graphicBuilder = new GraphicBuilder(schemeParameters);
        graphicBuilder.initUI();
        graphicBuilder.setVisible(true);
    }

    private void showAlertWithError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Constants.VALIDATION_ERROR);
        alert.setHeaderText(Constants.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Variable getSelectedVariable() {
        return radioButtonZ.isSelected() ? Variable.Z : Variable.R;
    }

    private SchemeType getSelectedSchemeType() {
        return radioButtonCN.isSelected() ? SchemeType.CN : SchemeType.IMPLICIT;
    }
}

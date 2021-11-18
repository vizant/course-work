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
        } catch (ValidateParametersException e) {
            showAlertWithError();
        }
    }

    private SchemeParameters validateParameters() throws ValidateParametersException {
        SchemeParameters schemeParameters;
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

            if (schemeParameters.getR() < 0)
                throw new ValidateParametersException();

            if (schemeParameters.getL() < 0)
                throw new ValidateParametersException();

            if (schemeParameters.getN() <= 0)
                throw new ValidateParametersException();

            if (schemeParameters.getLambda() <= 0)
                throw new ValidateParametersException();

            if (schemeParameters.getStepR() <= 0)
                throw new ValidateParametersException();

            if (schemeParameters.getStepZ() <= 0)
                throw new ValidateParametersException();

            if (schemeParameters.getFixedValues().size() > 10 || schemeParameters.getFixedValues().isEmpty())
                throw new ValidateParametersException();

            Predicate<Double> predicate = x -> x < 0;
            if (schemeParameters.getVariable() == Variable.Z)
                predicate = predicate.or(x -> x > schemeParameters.getL());
            else
                predicate = predicate.or(x -> x > schemeParameters.getR());

            for (Double fixedValue : schemeParameters.getFixedValues()) {
                if (predicate.test(fixedValue)) {
                    throw new ValidateParametersException();
                }
            }

        } catch (NumberFormatException e) {
            throw new ValidateParametersException();
        }
        return schemeParameters;
    }

    private void buildScheme(SchemeParameters schemeParameters) {
        var graphicBuilder = new GraphicBuilder(schemeParameters);
        graphicBuilder.initUI();
        graphicBuilder.setVisible(true);
    }

    private void showAlertWithError() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Constants.VALIDATION_ERROR);
        alert.setHeaderText(Constants.ERROR);
        alert.setContentText(Constants.INCORRECT_PARAMETERS_VALUES);
        alert.showAndWait();
    }

    private Variable getSelectedVariable() {
        return radioButtonZ.isSelected() ? Variable.Z : Variable.R;
    }

    private SchemeType getSelectedSchemeType() {
        return radioButtonCN.isSelected() ? SchemeType.CN : SchemeType.IMPLICIT;
    }
}

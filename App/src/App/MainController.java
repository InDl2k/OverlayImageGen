package App;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    @FXML
    private TextField field_Height;

    @FXML
    private TextField field_Width;

    @FXML
    private VBox vbox_img;

    @FXML
    private Button btn_clear;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_generate;

    @FXML
    private ImageView imgView_Main;

    @FXML
    private ComboBox<String> combBox_SelectLayer;

    @FXML
    private Spinner<Integer> spin_priority;

    private SpinnerValueFactory<Integer> valueFactory;
    private ImageBuilder imageBuilder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageBuilder = new ImageBuilder();
        valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);

        spin_priority.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                imageBuilder.setLayerPriority(combBox_SelectLayer.getValue(), spin_priority.getValue());
                imgView_Main.setImage(imageBuilder.build((int)imgView_Main.getFitWidth(), (int)imgView_Main.getFitHeight()));
            }
        });

        field_Width.textProperty().addListener(new ChangeListener<String>() {                   //input only nums
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    field_Width.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        field_Height.textProperty().addListener(new ChangeListener<String>() {                  //input only nums
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    field_Height.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    void addLayer(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int showOpenDialog = fileChooser.showOpenDialog(null);
        if(showOpenDialog != JFileChooser.APPROVE_OPTION){
            System.out.println("Something goes wrong with FileChooser");
        }
        else {
            File[] directories = fileChooser.getSelectedFiles();

            imageBuilder.getData(directories);

            combBox_SelectLayer.getItems().clear();
            combBox_SelectLayer.getItems().addAll(imageBuilder.getDirectoryNames());

            btn_generate.setDisable(false);
            btn_save.setDisable(false);
            btn_clear.setDisable(false);

            spin_priority.setValueFactory(valueFactory);
        }
    }

    @FXML
    void generateImage(ActionEvent event) {
        imageBuilder.generateImage();
        imgView_Main.setImage(imageBuilder.build((int)imgView_Main.getFitWidth(), (int)imgView_Main.getFitHeight()));
    }


    @FXML
    void clearImage(ActionEvent event) {
        imageBuilder.clear();
        spin_priority.setValueFactory(null);
        combBox_SelectLayer.getItems().clear();
        imgView_Main.setImage(null);

        btn_clear.setDisable(true);
        btn_generate.setDisable(true);
        btn_save.setDisable(true);
    }

    @FXML
    void saveImage(ActionEvent event) {
        imageBuilder.save(imgView_Main.getImage());
    }

    @FXML
    void selectLayer(ActionEvent event) {
        Integer val = imageBuilder.getLayerPriority(combBox_SelectLayer.getValue());
        if(val != null)
            spin_priority.getValueFactory().setValue(val);
    }

    @FXML
    void resizeImageBox(ActionEvent event) {

        try {
            int height = Integer.parseInt(field_Height.getText());
            int width = Integer.parseInt(field_Width.getText());

            if(width < 350){width = 350;}
            else if (width > 1280) {width = 1280;}

            if(height < 350){height = 350;}
            else if (height > 1024) {height = 1024;}


            if(!field_Height.getText().equals("") && !field_Width.getText().equals("")) {
                vbox_img.setPrefWidth(width);
                vbox_img.setPrefHeight(height);
                imgView_Main.setFitWidth(width);
                imgView_Main.setFitHeight(height);

                btn_save.getScene().getWindow().setHeight(height * 1.4);
                btn_save.getScene().getWindow().setWidth(width * 1.8);
            }

            imgView_Main.setImage(imageBuilder.build(width, height));
        }
        catch (NumberFormatException exc){
            System.out.println("Set size is too high");
        }
    }

}

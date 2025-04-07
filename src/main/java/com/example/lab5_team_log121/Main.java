package com.example.lab5_team_log121;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private ImageModel imageModel;
    private ThumbnailView thumbnailView;
    private PerspectiveView perspectiveView1;
    private PerspectiveView perspectiveView2;

    // Méthode start : initialise et lance l'application JavaFX
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Application d'Affichage d'Images");

        

        // Mise en page
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        // Initialisation du modèle de l'image
        imageModel = new ImageModel();
        
        // Création des vues
        thumbnailView = new ThumbnailView(imageModel);
        perspectiveView1 = new PerspectiveView(imageModel, new Perspective());
        perspectiveView2 = new PerspectiveView(imageModel, new Perspective());

        // Disposition en trois parties :
        // - Colonne gauche (vignette) avec bordure noire
        // - Deux vues de perspective à droite, côte à côte, chacune avec bordure noire
        VBox leftBox = new VBox(10);
        leftBox.setPadding(new Insets(10));
        leftBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid inside;");
        leftBox.getChildren().add(thumbnailView);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        leftBox.getChildren().add(spacer);

        Label creditsLabel = new Label("équipe log121 © 2025");
        leftBox.getChildren().add(creditsLabel);

        

        HBox rightBox = new HBox(10);
        rightBox.setPadding(new Insets(10));
        perspectiveView1.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid inside;");
        perspectiveView2.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid inside;");
        rightBox.getChildren().addAll(perspectiveView1, perspectiveView2);

        HBox mainBox = new HBox(10);
        mainBox.getChildren().addAll(leftBox, rightBox);
        root.setCenter(mainBox);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Méthode createMenuBar : crée et retourne la barre de menu de l'application
    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Fichier");

        //bouton ouvrir
        MenuItem openItem = new MenuItem("Ouvrir");
        openItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Ouvrir une image");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    imageModel.loadImage(file.getAbsolutePath());
                }
            }
        });

        //bouton sauvegarder
        MenuItem saveItem = new MenuItem("Sauvegarder");
        saveItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                //choisir emplacement de la sauvegarde
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Sauvegarder l'état des vues");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Perspectives", "*.per"));
                File file = fileChooser.showSaveDialog(stage);
                
                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    //sauvegarder l'image et un mémento de chaque perspective
                    oos.writeObject(imageModel);
                    oos.writeObject(perspectiveView1.getPerspective().saveState());
                    oos.writeObject(perspectiveView2.getPerspective().saveState());

                    //fermer les chaînes de sauvegarde
                    oos.close();
                    fos.close();
                    System.out.println("Fichier Enregistré avec succès!");
                } catch (Exception e) {

                }

            }
        });

        menuFile.getItems().add(openItem);
        menuFile.getItems().add(saveItem);
        menuBar.getMenus().add(menuFile);
        return menuBar;
    }

    // Méthode main : point d'entrée de l'application
    public static void main(String[] args) {
        launch(args);
    }
}

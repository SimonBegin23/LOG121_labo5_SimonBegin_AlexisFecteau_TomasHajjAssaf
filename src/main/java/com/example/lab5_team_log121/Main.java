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
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private ImageModel imageModel;
    private List<Perspective> perspectives = new ArrayList<>();
    private ThumbnailView thumbnailView;
    private PerspectiveView perspectiveView1;
    private PerspectiveView perspectiveView2;
    private PerspectiveController perspectiveController1;
    private PerspectiveController perspectiveController2;

    // Méthode start : initialise et lance l'application JavaFX
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Application d'Affichage d'Images");

        // Initialisation du modèle et création de deux perspectives par défaut
        imageModel = new ImageModel();
        Perspective p1 = new Perspective();
        Perspective p2 = new Perspective();
        perspectives.add(p1);
        perspectives.add(p2);

        // Création des vues
        thumbnailView = new ThumbnailView(imageModel);
        perspectiveView1 = new PerspectiveView(imageModel, p1);
        perspectiveView2 = new PerspectiveView(imageModel, p2);

        // Création des contrôleurs pour gérer les interactions sur chaque vue de perspective
        perspectiveController1 = new PerspectiveController(p1, perspectiveView1);
        perspectiveController2 = new PerspectiveController(p2, perspectiveView2);

        // Mise en page
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

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
        menuFile.getItems().add(openItem);
        menuBar.getMenus().add(menuFile);
        return menuBar;
    }

    // Méthode main : point d'entrée de l'application
    public static void main(String[] args) {
        launch(args);
    }
}

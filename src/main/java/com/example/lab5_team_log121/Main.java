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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
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

        // Initialisation du modèle de l'image
        imageModel = new ImageModel();
        
        // Création des vues
        thumbnailView = new ThumbnailView(imageModel);
        perspectiveView1 = new PerspectiveView(imageModel, new Perspective());
        perspectiveView2 = new PerspectiveView(imageModel, new Perspective());

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
        
        Command undoCommand = new UndoCommand();
        Command redoCommand = new RedoCommand();

        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");

        undoButton.setOnAction(e -> undoCommand.execute());
        redoButton.setOnAction(e -> redoCommand.execute());

        HBox buttonBox = new HBox(10, undoButton, redoButton);
        leftBox.getChildren().add(buttonBox);

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

                //Choix pour ouvrir une image
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );

                //Choix pour ouvrir un fichier .per sauvegardé auparavant
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Perspectives sérialisées", "*.ser"));

                //Sélectionner un fichier à charger
                File file = fileChooser.showOpenDialog(stage);

                if (file != null){
                    String extension = file.getName().substring(file.getName().lastIndexOf(".")+1);

                    //charger une sauvegarde .per
                    if (extension.equalsIgnoreCase("ser")){
                        try{
                            FileInputStream fis = new FileInputStream(file);
                            ObjectInputStream ois = new ObjectInputStream(fis);

                            imageModel.loadImage((String) ois.readObject());
                            
                            PerspectiveMemento memento1 = (PerspectiveMemento)ois.readObject();
                            PerspectiveMemento memento2 = (PerspectiveMemento)ois.readObject();

                            perspectiveView1.getPerspective().restoreState(memento1);
                            perspectiveView2.getPerspective().restoreState(memento2);

                            //réinitialiser l'historique à la sauvegarde chargée
                            PerspectiveCaretaker caretaker = PerspectiveCaretaker.getInstance();
                            caretaker.flushHistory();
                            caretaker.pushNewMemento(memento1);
                            caretaker.pushNewMemento(memento2);
                            
                            ois.close();
                            fis.close();

                            System.out.println("Fichier chargé avec succès!");


                        } catch (Exception e){
                            System.out.println("Erreur de charge!");
                            System.out.println(e.getMessage());
                        }

                    } else {
                        //charger une image
                        imageModel.loadImage(file.getAbsolutePath());
                    }
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
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Perspectives sérialisées", "*.ser"));
                File file = fileChooser.showSaveDialog(stage);
                
                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    //sauvegarder l'image et un mémento de chaque perspective
                    oos.writeObject(imageModel.getImagePath());
                    oos.writeObject(perspectiveView1.getPerspective().saveState());
                    oos.writeObject(perspectiveView2.getPerspective().saveState());

                    //fermer les chaînes de sauvegarde
                    oos.close();
                    fos.close();
                    System.out.println("Fichier enregistré avec succès!");
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

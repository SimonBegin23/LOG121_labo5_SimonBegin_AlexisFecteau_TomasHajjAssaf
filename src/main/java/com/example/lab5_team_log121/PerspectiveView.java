package com.example.lab5_team_log121;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;

// Constructeur et méthodes de mise à jour pour la vue de perspective.
public class PerspectiveView extends StackPane implements Observer {
    private Perspective perspective;
    private ImageModel imageModel;
    private ImageView imageView;
    
    private double dragStartX, dragStartY;
    private double initialOffsetX, initialOffsetY;

    final ContextMenu contextMenu = new ContextMenu();

    // Constructeur : initialise la vue de perspective, configure l'ImageView et attache les observateurs.
    public PerspectiveView(ImageModel imageModel, Perspective perspective) {
        this.imageModel = imageModel;
        this.perspective = perspective;
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        this.getChildren().add(imageView);
        perspective.attach(this);
        imageModel.attach(this);
        this.attachContextMenu();
        update(null, "Init");
        this.attachHandlers();

        //ajouter l'état initial de la vue à l'historique
        PerspectiveCaretaker.getInstance().pushNewMemento(perspective.saveState());
    }

    // Retourne l'ImageView utilisé pour afficher la perspective.
    public ImageView getImageView() {
        return imageView;
    }

    public Perspective getPerspective(){
        return perspective;
    }

    // Méthode update : met à jour l'affichage de l'ImageView en fonction de l'état actuel du modèle et de la perspective.
    @Override
    public void update(Subject subject, Object arg) {
        Image img = imageModel.getImage();
        if(img == null)
            return;
        imageView.setImage(img);
        double viewWidth = imageView.getFitWidth();
        double viewHeight = imageView.getFitHeight();
        double viewportWidth = viewWidth * perspective.getScale();
        double viewportHeight = viewHeight * perspective.getScale();
        double offsetX = perspective.getOffsetX();
        double offsetY = perspective.getOffsetY();
        Rectangle2D viewport = new Rectangle2D(offsetX, offsetY, viewportWidth, viewportHeight);
        imageView.setViewport(viewport);
    }

    //Met à jour temporairement le viewport lors d'un déplacement (pan) sans modifier définitivement le modèle.

    public void updatePan(double newOffsetX, double newOffsetY) {
        
        Image img = imageModel.getImage();
        if(img == null)
            return;
        double viewWidth = imageView.getFitWidth();
        double viewHeight = imageView.getFitHeight();
        double viewportWidth = viewWidth * perspective.getScale();
        double viewportHeight = viewHeight * perspective.getScale();
        double offsetX = newOffsetX;
        double offsetY = newOffsetY;
        Rectangle2D viewport = new Rectangle2D(offsetX, offsetY, viewportWidth, viewportHeight);
        imageView.setViewport(viewport);

    }

    private void attachHandlers() {

        // Gestionnaire d'événement pour le zoom via la molette de la souris (appel direct sur le modèle)
        this.getImageView().setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY();
                double factor = (deltaY > 0) ? 1.1 : 0.9;
                double centerX = event.getX() * perspective.getScale() + perspective.getOffsetX();
                double centerY = event.getY() * perspective.getScale() + perspective.getOffsetY();
                perspective.zoom(factor, centerX, centerY);
                event.consume();
            }
        });

        // Gestionnaire d'événement : démarre le pan en enregistrant la position initiale
        this.getImageView().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
                initialOffsetX = perspective.getOffsetX();
                initialOffsetY = perspective.getOffsetY();
            }
        });

        // Gestionnaire d'événement : fournit un feedback visuel pendant le pan
        this.getImageView().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double dx = event.getSceneX() - dragStartX;
                double dy = event.getSceneY() - dragStartY;
                PerspectiveView.this.updatePan(initialOffsetX - dx, initialOffsetY - dy);
            }
        });

        // Gestionnaire d'événement : finalise le pan en appliquant la translation sur le modèle
        this.getImageView().setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double dx = event.getSceneX() - dragStartX;
                double dy = event.getSceneY() - dragStartY;
                perspective.move(-dx, -dy);
            }
        });

        // Gestionnaire d'évènement: ouvrir le menu de contexte avec clic droit.
        this.getImageView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    PerspectiveView.this.contextMenu.show(imageView, event.getScreenX(), event.getScreenY());
                }
            }
            
        });
    }


    private void attachContextMenu(){
        MenuItem copier = new MenuItem("Copier");
        MenuItem coller = new MenuItem("Coller");

        //initier le bouton copier
        copier.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            try{
                //sérialiser un nouveau mémento sous forme binaire
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(perspective.saveState());
                oos.close();

                //encoder le binaire en String base64
                byte[] binaire = bos.toByteArray();
                String binaireEncode = Base64.getEncoder().encodeToString(binaire);

                //copier le string au clipboard
                content.putString(binaireEncode);
                clipboard.setContent(content);
                System.out.println("Copié avec succès!");

            } catch (Exception e){
                System.out.println("Erreur de copiage!");
                System.out.println(e.getMessage());
            }
        });

        //initier le bouton coller
        coller.setOnAction(event ->{
            final Clipboard clipboard = Clipboard.getSystemClipboard();

            if (clipboard.hasString()){
                try{
                    //décoder le string base64 du clipboard en forme binaire
                    byte[] binaire = Base64.getDecoder().decode(clipboard.getString());

                    //convertir le binaire en objet de PerspectiveMemento
                    ByteArrayInputStream bis = new ByteArrayInputStream(binaire);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    PerspectiveMemento mementoColle = (PerspectiveMemento)ois.readObject();

                    //restaurer le mémento collé et l'ajouter à l'historique
                    perspective.restoreState(mementoColle);
                    PerspectiveCaretaker.getInstance().pushNewMemento(mementoColle);

                    System.out.println("Collé avec succès!");

                } catch (Exception e){
                    System.out.println("Erreur de collage!");
                    System.out.println(e.getMessage());
                }
                
            } else {
                System.out.println("Presse-papiers vide ou impossible à coller!");
            }
        });

        //ajouter les boutons au menu
        this.contextMenu.getItems().addAll(copier, coller);   
    }
}

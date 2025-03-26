package com.example.lab5_team_log121;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        update(null, "Init");
        this.attachHandlers();
    }

    // Retourne l'ImageView utilisé pour afficher la perspective.
    public ImageView getImageView() {
        return imageView;
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
        if(offsetX + viewportWidth > img.getWidth()) {
            offsetX = img.getWidth() - viewportWidth;
        }
        if(offsetY + viewportHeight > img.getHeight()) {
            offsetY = img.getHeight() - viewportHeight;
        }
        if(offsetX < 0) offsetX = 0;
        if(offsetY < 0) offsetY = 0;
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
        if(offsetX + viewportWidth > img.getWidth()) {
            offsetX = img.getWidth() - viewportWidth;
        }
        if(offsetY + viewportHeight > img.getHeight()) {
            offsetY = img.getHeight() - viewportHeight;
        }
        if(offsetX < 0) offsetX = 0;
        if(offsetY < 0) offsetY = 0;
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
    }
}

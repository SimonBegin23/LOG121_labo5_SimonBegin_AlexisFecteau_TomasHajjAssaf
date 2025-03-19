package com.example.lab5_team_log121;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

// Constructeur et méthodes de mise à jour pour la vue de perspective.
public class PerspectiveView extends StackPane implements Observer {
    private Perspective perspective;
    private ImageModel imageModel;
    private ImageView imageView;

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
}

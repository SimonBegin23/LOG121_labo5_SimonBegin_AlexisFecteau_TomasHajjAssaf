package com.example.lab5_team_log121;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

// Constructeur et méthode de mise à jour pour la vue miniature (thumbnail)
public class ThumbnailView extends StackPane implements Observer {
    private ImageView imageView;
    private ImageModel imageModel;

    // Constructeur : initialise la ThumbnailView, configure l'ImageView et attache cette vue au modèle
    public ThumbnailView(ImageModel imageModel) {
        this.imageModel = imageModel;
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(150);
        this.getChildren().add(imageView);
        imageModel.attach(this);
        update(imageModel, "Init");
    }

    // Méthode update : met à jour l'affichage de l'ImageView en fonction de l'image du modèle
    @Override
    public void update(Subject subject, Object arg) {
        if(subject == imageModel) {
            Image img = imageModel.getImage();
            if(img != null) {
                imageView.setImage(img);
            }
        }
    }
}

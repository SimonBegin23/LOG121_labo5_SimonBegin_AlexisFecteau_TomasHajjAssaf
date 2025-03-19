package com.example.lab5_team_log121;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class PerspectiveController {
    private Perspective perspective;
    private PerspectiveView view;
    private double dragStartX, dragStartY;
    private double initialOffsetX, initialOffsetY;

    // Constructeur : initialise la perspective et la vue, puis attache les gestionnaires d'événements
    public PerspectiveController(Perspective perspective, PerspectiveView view) {
        this.perspective = perspective;
        this.view = view;
        attachHandlers();
    }

    // Méthode privée : attache les gestionnaires d'événements pour le zoom et le pan
    private void attachHandlers() {
        // Gestionnaire d'événement pour le zoom via la molette de la souris (appel direct sur le modèle)
        view.getImageView().setOnScroll(new EventHandler<ScrollEvent>() {
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
        view.getImageView().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
                initialOffsetX = perspective.getOffsetX();
                initialOffsetY = perspective.getOffsetY();
            }
        });

        // Gestionnaire d'événement : fournit un feedback visuel pendant le pan
        view.getImageView().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double dx = event.getSceneX() - dragStartX;
                double dy = event.getSceneY() - dragStartY;
                view.updatePan(initialOffsetX - dx, initialOffsetY - dy);
            }
        });

        // Gestionnaire d'événement : finalise le pan en appliquant la translation sur le modèle
        view.getImageView().setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double dx = event.getSceneX() - dragStartX;
                double dy = event.getSceneY() - dragStartY;
                perspective.move(-dx, -dy);
            }
        });
    }
}

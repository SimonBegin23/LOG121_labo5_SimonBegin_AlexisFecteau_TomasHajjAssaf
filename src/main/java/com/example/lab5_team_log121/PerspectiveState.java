package com.example.lab5_team_log121;

import java.io.Serializable;

public class PerspectiveState implements Serializable {
    private static final long serialVersionUID = 1L;

    private double scale;
    private double offsetX;
    private double offsetY;

    // Constructeur : initialise l'état avec l'échelle et les offsets fournis.
    public PerspectiveState(double scale, double offsetX, double offsetY) {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    // Retourne le facteur de zoom (scale) de la perspective.
    public double getScale() {
        return scale;
    }

    // Retourne l'offset horizontal de la perspective.
    public double getOffsetX() {
        return offsetX;
    }

    // Retourne l'offset vertical de la perspective.
    public double getOffsetY() {
        return offsetY;
    }
}

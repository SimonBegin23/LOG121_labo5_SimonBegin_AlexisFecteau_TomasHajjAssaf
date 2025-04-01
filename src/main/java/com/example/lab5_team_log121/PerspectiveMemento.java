package com.example.lab5_team_log121;

public class PerspectiveMemento {

    private Perspective originator;
    private double scale;
    private double offsetX;
    private double offsetY;

    // Constructeur : initialise l'état avec l'échelle et les offsets fournis.
    public PerspectiveMemento(double scale, double offsetX, double offsetY, Perspective originator) {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.originator = originator;
        
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

    //retourne la perspective ayant créé le mémento.
    public Perspective getOriginator(){
        return originator;
    }
}

package com.example.lab5_team_log121;

import java.util.ArrayList;
import java.util.List;

public class Perspective implements Subject {

    private double scale;   // Zoom (1.0 = 100%)
    private double offsetX; // Décalage horizontal
    private double offsetY; // Décalage vertical

    private transient List<Observer> observers = new ArrayList<>();

    // Constructeur : initialise la perspective avec un zoom de 1.0 et des offsets à 0.
    public Perspective() {
        this.scale = 1.0;
        this.offsetX = 0;
        this.offsetY = 0;
        observers = new ArrayList<>();
    }

    // Retourne le facteur de zoom actuel.
    public double getScale() {
        return scale;
    }

    // Définit le facteur de zoom et notifie les observateurs du changement.
    public void setScale(double scale) {
        this.scale = scale;
        notifyObservers("ScaleChanged");
    }

    // Retourne l'offset horizontal actuel.
    public double getOffsetX() {
        return offsetX;
    }

    // Retourne l'offset vertical actuel.
    public double getOffsetY() {
        return offsetY;
    }

    // Définit les offsets horizontal et vertical, puis notifie les observateurs.
    public void setOffset(double x, double y) {
        this.offsetX = x;
        this.offsetY = y;
        notifyObservers("OffsetChanged");
    }

    // Applique un zoom en modifiant l'échelle et ajuste l'offset pour conserver le point central
    public void zoom(double factor, double centerX, double centerY) {

        //ajouter l'état pré-zoom à l'historique
        PerspectiveCaretaker.getInstance().pushNewMemento(this.saveState());

        //zoomer la perspective
        double oldScale = scale;
        scale *= factor;
        offsetX = centerX - ((centerX - offsetX) * (scale / oldScale));
        offsetY = centerY - ((centerY - offsetY) * (scale / oldScale));
        notifyObservers("ZoomChanged");
    }

    // Effectue une translation en ajustant les offsets selon les valeurs fournies.
    public void move(double dx, double dy) {

        //ajouter l'état pré-déplacement à l'historique
        PerspectiveCaretaker.getInstance().pushNewMemento(this.saveState());

        //déplacer la perspective
        this.offsetX += dx;
        this.offsetY += dy;
        notifyObservers("Moved");
    }

    // Attache un observateur à cette perspective.
    @Override
    public void attach(Observer o) {
        if(observers == null) {
            observers = new ArrayList<>();
        }
        if (!observers.contains(o))
            observers.add(o);
    }

    // Détache un observateur de cette perspective.
    @Override
    public void detach(Observer o) {
        if(observers != null)
            observers.remove(o);
    }

    // Notifie tous les observateurs d'un changement avec l'argument fourni.
    @Override
    public void notifyObservers(Object arg) {
        if(observers == null)
            observers = new ArrayList<>();
        for (Observer o : observers) {
            o.update(this, arg);
        }
    }

    //Sauvegarde l'état courant de la perspective dans un objet memento.

    public PerspectiveMemento saveState() {
        return new PerspectiveMemento(scale, offsetX, offsetY, this);
    }

    // Restaure l'état de la perspective à partir d'un objet memento et notifie les observateurs.

    public void restoreState(PerspectiveMemento persMeme) {
        this.scale = persMeme.getScale();
        this.offsetX = persMeme.getOffsetX();
        this.offsetY = persMeme.getOffsetY();
        notifyObservers("StateRestored");
    }
}

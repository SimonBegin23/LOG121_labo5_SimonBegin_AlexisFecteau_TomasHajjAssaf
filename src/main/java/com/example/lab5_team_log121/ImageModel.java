package com.example.lab5_team_log121;

import javafx.scene.image.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageModel implements Subject, Serializable {
    private static final long serialVersionUID = 1L;

    private String imagePath;
    private transient Image image; // On sauvegarde seulement le chemin
    private transient List<Observer> observers = new ArrayList<>();

    public ImageModel() {
        this.imagePath = null;
        this.image = null;
    }

    //Charge l’image à partir d’un chemin donné.
    public void loadImage(String path) {
        this.imagePath = path;
        this.image = new Image("file:" + path);
        notifyObservers("ImageLoaded");
    }

    //Retourne l’image
    public Image getImage() {
        if(image == null && imagePath != null) {
            image = new Image("file:" + imagePath);
        }
        return image;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void attach(Observer o) {
        if (!observers.contains(o))
            observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        for (Observer o : observers) {
            o.update(this, arg);
        }
    }


}

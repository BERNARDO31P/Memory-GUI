package com.forrer.memory;

import javafx.scene.image.WritableImage;

/**
 *Die Klasse "ImageView" wird überladen damit zwei Variablen im ursprünglichen Objekt gespeichert werden können
 */
public class ImageView extends javafx.scene.image.ImageView {
    public int fieldId;
    public String fieldSmiley;

    ImageView(WritableImage writableImage) {
        super(writableImage);

    }
}

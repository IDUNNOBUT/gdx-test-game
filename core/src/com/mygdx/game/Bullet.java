package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    Vector2 position;
    Texture image;

    public Bullet(Vector2 position, Texture image) {
        this.position = position;
        this.image = image;
    }

    public void update(float delta) {
        position.y += 200 * delta;
    }

    public void render(Batch batch) {
        batch.draw(image, position.x, position.y);
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
    }
}

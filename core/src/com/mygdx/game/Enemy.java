package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    Vector2 position;
    Texture image;
    Animation<TextureRegion> animation;
    float speed = 100f;

    // Конструктор для статического изображения
    public Enemy(Vector2 position, Texture image) {
        this.position = position;
        this.image = image;
        this.animation = null;
    }

    // Конструктор для анимации
    public Enemy(Vector2 position, Animation<TextureRegion> animation) {
        this.position = position;
        this.image = null;
        this.animation = animation;
    }

    public void update(float delta) {
        position.y -= speed * delta;
    }

    public void render(Batch batch, float stateTime) {
        if (image != null) {
            batch.draw(image, position.x, position.y);
        } else if (animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, position.x, position.y);
        }
    }

    public Rectangle getBoundingRectangle() {
        if (image != null) {
            return new Rectangle(position.x, position.y, image.getWidth(), image.getHeight());
        } else if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(0);
            return new Rectangle(position.x, position.y, frame.getRegionWidth(), frame.getRegionHeight());
        }
        return new Rectangle(); // Возвращаем пустой прямоугольник по умолчанию
    }

    public float getHeight() {
        if (image != null) {
            return image.getHeight();
        } else if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(0);
            return frame.getRegionHeight();
        }
        return 0; // Возвращаем 0 по умолчанию
    }
}

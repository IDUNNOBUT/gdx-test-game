package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    Vector2 position;
    Animation<TextureRegion> animation;
    float stateTime;

    public Player(Vector2 position, Animation<TextureRegion> animation) {
        this.position = position;
        this.animation = animation;
        this.stateTime = 0f;
    }

    public void render(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y);
    }

    public void setPosition(float x) {
        position.x = x;
    }

    public void moveLeft(float delta) {
        position.x -= 200 * delta;
        if (position.x < 0) position.x = 0;
    }

    public void moveRight(float delta) {
        position.x += 200 * delta;
        if (position.x > Gdx.graphics.getWidth() - getWidth())
            position.x = Gdx.graphics.getWidth() - getWidth();
    }

    public void shoot(GameScreen screen) {
        screen.shoot(new Vector2(position.x + getWidth() / 2, position.y + getHeight()));
    }

    public float getHeight() {
        return animation.getKeyFrame(stateTime).getRegionHeight();
    }

    public float getWidth() {
        return animation.getKeyFrame(stateTime).getRegionWidth();
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, getWidth(), getHeight());
    }
}

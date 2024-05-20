package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // важно!
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose(); // освобождение ресурсов шрифта
    }

    public void restartGame() {
        this.setScreen(new GameScreen(this));
    }
}

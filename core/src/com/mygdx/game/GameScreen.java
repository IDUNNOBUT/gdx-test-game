package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    final MyGdxGame game;

    Texture bulletImage;
    Texture enemyImage;
    Texture backgroundImage;
    Animation<TextureRegion> enemyAnimation;
    Animation<TextureRegion> playerAnimation;

    Animation<TextureRegion> backAnimation;

    Player player;
    Array<Bullet> bullets;
    Array<Enemy> enemies;

    float shootTimer;
    float stateTime;
    float spawnTimer;

    int killedEnemies;
    int enemiesToSpawn = 10;

    boolean gameOver;
    Stage stage;
    TextButton restartButton;

    Music backgroundMusic;
    Sound hitSound;

    public GameScreen(final MyGdxGame game) {
        this.game = game;
        backgroundImage = new Texture("menu_background.png");
        bulletImage = new Texture("bullet.png");
        enemyImage = new Texture("enemy.png");

        // Load background music and hit sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));

        // Animated enemy
        Texture enemySheet = new Texture("animated_enemy.png");
        TextureRegion[][] tmp = TextureRegion.split(enemySheet, enemySheet.getWidth() / 2, enemySheet.getHeight() / 2);
        TextureRegion[] enemyFrames = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                enemyFrames[index++] = tmp[i][j];
            }
        }
        enemyAnimation = new Animation<TextureRegion>(0.1f, enemyFrames);

        // Animated player
        Texture playerSheet = new Texture("animated_player.png");
        TextureRegion[][] playerTmp = TextureRegion.split(playerSheet, playerSheet.getWidth() / 2, playerSheet.getHeight() / 2);
        TextureRegion[] playerFrames = new TextureRegion[4];
        int playerIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                playerFrames[playerIndex++] = playerTmp[i][j];
            }
        }
        playerAnimation = new Animation<TextureRegion>(0.1f, playerFrames);


        // Animated background
        Texture backSheet = new Texture("animated_background.png");
        TextureRegion[][] backTmp = TextureRegion.split(backSheet, backSheet.getWidth() / 2, backSheet.getHeight() / 2);
        TextureRegion[] backFrames = new TextureRegion[4];
        int backIndex = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                backFrames[backIndex++] = backTmp[i][j];
            }
        }

        backAnimation = new Animation<TextureRegion>(0.15f, backFrames);

        player = new Player(new Vector2(100, 100), playerAnimation);
        bullets = new Array<>();
        enemies = new Array<>();

        // Initial spawn of enemies
        for (int i = 0; i < enemiesToSpawn; i++) {
            spawnEnemy();
        }

        shootTimer = 0;
        stateTime = 0;
        spawnTimer = 0;
        killedEnemies = 0;
        gameOver = false;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;
        restartButton = new TextButton("Again", textButtonStyle);
        restartButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 25);
        restartButton.setVisible(false);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.restartGame();
            }
        });
        stage.addActor(restartButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        game.batch.begin();
        TextureRegion currentFrame = backAnimation.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (gameOver) {
            game.font.draw(game.batch, "Game Over! Final Score: " + killedEnemies, 100, Gdx.graphics.getHeight() / 2 + 50);
        } else {
            player.render(game.batch);
            for (Bullet bullet : bullets) {
                bullet.render(game.batch);
            }
            for (Enemy enemy : enemies) {
                enemy.render(game.batch, stateTime);
            }
            game.font.draw(game.batch, "Score: " + killedEnemies, 10, Gdx.graphics.getHeight() - 10);
        }
        game.batch.end();

        if (gameOver) {
            restartButton.setVisible(true);
            stage.act();
            stage.draw();
        } else {
            handleInput(delta);
            update(delta);
        }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float playerCenterX = player.position.x + player.getWidth() / 2;
            float tolerance = 10f;

            if (touchX < playerCenterX - tolerance) {
                player.moveLeft(delta);
            } else if (touchX > playerCenterX + tolerance) {
                player.moveRight(delta);
            }
        }
    }

    private void update(float delta) {
        shootTimer += delta;
        stateTime += delta;
        spawnTimer += delta;

        if (shootTimer >= 0.5f) {
            player.shoot(this);
            shootTimer = 0;
        }

        if (spawnTimer >= 30f) {
            enemiesToSpawn++;
            spawnTimer = 0;
        }

        Array<Bullet> bulletsToRemove = new Array<>();
        Array<Enemy> enemiesToRemove = new Array<>();

        for (Bullet bullet : bullets) {
            bullet.update(delta);
            if (bullet.position.y > Gdx.graphics.getHeight()) {
                bulletsToRemove.add(bullet);
            } else {
                for (Enemy enemy : enemies) {
                    if (bullet.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                        bulletsToRemove.add(bullet);
                        enemiesToRemove.add(enemy);
                        killedEnemies++;
                        hitSound.play();
                        break;
                    }
                }
            }
        }

        bullets.removeAll(bulletsToRemove, true);
        enemies.removeAll(enemiesToRemove, true);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            if (enemy.position.y + enemy.getHeight() < 0) {
                enemiesToRemove.add(enemy);
            }

            if (enemy.getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
                gameOver = true;
                return;
            }
        }

        enemies.removeAll(enemiesToRemove, true);

        while (enemies.size < enemiesToSpawn) {
            spawnEnemy();
        }
    }

    private void spawnEnemy() {
        float x = (float) Math.random() * (Gdx.graphics.getWidth() - enemyImage.getWidth());
        float y = Gdx.graphics.getHeight() - enemyImage.getHeight();
        if (Math.random() > 0.5) {
            enemies.add(new Enemy(new Vector2(x, y), enemyImage));
        } else {
            enemies.add(new Enemy(new Vector2(x, y), enemyAnimation));
        }
    }

    public void shoot(Vector2 position) {
        Bullet bullet = new Bullet(new Vector2(position.x - bulletImage.getWidth() / 2, position.y + player.getHeight()), bulletImage);
        bullets.add(bullet);
    }

    @Override
    public void show() {
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        bulletImage.dispose();
        enemyImage.dispose();
        stage.dispose();
        backgroundMusic.dispose();
        hitSound.dispose();
    }
}

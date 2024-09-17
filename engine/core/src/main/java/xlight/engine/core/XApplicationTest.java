package xlight.engine.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ScreenUtils;

public class XApplicationTest implements ApplicationListener {

    int input = 0;

    public XApplicationTest() {
    }

    @Override
    public void create() {
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1, true);

        boolean cursorCatched = Gdx.input.isCursorCatched();
        int positionX = Gdx.input.getX();
        int deltaX = Gdx.input.getDeltaX();


        System.out.println("X: " + positionX + " deltaX: " + deltaX + " cursorCatched: " + cursorCatched);

        boolean buttonPressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);

        if(input == 0 && buttonPressed) {
            input = 1;
            Gdx.input.setCursorCatched(true);
        }

        if(!buttonPressed && input == 1) {
            input = 0;
            Gdx.input.setCursorCatched(false);
        }
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
    public void dispose() {
    }
}
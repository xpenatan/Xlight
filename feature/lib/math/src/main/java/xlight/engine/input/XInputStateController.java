package xlight.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;

public class XInputStateController {

    private IntArray keyList = new IntArray();
    private IntArray buttonList = new IntArray();

    IntSet keyDown = new IntSet();
    IntSet buttonDown = new IntSet();
    IntSet keyUp = new IntSet();
    IntSet buttonUp = new IntSet();

    boolean isDragging = false;
    boolean initDragging = false;

    private int startX;
    private int startY;

    public void update() {
        buttonUp.clear();
        keyUp.clear();

        for(int i = 0; i < keyList.size; i++) {
            int key = keyList.get(i);
            if(Gdx.input.isKeyJustPressed(key)) {
                keyDown.add(key);
            }
            else if(!Gdx.input.isKeyPressed(key) && keyDown.contains(key)) {
                keyDown.remove(key);
                keyUp.add(key);
            }
        }
        for(int i = 0; i < buttonList.size; i++) {
            int key = buttonList.get(i);
            if(Gdx.input.isButtonJustPressed(key)) {
                buttonDown.add(key);
            }
            else if(!Gdx.input.isButtonPressed(key) && buttonDown.contains(key)) {
                buttonDown.remove(key);
                buttonUp.add(key);
            }
        }

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        if(buttonDown.size > 0) {
            if(!initDragging) {
                initDragging = true;
                startX = mouseX;
                startY = mouseY;
            }
        }
        else {
            initDragging = false;
            isDragging = false;
        }

        if(initDragging) {
            if(!isDragging) {
                if(mouseX != startX || mouseY != startY) {
                    isDragging = true;
                }
            }
        }
    }

    public void registerKeys(int key) {
        keyList.add(key);
    }

    public void registerButtons(int key) {
        buttonList.add(key);
    }

    public boolean isButtonJustPressedUp(int button) {
        return buttonUp.contains(button);
    }

    public boolean isKeyJustPressedUp(int key) {
        return keyUp.contains(key);
    }

    public boolean isButtonDragging() {
        return isDragging;
    }
}
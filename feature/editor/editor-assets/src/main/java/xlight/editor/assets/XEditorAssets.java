package xlight.editor.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class XEditorAssets {

    private static final String IMAGE_PATH = "editor/images/";
    private static final String MENU_PATH = IMAGE_PATH + "menu/";

    public static Texture img_folderTexture;
    public static Texture img_fileTexture;

    public static Texture ic_eyeOpenTexture;
    public static Texture ic_eyeCloseTexture;
    public static Texture ic_folderTexture;
    public static Texture ic_entityTexture;
    public static Texture ic_trashTexture;
    public static Texture ic_addTexture;
    public static Texture ic_gearTexture;

    public static Texture playTexture;
    public static Texture stopTexture;
    public static Texture playStepTexture;
    public static Texture saveTexture;
    public static Texture loadTexture;

    public static Texture axisRotateTexture;
    public static Texture axisPositionTexture;
    public static Texture axisScaleTexture;
    public static Texture axisLocalTexture;
    public static Texture axisGlobalTexture;

    public static FileHandle font_CousineRegular;
    public static FileHandle font_DroidSans;

    public static void loadAssets() {
        ic_eyeOpenTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_eye-open.png"));
        ic_eyeCloseTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_eye-close.png"));
        ic_folderTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_folder.png"));
        ic_entityTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_entity.png"));
        ic_trashTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_trash.png"));
        ic_addTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_add.png"));
        ic_gearTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "ic_gear.png"));

        playTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_play.png"));
        playTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        stopTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_stop.png"));
        stopTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        playStepTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_step.png"));
        playStepTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        saveTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_save.png"));
        saveTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        loadTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_load.png"));
        loadTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        axisRotateTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_rotation.png"));
        axisRotateTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        axisPositionTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_translation.png"));
        axisPositionTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        axisScaleTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_scale.png"));
        axisScaleTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        axisLocalTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_local.png"));
        axisLocalTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        axisGlobalTexture = new Texture(Gdx.files.internal(MENU_PATH + "ic_global.png"));
        axisGlobalTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        img_folderTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "img_folder.png"));
        img_folderTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        img_folderTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        img_fileTexture = new Texture(Gdx.files.internal(IMAGE_PATH + "img_file.png"));
        img_fileTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        img_fileTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        font_CousineRegular = Gdx.files.classpath("editor/fonts/Cousine-Regular.ttf");
        font_DroidSans = Gdx.files.classpath("editor/fonts/DroidSans.ttf");
    }

    public static void disposeAssets() {
        ic_eyeOpenTexture.dispose();
        ic_eyeCloseTexture.dispose();
        ic_folderTexture.dispose();
        ic_entityTexture.dispose();
        ic_trashTexture.dispose();
        ic_addTexture.dispose();
        ic_gearTexture.dispose();

        playTexture.dispose();
        stopTexture.dispose();
        playStepTexture.dispose();
        saveTexture.dispose();
        loadTexture.dispose();
        axisRotateTexture.dispose();
        axisPositionTexture.dispose();
        axisScaleTexture.dispose();
        axisLocalTexture.dispose();
        axisGlobalTexture.dispose();

        img_folderTexture.dispose();
        img_fileTexture.dispose();
    }
}

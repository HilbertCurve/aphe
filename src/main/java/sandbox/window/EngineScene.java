package sandbox.window;

import org.joml.Vector2f;
import math.MyMath;

public class EngineScene extends Scene {
    public static final float TILE_WIDTH = 32;

    private Spritesheet sprites;
    private GameObject obj;

    public EngineScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpritesheet("assets/tests/spritesheet.png");

        obj = new GameObject(
                "rotationTest",
                new Transform(new Vector2f(128, 0), new Vector2f(32, 32)),
                1
        );

        obj.addComponent(new SpriteRenderer().init(sprites.getSprite(0)));

        GameObject temp = new GameObject(
                "center",
                new Transform(-16, -16, 32, 32),
                2
        );

        temp.addComponent(new SpriteRenderer().init(sprites.getSprite(8)));

        addGameObjectToScene(obj);
        addGameObjectToScene(temp);
}

    @Override
    public void loadResources() {
        super.loadResources();

        AssetPool.addSpritesheet("assets/tests/spritesheet.png", new Spritesheet(
                new Texture().init("assets/tests/spritesheet.png"),
                16, 16, 26, 0
        ));
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        //MyMath.rotate(new Vector2f(0, 0), obj.transform.position, 123f);

        this.camera.update(dt);

        this.renderer.render();
    }
}

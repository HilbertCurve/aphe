package sandbox.window;

import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private boolean isDirty;

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private SpriteTexture spriteTexture = new SpriteTexture();
    private transient Transform lastTransform;
    private transient Vector2f velocity = new Vector2f(0, 0);

    public SpriteRenderer init(Vector4f color) {
        this.color = color;
        this.spriteTexture = new SpriteTexture().init(null);
        this.isDirty = true;
        return this;
    }

    public SpriteRenderer init(SpriteTexture spriteTexture) {
        this.spriteTexture = spriteTexture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.isDirty = true;
        return this;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        gameObject.transform.position = gameObject.transform.position.add(velocity);
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Color Picker: ", imColor)) {
            this.color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            this.isDirty = true;
        }
    }

    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return spriteTexture.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return spriteTexture.getTexCoords();
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.isDirty = true;
        }
    }

    public void accel(Vector2f v) {
        setVelocity(this.getVelocity().add(v));
    }

    public void accel(float dx, float dy) {
        setVelocity(this.getVelocity().add(new Vector2f(dx, dy)));
    }

    public void setVelocity(Vector2f v) {
        velocity = v;
    }

    public void setSprite(SpriteTexture spriteTexture) {
        this.spriteTexture = spriteTexture;
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}

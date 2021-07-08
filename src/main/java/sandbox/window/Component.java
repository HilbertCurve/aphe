package sandbox.window;

import com.google.gson.*;

import java.lang.reflect.Type;

public abstract class Component {
    public transient GameObject gameObject = null;

    public Component() {

    }

    public void start() {

    }

    public void update(float dt) {

    }

    public void imgui() {

    }
}

class ComponentJsonAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {
    private static final String myType = "myType";

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get("myType").getAsString();

        try {
            Class<?> clz = Class.forName(className);
            return context.deserialize(json, clz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement e = context.serialize(src, src.getClass());
        e.getAsJsonObject().addProperty(
                myType,
                src.getClass().getCanonicalName()
        );

        return e;
    }
}
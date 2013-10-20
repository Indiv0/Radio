package in.nikitapek.radio.serialization;

import com.amshulman.typesafety.gson.GsonTypeAdapter;
import com.google.gson.*;
import in.nikitapek.radio.util.SupplementaryTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationTypeAdapter implements GsonTypeAdapter<Location> {

    private static final Type TYPE = SupplementaryTypes.LOCATION;

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray arr = new JsonArray();
        arr.add(new JsonPrimitive(src.getWorld().getName()));
        arr.add(new JsonPrimitive(src.getX()));
        arr.add(new JsonPrimitive(src.getY()));
        arr.add(new JsonPrimitive(src.getZ()));

        return arr;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray arr = json.getAsJsonArray();
        World w = Bukkit.getWorld(arr.get(0).getAsString());
        return new Location(w, arr.get(1).getAsDouble(), arr.get(2).getAsDouble(), arr.get(3).getAsDouble());
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}

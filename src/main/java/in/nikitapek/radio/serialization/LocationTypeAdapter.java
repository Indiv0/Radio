package in.nikitapek.radio.serialization;

import in.nikitapek.radio.util.SupplementaryTypes;

import java.lang.reflect.Type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.amshulman.typesafety.gson.GsonTypeAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public final class LocationTypeAdapter implements GsonTypeAdapter<Location> {

    private static final Type TYPE = SupplementaryTypes.LOCATION;

    @Override
    public JsonElement serialize(final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonArray arr = new JsonArray();
        arr.add(new JsonPrimitive(src.getWorld().getName()));
        arr.add(new JsonPrimitive(src.getX()));
        arr.add(new JsonPrimitive(src.getY()));
        arr.add(new JsonPrimitive(src.getZ()));

        return arr;
    }

    @Override
    public Location deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonArray arr = json.getAsJsonArray();
        final World w = Bukkit.getWorld(arr.get(0).getAsString());
        return new Location(w, arr.get(1).getAsDouble(), arr.get(2).getAsDouble(), arr.get(3).getAsDouble());
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}

package in.nikitapek.radio.util;

import com.google.gson.reflect.TypeToken;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.TreeSet;

public final class SupplimentaryTypes {
    @SuppressWarnings("rawtypes")
    public static final Type TREESET = new TypeToken<TreeSet>() {}.getType();

    public static final Type LARGEDECIMAL = new TypeToken<LargeDecimal>() {}.getType();
    public static final Type FREQUENCY = new TypeToken<Frequency>() {}.getType();
    public static final Type LOCATION = new TypeToken<Location>() {}.getType();
    public static final Type RADIO = new TypeToken<Radio>() {}.getType();
    public static final Type WORLD = new TypeToken<World>() {}.getType();

    private SupplimentaryTypes() {}
}

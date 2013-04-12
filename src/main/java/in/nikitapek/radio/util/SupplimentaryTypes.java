package in.nikitapek.radio.util;

import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.reflect.TypeToken;

public final class SupplimentaryTypes {
    private SupplimentaryTypes() {}

    @SuppressWarnings("rawtypes")
    public static final Type TREESET = new TypeToken<TreeSet>() {}.getType();

    public static final Type BIGDECIMAL = new TypeToken<BigDecimal>() {}.getType();
    public static final Type FREQUENCY = new TypeToken<Frequency>() {}.getType();
    public static final Type LOCATION = new TypeToken<Location>() {}.getType();
    public static final Type RADIO = new TypeToken<Radio>() {}.getType();
    public static final Type WORLD = new TypeToken<World>() {}.getType();
}

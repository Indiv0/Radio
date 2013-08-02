package in.nikitapek.radio.serialization;

import in.nikitapek.radio.util.ScaleInvariantBigDecimal;
import in.nikitapek.radio.util.SupplementaryTypes;

import java.lang.reflect.Type;

import com.amshulman.typesafety.gson.GsonTypeAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class FrequencyTypeAdapter implements GsonTypeAdapter<Frequency> {

    private static final Type TYPE = SupplementaryTypes.FREQUENCY;

    @Override
    public JsonElement serialize(Frequency src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getFrequency().toPlainString());
    }

    @Override
    public Frequency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Frequency(new ScaleInvariantBigDecimal(json.getAsString()));
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}

package in.nikitapek.radio.util;

import com.amshulman.mbapi.util.ConstructorFactory;
import in.nikitapek.radio.serialization.Frequency;

public class RadioConstructorFactory extends ConstructorFactory<Frequency> {
    @Override
    public Frequency get() {
        return new Frequency();
    }
}

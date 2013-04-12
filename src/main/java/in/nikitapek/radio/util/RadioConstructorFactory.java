package in.nikitapek.radio.util;

import in.nikitapek.radio.serialization.Frequency;

import com.amshulman.mbapi.util.ConstructorFactory;

public class RadioConstructorFactory extends ConstructorFactory<Frequency> {
    @Override
    public Frequency get() {
        return new Frequency();
    }
}

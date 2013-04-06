package com.github.indiv0.radio.util;

import com.amshulman.mbapi.util.ConstructorFactory;
import com.github.indiv0.radio.serialization.Frequency;

public class RadioConstructorFactory extends ConstructorFactory<Frequency> {
    @Override
    public Frequency get() {
        return new Frequency();
    }
}

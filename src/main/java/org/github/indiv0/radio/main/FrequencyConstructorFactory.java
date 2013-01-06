package org.github.indiv0.radio.main;

import org.github.indiv0.radio.serialization.Frequency;

import ashulman.mbapi.util.ConstructorFactory;

public class FrequencyConstructorFactory extends ConstructorFactory<Frequency> {
    @Override
    public Frequency get() {
        return new Frequency();
    }
}

package com.github.indiv0.radio.util;

import com.github.indiv0.radio.serialization.Frequency;

import ashulman.mbapi.util.ConstructorFactory;

public class FrequencyConstructorFactory extends ConstructorFactory<Frequency> {
    @Override
    public Frequency get() {
        return new Frequency();
    }
}

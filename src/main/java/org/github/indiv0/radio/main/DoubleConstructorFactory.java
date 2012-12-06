package org.github.indiv0.radio.main;

import ashulman.mbapi.util.ConstructorFactory;

public class DoubleConstructorFactory extends ConstructorFactory<Double> {
    @Override
    public Double get() {
        return RadioPlugin.OFF;
    }
}

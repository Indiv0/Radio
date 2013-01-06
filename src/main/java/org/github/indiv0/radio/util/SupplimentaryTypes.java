package org.github.indiv0.radio.util;

import java.lang.reflect.Type;
import java.util.HashSet;

import org.github.indiv0.radio.serialization.Radio;

import com.google.gson.reflect.TypeToken;

public final class SupplimentaryTypes {
    private SupplimentaryTypes() {
    }

    @SuppressWarnings("rawtypes")
    public static final Type HASHSET = new TypeToken<HashSet>() {
    }.getType();

    public static final Type RADIO = new TypeToken<Radio>() {
    }.getType();
}

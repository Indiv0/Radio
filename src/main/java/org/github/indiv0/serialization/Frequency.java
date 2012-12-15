package org.github.indiv0.serialization;

import java.math.BigDecimal;

public class Frequency implements Comparable<Frequency> {
    public static final BigDecimal SCANNING = BigDecimal.ONE.negate();
    public static final BigDecimal OFF = BigDecimal.ZERO;

    BigDecimal freq;

    public Frequency() {
        this(OFF);
    }

    public Frequency(BigDecimal frequency) {
        freq = frequency;
    }

    public boolean isOff() {
        return OFF.equals(freq);
    }

    public boolean isScanning() {
        return SCANNING.equals(freq);
    }

    @Override
    public int compareTo(Frequency obj) {
        return freq.compareTo(obj.freq);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((freq == null) ? 0 : freq.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof Frequency)) { return false; }
        Frequency other = (Frequency) obj;
        if (freq == null) {
            if (other.freq != null) { return false; }
        } else if (!freq.equals(other.freq)) { return false; }
        return true;
    }
}

package in.nikitapek.radio.serialization;

import in.nikitapek.radio.util.LargeDecimal;

public final class Frequency implements Comparable<Frequency> {
    public static final LargeDecimal SCANNING = new LargeDecimal(-1);
    public static final LargeDecimal OFF = new LargeDecimal(0);

    private LargeDecimal freq;

    public Frequency() {
        this(Frequency.OFF);
    }

    public Frequency(final LargeDecimal frequency) {
        freq = frequency;
    }

    public LargeDecimal getFrequency() {
        return freq;
    }

    public void setFrequency(final LargeDecimal frequency) {
        freq = frequency;
    }

    @Override
    public int compareTo(final Frequency obj) {
        return freq.compareTo(obj.freq);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (freq == null ? 0 : freq.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Frequency)) {
            return false;
        }
        final Frequency other = (Frequency) obj;
        if (freq == null) {
            if (other.freq != null) {
                return false;
            }
        } else if (!freq.equals(other.freq)) {
            return false;
        }
        return true;
    }
}

package in.nikitapek.radio.util;

import java.math.BigDecimal;

public class LargeDecimal extends BigDecimal {
    public LargeDecimal(double value) {
        super(value);
    }

    public LargeDecimal(String value) {
        super(value);
    }

    public boolean equals(LargeDecimal other) {
        return compareTo(other) == 0;
    }

    public int hashCode() {
        return Double.valueOf(this.toString()).hashCode();
    }
}

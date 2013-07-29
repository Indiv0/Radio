package in.nikitapek.radio.util;

import java.math.BigDecimal;

public class LargeDecimal extends BigDecimal {
    public LargeDecimal(double value) {
        super(value);
    }

    public LargeDecimal(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object other) {
        return compareTo((LargeDecimal) other) == 0;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(this.toString()).hashCode();
    }
}

package in.nikitapek.radio.util;

import java.math.BigDecimal;

public final class ScaleInvariantBigDecimal extends BigDecimal {

    private static final long serialVersionUID = -6961257361561348906L;

    public ScaleInvariantBigDecimal(double value) {
        super(value);
    }

    public ScaleInvariantBigDecimal(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return compareTo((ScaleInvariantBigDecimal) obj) == 0;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(this.toString()).hashCode();
    }
}

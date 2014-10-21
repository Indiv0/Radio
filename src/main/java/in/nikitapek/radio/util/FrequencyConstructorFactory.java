package in.nikitapek.radio.util;

import com.amshulman.mbapi.util.ConstructorFactory;
import in.nikitapek.radio.serialization.Frequency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

import org.mindrot.jbcrypt.BCrypt;

public class FrequencyConstructorFactory extends ConstructorFactory<Frequency> {
    private final static BigInteger MAX_FREQUENCY = new BigInteger("64").pow(31);
    private final String privateSalt;

    public FrequencyConstructorFactory(RadioConfigurationContext configurationContext) {
        this.privateSalt = configurationContext.privateSalt;
    }

    @Override
    public Frequency get() {
        return new Frequency();
    }

    public Frequency getFrequencyFromString(String stringFrequency) {
        /*
        bytes = BCrypt.decode_base64(hash);
        BigInteger unscaledFrequency = 0;
        for (int i = 0; i < bytes.length; i++) {
                unscaledFrequency += bytes[i] * 64^i;
        }
        BigInteger frequency = unscaledFrequency / MAX_FREQUENCY;
        */
        System.out.println("passphrase: " + stringFrequency);
        System.out.println("salt: " + privateSalt);
        String hashed = BCrypt.hashpw(stringFrequency, privateSalt);
        // Strip everything but the actual encrypted data.
        hashed = hashed.substring(hashed.length() - 31);

        System.out.println("bcrypted: " + hashed);

        Method decode_base64;
        try {
            decode_base64 = BCrypt.class.getDeclaredMethod("decode_base64", String.class, int.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Failed to find the BCrypt.decode_base64 method.");
            return null;
        }
        decode_base64.setAccessible(true);
        byte[] decodedBytes;
        try {
            decodedBytes = (byte[]) decode_base64.invoke(null, hashed, 200);
        } catch (IllegalAccessException e) {
            System.out.println("Failed to invoke the BCrypt.decode_base64 method.");
            return null;
        } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException for BCrypt.decode_base64 method.");
            return null;
        }

        System.out.println(decodedBytes);
        for (int i = 0; i < decodedBytes.length; i++) {
            System.out.println(decodedBytes[i]);
        }
        System.out.println("unscaled: " + "");

        ScaleInvariantBigDecimal frequencyDecimal;
        try {
            frequencyDecimal = new ScaleInvariantBigDecimal(hashed);
        } catch (NumberFormatException e) {
            return null;
        }

        return new Frequency(frequencyDecimal);
    }
}

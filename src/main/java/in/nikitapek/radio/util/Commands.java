package in.nikitapek.radio.util;

import com.amshulman.mbapi.util.PermissionsEnum;

public enum Commands implements PermissionsEnum {
    RADIO;

    @Override
    public String getPrefix() {
        return "radio.";
    }
}

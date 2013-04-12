package in.nikitapek.radio.util;

import com.amshulman.mbapi.util.PermissionsEnum;

public enum Commands implements PermissionsEnum {
    RADIO, TUNE, SCAN, OFF;

    @Override
    public String getPrefix() {
        return "radio.";
    }
}

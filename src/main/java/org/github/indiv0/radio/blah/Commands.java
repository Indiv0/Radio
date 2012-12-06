package org.github.indiv0.radio.blah;

import ashulman.mbapi.util.PermissionsEnum;

public enum Commands implements PermissionsEnum {
    RADIO;

    @Override
    public String getPrefix() {
        return "radio.";
    }
}

package org.github.indiv0.radio.main;

import ashulman.mbapi.util.PermissionsEnum;

public enum Commands implements PermissionsEnum {
    RADIO, TUNE, SCAN, OFF;

    @Override
    public String getPrefix() {
        return "radio.";
    }
}

package com.jeik.authamo;

public class Key {
    public Key(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    private String name;

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private boolean enabled;
}

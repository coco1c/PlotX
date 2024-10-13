package com.coco.plotX.hooks;

public abstract class Extension {
    protected boolean isEnabled = false;

    public void setEnabled(boolean i) {
        isEnabled = i;
    }

    public boolean getEnabled() {
        return isEnabled;
    }
}

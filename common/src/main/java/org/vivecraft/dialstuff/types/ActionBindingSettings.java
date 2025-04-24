package org.vivecraft.dialstuff.types;

import org.vivecraft.client_vr.provider.control.VRInputAction;
import org.vivecraft.client_vr.settings.VRSettings;

public class ActionBindingSettings {
    ControllerButton button;
    VRSettings.ActionMode actionMode;
    private boolean pressUntilNotTouch;
    private VRInputAction vrInputAction;
    private int hapticAmplitude;
    private float hapticDuration;
    private int hapticFrequency;
    private boolean toggleActive;
    public int heldTimer;
    private boolean isDefault;

    public ActionBindingSettings(ControllerButton button, VRInputAction vrInputAction) {
        this(button, vrInputAction, VRSettings.ActionMode.SINGLE, false, 0, 0,0);
        isDefault = true;
    }

    public ActionBindingSettings(ControllerButton button, VRInputAction vrInputAction, VRSettings.ActionMode actionMode, boolean pressUntilNotTouch, int hapticAmplitude, float hapticDuration, int hapticFrequency) {
        this.button = button;
        this.actionMode = actionMode;
        this.pressUntilNotTouch = pressUntilNotTouch;
        this.vrInputAction = vrInputAction;
        this.hapticAmplitude = hapticAmplitude;
        this.hapticDuration = hapticDuration;
        this.hapticFrequency = hapticFrequency;
    }

    public void update(ControllerButton button, VRSettings.ActionMode actionMode, boolean pressUntilNotTouch, int hapticAmplitude, float hapticDuration, int hapticFrequency) {
        this.button = button;
        this.actionMode = actionMode;
        this.pressUntilNotTouch = pressUntilNotTouch;
        this.hapticAmplitude = hapticAmplitude;
        this.hapticDuration = hapticDuration;
        this.hapticFrequency = hapticFrequency;
    }

    public ControllerButton getControllerButton() {
        return button;
    }

    public void setControllerButton(ControllerButton button) {
        this.button = button;
    }

    public VRInputAction getVrInputAction() { return vrInputAction; }
    public VRSettings.ActionMode getActionMode() { return actionMode; }

    public int getHapticAmplitude() {
        return hapticAmplitude;
    }

    public float getHapticDuration() {
        return hapticDuration;
    }

    public int getHapticFrequency() {
        return hapticFrequency;
    }

    public boolean isPressUntilNotTouch() {
        return pressUntilNotTouch;
    }

    public void incrementHeldTimer() {
        heldTimer++;
    }

    public void resetHeldTimer() {
        heldTimer = 0;
    }

    public boolean toggleable() {
        return actionMode == VRSettings.ActionMode.TOGGLE || actionMode == VRSettings.ActionMode.TOGGLE_HELD;
    }

    public boolean longHold() {
        return actionMode == VRSettings.ActionMode.LONG;
    }

    public boolean shortHold() {
        return actionMode == VRSettings.ActionMode.HELD;
    }

    public boolean toggle() {
        return toggleActive = !toggleActive;
    }

    public boolean isLong() {
        return heldTimer > 100;
    }

    public boolean isSingle() {
        return actionMode == VRSettings.ActionMode.TOGGLE || actionMode == VRSettings.ActionMode.SINGLE;
    }

    public boolean isDefault() {
        return isDefault;
    }
}


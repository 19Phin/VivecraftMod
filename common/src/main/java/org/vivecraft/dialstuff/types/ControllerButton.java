package org.vivecraft.dialstuff.types;

import java.util.HashMap;
import java.util.Map;

public enum ControllerButton {
    A_CLICK("A", "A", "boolean", ButtonFamily.A, InputKind.CLICK),
    A_TOUCH("ATouch", "A", "boolean", ButtonFamily.A, InputKind.TOUCH),

    B_CLICK("B", "B", "boolean", ButtonFamily.B, InputKind.CLICK),
    B_TOUCH("BTouch", "B", "boolean", ButtonFamily.B, InputKind.TOUCH),

    X_CLICK("X", "X", "boolean", ButtonFamily.X, InputKind.CLICK),
    X_TOUCH("XTouch", "X", "boolean", ButtonFamily.X, InputKind.TOUCH),

    Y_CLICK("Y", "Y", "boolean", ButtonFamily.Y, InputKind.CLICK),
    Y_TOUCH("YTouch", "Y", "boolean", ButtonFamily.Y, InputKind.TOUCH),

    TRIGGER_LEFT_CLICK("triggerLeftClick", "Left Trigger", "boolean", ButtonFamily.TRIGGER_LEFT, InputKind.CLICK),
    TRIGGER_LEFT_TOUCH("triggerLeftTouch", "Left Trigger", "boolean", ButtonFamily.TRIGGER_LEFT, InputKind.TOUCH),

    TRIGGER_RIGHT_CLICK("triggerRightClick", "Right Trigger", "boolean", ButtonFamily.TRIGGER_RIGHT, InputKind.CLICK),
    TRIGGER_RIGHT_TOUCH("triggerRightTouch", "Right Trigger", "boolean", ButtonFamily.TRIGGER_RIGHT, InputKind.TOUCH),

    GRIP_LEFT_CLICK("gripLeftClick", "Left Grip", "boolean", ButtonFamily.GRIP_LEFT, InputKind.CLICK),
    GRIP_LEFT_TOUCH("gripLeftTouch", "Left Grip", "boolean", ButtonFamily.GRIP_LEFT, InputKind.TOUCH),

    GRIP_RIGHT_CLICK("gripRightClick", "Right Grip", "boolean", ButtonFamily.GRIP_RIGHT, InputKind.CLICK),
    GRIP_RIGHT_TOUCH("gripRightTouch", "Right Grip", "boolean", ButtonFamily.GRIP_RIGHT, InputKind.TOUCH),

    TRIGGER_LEFT_VECTOR("triggerLeft", "Left Trigger", "vector1", ButtonFamily.TRIGGER_LEFT, InputKind.VECTOR),
    TRIGGER_RIGHT_VECTOR("triggerRight", "Right Trigger", "vector1", ButtonFamily.TRIGGER_RIGHT, InputKind.VECTOR),
    GRIP_LEFT_VECTOR("gripLeft", "Left Grip", "vector1", ButtonFamily.GRIP_LEFT, InputKind.VECTOR),
    GRIP_RIGHT_VECTOR("gripRight", "Right Grip", "vector1", ButtonFamily.GRIP_RIGHT, InputKind.VECTOR),

    STICK_LEFT_CLICK("stickLeftClick", "Left Stick", "boolean", ButtonFamily.STICK_LEFT, InputKind.CLICK),
    STICK_LEFT_TOUCH("stickLeftTouch", "Left Stick", "boolean", ButtonFamily.STICK_LEFT, InputKind.TOUCH),

    STICK_RIGHT_CLICK("stickRightClick", "Right Stick", "boolean", ButtonFamily.STICK_RIGHT, InputKind.CLICK),
    STICK_RIGHT_TOUCH("stickRightTouch", "Left Stick", "boolean", ButtonFamily.STICK_RIGHT, InputKind.TOUCH),

    STICK_LEFT("stickLeft", "Left Stick", "vector2", ButtonFamily.STICK_LEFT, InputKind.VECTOR2),
    STICK_RIGHT("stickRight", "Right Stick", "vector2", ButtonFamily.STICK_RIGHT, InputKind.VECTOR2),

    MENU("menu", "Menu Button", "boolean", ButtonFamily.MENU, InputKind.CLICK),
    SYSTEM("system", "System Button", "boolean", ButtonFamily.SYSTEM, InputKind.CLICK),
    TOUCH_LEFT("touchLeft", "Left Touchpad", "boolean", ButtonFamily.TOUCH_LEFT, InputKind.TOUCH),
    TOUCH_RIGHT("touchRight", "Right Touchpad", "boolean", ButtonFamily.TOUCH_RIGHT, InputKind.TOUCH);

    private final String name;
    private final String formattedName;
    private final String type;
    private long handle;

    private final ButtonFamily family;
    private final InputKind kind;

    private static final Map<ButtonFamily, Map<InputKind, ControllerButton>> LOOKUP = new HashMap<>();

    static {
        for (ControllerButton btn : values()) {
            LOOKUP
                .computeIfAbsent(btn.family, f -> new HashMap<>())
                .put(btn.kind, btn);
        }
    }

    ControllerButton(String name, String formattedName, String type, ButtonFamily family, InputKind kind) {
        this.name = name;
        this.formattedName = formattedName;
        this.type = type;
        this.family = family;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String getType() {
        return type;
    }

    public void setHandle(long handle) {
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

    public InputKind getKind() {
        return kind;
    }

    public ControllerButton ofKind(InputKind kind) {
        return LOOKUP.getOrDefault(this.family, Map.of()).get(kind);
    }


    public enum ButtonFamily {
        A,
        B,
        X,
        Y,
        TRIGGER_LEFT,
        TRIGGER_RIGHT,
        LSTICK,
        RSTICK,
        GRIP_LEFT,
        GRIP_RIGHT,
        STICK_LEFT,
        STICK_RIGHT,
        MENU,
        SYSTEM,
        TOUCH_LEFT,
        TOUCH_RIGHT;
    }

    public enum InputKind {
        CLICK,
        TOUCH,
        VECTOR,
        VECTOR2
    }
}

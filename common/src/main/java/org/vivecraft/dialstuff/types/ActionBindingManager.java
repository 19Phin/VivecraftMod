package org.vivecraft.dialstuff.types;

import net.minecraft.client.KeyMapping;
import org.vivecraft.client_vr.provider.MCVR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionBindingManager {

    private static final List<ControllerButton> SIMPLE_BIND_BUTTONS = getSimpleBinds();
    private static final Map<String, Map<ControllerButton, String>> controllerPaths = loadControllerPaths();
    private static final Map<ControllerButton, List<ActionBindingSettings>> bindings = new HashMap<>();

    public static boolean isSimpleBindable(ControllerButton button) {
        return SIMPLE_BIND_BUTTONS.contains(button);
    }

    public static void addBinding(ActionBindingSettings settings) {
        bindings.computeIfAbsent(settings.button, k -> new ArrayList<>()).add(settings);
    }

    public static void tick() {
        for(List<ActionBindingSettings> settingsList : bindings.values()) {
            for (ActionBindingSettings settings : settingsList) {
                settings.incrementHeldTimer();
            }
        }
    }

    public static List<ActionBindingSettings> getBindingsForButton(ControllerButton button, boolean active) {
        List<ActionBindingSettings> result = getBindingsForButton(button);
        if (active && button.getKind().equals(ControllerButton.InputKind.TOUCH)) {
            result.addAll(bindings.computeIfAbsent(button.ofKind(ControllerButton.InputKind.CLICK), key -> new ArrayList<>()).stream()
                .filter(ActionBindingSettings::isPressUntilNotTouch).toList());
        }
        return result;
    }

    public static List<ActionBindingSettings> getBindingsForButton(ControllerButton button) {
        return new ArrayList<>(bindings.computeIfAbsent(button, key -> new ArrayList<>()));
    }

    public static Map<KeyMapping, List<ActionBindingSettings>> makeBindingsByKey() {
        return bindings.values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(binding -> binding.getVrInputAction().keyBinding));
    }

    public static void updateBindings(Map<KeyMapping, List<ActionBindingSettings>> newBindings) {
        bindings.clear();
        bindings.putAll(newBindings.values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(ActionBindingSettings::getControllerButton)));
    }

    public static Map<ControllerButton, List<ActionBindingSettings>> getBindings() {
        return bindings;
    }

    static {loadFromConfig();}

    public static void loadFromConfig() {
        addBinding(new ActionBindingSettings(ControllerButton.MENU, MCVR.get().getInputAction("vivecraft.key.ingameMenuButton")));
        addBinding(new ActionBindingSettings(ControllerButton.Y_CLICK, MCVR.get().getInputAction("key.inventory")));

        addBinding(new ActionBindingSettings(ControllerButton.GRIP_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.guiShift")));
        addBinding(new ActionBindingSettings(ControllerButton.GRIP_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.guiMiddleClick")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.guiLeftClick")));
        addBinding(new ActionBindingSettings(ControllerButton.A_CLICK, MCVR.get().getInputAction("vivecraft.key.guiRightClick")));
        addBinding(new ActionBindingSettings(ControllerButton.STICK_LEFT, MCVR.get().getInputAction("vivecraft.key.guiScrollAxis")));

        addBinding(new ActionBindingSettings(ControllerButton.GRIP_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.hotbarPrev")));
        addBinding(new ActionBindingSettings(ControllerButton.GRIP_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.hotbarNext")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_RIGHT_CLICK, MCVR.get().getInputAction("key.attack")));
        addBinding(new ActionBindingSettings(ControllerButton.X_CLICK, MCVR.get().getInputAction("vivecraft.key.teleport")));
        addBinding(new ActionBindingSettings(ControllerButton.B_CLICK, MCVR.get().getInputAction("vivecraft.key.radialMenu")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_LEFT_CLICK, MCVR.get().getInputAction("key.use")));
        addBinding(new ActionBindingSettings(ControllerButton.STICK_LEFT, MCVR.get().getInputAction("vivecraft.key.freeMoveStrafe")));
        addBinding(new ActionBindingSettings(ControllerButton.STICK_RIGHT, MCVR.get().getInputAction("vivecraft.key.rotateAxis")));
        addBinding(new ActionBindingSettings(ControllerButton.X_CLICK, MCVR.get().getInputAction("vivecraft.key.teleportFallback")));
        addBinding(new ActionBindingSettings(ControllerButton.A_CLICK, MCVR.get().getInputAction("key.jump")));
        addBinding(new ActionBindingSettings(ControllerButton.STICK_RIGHT_CLICK, MCVR.get().getInputAction("key.sneak")));

        addBinding(new ActionBindingSettings(ControllerButton.GRIP_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.keyboardShift")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.keyboardClick")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.keyboardClick")));

        addBinding(new ActionBindingSettings(ControllerButton.GRIP_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.vrInteract")));
        addBinding(new ActionBindingSettings(ControllerButton.GRIP_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.vrInteract")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.vrInteract")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.vrInteract")));

        addBinding(new ActionBindingSettings(ControllerButton.GRIP_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.climbeyGrab")));
        addBinding(new ActionBindingSettings(ControllerButton.GRIP_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.climbeyGrab")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_LEFT_CLICK, MCVR.get().getInputAction("vivecraft.key.climbeyGrab")));
        addBinding(new ActionBindingSettings(ControllerButton.TRIGGER_RIGHT_CLICK, MCVR.get().getInputAction("vivecraft.key.climbeyGrab")));
    }

    public static Map<String, Map<ControllerButton, String>> loadControllerPaths() {
        Map<ControllerButton, String> controller = new HashMap<>();
        controller.put(ControllerButton.A_TOUCH, "/user/hand/right/input/a/touch");
        controller.put(ControllerButton.A_CLICK, "/user/hand/right/input/a/click");
        controller.put(ControllerButton.B_TOUCH, "/user/hand/right/input/b/touch");
        controller.put(ControllerButton.B_CLICK, "/user/hand/right/input/b/click");
        controller.put(ControllerButton.X_TOUCH, "/user/hand/left/input/x/touch");
        controller.put(ControllerButton.X_CLICK, "/user/hand/left/input/x/click");
        controller.put(ControllerButton.Y_TOUCH, "/user/hand/left/input/y/touch");
        controller.put(ControllerButton.Y_CLICK, "/user/hand/left/input/y/click");

        controller.put(ControllerButton.TRIGGER_LEFT_TOUCH, "/user/hand/left/input/trigger/touch");
        controller.put(ControllerButton.TRIGGER_LEFT_CLICK, "/user/hand/left/input/trigger");
        controller.put(ControllerButton.TRIGGER_RIGHT_TOUCH, "/user/hand/right/input/trigger/touch");
        controller.put(ControllerButton.TRIGGER_RIGHT_CLICK, "/user/hand/right/input/trigger");
        controller.put(ControllerButton.GRIP_LEFT_VECTOR, "/user/hand/left/input/squeeze/value");
        controller.put(ControllerButton.GRIP_LEFT_CLICK, "/user/hand/left/input/squeeze");
        controller.put(ControllerButton.GRIP_RIGHT_VECTOR, "/user/hand/right/input/squeeze/value");
        controller.put(ControllerButton.GRIP_RIGHT_CLICK, "/user/hand/right/input/squeeze");

        controller.put(ControllerButton.STICK_LEFT_TOUCH, "/user/hand/left/input/thumbstick/touch");
        controller.put(ControllerButton.STICK_LEFT_CLICK, "/user/hand/left/input/thumbstick/click");
        controller.put(ControllerButton.STICK_RIGHT_TOUCH, "/user/hand/right/input/thumbstick/touch");
        controller.put(ControllerButton.STICK_RIGHT_CLICK, "/user/hand/right/input/thumbstick/click");
        controller.put(ControllerButton.STICK_LEFT, "/user/hand/left/input/thumbstick");
        controller.put(ControllerButton.STICK_RIGHT, "/user/hand/right/input/thumbstick");
        controller.put(ControllerButton.MENU, "/user/hand/left/input/menu/click");
        controller.put(ControllerButton.SYSTEM, "/user/hand/right/input/system/click");

        controller.put(ControllerButton.TOUCH_LEFT, "/user/hand/left/input/thumbrest/touch");
        controller.put(ControllerButton.TOUCH_RIGHT, "/user/hand/right/input/thumbrest/touch");
        Map<String, Map<ControllerButton, String>> controllerPaths = new HashMap<>();
        controllerPaths.put("/interaction_profiles/oculus/touch_controller", controller);
        return controllerPaths;
    }

    private static List<ControllerButton> getSimpleBinds() {
        List<ControllerButton> simpleBinds = new ArrayList<>();
        simpleBinds.add(ControllerButton.A_CLICK);
        simpleBinds.add(ControllerButton.B_CLICK);
        simpleBinds.add(ControllerButton.X_CLICK);
        simpleBinds.add(ControllerButton.Y_CLICK);

        simpleBinds.add(ControllerButton.TRIGGER_LEFT_CLICK);
        simpleBinds.add(ControllerButton.TRIGGER_RIGHT_CLICK);
        simpleBinds.add(ControllerButton.GRIP_LEFT_CLICK);
        simpleBinds.add(ControllerButton.GRIP_RIGHT_CLICK);

        simpleBinds.add(ControllerButton.STICK_LEFT_CLICK);
        simpleBinds.add(ControllerButton.STICK_RIGHT_CLICK);
        simpleBinds.add(ControllerButton.MENU);
        simpleBinds.add(ControllerButton.SYSTEM);

        simpleBinds.add(ControllerButton.TOUCH_LEFT);
        simpleBinds.add(ControllerButton.TOUCH_RIGHT);
        return simpleBinds;
    }

    public static Map<ControllerButton, String> pathsForHeadset(String headset) {
        return controllerPaths.get(headset);
    }

    public void saveToConfig() {
        // ...
    }
}


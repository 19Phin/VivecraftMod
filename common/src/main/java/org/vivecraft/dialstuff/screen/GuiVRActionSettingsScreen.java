package org.vivecraft.dialstuff.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.vivecraft.client.gui.framework.VROptionEntry;
import org.vivecraft.client_vr.provider.openxr.MCOpenXR;
import org.vivecraft.client_vr.settings.VRSettings;
import org.vivecraft.client_vr.settings.VRSettings.VrOptions;
import org.vivecraft.dialstuff.types.ActionBindingSettings;
import org.vivecraft.dialstuff.types.ControllerButton;

public class GuiVRActionSettingsScreen extends ClickableWidgetScreen {

    private static final VROptionEntry[] BUTTON_OPTIONS = new VROptionEntry[]{
        new VROptionEntry(VRSettings.VrOptions.DUMMY, true),
        new VROptionEntry(VrOptions.ACTION_MODE),
        new VROptionEntry(VrOptions.ACTION_TOUCH),
        new VROptionEntry(VrOptions.ACTION_HOLD_TOUCH),
        new VROptionEntry(VrOptions.ACTION_HAPTIC_AMPLITUDE),
        new VROptionEntry(VrOptions.ACTION_HAPTIC_DURATION),
        new VROptionEntry(VrOptions.ACTION_HAPTIC_FREQUENCY)
    };

    private static final VROptionEntry[] TRIGGER_OPTIONS = new VROptionEntry[]{
        new VROptionEntry(VrOptions.ACTION_ACTIVATION_THRESHOLD),
        new VROptionEntry(VrOptions.ACTION_DEACTIVATION_THRESHOLD)
    };

    private Button btnChange;
    private ControllerButton button;
    private final Component mappingLabel;
    public boolean mappingActive;
    ActionBindingSettings mapping;
    Runnable onClose;

    @Override
    public boolean isMappingActive() {
        return mappingActive;
    }
    @Override
    public void setButton(ControllerButton button) {
        this.button = button;
        if (button == null) {
            this.btnChange.setMessage(Component.literal("Not Bound"));
        } else {
            this.btnChange.setMessage(Component.literal(button.getFormattedName()));
        }
        mappingActive = false;
    }

    @Override
    protected boolean onDoneClicked() {
        if (button == null) {
            return super.onDoneClicked();
        }

        if(vrSettings.actionTouch) {
            button = button.ofKind(ControllerButton.InputKind.TOUCH);
        }
        mapping.update(
            button,
            vrSettings.actionMode,
            vrSettings.actionHoldTouch,
            vrSettings.actionHapticAmplitude,
            vrSettings.actionHapticDuration,
            vrSettings.actionHapticFrequency
        );
        onClose.run();
        return super.onDoneClicked();
    }

    public GuiVRActionSettingsScreen(Screen lastScreen, ActionBindingSettings settings, Runnable onClose) {
        super(lastScreen);
        this.mapping = settings;
        this.button = mapping.getControllerButton();
        vrSettings.actionMode = mapping.getActionMode();
        vrSettings.actionHoldTouch = mapping.isPressUntilNotTouch();
        vrSettings.actionHapticAmplitude = mapping.getHapticAmplitude();
        vrSettings.actionHapticDuration = mapping.getHapticDuration();
        vrSettings.actionHapticFrequency = mapping.getHapticFrequency();

        this.mappingLabel = Component.literal(settings.getControllerButton().getFormattedName());
        this.onClose = onClose;
    }

    @Override
    public void init() {
        this.vrTitle = "vivecraft.options.screen.actionsettings";
        super.init(BUTTON_OPTIONS, true);
        super.init(TRIGGER_OPTIONS, false);

        this.addRenderableWidget(
            this.btnChange = Button.builder(mappingLabel, (button) -> {
                mappingActive = true;
                MCOpenXR.get().skipNextInput = 2;
            })
                .bounds(width / 2 - 75, (int) Math.ceil((float) (height / 6) - 10.0F), 150, 20)
                .createNarration((supplier) ->
                    button == null
                        ? Component.translatable("narrator.controls.unbound", this.mappingLabel)
                        : Component.translatable("narrator.controls.bound", this.mappingLabel, supplier.get())
                )
                .build());

        this.addDefaultButtons();
        this.loadDefaults();
    }
}

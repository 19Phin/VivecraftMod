package org.vivecraft.dialstuff.screen;

import net.minecraft.client.gui.screens.Screen;
import org.vivecraft.client.gui.framework.GuiVROptionsBase;
import org.vivecraft.dialstuff.types.ControllerButton;

public abstract class ClickableWidgetScreen extends GuiVROptionsBase {

    public ClickableWidgetScreen(Screen lastScreen) {
        super(lastScreen);
    }

    public abstract boolean isMappingActive();
    public abstract void setButton(ControllerButton button);
    public void updateButtons() {}
}

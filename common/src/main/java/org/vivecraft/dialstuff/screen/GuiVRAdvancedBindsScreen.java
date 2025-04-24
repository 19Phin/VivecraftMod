package org.vivecraft.dialstuff.screen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.vivecraft.client.gui.framework.GuiVROptionsBase;
import org.vivecraft.dialstuff.types.ActionBindingSettings;

import java.util.List;

public class GuiVRAdvancedBindsScreen extends GuiVROptionsBase {

    private GuiVRAdvancedBindsList multiBindsList;
    private final KeyMapping mapping;
    private final List<ActionBindingSettings> buttons;

    public GuiVRAdvancedBindsScreen(Screen previous, KeyMapping mapping, List<ActionBindingSettings> buttons, Component label) {
        super(previous);
        this.vrTitle = label.getString();
        this.mapping = mapping;
        this.buttons = buttons;
    }

    @Override
    public void init() {
        super.init();
        if (multiBindsList == null) {
            this.multiBindsList = new GuiVRAdvancedBindsList(this, Minecraft.getInstance(), buttons, mapping);
            this.addRenderableWidget(this.multiBindsList);
        }
        this.visibleList = this.multiBindsList;
        this.addDefaultButtons();
    }

    @Override
    protected void loadDefaults() {
        this.multiBindsList.reloadRows();
    }

    @Override
    protected boolean onDoneClicked() {
        // Save changes to your manager, etc.
        return super.onDoneClicked();
    }
}

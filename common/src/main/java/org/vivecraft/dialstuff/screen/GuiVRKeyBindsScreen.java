package org.vivecraft.dialstuff.screen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.dialstuff.types.ActionBindingManager;
import org.vivecraft.dialstuff.types.ActionBindingSettings;
import org.vivecraft.dialstuff.types.ControllerButton;

import java.util.List;
import java.util.Map;

public class GuiVRKeyBindsScreen extends ClickableWidgetScreen {
    @Nullable
    private GuiVRKeyBindsList.BindRow activeMapping;
    private GuiVRKeyBindsList bindListWidget;
    private final Map<KeyMapping, List<ActionBindingSettings>> bindings;

    public GuiVRKeyBindsScreen(Screen previousMenu) {
        super(previousMenu);
        this.bindings = ActionBindingManager.makeBindingsByKey();
    }

    @Override
    protected void init() {
        this.vrTitle = "vivecraft.options.screen.bindings";
        super.init();
        this.bindListWidget = new GuiVRKeyBindsList(this, Minecraft.getInstance(), bindings);

        this.visibleList = this.bindListWidget;
        this.addRenderableWidget(this.bindListWidget);

        super.addDefaultButtons();
    }

    @Override
    protected void loadDefaults() {
        for (KeyMapping map : this.minecraft.options.keyMappings) {
            map.setKey(map.getDefaultKey());
        }
        this.bindListWidget.updateButtons();
    }

    @Override
    protected boolean onDoneClicked() {
        ActionBindingManager.updateBindings(bindings);
        return super.onDoneClicked();
    }

    public GuiVRKeyBindsList.BindRow getActiveMapping() {
        return this.activeMapping;
    }

    @Override
    public boolean isMappingActive() {
        return this.activeMapping != null;
    }

    @Override
    public void setButton(ControllerButton button) {
        this.activeMapping.setButton(button);
        activeMapping = null;
    }

    public void setActiveMapping(GuiVRKeyBindsList.BindRow mapping) {
        this.activeMapping = mapping;
    }

    @Override
    public void updateButtons() {
        bindListWidget.updateButtons();
    }
    //TODO 6
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.activeMapping != null) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        boolean foundNonDefaults = false;
        for (KeyMapping map : this.minecraft.options.keyMappings) {
            if (!map.isDefault()) {
                foundNonDefaults = true;
                break;
            }
        }
        if (this.btnDefaults != null) {
            this.btnDefaults.active = foundNonDefaults;
        }

    }
}

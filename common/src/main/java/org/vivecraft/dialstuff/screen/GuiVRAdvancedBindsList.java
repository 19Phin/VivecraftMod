package org.vivecraft.dialstuff.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.vivecraft.client_vr.provider.MCVR;
import org.vivecraft.dialstuff.types.ActionBindingSettings;
import org.vivecraft.dialstuff.types.ControllerButton;

import java.util.List;

public class GuiVRAdvancedBindsList extends ContainerObjectSelectionList<GuiVRAdvancedBindsList.AbstractRow> {

    private static final int ROW_HEIGHT = 20;

    private final GuiVRAdvancedBindsScreen parentScreen;
    private final List<ActionBindingSettings> buttons;
    private final net.minecraft.client.KeyMapping singleKeyMapping;

    public GuiVRAdvancedBindsList(GuiVRAdvancedBindsScreen parent,
        Minecraft minecraft,
        List<ActionBindingSettings> buttons,
        net.minecraft.client.KeyMapping targetMapping) {
        super(minecraft, parent.width, parent.height - 66, 33, ROW_HEIGHT);

        this.parentScreen = parent;
        this.singleKeyMapping = targetMapping;
        this.buttons = buttons;

        reloadRows();
    }


    public void reloadRows() {
        this.clearEntries();

        this.addEntry(new NewBindingRow());

        for (ActionBindingSettings binding : buttons) {
            this.addEntry(new ActionBindingRow(binding));
        }
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    public void updateEntries() {
        for (AbstractRow row : this.children()) {
            row.refreshRow();
        }
    }

    public static abstract class AbstractRow extends ContainerObjectSelectionList.Entry<AbstractRow> {
        abstract void refreshRow();
    }

    public class NewBindingRow extends AbstractRow {
        private final Button newBindingButton;

        public NewBindingRow() {
            this.newBindingButton = Button.builder(
                Component.literal("New Binding"),
                b -> {
                    ActionBindingSettings newSet = new ActionBindingSettings(
                        ControllerButton.A_CLICK,
                        MCVR.get().getInputAction(singleKeyMapping.getName())
                    );
                    buttons.add(newSet);
                    minecraft.setScreen(new GuiVRActionSettingsScreen(
                        GuiVRAdvancedBindsList.this.parentScreen,
                        newSet,
                        GuiVRAdvancedBindsList.this::reloadRows));
                }
            ).bounds(0, 0, 100, 20).build();
        }

        @Override
        public void render(
            GuiGraphics guiGraphics,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean isHovered,
            float partialTick
        ) {
            this.newBindingButton.setPosition(left + 10, top);
            this.newBindingButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(newBindingButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(newBindingButton);
        }

        @Override
        void refreshRow() {
        }
    }

    public class ActionBindingRow extends AbstractRow {
        private final ActionBindingSettings binding;
        private final Button openSettingsButton;
        private final Button deleteButton;

        public ActionBindingRow(ActionBindingSettings binding) {
            this.binding = binding;
            this.openSettingsButton = Button.builder(
                Component.literal("Config"),
                b -> {
                    minecraft.setScreen(new GuiVRActionSettingsScreen(
                        GuiVRAdvancedBindsList.this.parentScreen,
                        binding,
                        GuiVRAdvancedBindsList.this::reloadRows));
                }
            ).bounds(0, 0, 60, 20).build();
            this.deleteButton = Button.builder(
                Component.translatable("selectServer.delete"),
                b -> {
                    GuiVRAdvancedBindsList.this.buttons.remove(binding);
                    GuiVRAdvancedBindsList.this.reloadRows();
                }
            ).bounds(0, 0, 60, 20).build();
        }

        @Override
        public void render(
            GuiGraphics guiGraphics,
            int index,
            int top,
            int left,
            int width,
            int height,
            int mouseX,
            int mouseY,
            boolean isHovered,
            float partialTick
        ) {
            String label;
            if (binding.getControllerButton() != null) {
                label = "Binding: " + (binding.getControllerButton().getFormattedName());
            } else {
                label = "Binding: null";
            }
            guiGraphics.drawString(minecraft.font, label, left, top + 6, 0xCCCCCC);

            this.openSettingsButton.setPosition(left + 120, top);
            this.openSettingsButton.render(guiGraphics, mouseX, mouseY, partialTick);
            this.deleteButton.setPosition(left + 200, top);
            this.deleteButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.openSettingsButton, this.deleteButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.openSettingsButton, this.deleteButton);
        }

        @Override
        void refreshRow() {
        }
    }
}

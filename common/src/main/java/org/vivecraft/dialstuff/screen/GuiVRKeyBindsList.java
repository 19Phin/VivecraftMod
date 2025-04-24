package org.vivecraft.dialstuff.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.client.VivecraftVRMod;
import org.vivecraft.client_vr.provider.MCVR;
import org.vivecraft.client_vr.provider.openxr.MCOpenXR;
import org.vivecraft.dialstuff.types.ActionBindingSettings;
import org.vivecraft.dialstuff.types.ControllerButton;

import java.util.*;
import java.util.stream.Stream;

public class GuiVRKeyBindsList extends ContainerObjectSelectionList<GuiVRKeyBindsList.Row> {
    private static final int ROW_HEIGHT = 20;

    final GuiVRKeyBindsScreen ownerScreen;
    private int widestLabel;

    public GuiVRKeyBindsList(GuiVRKeyBindsScreen parent, Minecraft minecraft, Map<KeyMapping, List<ActionBindingSettings>> bindings) {
        super(minecraft, parent.width, parent.height - 66, 33, ROW_HEIGHT);
        this.ownerScreen = parent;

        String lastCategory = null;
        for (KeyMapping mapping : Stream.concat(Arrays.stream(minecraft.options.keyMappings),
                                    VivecraftVRMod.INSTANCE.getHiddenKeyBindings().stream()).toList()) {
            String cat = mapping.getCategory();
            if (!Objects.equals(cat, lastCategory)) {
                lastCategory = cat;
                this.addEntry(new CategoryRow(Component.translatable(cat)));
            }

            Component mappingLabel = Component.translatable(mapping.getName());
            int labelWidth = minecraft.font.width(mappingLabel);
            if (labelWidth > this.widestLabel) {
                this.widestLabel = labelWidth;
            }
            this.addEntry(new BindRow(mapping, bindings.computeIfAbsent(mapping, key -> new ArrayList<>()), mappingLabel));
        }
    }

    public void updateButtons() {
        this.refreshRows();
    }

    public void refreshRows() {
        for (Row row : this.children()) {
            row.refreshRow();
        }
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    public static abstract class Row extends ContainerObjectSelectionList.Entry<Row> {
        abstract void refreshRow();
    }
    
    public class CategoryRow extends Row {
        private final Component categoryName;
        private final int textWidth;

        public CategoryRow(Component categoryName) {
            this.categoryName = categoryName;
            this.textWidth = GuiVRKeyBindsList.this.minecraft.font.width(this.categoryName);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isHovered, float partialTick) {
            Font fontObj = GuiVRKeyBindsList.this.minecraft.font;
            int centerX = GuiVRKeyBindsList.this.width / 2 - (this.textWidth / 2);
            guiGraphics.drawString(fontObj, this.categoryName, centerX, top + height - 9 - 1, 0xFFFFFF);
        }

        @Nullable
        @Override
        public net.minecraft.client.gui.ComponentPath nextFocusPath(FocusNavigationEvent event) {
            return null;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                @Override
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput output) {
                    output.add(NarratedElementType.TITLE, categoryName);
                }
            });
        }

        @Override
        void refreshRow() {}
    }

    public class BindRow extends Row {
        private static final Component LABEL_RESET = Component.translatable("controls.reset");
        private static final Component ADVANCED_LABEL = Component.literal("Adv.");
        private final KeyMapping targetMapping;
        private final Component mappingLabel;
        private final List<ActionBindingSettings> buttons;

        private final Button btnChange;
        private final Button btnReset;
        private final Button btnAdvanced;
        private boolean conflictsFound;

        BindRow(KeyMapping mapping, List<ActionBindingSettings> buttons, Component label) {
            this.targetMapping = mapping;
            this.mappingLabel = label;
            this.buttons = buttons;

            this.btnChange = Button.builder(label, (button) -> {
                    GuiVRKeyBindsList.this.ownerScreen.setActiveMapping(this);
                    GuiVRKeyBindsList.this.updateButtons();
                    MCOpenXR.get().skipNextInput = 2;
                })
                .bounds(0, 0, 75, 20)
                .createNarration((supplier) -> {
                        if (buttons.size() == 1) {
                            return Component.translatable("narrator.controls.bound", this.mappingLabel, supplier.get());
                        } else if (buttons.isEmpty()) {
                            return Component.translatable("narrator.controls.unbound", this.mappingLabel, supplier.get());
                        } else {
                            return Component.translatable("narrator.controls.advanced", this.mappingLabel, supplier.get());
                        }
                    }
                )
                .build();

            this.btnReset = Button.builder(LABEL_RESET, (button) -> {
                    //TODO this
                    GuiVRKeyBindsList.this.updateButtons();
                })
                .bounds(0, 0, 50, 20)
                .createNarration((supplier) ->
                    Component.translatable("narrator.controls.reset", this.mappingLabel)
                )
                .build();

            this.btnAdvanced = Button.builder(ADVANCED_LABEL, (button) -> {
                    Minecraft.getInstance().setScreen(new GuiVRAdvancedBindsScreen(
                        GuiVRKeyBindsList.this.ownerScreen, targetMapping, buttons, mappingLabel));
                })
                .bounds(0, 0, 40, 20)
                .createNarration((supplier) ->
                    Component.translatable("narrator.controls.advanced", this.mappingLabel)
                )
                .build();

            this.refreshRow();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
            int mouseX, int mouseY, boolean isHovered, float partialTick) {

            int buttonX = GuiVRKeyBindsList.this.scrollBarX() - this.btnAdvanced.getWidth() - 10;
            int buttonY = top - 2;
            this.btnAdvanced.setPosition(buttonX, buttonY);
            this.btnAdvanced.render(guiGraphics, mouseX, mouseY, partialTick);

            buttonX = buttonX - 5 - this.btnReset.getWidth();
            this.btnReset.setPosition(buttonX, buttonY);
            this.btnReset.render(guiGraphics, mouseX, mouseY, partialTick);

            buttonX = buttonX - 5 - this.btnChange.getWidth();
            this.btnChange.setPosition(buttonX, buttonY);
            this.btnChange.render(guiGraphics, mouseX, mouseY, partialTick);

            Font fontObj = GuiVRKeyBindsList.this.minecraft.font;
            guiGraphics.drawString(
                fontObj,
                this.mappingLabel,
                left,
                top + (height / 2) - (9 / 2),
                0xFFFFFF
            );

            if (this.conflictsFound) {
                int barX = this.btnChange.getX() - 6;
                guiGraphics.fill(barX, top - 1, barX + 3, top + height, 0xFFFF0000);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.btnChange, this.btnReset, this.btnAdvanced);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.btnChange, this.btnReset, this.btnAdvanced);
        }

        @Override
        void refreshRow() {
            if (buttons.isEmpty()) {
                this.btnChange.setMessage(Component.literal("Not Bound"));
            } else if (buttons.size() == 1) {
                String text = buttons.getFirst().getControllerButton().getFormattedName();
                this.btnChange.setMessage(Component.literal(text));
            } else {
                this.btnChange.setMessage(Component.literal("[Adv.]"));
            }
            this.btnReset.active = !this.targetMapping.isDefault();
            this.conflictsFound = false;

            MutableComponent collisionMsg = Component.empty();
            if (!this.targetMapping.isUnbound()) {
                for (KeyMapping otherMapping : GuiVRKeyBindsList.this.minecraft.options.keyMappings) {
                    if (otherMapping != this.targetMapping && this.targetMapping.same(otherMapping)) {
                        if (this.conflictsFound) {
                            collisionMsg.append(", ");
                        }
                        this.conflictsFound = true;
                        collisionMsg.append(Component.translatable(otherMapping.getName()));
                    }
                }
            }

            if (this.conflictsFound) {
                this.btnChange.setMessage(
                    Component.literal("[ ")
                        .append(this.btnChange.getMessage().copy().withStyle(ChatFormatting.WHITE))
                        .append(" ]")
                        .withStyle(ChatFormatting.RED)
                );
                this.btnChange.setTooltip(
                    Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", collisionMsg))
                );
            } else {
                this.btnChange.setTooltip(null);
            }

            if (GuiVRKeyBindsList.this.ownerScreen.getActiveMapping() == this) {
                this.btnChange.setMessage(
                    Component.literal("> ")
                        .append(this.btnChange.getMessage().copy()
                            .withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                        .append(" <")
                        .withStyle(ChatFormatting.YELLOW)
                );
            }
        }

        public void setButton(ControllerButton button) {
            if (buttons.isEmpty()) {
                buttons.add(new ActionBindingSettings(button, MCVR.get().getInputAction(targetMapping)));
            } else if (buttons.size() == 1) {
                this.buttons.getFirst().setControllerButton(button);
            } else {
                throw new RuntimeException();
            }
        }
    }
}

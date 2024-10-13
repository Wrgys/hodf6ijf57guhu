package com.wrgy.customportalmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wrgy.customportalmod.client.CustomCommandBlockScreen;
import com.wrgy.customportalmod.client.ForcedConvert;
import com.wrgy.customportalmod.utils.IEditBoxMixin;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.wrgy.customportalmod.CustomPortalMod.WRGY_isAutoEnabled_39058;
import static com.wrgy.customportalmod.client.CustomCommandBlockScreen.transformCommand;
import static com.wrgy.customportalmod.client.ForcedConvert.forceTransformCommand;

@Mixin(CommandBlockEditScreen.class)
public abstract class CommandBlockEditScreenMixin extends AbstractCommandBlockEditScreen {

    @Shadow
    @Final
    private CommandBlockEntity autoCommandBlock;
    @Unique
    private Button transformButton;
    @Unique
    private Button forceTransformButton;
    @Unique
    private Button toggleAutoButton;
    @Unique
    private static final ResourceLocation AUTO_DISABLED_TEXTURE = new ResourceLocation("customportalmod:textures/custom/togglebutton_auto_disabled.png");
    @Unique
    private static final ResourceLocation AUTO_ENABLED_TEXTURE = new ResourceLocation("customportalmod:textures/custom/togglebutton_auto_enabled.png");
    @Unique
    private static final ResourceLocation AUTO_DISABLED_HOVER_TEXTURE = new ResourceLocation("customportalmod:textures/custom/togglebutton_auto_disabled_hovered.png");
    @Unique
    private static final ResourceLocation AUTO_ENABLED_HOVER_TEXTURE = new ResourceLocation("customportalmod:textures/custom/togglebutton_auto_enabled_hovered.png");
    @Unique
    public boolean autoOriginalState = WRGY_isAutoEnabled_39058;


    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo info) {
        CommandBlockEditScreen screen = (CommandBlockEditScreen) (Object) this;
        IEditBoxMixin mixin = (IEditBoxMixin) this.commandEdit;
        this.forceTransformButton = new Button(410, 198, 20, 20, new TextComponent("F"), button -> {
            BlockPos blockPos = autoCommandBlock.getBlockPos();
            int[] blockPosition = new int[] {blockPos.getX(),blockPos.getY(),blockPos.getZ()};
            String command = this.commandEdit.getValue();
            String highlightedText = this.commandEdit.getHighlighted();
            int highlightedStartPos = mixin.getHighlightedStartLocation();
            String transformedCommand = forceTransformCommand(command, blockPosition, highlightedText, highlightedStartPos);
            this.commandEdit.setValue(transformedCommand);
        });

        this.transformButton = new Button(166, 198, 20, 20, new TextComponent("Auto: REL"), button -> {
            BlockPos blockPos = autoCommandBlock.getBlockPos();
            int[] blockPosition = new int[] {blockPos.getX(),blockPos.getY(),blockPos.getZ()};
            String command = this.commandEdit.getValue();
            String highlightedText = this.commandEdit.getHighlighted();
            int highlightedStartPos = mixin.getHighlightedStartLocation();
            String transformedCommand = transformCommand(command, blockPosition, highlightedText, highlightedStartPos, false);
            this.commandEdit.setValue(transformedCommand);
        });

        this.toggleAutoButton = new Button(434, 198, 40, 20, null, button -> {
            WRGY_isAutoEnabled_39058 = !WRGY_isAutoEnabled_39058;
        }) {
            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
                if (this.visible) {
                    boolean isHovered = isMouseOver(mouseX, mouseY);
                    ResourceLocation texture = (WRGY_isAutoEnabled_39058 ? (isHovered ? AUTO_ENABLED_HOVER_TEXTURE : AUTO_ENABLED_TEXTURE) : (isHovered ? AUTO_DISABLED_HOVER_TEXTURE : AUTO_DISABLED_TEXTURE));
                    RenderSystem.setShaderTexture(0, texture);
                    blit(poseStack, this.x, this.y, 0, 0, this.width, this.height, 40, 20);
                }
            }

            @Override
            public boolean isMouseOver(double mouseX, double mouseY) {
                return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
            }
        };

        // Add the buttons to the screen
        screen.addRenderableWidget(this.transformButton);
        screen.addRenderableWidget(this.toggleAutoButton);
        screen.addRenderableWidget(this.forceTransformButton);
    }

    @Override
    public void onClose() {
        WRGY_isAutoEnabled_39058 = autoOriginalState;
        super.onClose();
    }
    @Override
    protected void onDone() {
        if (WRGY_isAutoEnabled_39058) {
            BlockPos blockPos = autoCommandBlock.getBlockPos();
            int[] blockPosition = new int[] {blockPos.getX(),blockPos.getY(),blockPos.getZ()};
            String command = this.commandEdit.getValue();
            String transformedCommand = transformCommand(command, blockPosition, "", 0, true);
            transformedCommand = transformCommand(transformedCommand, blockPosition, "", 0, false);
            this.commandEdit.setValue(transformedCommand);
        }
        super.onDone();
    }
}

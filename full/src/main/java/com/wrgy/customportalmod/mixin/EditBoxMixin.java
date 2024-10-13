package com.wrgy.customportalmod.mixin;

import com.wrgy.customportalmod.utils.IEditBoxMixin;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EditBox.class)
public abstract class EditBoxMixin implements IEditBoxMixin {
    @Shadow
    private int cursorPos;
    @Shadow
    private int highlightPos;
    @Override
    public int getHighlightedStartLocation() {
        int pos = Math.min(this.cursorPos, this.highlightPos);
        return pos;
    }
}

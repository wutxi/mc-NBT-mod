package com.example.copynbt.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    // Слот, на который наведён курсор (нужен, чтобы знать, какой предмет копировать)
    @Accessor("focusedSlot")
    Slot getFocusedSlot();
}

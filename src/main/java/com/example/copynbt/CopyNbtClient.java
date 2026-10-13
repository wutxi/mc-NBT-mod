package com.example.copynbt;

import com.example.copynbt.mixin.HandledScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CopyNbtClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Клавиша по умолчанию — C, можно переназначить в настройках управления
        KeyBinding copyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.copynbt.copy",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "key.categories.misc"
        ));

        // Вне экранов: копируем предмет из основной руки
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (copyKey.wasPressed()) {
                if (client.currentScreen == null && client.player != null) {
                    copy(client, client.player.getMainHandStack());
                }
            }
        });

        // В инвентаре / сундуке: копируем предмет под курсором
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!(screen instanceof HandledScreen<?> handled)) {
                return;
            }
            ScreenKeyboardEvents.afterKeyPress(screen).register((s, key, scancode, modifiers) -> {
                if (!copyKey.matchesKey(key, scancode)) {
                    return;
                }
                Slot slot = ((HandledScreenAccessor) handled).getFocusedSlot();
                MinecraftClient mc = MinecraftClient.getInstance();
                if (slot == null) {
                    if (mc.player != null) {
                        mc.player.sendMessage(Text.literal("Наведите курсор на предмет"), true);
                    }
                    return;
                }
                copy(mc, slot.getStack());
            });
        });
    }

    private static void copy(MinecraftClient client, ItemStack stack) {
        if (client.player == null || client.world == null) {
            return;
        }
        if (stack.isEmpty()) {
            client.player.sendMessage(Text.literal("Предмет пустой"), true);
            return;
        }

        // Сериализуем весь предмет (id, count, components) в SNBT-строку
        ItemStack.CODEC.encodeStart(client.world.getRegistryManager().getOps(NbtOps.INSTANCE), stack)
                .result()
                .ifPresentOrElse(nbt -> {
                    client.keyboard.setClipboard(nbt.toString());
                    client.player.sendMessage(Text.literal("NBT предмета скопирован в буфер обмена"), true);
                }, () -> client.player.sendMessage(Text.literal("Не удалось сериализовать предмет"), true));
    }
}

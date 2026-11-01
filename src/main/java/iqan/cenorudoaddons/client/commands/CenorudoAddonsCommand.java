package iqan.cenorudoaddons.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import iqan.cenorudoaddons.client.MoneyMakersKeyBindings;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class CenorudoAddonsCommand {
  public static void register(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(
        ClientCommands.literal("cenorudoaddons").executes(ctx -> {
          Minecraft client = Minecraft.getInstance();
          // Abre a tela de config no proximo tick (fora da execucao do comando)
          client.execute(() -> MoneyMakersKeyBindings.openConfigScreen(client));
          return 1;
        }));
  }
}

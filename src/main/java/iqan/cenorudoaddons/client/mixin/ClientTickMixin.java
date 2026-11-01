package iqan.cenorudoaddons.client.mixin;

import iqan.cenorudoaddons.client.events.Event;
import iqan.cenorudoaddons.client.events.EventBus;
import iqan.cenorudoaddons.client.events.impl.ClientTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class ClientTickMixin {
  @Inject(method = {"tick"}, at = {@At("HEAD")})
  private void iq$onTick(CallbackInfo ci) {
    EventBus.post((Event)ClientTickEvent.create((Minecraft)(Object)this));
  }
}



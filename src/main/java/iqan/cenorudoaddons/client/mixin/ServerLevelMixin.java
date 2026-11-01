package iqan.cenorudoaddons.client.mixin;

import iqan.cenorudoaddons.client.events.Event;
import iqan.cenorudoaddons.client.events.EventBus;
import iqan.cenorudoaddons.client.events.impl.EntitySpawnEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ServerLevel.class})
public class ServerLevelMixin
{
  @Inject(method = {"addFreshEntity"}, at = {@At("TAIL")})
  private void onSpawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
    if (!((Boolean)cir.getReturnValue()).booleanValue()) {
      return;
    }
    
    EventBus.post((Event)new EntitySpawnEvent(entity));
  }
}



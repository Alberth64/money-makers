package iqan.cenorudoaddons.client.events.impl;

import iqan.cenorudoaddons.client.events.Event;
import net.minecraft.world.entity.Entity;

public final class EntitySpawnEvent
  implements Event {
  private final Entity entity;
  
  public EntitySpawnEvent(Entity entity) {
    this.entity = entity;
  }
  
  public Entity entity() {
    return this.entity;
  }
}



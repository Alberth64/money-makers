package iqan.cenorudoaddons.client.events.impl;

import iqan.cenorudoaddons.client.events.Event;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ClientTickEvent
  implements Event
{
  private final Minecraft client;
  private final long tickCount;
  
  public ClientTickEvent(Minecraft client, long tickCount) {
    this.client = client;
    this.tickCount = tickCount;
  }
  
  public Minecraft client() {
    return this.client;
  }
  
  public long tickCount() {
    return this.tickCount;
  }
  
  private static final AtomicLong TICK_COUNTER = new AtomicLong(0L);
  
  @Contract("_ -> new")
  @NotNull
  public static ClientTickEvent create(Minecraft client) {
    return new ClientTickEvent(client, TICK_COUNTER.incrementAndGet());
  }
  
  public boolean isNthTick(int n) {
    return (n > 0 && this.tickCount % n == 0L);
  }
  
  public boolean isInGame() {
    return (this.client.player != null && this.client.level != null);
  }
}



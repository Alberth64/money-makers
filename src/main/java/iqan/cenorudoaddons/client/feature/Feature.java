package iqan.cenorudoaddons.client.feature;

import iqan.cenorudoaddons.client.events.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class Feature
{
  protected final Logger log;
  protected static final Minecraft client = Minecraft.getInstance();
  
  private final String id;
  
  private final String name;
  private final BooleanSupplier enabledSupplier;
  private final AtomicBoolean active = new AtomicBoolean(false);
  
  private final List<EventBus.Subscription<?>> subscriptions = new ArrayList<>();




  
  protected Feature(@NotNull String id, @NotNull String name, @NotNull BooleanSupplier enabledSupplier) {
    this.id = id;
    this.name = name;
    this.enabledSupplier = enabledSupplier;
    this.log = LoggerFactory.getLogger("Feature-" + name);
  }
  
  public final String getId() {
    return this.id;
  }
  
  public final String getName() {
    return this.name;
  }
  
  public final boolean isEnabled() {
    return this.enabledSupplier.getAsBoolean();
  }
  
  public final boolean isActive() {
    return this.active.get();
  }
  
  public final boolean activate() {
    if (!this.active.compareAndSet(false, true)) {
      this.log.debug("Feature {} already active", this.name);
      return false;
    } 
    
    this.log.info("Activating feature: {}", this.name);
    
    try {
      onActivate();
    } catch (Exception e) {
      this.log.error("Error activating feature {}", this.name, e);
      this.active.set(false);
      cleanupSubscriptions();
      return false;
    } 
    
    return true;
  }
  
  public final boolean deactivate() {
    if (!this.active.compareAndSet(true, false)) {
      this.log.debug("Feature {} already inactive", this.name);
      return false;
    } 
    
    this.log.info("Deactivating feature: {}", this.name);
    
    cleanupSubscriptions();
    
    try {
      onDeactivate();
    } catch (Exception e) {
      this.log.error("Error deactivating feature {}", this.name, e);
    } 
    
    return true;
  }



  
  protected <T extends iqan.cenorudoaddons.client.events.Event> void subscribe(@NotNull Class<T> eventClass, @NotNull Consumer<T> handler) {
    EventBus.Subscription<T> subscription = EventBus.subscribe(eventClass, handler);
    this.subscriptions.add(subscription);
  }
  
  protected void subscribe(@NotNull EventBus.Subscription<?> subscription) {
    this.subscriptions.add(subscription);
  }
  
  private void cleanupSubscriptions() {
    for (EventBus.Subscription<?> subscription : this.subscriptions) {
      try {
        subscription.unsubscribe();
      } catch (Exception e) {
        this.log.warn("Error unsubscribing", e);
      } 
    } 
    
    this.subscriptions.clear();
  }
  
  protected void onActivate() {}
  
  protected void onDeactivate() {}
}



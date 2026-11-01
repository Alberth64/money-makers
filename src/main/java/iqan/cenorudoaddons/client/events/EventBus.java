package iqan.cenorudoaddons.client.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class EventBus
{
  private static final Logger log = LoggerFactory.getLogger(EventBus.class);
  
  private static final Map<Class<? extends Event>, List<Subscription<?>>> SUBSCRIPTIONS = new ConcurrentHashMap<>();








  
  @NotNull
  public static <T extends Event> Subscription<T> subscribe(@NotNull Class<T> eventType, @NotNull Consumer<T> handler) {
    Subscription<T> subscription = new Subscription<>(eventType, handler);
    
    ((List<Subscription<T>>)SUBSCRIPTIONS
      .computeIfAbsent(eventType, k -> new CopyOnWriteArrayList()))
      .add(subscription);
    
    log.debug("Subscribed to event: {}", eventType.getSimpleName());
    return subscription;
  }
  
  public static void unsubscribe(@NotNull Subscription<?> subscription) {
    List<Subscription<?>> list = SUBSCRIPTIONS.get(subscription.eventClass());
    
    if (list != null) {
      list.remove(subscription);
      log.debug("Unsubscribed from event: {}", subscription.eventClass().getSimpleName());
    } 
  }
  
  @NotNull
  public static <T extends Event> T post(@NotNull T event) {
    List<Subscription<?>> list = SUBSCRIPTIONS.get(event.getClass());
    
    if (list == null || list.isEmpty()) {
      return event;
    }
    
    boolean cancellable = event instanceof Cancellable;
    
    for (Subscription<?> sub : list) {
      if (cancellable && ((Cancellable)event).isCancelled()) {
        break;
      }


      
      try {
        Subscription<T> typed = (Subscription)sub;
        
        typed.handler().accept(event);
      }
      catch (Exception e) {
        log.error("Error handling event: {}", event.getClass().getSimpleName(), e);
      } 
    } 
    
    return event;
  }
  public record Subscription<T extends Event>(Class<T> eventClass, Consumer<T> handler) {
    public void unsubscribe() {
      EventBus.unsubscribe(this);
    }
  }
}

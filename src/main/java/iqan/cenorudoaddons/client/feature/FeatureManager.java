package iqan.cenorudoaddons.client.feature;
import iqan.cenorudoaddons.client.events.EventBus;
import iqan.cenorudoaddons.client.events.impl.ClientTickEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FeatureManager {
  private static final Logger log = LoggerFactory.getLogger(FeatureManager.class);
  
  private static final FeatureManager INSTANCE = new FeatureManager();
  
  private static final int SYNC_INTERVAL_TICKS = 20;
  
  private final Map<String, Feature> features = new LinkedHashMap<>();


  
  private EventBus.Subscription<ClientTickEvent> tickSubscription;


  
  public static FeatureManager get() {
    return INSTANCE;
  }
  
  public void register(@NotNull Feature... features) {
    for (Feature feature : features) {
      this.features.put(feature.getId(), feature);
      log.debug("Feature registrada: {}", feature.getName());
    } 
  }
  
  public void start() {
    this.tickSubscription = EventBus.subscribe(ClientTickEvent.class, this::onTick);
    
    syncAllFeatures();
    
    log.info("FeatureManager iniciado com {} features", Integer.valueOf(this.features.size()));
  }
  
  public void stop() {
    if (this.tickSubscription != null) {
      this.tickSubscription.unsubscribe();
      this.tickSubscription = null;
    } 
    
    this.features.values().forEach(Feature::deactivate);
    
    log.info("FeatureManager parado");
  }
  
  @Nullable
  public Feature get(@NotNull String id) {
    return this.features.get(id);
  }

  
  @Nullable
  public <T extends Feature> T get(@NotNull Class<T> type) {
    Objects.requireNonNull(type);


    
    Objects.requireNonNull(type); return (T)this.features.values().stream().filter(type::isInstance)
      .findFirst()
      .orElse(null);
  }
  
  @NotNull
  public Collection<Feature> all() {
    return Collections.unmodifiableCollection(this.features.values());
  }
  
  private void onTick(@NotNull ClientTickEvent event) {
    if (event.isNthTick(20)) {
      syncAllFeatures();
    }
  }
  
  private void syncAllFeatures() {
    for (Feature feature : this.features.values()) {
      
      boolean shouldBeActive = feature.isEnabled();
      
      if (shouldBeActive && !feature.isActive()) {
        feature.activate(); continue;
      }  if (!shouldBeActive && feature.isActive())
        feature.deactivate(); 
    } 
  }
}



package iqan.cenorudoaddons.client.utils;

import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelTerrainRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RotationUtils
{
  private static final Logger log = LoggerFactory.getLogger(RotationUtils.class);
  
  private static final float OVERSHOOT_SWING = 1.5F;
  
  private static final Minecraft mc = Minecraft.getInstance();
  
  private static volatile boolean initialized = false;
  
  private static volatile boolean rotating = false;
  
  private static volatile boolean backRotation = false;
  
  private static long startTimeMs;
  
  private static long durationMs;
  
  private static float startYaw;
  private static float startPitch;
  private static float yawDelta;
  private static float pitchDelta;
  private static boolean easeOutBack;
  
  public static boolean isRotating() {
    return rotating;
  }
  
  public static void init() {
    if (initialized)
      return;  initialized = true;
    
    LevelRenderEvents.START_MAIN.register(context -> update());
  }
  
  public static void stop() {
    rotating = false;
    backRotation = false;
  }
  
  public static boolean smoothRotate(float targetYaw, float targetPitch) {
    return smoothRotate(targetYaw, targetPitch, 250, 450, false, true);
  }
  
  public static boolean smoothRotate(float targetYaw, float targetPitch, int minTimeMs, int maxTimeMs) {
    return smoothRotate(targetYaw, targetPitch, minTimeMs, maxTimeMs, false, true);
  }







  
  public static boolean smoothRotate(float targetYaw, float targetPitch, int minTimeMs, int maxTimeMs, boolean isBackRotation, boolean easeOutBack) {
    LocalPlayer player = mc.player;
    
    if (player == null || mc.gui.screen() != null) {
      return false;
    }
    log.info("targetYaw={}, targetPitch={}", Float.valueOf(targetYaw), Float.valueOf(targetPitch));
    
    if (Math.abs(targetYaw) > 180.0F || Math.abs(targetPitch) > 90.0F) {
      return false;
    }
    startYaw = player.getYRot();
    startPitch = player.getXRot();
    
    yawDelta = wrapDegrees(targetYaw - startYaw);
    pitchDelta = targetPitch - startPitch;
    
    float totalDistance = (float)Math.sqrt((yawDelta * yawDelta + pitchDelta * pitchDelta));
    float distanceRatio = Math.min(totalDistance, 180.0F) / 180.0F;
    
    int min = Math.max(minTimeMs, 0);
    int max = Math.max(maxTimeMs, min);
    
    double jitter = ThreadLocalRandom.current().nextDouble(0.85D, 1.15D);
    
    durationMs = Math.max(1L, 
        
        Math.round((min + distanceRatio * (max - min)) * jitter));

    
    startTimeMs = System.currentTimeMillis();
    rotating = true;
    backRotation = isBackRotation;
    RotationUtils.easeOutBack = easeOutBack;
    
    return true;
  }
  
  private static void update() {
    if (!rotating)
      return; 
    LocalPlayer player = mc.player;
    
    if (player == null || mc.gui.screen() != null) {
      stop();
      
      return;
    } 
    long elapsed = System.currentTimeMillis() - startTimeMs;
    
    if (elapsed >= durationMs) {
      player.setYRot(startYaw + yawDelta);
      player.setXRot(clampPitch(startPitch + pitchDelta));
      stop();
      
      return;
    } 
    float t = (float)elapsed / (float)durationMs;
    float ease = calculateEase(t, backRotation, easeOutBack);
    
    player.setYRot(startYaw + yawDelta * ease);
    player.setXRot(clampPitch(startPitch + pitchDelta * ease));
  }
  
  private static float calculateEase(float t, boolean backRotation, boolean easeOutBack) {
    if (backRotation) {
      return (t == 1.0F) ? 
        1.0F : 
        (float)(1.0D - Math.pow(2.0D, -10.0D * t));
    }
    if (easeOutBack) {
      float tm1 = t - 1.0F;
      return tm1 * tm1 * (2.5F * tm1 + 1.5F) + 1.0F;
    } 
    
    return 1.0F - (float)Math.pow((1.0F - t), 4.0D);
  }
  
  private static float wrapDegrees(float angle) {
    float wrapped = angle % 360.0F;
    
    if (wrapped >= 180.0F) wrapped -= 360.0F; 
    if (wrapped < -180.0F) wrapped += 360.0F;
    
    return wrapped;
  }
  
  private static float clampPitch(float pitch) {
    return Mth.clamp(pitch, -90.0F, 90.0F);
  }
  
  public static Vec3 calculateRotationToEntity(Entity target) {
    LocalPlayer player = mc.player;
    
    if (player == null) {
      return null;
    }
    Vec3 eyePos = player.getEyePosition();
    
    Vec3 targetPos = target.position().add(0.0D, (target
        
        .getBbHeight() * 0.5F), 0.0D);


    
    double dx = targetPos.x - eyePos.x;
    double dy = targetPos.y - eyePos.y;
    double dz = targetPos.z - eyePos.z;
    
    double flatDistance = Math.sqrt(dx * dx + dz * dz);
    
    float targetYaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
    float targetPitch = (float)-Math.toDegrees(Math.atan2(dy, flatDistance));
    
    return new Vec3(
        Mth.wrapDegrees(targetYaw), 
        Mth.clamp(targetPitch, -90.0F, 90.0F), 0.0D);
  }
}



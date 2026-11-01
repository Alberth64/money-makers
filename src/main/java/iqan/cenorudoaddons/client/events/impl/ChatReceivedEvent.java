package iqan.cenorudoaddons.client.events.impl;

import iqan.cenorudoaddons.client.events.Cancellable;
import iqan.cenorudoaddons.client.events.Event;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChatReceivedEvent
  implements Event, Cancellable
{
  private final Component text;
  private final String message;
  private final String strippedMessage;
  private boolean cancelled;
  
  public ChatReceivedEvent(@NotNull Component text) {
    this.text = text;
    this.message = text.getString();
    this.strippedMessage = stripFormatting(this.message);
  }
  
  public Component getText() {
    return this.text;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public String getStrippedMessage() {
    return this.strippedMessage;
  }
  
  public boolean contains(@NotNull String str) {
    return this.strippedMessage.toLowerCase().contains(str.toLowerCase());
  }
  
  public boolean startsWith(String prefix) {
    return this.strippedMessage.startsWith(prefix);
  }

  
  public boolean isCancelled() {
    return this.cancelled;
  }

  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  @Contract(pure = true)
  @NotNull
  private static String stripFormatting(String text) {
    if (text == null) return "";

    
    return text.replaceAll("§.", "");
  }
}



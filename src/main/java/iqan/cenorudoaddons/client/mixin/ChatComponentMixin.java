package iqan.cenorudoaddons.client.mixin;

import iqan.cenorudoaddons.client.events.Event;
import iqan.cenorudoaddons.client.events.EventBus;
import iqan.cenorudoaddons.client.events.impl.ChatReceivedEvent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;




@Mixin({ChatComponent.class})
public class ChatComponentMixin
{
  @Inject(method = {"addMessage"}, at = {@At("HEAD")}, cancellable = true)
  private void iq$onAddMessageSimple(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
    ChatReceivedEvent event = (ChatReceivedEvent)EventBus.post((Event)new ChatReceivedEvent(contents));
    
    if (event.isCancelled())
      ci.cancel(); 
  }
}



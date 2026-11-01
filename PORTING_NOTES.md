# Porte cenorudoaddons 26.1.2 -> 26.2

Alteracoes de codigo (todas vindas da reorganizacao Gui/Hud do 26.2):
- `Minecraft.screen`            -> `Minecraft.gui.screen()`   (AutoFishingFeature, RotationUtils x2)
- `Minecraft.setScreen(...)`    -> `Minecraft.gui.setScreen(...)` (MoneyMakersKeyBindings)
- CenorudoAddonsCommand: lambda quebrada pelo decompilador reescrita.
- Duplicata `EventBus$Subscription.java` removida (ja existe dentro de EventBus.java).

Build/metadata:
- fabric.mod.json: minecraft `~26.2`
- Resourceful Config 4.0.1 (26.1) -> 5.0.0 (26.2), embutido via jar-in-jar
- Loom 1.17, Gradle 9.5.1+, JDK 25

Para conferir ao compilar (nao verifiquei contra o jar do 26.2):
- Mixin ChatComponent#addMessage(Component, MessageSignature, GuiMessageSource, GuiMessageTag)
- Mixin Minecraft#tick e ServerLevel#addFreshEntity
- LevelRenderEvents.START_MAIN / LevelTerrainRenderContext (Fabric API)
- ResourcefulConfig 5.0.0 (mudou de major: API pode ter mudado)

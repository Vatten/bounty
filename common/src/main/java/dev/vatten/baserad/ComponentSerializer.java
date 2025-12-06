package dev.vatten.baserad;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.*;

public class ComponentSerializer implements Serializer<Component, String> {
    private final MiniMessage MINIMESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolvers(
                            StandardTags.color(),
                            StandardTags.keybind(),
                            StandardTags.translatable(),
                            StandardTags.translatableFallback(),
                            StandardTags.font(),
                            StandardTags.decorations(),
                            StandardTags.gradient(),
                            StandardTags.rainbow(),
                            StandardTags.reset(),
                            StandardTags.newline(),
                            StandardTags.transition(),
                            StandardTags.selector(),
                            StandardTags.score(),
                            StandardTags.nbt(),
                            StandardTags.pride(),
                            StandardTags.shadowColor()
//                            StandardTags.sprite(), //TODO: add these back cause my testing server is 1.21.8 (dont ask)
//                            StandardTags.sequentialHead()
                    ).build())
            .build();

    @Override
    public String serialize(Component component) {
        return MINIMESSAGE.serialize(component);
    }

    @Override
    public Component deserialize(String s) {
        return MINIMESSAGE.deserialize(s);
    }
}

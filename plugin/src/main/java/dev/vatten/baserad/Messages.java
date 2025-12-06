package dev.vatten.baserad;

import dev.vatten.baserad.interfaces.RenderableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class Messages {
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm 'UTC'");
    public static Style DEFAULT_STYLE = Style.style(TextColor.color(0xffd0c9));
    public static Style PRIMARY_BUTTON_STYLE = Style.style(TextColor.color(0xc573ff));
    public static Style TARGET_STYLE = Style.style(TextColor.color(0xff3c1f));
    public static Style SETTER_STYLE = Style.style(TextColor.color(0x99e9ff));
    public static Style CLAIMER_STYLE = Style.style(TextColor.color(0xfff266));
    public static Style WHITE_STYLE = Style.style(TextColor.color(0xf2f2f2));
    public static Function<Bounty, ClickEvent> BOUNTY_VIEW_CLICKEVENT = (bounty) -> ClickEvent.runCommand("/bounty:bounty view " + bounty.getId());
    public static Function<Bounty, ClickEvent> BOUNTY_REWARDS_CLICKEVENT = (bounty) -> ClickEvent.runCommand("/bounty:bounty rewards " + bounty.getId());
    public static Component BOUNTY = Component.text("☠ Bounty™ ☠").color(TextColor.color(0xff4b1f));
    public static Component CLICK_TO_VIEW = Component.text("[ Click to view ]").style(PRIMARY_BUTTON_STYLE);

    private final Plugin plugin;

    public Messages(Plugin plugin) {
        this.plugin = plugin;
    }

    public static Component formatBountyStatus(Bounty.Status status) {
        return switch(status) {
            case PENDING -> Component.text("⏳ Target has yet to respond to this bounty").color(TextColor.color(0xFABB64));
            case ACCEPTED -> Component.text("✔ Target accepted this bounty").color(TextColor.color(0x66FF78));
            case REJECTED -> Component.text("❌ Target rejected this bounty").color(TextColor.color(0xFF8066));
            case CLAIMED -> Component.text("⚑ This bounty has been claimed!").color(TextColor.color(0xFFF266));
        };
    }

    public static Component formatBountyStatusIcon(Bounty.Status status) {
        Component component = Component.text("?");
        switch(status) {
            case PENDING -> component = Component.text("⏳").color(TextColor.color(0xFABB64));
            case ACCEPTED -> component = Component.text("✔").color(TextColor.color(0x66FF78));
            case REJECTED -> component = Component.text("❌").color(TextColor.color(0xFF8066));
            case CLAIMED -> component = Component.text("⚑").color(TextColor.color(0xFFF266));
        }
        component = component.hoverEvent(HoverEvent.showText(formatBountyStatus(status)));
        return component;
    }

    public Component createBountyView(Bounty bounty) {
        RenderableComponent.MultiLineBuilder builder = RenderableComponent.multiLine();
        /**
         * <#ffd0c9>◆ Target: <#ff3c1f>☠ Wibbyt</#ff3c1f><br>◆ Set by: <#99e9ff>🏹 Vattendroppen236</#99e9ff><br><space><space><space>⌚ <#f2f2f2>Tue, 02 Dec 2025 10:41 UTC
         * <br><space><#2bff47>✔ This bounty has been accepted and is active</#2bff47>
         * <br><#ffd0c9>◆ Claimed by: <#bbff73>⧈ Vattendroppen236</#bbff73><br><space><space><space>⌚ <#f2f2f2>Tue, 02 Dec 2025 10:41 UTC
         * <br><#ffd0c9>◆ Reward: <#c573ff>[ ⛏ Click to view ]</#c573ff>
         */
        builder.addLine(Component.text("◆ Target: ")
                .append(Component.text("◎ " + plugin.getBountyPlayerByUUID(bounty.getTarget()).getName()).style(TARGET_STYLE))
        );
        if(bounty.getStatus() == Bounty.Status.PENDING || bounty.getStatus() == Bounty.Status.REJECTED || bounty.getStatus() == Bounty.Status.ACCEPTED) {
            builder.addLine(Component.text("   ").append(formatBountyStatus(bounty.getStatus())));
        }
        builder.addLine(Component.text("◆ Set by: ")
                .append(Component.text("⚑ " + plugin.getBountyPlayerByUUID(bounty.getSetter()).getName()).style(SETTER_STYLE))
        );
        builder.addLine(Component.text("   ⌚ ")
                .append(Component.text(DATE_TIME_FORMATTER.format(bounty.getSetTime().atZone(ZoneId.of("UTC")))).style(WHITE_STYLE))
        );
        if(bounty.getStatus() == Bounty.Status.CLAIMED) {
            builder.addLine(Component.empty());
            builder.addLine(Component.text("◆ Claimed by: ")
                    .append(Component.text("\uD83C\uDFF9 " + plugin.getBountyPlayerByUUID(bounty.getClaimer()).getName()).style(CLAIMER_STYLE))
            );
            if(bounty.getDeathMessage() != null) {
                builder.addLine(Component.text("   ☠ ")
                        .append(Component.text("\""))
                        .append(bounty.getDeathMessage().applyFallbackStyle(Style.style(NamedTextColor.WHITE)))
                        .append(Component.text("\""))
                );
            }
            builder.addLine(Component.text("   ⌚ ")
                    .append(Component.text(DATE_TIME_FORMATTER.format(bounty.getClaimTime().atZone(ZoneId.of("UTC")))).style(WHITE_STYLE))
            );
        }
        builder.addLine(Component.empty());
        builder.addLine(Component.text("◆ Reward: ")
                .append(Component.text("[ ⛏ Click to view ]").style(PRIMARY_BUTTON_STYLE).clickEvent(BOUNTY_REWARDS_CLICKEVENT.apply(bounty)))
        );

        return builder.build().asComponent().applyFallbackStyle(DEFAULT_STYLE);
    }

    public Component createBountyPreview(Bounty bounty) {
        RenderableComponent.FieldBuilder builder = RenderableComponent.field();

        builder.addComponent(Component.text("◎ " + plugin.getBountyPlayerByUUID(bounty.getTarget()).getName()).style(TARGET_STYLE));
//        builder.addComponent(Component.text("⚑ " + plugin.getBountyPlayerByUUID(bounty.getSetter()).getName()).style(SETTER_STYLE));
        if(bounty.getStatus() == Bounty.Status.PENDING || bounty.getStatus() == Bounty.Status.REJECTED || bounty.getStatus() == Bounty.Status.ACCEPTED) {
            builder.addComponent(formatBountyStatusIcon(bounty.getStatus()));
        }

        RenderableComponent.FieldBuilder builder2 = RenderableComponent.field();
        Component component = builder.build().asComponent();

        if(bounty.getStatus() == Bounty.Status.CLAIMED) {
            component = component.decoration(TextDecoration.STRIKETHROUGH, true);
            builder2.addComponent(component);
            builder2.addComponent(Component.text("\uD83C\uDFF9 " + plugin.getBountyPlayerByUUID(bounty.getClaimer()).getName()).style(CLAIMER_STYLE));
        } else {
            builder2.addComponent(component);
        }
        builder2.addComponent(CLICK_TO_VIEW);

        return builder2.build().asComponent().applyFallbackStyle(DEFAULT_STYLE).clickEvent(BOUNTY_VIEW_CLICKEVENT.apply(bounty));
    }

    public Component createBountyReceivedMessage(Bounty bounty) {
        return Component.text()
                .append(Component.text(plugin.getBountyPlayerByUUID(bounty.getSetter()).getName() + " has requested to put a bounty on you, and awaits your answer. ").style(DEFAULT_STYLE))
                .append(CLICK_TO_VIEW)
                .clickEvent(BOUNTY_VIEW_CLICKEVENT.apply(bounty))
                .build();
    }
}

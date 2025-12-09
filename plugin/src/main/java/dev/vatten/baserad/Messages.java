package dev.vatten.baserad;

import dev.vatten.baserad.interfaces.RenderableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class Messages {
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm 'UTC'");
    public static Style DEFAULT_STYLE = Style.style(TextColor.color(0xffd0c9));
    public static Style PRIMARY_BUTTON_STYLE = Style.style(TextColor.color(0xc573ff));
    public static Style TARGET_STYLE = Style.style(TextColor.color(0xff3c1f));
    public static Style SETTER_STYLE = Style.style(TextColor.color(0x99e9ff));
    public static Style CLAIMER_STYLE = Style.style(TextColor.color(0xfff266));
    public static Style WHITE_STYLE = Style.style(TextColor.color(0xf2f2f2));
    public static Style FIRE_STYLE = Style.style(TextColor.color(0xFF7226));
    public static Style LIGHT_FIRE_STYLE = Style.style(TextColor.color(0xFF9C66));
    public static Function<Bounty, ClickEvent> BOUNTY_VIEW_CLICKEVENT = (bounty) -> ClickEvent.runCommand("/bounty:bounty view " + bounty.getId());
    public static Function<Bounty, ClickEvent> BOUNTY_REWARDS_CLICKEVENT = (bounty) -> ClickEvent.runCommand("/bounty:bounty rewards " + bounty.getId());
    public static ClickEvent PENDING_BOUNTIES_CLICKEVENT = ClickEvent.runCommand("/bounty:bounty pending");
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
        builder.addLine(Component.text()
                .append(Component.text("◆ "))
                .append(Component.text(bounty.getHunters().size(), FIRE_STYLE))
                .append(Component.text(" players hunting this bounty: "))
                .append(Component.text("[ ⚔ Hover to view ]").style(FIRE_STYLE).hoverEvent(HoverEvent.showText(createBountyHuntersComponent(bounty))))
                .build().applyFallbackStyle(LIGHT_FIRE_STYLE)
        );

        return builder.build().asComponent().applyFallbackStyle(DEFAULT_STYLE);
    }

    public Component createBountyPreview(Bounty bounty) {
        RenderableComponent.FieldBuilder builder = RenderableComponent.field();

        TextComponent.Builder textBuilder = Component.text();
        textBuilder.append(Component.text("◎ " + plugin.getBountyPlayerByUUID(bounty.getTarget()).getName()).style(TARGET_STYLE));
//        builder.addComponent(Component.text("⚑ " + plugin.getBountyPlayerByUUID(bounty.getSetter()).getName()).style(SETTER_STYLE));
        if(bounty.getStatus() == Bounty.Status.PENDING || bounty.getStatus() == Bounty.Status.REJECTED || bounty.getStatus() == Bounty.Status.ACCEPTED) {
            textBuilder.append(Component.text().append(Component.text("(", NamedTextColor.DARK_GRAY)).append(formatBountyStatusIcon(bounty.getStatus())).append(Component.text(")", NamedTextColor.DARK_GRAY)).build());
        }
        builder.addComponent(textBuilder.build());

        RenderableComponent.FieldBuilder builder2 = RenderableComponent.field();
        Component component = builder.build().asComponent();

        if(bounty.getStatus() == Bounty.Status.CLAIMED) {
            component = component.decoration(TextDecoration.STRIKETHROUGH, true);
            builder2.addComponent(component);
            builder2.addComponent(Component.text("\uD83C\uDFF9 " + plugin.getBountyPlayerByUUID(bounty.getClaimer()).getName()).style(CLAIMER_STYLE));
        } else {
            builder2.addComponent(component);
        }
        if(!bounty.getHunters().isEmpty()) {
            if(bounty.getHunters().size() > plugin.PLUGIN_CONFIG.getData().getHotBountyThreshold()) {
                builder2.addComponent(Component.text().append(Component.text("« \uD83D\uDD25 ", FIRE_STYLE)).append(Component.text(bounty.getHunters().size(), LIGHT_FIRE_STYLE)).append(Component.text(" »", FIRE_STYLE)).build());
            } else {
                builder2.addComponent(Component.text().append(Component.text("‹ ", FIRE_STYLE)).append(Component.text(bounty.getHunters().size(), LIGHT_FIRE_STYLE)).append(Component.text(" ›", FIRE_STYLE)).build());
            }
        }
        builder2.addComponent(CLICK_TO_VIEW);

        return builder2.build().asComponent().applyFallbackStyle(DEFAULT_STYLE).clickEvent(BOUNTY_VIEW_CLICKEVENT.apply(bounty));
    }

    public Component createBountyHuntersComponent(Bounty bounty) {
        TextComponent.Builder builder = Component.text()
                .append(Component.text("Players interested in hunting this bounty:"));
        if(bounty.getHunters().isEmpty()) {
            builder.appendNewline();
            builder.append(Component.text("No players have expressed interest yet.").style(Style.style(NamedTextColor.GRAY)));
        } else {
            List<TextComponent> playerNames = plugin.getBountyPlayers(bounty.getHunters()).stream().map(bountyPlayer -> Component.text(bountyPlayer.getName())).toList();
            builder.appendNewline();
            builder.append(Component.join(JoinConfiguration.commas(true), playerNames).style(Style.style(NamedTextColor.GRAY)));
        }
        return builder.build().applyFallbackStyle(DEFAULT_STYLE);
    }

    public Component createBountyReceivedMessage(Bounty bounty) {
        return Component.text()
                .append(Component.text(plugin.getBountyPlayerByUUID(bounty.getSetter()).getName() + " has requested to put a bounty on you, and awaits your answer. ").style(DEFAULT_STYLE))
                .append(CLICK_TO_VIEW)
                .clickEvent(BOUNTY_VIEW_CLICKEVENT.apply(bounty))
                .build();
    }

    public Component createPendingBountiesMessage(List<Bounty> bountyRequests) {
        return Component.text()
                .append(Component.text("You have " + bountyRequests.size() + " pending bounty request(s) awaiting your answer. ").style(DEFAULT_STYLE))
                .append(CLICK_TO_VIEW)
                .clickEvent(PENDING_BOUNTIES_CLICKEVENT)
                .build();
    }

    public Component createBountyList(List<Bounty> bounties, Function<Bounty, Component> bountyFormatter) {
        RenderableComponent.MultiLineBuilder builder = RenderableComponent.multiLine().spacing(1);
        for(Bounty bounty : bounties) {
            builder.addLine(bountyFormatter.apply(bounty));
        }
        return builder.build().asComponent();
    }
}

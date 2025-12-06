package dev.vatten.baserad;

import dev.vatten.baserad.commands.BountyCommand;
import dev.vatten.baserad.configs.BountyStorage;
import dev.vatten.baserad.configs.PlayerStorage;
import dev.vatten.baserad.configs.PluginConfig;
import dev.vatten.baserad.events.PlayerJoinEvent;
import dev.vatten.baserad.events.PlayerKillPlayerEvent;
import dev.vatten.baserad.results.BountyResult;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Plugin extends VattenPlugin {
    public ConfigInstance<PluginConfig> PLUGIN_CONFIG;
    public ConfigInstance<BountyStorage> BOUNTY_STORAGE;
    public ConfigInstance<PlayerStorage> PLAYER_STORAGE;

    @Getter
    private Messages messages;

    protected Plugin(VattenPlatform<?, ?> pluginInterface, Type type, Path path) {
        super(pluginInterface, type, path);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        PLUGIN_CONFIG = new ConfigInstance<>(this, "config", PluginConfig.class);
        BOUNTY_STORAGE = new ConfigInstance<>(this, "bounties", BountyStorage.class);
        PLAYER_STORAGE = new ConfigInstance<>(this, "players", PlayerStorage.class);

        messages = new Messages(this);

        registerCommands(
                // Commands here
                new BountyCommand(this)
        );

        getEventHandler().registerEventHandler(PlayerJoinEvent.class, this::onJoin);
        getEventHandler().registerEventHandler(PlayerKillPlayerEvent.class, this::onPlayerKillPlayer);
    }

    @Override
    protected void reload() {
        super.reload();

        PLUGIN_CONFIG.load();
        BOUNTY_STORAGE.load();
        PLAYER_STORAGE.load();
    }

    public BountyPlayer getBountyPlayerByName(String name) {
        for(BountyPlayer player : PLAYER_STORAGE.getData().getPlayers()) {
            if(player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public BountyPlayer getBountyPlayerByUUID(UUID uuid) {
        for(BountyPlayer player : PLAYER_STORAGE.getData().getPlayers()) {
            if(player.getUuid().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public BountyResult setBounty(UUID setter, UUID target) {
        if(!PLUGIN_CONFIG.getData().isShouldSelfBounty() && setter.equals(target)) {
            return new BountyResult("You can't set a bounty on yourself");
        }
        Bounty bounty = new Bounty(setter, target, List.of(new Item("minecraft:stone", "[]", 67)));
        BOUNTY_STORAGE.getData().getBounties().add(bounty);
        BOUNTY_STORAGE.save();
        VattenPlayer targetPlayer = getPlayer(target);
        if(targetPlayer != null) {
            targetPlayer.sendMessage(getMessages().createBountyReceivedMessage(bounty));
        }
        return new BountyResult(bounty);
    }

    public List<Bounty> getBounties(Predicate<Bounty> predicate) {
        List<Bounty> bounties = new ArrayList<>();
        for(Bounty bounty : BOUNTY_STORAGE.getData().getBounties()) {
            if(predicate.test(bounty)) {
                bounties.add(bounty);
            }
        }
        return bounties;
    }

    public Bounty getBounty(Predicate<Bounty> predicate) {
        for(Bounty bounty : BOUNTY_STORAGE.getData().getBounties()) {
            if(predicate.test(bounty)) {
                return bounty;
            }
        }
        return null;
    }

    private void onJoin(PlayerJoinEvent event) {
        if(getBountyPlayerByUUID(event.getPlayer().getUuid()) == null) {
            PLAYER_STORAGE.getData().getPlayers().add(new BountyPlayer(event.getPlayer().getUuid(), event.getPlayer().getName()));
            PLAYER_STORAGE.save();
        }
        pluginInterface.scheduleTask(() -> {
            List<Bounty> bountyRequests = getBounties((bounty) -> bounty.getStatus() == Bounty.Status.PENDING && bounty.getTarget().equals(event.getPlayer().getUuid()));
            if(!bountyRequests.isEmpty()) {
                event.getPlayer().sendMessage(Component.text("You have " + bountyRequests.size() + " bounties pending for your response. Click here to view them.").clickEvent(ClickEvent.runCommand("/bounty:bounty pending")));
            }
        }, 5000);
    }

    private void onPlayerKillPlayer(PlayerKillPlayerEvent event) {
        List<Bounty> bounties = getBounties((bounty) ->
                bounty.getStatus() == Bounty.Status.ACCEPTED
                && bounty.getTarget().equals(event.getPlayer().getUuid())
                && (PLUGIN_CONFIG.getData().isShouldSetterClaim() || !bounty.getSetter().equals(event.getKiller().getUuid()))
                && (PLUGIN_CONFIG.getData().isShouldTargetClaim() || !bounty.getTarget().equals(event.getKiller().getUuid())));
        if(!bounties.isEmpty()) {
            broadcast(player -> player.sendMessage(Component.text(event.getKiller().getName() + " claimed " + bounties.size() + " bounties on " + event.getPlayer().getName())));
            for(Bounty bounty : bounties) {
                bounty.claim(event.getKiller().getUuid(), event.getDeathMessage());
                BOUNTY_STORAGE.save();
            }
        }
    }

    private void broadcast(Consumer<VattenPlayer> consumer) {
        for(VattenPlayer player : getPlayers().values()) {
            consumer.accept(player);
        }
    }
}
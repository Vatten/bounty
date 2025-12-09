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
import java.util.Comparator;
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

    public List<BountyPlayer> getBountyPlayers(List<UUID> uuids) {
        BountyPlayer[] playersArray = new BountyPlayer[uuids.size()];
        for(BountyPlayer player : PLAYER_STORAGE.getData().getPlayers()) {
            if(uuids.contains(player.getUuid())) {
                playersArray[uuids.indexOf(player.getUuid())] = player;
            }
        }
        List<BountyPlayer> players = new ArrayList<>();
        for(BountyPlayer player : playersArray) {
            if(player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public BountyPlayer getBountyPlayer(Predicate<BountyPlayer> predicate) {
        for(BountyPlayer player : PLAYER_STORAGE.getData().getPlayers()) {
            if(predicate.test(player)) {
                return player;
            }
        }
        return null;
    }

    public BountyPlayer getBountyPlayerByName(String name) {
        return getBountyPlayer(player -> player.getName().equalsIgnoreCase(name));
    }

    public BountyPlayer getBountyPlayerByUUID(UUID uuid) {
        return getBountyPlayer(player -> player.getUuid().equals(uuid));
    }

    public BountyResult setBounty(UUID setter, UUID target) {
        if(!PLUGIN_CONFIG.getData().isShouldSelfBounty() && setter.equals(target)) {
            return new BountyResult("You can't set a bounty on yourself");
        }
        Bounty bounty = new Bounty(setter, target, List.of(new Item("minecraft:stone", "[]", 67)));
        BOUNTY_STORAGE.edit(bountyStorage -> bountyStorage.getBounties().add(bounty));
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
            PLAYER_STORAGE.edit(playerStorage -> playerStorage.getPlayers().add(new BountyPlayer(event.getPlayer().getUuid(), event.getPlayer().getName())));
        }
        pluginInterface.scheduleTask(() -> {
            List<Bounty> bountyRequests = getBounties((bounty) -> bounty.getStatus() == Bounty.Status.PENDING && bounty.getTarget().equals(event.getPlayer().getUuid()));
            if(!bountyRequests.isEmpty()) {
                event.getPlayer().sendMessage(getMessages().createPendingBountiesMessage(bountyRequests));
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
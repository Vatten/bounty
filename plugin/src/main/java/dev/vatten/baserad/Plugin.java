package dev.vatten.baserad;

import dev.vatten.baserad.commands.BountyCommand;
import dev.vatten.baserad.configs.BountyStorage;
import dev.vatten.baserad.configs.PlayerStorage;
import dev.vatten.baserad.configs.PluginConfig;
import dev.vatten.baserad.events.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class Plugin extends VattenPlugin {
    public ConfigInstance<PluginConfig> PLUGIN_CONFIG;
    public ConfigInstance<BountyStorage> BOUNTY_STORAGE;
    public ConfigInstance<PlayerStorage> PLAYER_STORAGE;

    protected Plugin(VattenPlatform<?, ?> pluginInterface, Type type, Path path) {
        super(pluginInterface, type, path);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        PLUGIN_CONFIG = new ConfigInstance<>(this, "config", PluginConfig.class);
        BOUNTY_STORAGE = new ConfigInstance<>(this, "bounties", BountyStorage.class);
        PLAYER_STORAGE = new ConfigInstance<>(this, "players", PlayerStorage.class);

        registerCommands(
                // Commands here
                new BountyCommand(this)
        );

        getEventHandler().registerEventHandler(PlayerJoinEvent.class, this::onJoin);
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

    public Bounty setBounty(UUID setter, UUID target) {
        Bounty bounty = new Bounty(setter, target, List.of(new Item("stone", "[]", 67)));
        BOUNTY_STORAGE.getData().getBounties().add(bounty);
        BOUNTY_STORAGE.save();
        VattenPlayer targetPlayer = getPlayer(target);
        if(targetPlayer != null) {
            targetPlayer.sendMessage(Component.text("You have gotten a bounty set on you. Do you accept? ").append(Component.text("[ ACCEPT ]").clickEvent(ClickEvent.runCommand("/bounty:bounty accept " + bounty.getId()))));
        }
        return bounty;
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
            List<Bounty> bountyRequests = getBounties((bounty) -> {
                return bounty.getTarget().equals(event.getPlayer().getUuid()) && bounty.getStatus() == Bounty.Status.PENDING;
            });
            if(!bountyRequests.isEmpty()) {
                event.getPlayer().sendMessage(Component.text("You have " + bountyRequests.size() + " bounties pending for your response. Click here to view them.").clickEvent(ClickEvent.runCommand("/bounty:bounty pending")));
            }
        }, 5000);
    }
}
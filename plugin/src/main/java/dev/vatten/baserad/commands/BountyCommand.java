/*
 *    Copyright 2025 vatten <vatten.dev>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dev.vatten.baserad.commands;

import dev.vatten.baserad.*;
import dev.vatten.baserad.interfaces.RenderableComponent;
import dev.vatten.baserad.results.BountyResult;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BountyCommand extends Command {
    private final Plugin plugin;

    public BountyCommand(Plugin plugin) {
        super("bounty", List.of());
        this.plugin = plugin;
    }

    @Override
    public void execute(VattenPlayer player, String[] args) {
        if(args.length == 1) {
//            if(args[0].equalsIgnoreCase("help")) {
//                player.sendMessage(RenderableComponent.multiLine()
//                                .addLines(
//                                        Component.text("\uD83D\uDC80 Bounty \uD83D\uDC80").color(NamedTextColor.RED)
//
//                                )
//                        .build().asComponent());
//            }
            if(args[0].equalsIgnoreCase("reload")) {
                plugin.getApi().reload();
                player.sendMessage(TextFormatter.SUCCESS.format("Reloaded config files!"));
            }
            if(args[0].equalsIgnoreCase("set")) {
                player.sendMessage(TextFormatter.INFO.format(Component.text("Usage: /bounty set <player>")));
            }
            if(args[0].equalsIgnoreCase("top")) {
//                List<Bounty> bounties = plugin.getBounties((bounty) -> bounty.getTarget().equals(player.getUuid()));
                List<Bounty> bounties = plugin.getBounties((bounty) -> true);
                bounties.sort(Comparator.comparingInt(b -> -b.getHunters().size()));
                player.sendMessage(plugin.getMessages().createBountyList(bounties, plugin.getMessages()::createBountyPreview));
            }
            if(args[0].equalsIgnoreCase("list")) {
                List<Bounty> bounties = plugin.getBounties((bounty) -> bounty.getTarget().equals(player.getUuid()));
                player.sendMessage(plugin.getMessages().createBountyList(bounties, plugin.getMessages()::createBountyPreview));
            }
            if(args[0].equalsIgnoreCase("pending")) {
                List<Bounty> pendingBounties = plugin.getBounties((bounty) -> bounty.getStatus() == Bounty.Status.PENDING && bounty.getTarget().equals(player.getUuid()));
                player.sendMessage(plugin.getMessages().createBountyList(pendingBounties, plugin.getMessages()::createBountyPreview));
            }
        }
        if(args.length == 2) {
            if(args[0].equals("set")) {
                BountyPlayer target = plugin.getBountyPlayerByName(args[1]);
                if(target == null) {
                    player.sendMessage(TextFormatter.SEVERE.format(Component.text("Failed to set bounty: Player doesn't exist or has never join this server")));
                    return;
                }
                BountyResult result = plugin.setBounty(player.getUuid(), target.getUuid());
                if(result.wasSuccessful()) {
                    player.sendMessage(TextFormatter.INFO.format(Component.text("Bounty set on " + player.getName())));
                } else {
                    player.sendMessage(TextFormatter.SEVERE.format(Component.text("Failed to set bounty: " + result.getReason())));
                }
            }
            if(args[0].equalsIgnoreCase("view")) {
                Bounty bounty = plugin.getBounty((b) -> b.getId().equals(args[1]));
                player.sendMessage(plugin.getMessages().createBountyView(bounty));
            }
        }
    }

    @Override
    public List<String> onTabComplete(VattenPlayer player, String[] args) {
        List<String> completions = new ArrayList<>();
        if(args.length <= 1) {
            completions.addAll(List.of("reload", "set", "top", "list", "pending"));
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("set")) {
                completions.addAll(plugin.PLAYER_STORAGE.getData().getPlayers().stream().map(BountyPlayer::getName).toList());
            }
        }
        return completions.stream().filter(s -> args.length == 0 || s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).limit(50).toList();
    }
}

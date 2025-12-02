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
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
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
            if(args[0].equalsIgnoreCase("set")) {
                player.sendMessage(TextFormatter.INFO.format(Component.text("Usage: /bounty set <player>")));
            }
            if(args[0].equalsIgnoreCase("pending")) {
                List<Bounty> pendingBounties = plugin.getBounties((bounty) -> bounty.getTarget().equals(player.getUuid()) && bounty.getStatus() == Bounty.Status.PENDING);
                for(Bounty bounty : pendingBounties) {
                    player.sendMessage(Component.text(bounty.getId()));
                }
            }
        }
        if(args.length == 2) {
            if(args[0].equals("set")) {
                BountyPlayer target = plugin.getBountyPlayerByName(args[1]);
                if(target == null) {
                    player.sendMessage(TextFormatter.INFO.format(Component.text("Player not found")));
                    return;
                }
                plugin.setBounty(player.getUuid(), target.getUuid());
                player.sendMessage(TextFormatter.INFO.format(Component.text("Bounty set on " + player.getName())));
            }
        }
    }

    @Override
    public List<String> onTabComplete(VattenPlayer player, String[] args) {
        List<String> completions = new ArrayList<>();
        if(args.length <= 1) {
            completions.addAll(List.of("set", "pending"));
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("set")) {
                completions.addAll(plugin.PLAYER_STORAGE.getData().getPlayers().stream().map(BountyPlayer::getName).toList());
            }
        }
        return completions.stream().filter(s -> args.length == 0 || s.startsWith(args[args.length - 1].toLowerCase())).limit(50).toList();
    }
}

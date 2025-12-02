package dev.vatten.baserad;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.audience.Audience;

import java.util.UUID;

@Configuration
@NoArgsConstructor
@Getter
public class BountyPlayer {
    private UUID uuid;
    private String name;

    BountyPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}

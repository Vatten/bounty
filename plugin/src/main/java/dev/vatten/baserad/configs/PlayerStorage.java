package dev.vatten.baserad.configs;

import de.exlll.configlib.Configuration;
import dev.vatten.baserad.BountyPlayer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PlayerStorage {
    @Getter
    private List<BountyPlayer> players = new ArrayList<>();
}

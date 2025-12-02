package dev.vatten.baserad.configs;

import de.exlll.configlib.Configuration;
import dev.vatten.baserad.Bounty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BountyStorage {
    @Getter
    private List<Bounty> bounties = new ArrayList<>();
}

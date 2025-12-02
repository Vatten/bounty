package dev.vatten.baserad;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@NoArgsConstructor
@Getter
public class Bounty {
    private String id;
    private Status status;

    private Instant setTime;
    private UUID setter;

    private UUID target;
    private List<Item> rewards;

    private UUID claimer;
    private Instant claimTime;
    private List<Item> rewardsToClaim;

    private List<UUID> hunters;

    public Bounty(UUID setter, UUID target, List<Item> rewards) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.status = Status.PENDING;

        this.setTime = Instant.now();
        this.setter = setter;

        this.target = target;
        this.rewards = rewards;

        this.claimer = null;
        this.claimTime = null;
        this.rewardsToClaim = null;

        this.hunters = new ArrayList<>();
    }

    public void accept() {
        this.status = Status.ACCEPTED;
    }

    public void reject() {
        this.status = Status.REJECTED;
        this.rewardsToClaim = List.copyOf(this.rewards);
    }

    public void claim(UUID claimer) {
        this.status = Status.CLAIMED;
        this.claimTime = Instant.now();
        this.claimer = claimer;
        this.rewardsToClaim = List.copyOf(this.rewards);
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        CLAIMED
    }
}

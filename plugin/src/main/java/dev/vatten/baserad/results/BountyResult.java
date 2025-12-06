package dev.vatten.baserad.results;

import dev.vatten.baserad.Bounty;
import lombok.Getter;

public class BountyResult extends Result {
    @Getter
    private final Bounty bounty;

    public BountyResult(Bounty bounty) {
        super(true, "");
        this.bounty = bounty;
    }

    public BountyResult(String reason) {
        super(false, reason);
        this.bounty = null;
    }
}

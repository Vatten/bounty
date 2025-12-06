package dev.vatten.baserad.results;

import lombok.Getter;

public class Result {
    public static Result SUCCESS = new Result(true, "");
    private final boolean success;
    @Getter
    private final String reason;

    public Result(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    public boolean wasSuccessful() {
        return success;
    }
}

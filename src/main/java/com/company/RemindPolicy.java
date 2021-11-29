package com.company;

public class RemindPolicy {
    public final int maxAttempts;
    public final long delaySeconds;

    public RemindPolicy() {
        maxAttempts = 5;
        delaySeconds = 3 * 24 * 60 * 60;
    }

    public RemindPolicy(int maxAttempts, long delaySeconds) {
        if (maxAttempts <= 0 || delaySeconds <= 0)
            throw new IllegalArgumentException();

        this.maxAttempts = maxAttempts;
        this.delaySeconds = delaySeconds;
    }
}

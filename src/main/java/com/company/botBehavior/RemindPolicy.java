package com.company.botBehavior;

import com.google.inject.Inject;

public class RemindPolicy {
    public final int maxAttempts;
    public final long delaySeconds;

    public RemindPolicy() {
        maxAttempts = 5;
        delaySeconds = 3 * 24 * 60 * 60;
    }

    @Inject
    public RemindPolicy(int maxAttempts, long delaySeconds) {
        if (maxAttempts <= 0 || delaySeconds <= 0)
            throw new IllegalArgumentException();

        this.maxAttempts = maxAttempts;
        this.delaySeconds = delaySeconds;
    }
}

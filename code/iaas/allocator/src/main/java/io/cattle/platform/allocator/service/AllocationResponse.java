package io.cattle.platform.allocator.service;

import io.cattle.platform.allocator.constraint.Constraint;

import java.util.Set;

public class AllocationResponse {
    boolean successful;
    Set<Constraint> failedConstraints;

    public AllocationResponse(boolean successful) {
        this.successful = successful;
    }

    public AllocationResponse(Set<Constraint> failed) {
        this(false);
        this.failedConstraints = failed;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public Set<Constraint> getFailedConstraints() {
        return failedConstraints;
    }
}

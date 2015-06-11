package io.cattle.platform.allocator.service;

public interface Allocator {

    public AllocationResponse allocate(AllocationRequest request);

    public AllocationResponse deallocate(AllocationRequest request);

}
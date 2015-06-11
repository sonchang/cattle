package io.cattle.platform.allocator.eventing.impl;

import io.cattle.platform.allocator.constraint.Constraint;
import io.cattle.platform.allocator.constraint.UserDefinedConstraint;
import io.cattle.platform.allocator.eventing.AllocatorEventListener;
import io.cattle.platform.allocator.service.AllocationRequest;
import io.cattle.platform.allocator.service.AllocationResponse;
import io.cattle.platform.allocator.service.Allocator;
import io.cattle.platform.archaius.util.ArchaiusUtil;
import io.cattle.platform.eventing.EventService;
import io.cattle.platform.eventing.model.Event;
import io.cattle.platform.eventing.model.EventVO;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.DynamicBooleanProperty;

public class AllocatorEventListenerImpl implements AllocatorEventListener {

    private static final DynamicBooleanProperty FAIL_ON_NO_ALLOCATOR = ArchaiusUtil.getBoolean("allocator.fail.not.handled");
    private static final Logger log = LoggerFactory.getLogger(AllocatorEventListenerImpl.class);

    List<Allocator> allocators;
    EventService eventService;

    @Override
    public void instanceAllocate(Event event) {
        allocate(event);
    }

    @Override
    public void instanceDeallocate(Event event) {
        deallocate(event);
    }

    @Override
    public void volumeAllocate(Event event) {
        allocate(event);
    }

    @Override
    public void volumeDeallocate(Event event) {
        deallocate(event);
    }

    protected void allocate(Event event) {
        log.info("Allocating [{}:{}]", event.getResourceType(), event.getResourceId());

        AllocationRequest request = new AllocationRequest(event);
        boolean handled = false;

        AllocationResponse response = null;
        for (Allocator allocator : allocators) {
            response = allocator.allocate(request);
            if (response.isSuccessful()) {
                handled = true;
                log.info("Allocator [{}] handled request [{}]", allocator, request);
                break;
            }
        }

        if (handled) {
            if (request.isSendReply()) {
                eventService.publish(EventVO.reply(event));
            }
        } else {
            log.error("No allocator handled [{}]", event);
            if (FAIL_ON_NO_ALLOCATOR.get()) {
                String failureMessage = "Failed to find a placement";
                if (response != null && !response.getFailedConstraints().isEmpty()) {
                    failureMessage = getAllocationFailureMessage(response.getFailedConstraints());
                }
                eventService.publish(EventVO.reply(event).withTransitioningMessage(failureMessage).withTransitioning(Event.TRANSITIONING_ERROR));
            }
        }
    }

    private String getAllocationFailureMessage(Set<Constraint> failedConstraints) {
        StringBuilder sb = new StringBuilder();
        for (Constraint c: failedConstraints) {
            if (c instanceof UserDefinedConstraint) {
                sb.append(((UserDefinedConstraint)c).getFailureMessage());
            }
        }
        return sb.toString();
    }

    protected void deallocate(Event event) {
        log.info("Deallocating [{}:{}]", event.getResourceType(), event.getResourceId());

        AllocationRequest request = new AllocationRequest(event);
        boolean handled = false;

        for (Allocator allocator : allocators) {
            if (allocator.deallocate(request).isSuccessful()) {
                handled = true;
                log.info("Deallocator [{}] handled request [{}]", allocator, request);
                break;
            }
        }

        if (handled && request.isSendReply()) {
            eventService.publish(EventVO.reply(event));
        }
    }

    public EventService getEventService() {
        return eventService;
    }

    @Inject
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public List<Allocator> getAllocators() {
        return allocators;
    }

    @Inject
    public void setAllocators(List<Allocator> allocators) {
        this.allocators = allocators;
    }

}

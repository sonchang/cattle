package io.cattle.platform.allocator.constraint;

import io.cattle.platform.allocator.dao.AllocatorDao;
import io.cattle.platform.allocator.service.AllocationAttempt;
import io.cattle.platform.allocator.service.AllocationCandidate;
import io.cattle.platform.core.model.Port;

import java.util.List;
import java.util.Set;

public class PortsConstraint extends HardConstraint implements Constraint, UserDefinedConstraint {

    AllocatorDao allocatorDao;

    List<Port> ports;

    public PortsConstraint(List<Port> ports, AllocatorDao allocatorDao) {
        this.ports = ports;
        this.allocatorDao = allocatorDao;
    }

    @Override
    public boolean matches(AllocationAttempt attempt,
            AllocationCandidate candidate) {
        Set<Long> hostIds = candidate.getHosts();

        for (Long hostId : hostIds) {
            // TODO: Performance improvement. Move more of the filtering into the DB query itself
            List<Port> portsUsedByHost = allocatorDao.getUsedPortsForHostExcludingInstance(hostId, attempt.getInstanceId());
            for (Port portUsed : portsUsedByHost) {
                for (Port requestedPort : ports) {
                    if (requestedPort.getPublicPort() != null &&
                            requestedPort.getPublicPort().equals(portUsed.getPublicPort()) &&
                            (requestedPort.getPublicIpAddressId() == null ||
                                requestedPort.getPublicIpAddressId() != null && requestedPort.getPublicIpAddressId().equals(portUsed.getPublicIpAddressId()))
                            &&
                            requestedPort.getProtocol().equals(portUsed.getProtocol())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Port port: ports) {
            sb.append("{ public: ipAddressId=");
            sb.append(port.getPublicIpAddressId());
            sb.append(", port=");
            sb.append(port.getPublicPort());
            sb.append("; private: ipAddressId=");
            sb.append(port.getPrivateIpAddressId());
            sb.append(", port=");
            sb.append(port.getPrivatePort());
            sb.append("; protocol=");
            sb.append(port.getProtocol());
            sb.append("} ");
        }
        return String.format("host needs ports %s available", sb.toString());
    }

    @Override
    public String getFailureMessage() {
        // TODO: Possibly provide link to the constraint rather than describing it in detail
        return "Port conflict";
    }
}

package io.cattle.platform.servicediscovery.deployment.impl;

import io.cattle.platform.core.model.Service;
import io.cattle.platform.servicediscovery.api.constants.ServiceDiscoveryConstants;
import io.cattle.platform.servicediscovery.api.util.ServiceDiscoveryUtil;
import io.cattle.platform.servicediscovery.deployment.ServiceDeploymentPlanner;
import io.cattle.platform.servicediscovery.deployment.ServiceDeploymentPlannerFactory;
import io.cattle.platform.servicediscovery.deployment.impl.DeploymentManagerImpl.DeploymentServiceContext;

import java.util.List;
import java.util.Map;

public class ServiceDeploymentPlannerFactoryImpl implements ServiceDeploymentPlannerFactory {

    @Override
    public ServiceDeploymentPlanner createServiceDeploymentPlanner(List<Service> services, List<DeploymentUnit> units,
            DeploymentServiceContext context) {

        if (services.isEmpty()) {
            return null;
        }

        Service service = services.get(0);
        Map<String, String> serviceLabels = ServiceDiscoveryUtil.getServiceLabels(service, context.allocatorService);
        String globalService = serviceLabels.get(ServiceDiscoveryConstants.LABEL_SERVICE_GLOBAL);

        if (service.getKind().equalsIgnoreCase(ServiceDiscoveryConstants.KIND.EXTERNALSERVICE.name())
                || service.getKind().equalsIgnoreCase(ServiceDiscoveryConstants.KIND.DNSSERVICE.name())) {
            return new ExternalServiceDeploymentPlanner(services, units, context);
        } else if (globalService != null) {
            return new GlobalServiceDeploymentPlanner(services, units, context);
        } else {
            return new DefaultServiceDeploymentPlanner(services, units, context);
        }
    }

}

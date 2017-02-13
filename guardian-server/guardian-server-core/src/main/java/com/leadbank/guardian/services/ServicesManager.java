package com.leadbank.guardian.services;

import com.leadbank.guardian.authentication.principal.Service;

import java.util.Collection;

public interface ServicesManager {

    RegisteredService save(RegisteredService registeredService);

    RegisteredService delete(long id);

    RegisteredService findServiceBy(Service service);
    
    RegisteredService findServiceBy(long id);

    Collection<RegisteredService> getAllServices();

    boolean matchesExistingService(Service service);
}

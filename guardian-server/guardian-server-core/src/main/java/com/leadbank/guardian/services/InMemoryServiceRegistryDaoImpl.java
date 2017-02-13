package com.leadbank.guardian.services;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryServiceRegistryDaoImpl implements ServiceRegistryDao {
    
    private List<RegisteredService> registeredServices = new ArrayList<RegisteredService>();
    
    public boolean delete(RegisteredService registeredService) {
        return this.registeredServices.remove(registeredService);
    }

    public RegisteredService findServiceById(final long id) {
        for (final RegisteredService r : this.registeredServices) {
            if (r.getId() == id) {
                return r;
            }
        }
        
        return null;
    }

    public List<RegisteredService> load() {
        return this.registeredServices;
    }

    public RegisteredService save(final RegisteredService registeredService) {
        if (registeredService.getId() == -1) {
            ((AbstractRegisteredService) registeredService).setId(findHighestId()+1);
        }

        this.registeredServices.remove(registeredService);
        this.registeredServices.add(registeredService);
        
        return registeredService;
    }

    public void setRegisteredServices(final List<RegisteredService> registeredServices) {
        this.registeredServices = registeredServices;
    }

    private long findHighestId() {
        long id = 0;

        for (final RegisteredService r : this.registeredServices) {
            if (r.getId() > id) {
                id = r.getId();
            }
        }

        return id;
    }
}

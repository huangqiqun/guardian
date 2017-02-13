package com.leadbank.guardian.authentication.principal;

public interface PersistentIdGenerator {

    String generate(Principal principal, Service service);
}

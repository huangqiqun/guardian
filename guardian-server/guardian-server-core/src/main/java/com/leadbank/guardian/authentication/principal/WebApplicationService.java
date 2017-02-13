package com.leadbank.guardian.authentication.principal;

public interface WebApplicationService extends Service {

    Response getResponse(String ticketId);

    String getArtifactId();
}

package com.leadbank.guardian.authentication;

import com.leadbank.guardian.authentication.principal.Credentials;

public interface AuthenticationMetaDataPopulator {

    Authentication populateAttributes(Authentication authentication,
                                      Credentials credentials);
}

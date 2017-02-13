package com.leadbank.guardian.web.view;

import java.util.Map;

import com.leadbank.guardian.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractGuardianView extends AbstractView {
    
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final Assertion getAssertionFrom(final Map model) {
        return (Assertion) model.get("assertion");
    }
}

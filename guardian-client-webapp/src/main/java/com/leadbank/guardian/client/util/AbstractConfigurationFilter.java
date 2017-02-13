package com.leadbank.guardian.client.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leadbank.common.context.ApplicationContextConfig;

public abstract class AbstractConfigurationFilter implements Filter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean ignoreInitConfiguration = false;

    protected final String getPropertyFromInitParams(final FilterConfig filterConfig, final String propertyName,
            final String defaultValue) {
        final String value = filterConfig.getInitParameter(propertyName);

        if (CommonUtils.isNotBlank(value)) {
            if ("renew".equals(propertyName)) {
                throw new IllegalArgumentException(
                        "Renew MUST be specified via context parameter or JNDI environment to avoid misconfiguration.");
            }
            logger.info("Property [{}] loaded from FilterConfig.getInitParameter with value [{}]", propertyName, value);
            return value;
        }

        final String value2 = filterConfig.getServletContext().getInitParameter(propertyName);
        
        if (CommonUtils.isNotBlank(value2)) {
            logger.info("Property [{}] loaded from ServletContext.getInitParameter with value [{}]", propertyName,
                    value2);
            return value2;
        }
        InitialContext context;
        try {
            context = new InitialContext();
        } catch (final NamingException e) {
            logger.warn(e.getMessage(), e);
            return defaultValue;
        }

        final String shortName = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
        final String value3 = loadFromContext(context, "java:comp/env/cas/" + shortName + "/" + propertyName);

        if (CommonUtils.isNotBlank(value3)) {
            logger.info("Property [{}] loaded from JNDI Filter Specific Property with value [{}]", propertyName, value3);
            return value3;
        }

        final String value4 = loadFromContext(context, "java:comp/env/cas/" + propertyName);

        if (CommonUtils.isNotBlank(value4)) {
            logger.info("Property [{}] loaded from JNDI with value [{}]", propertyName, value4);
            return value4;
        }
        
        final String value5 = ApplicationContextConfig.get(propertyName);
        
        if (CommonUtils.isNotBlank(value5)) {
        	logger.info("Property [{}] loaded from ZooKeeper with value [{}]", propertyName, value5);
        	return value5;
        }

        logger.info("Property [{}] not found.  Using default value [{}]", propertyName, defaultValue);
        return defaultValue;
    }

    protected final boolean parseBoolean(final String value) {
        return ((value != null) && value.equalsIgnoreCase("true"));
    }

    protected final String loadFromContext(final InitialContext context, final String path) {
        try {
            return (String) context.lookup(path);
        } catch (final NamingException e) {
            return null;
        }
    }

    public final void setIgnoreInitConfiguration(boolean ignoreInitConfiguration) {
        this.ignoreInitConfiguration = ignoreInitConfiguration;
    }

    protected final boolean isIgnoreInitConfiguration() {
        return this.ignoreInitConfiguration;
    }
}

package org.richfaces.arquillian.configuration;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

public class FundamentalTestConfigurator {

    @Inject
    @SuiteScoped
    private InstanceProducer<FundamentalTestConfiguration> configuration;

    public void configureGraphene(@Observes BeforeSuite event, ArquillianDescriptor descriptor) {
        FundamentalTestConfiguration c = new FundamentalTestConfiguration();
        c.configure(descriptor, Default.class).validate();
        this.configuration.set(c);
        FundamentalTestConfigurationContext.set(c);
    }

    public void unconfigureGraphene(@Observes AfterSuite event) {
        FundamentalTestConfigurationContext.reset();
    }
}

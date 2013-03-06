package org.richfaces.arquillian.configuration;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.drone.configuration.ConfigurationMapper;
import org.jboss.arquillian.drone.spi.DroneConfiguration;

public class FundamentalTestConfiguration implements DroneConfiguration<FundamentalTestConfiguration> {

    private String richfacesVersion;
    private Boolean servletContainerSetup;
    private String currentBuildRichfacesVersion = "4.3.1-SNAPSHOT";
    private String jsfImplementation;

    /**
     * Get version of RichFaces dependencies to use with the test.
     *
     * By default, current project's version will be used.
     */
    public String getRichFacesVersion() {
        if (richfacesVersion == null || richfacesVersion.isEmpty()) {
            return currentBuildRichfacesVersion;
        }
        return richfacesVersion;
    }

    public boolean isCurrentRichFacesVersion() {
        return currentBuildRichfacesVersion.equals(getRichFacesVersion());
    }

    /**
     * Add JSF to the WebArchive for support of plain Servlet containers (Tomcat, Jetty, etc.)
     *
     * @return
     */
    public boolean servletContainerSetup() {
        return servletContainerSetup;
    }

    public String getJsfImplementation() {
        return jsfImplementation;
    }

    public void validate() {
        if (servletContainerSetup == null) {
            throw new IllegalArgumentException("The servletContainerSetup configuration needs to be specified");
        }
    }

    @Override
    public String getConfigurationName() {
        return "richfaces";
    }

    @Override
    public FundamentalTestConfiguration configure(ArquillianDescriptor descriptor, Class<? extends Annotation> qualifier) {
        return ConfigurationMapper.fromArquillianDescriptor(descriptor, this, qualifier);
    }

}

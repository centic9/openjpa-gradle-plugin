package com.radcortez.gradle.plugin.openjpa.enhance

import com.radcortez.gradle.plugin.openjpa.OpenJpa
import com.radcortez.gradle.plugin.openjpa.OpenJpaExtension
import org.apache.openjpa.lib.util.Options
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Description.
 *
 * @author Roberto Cortez
 */
class EnhanceExtension {
    private static final Logger LOG = LoggerFactory.getLogger(EnhanceExtension.class)

    Project project

    @Input
    @Optional
    boolean addDefaultConstructor = true
    @Input
    @Optional
    boolean enforcePropertyRestrictions = false
    @Input
    @Optional
    boolean tmpClassLoader = false;

    EnhanceExtension(final Project project) {
        this.project = project

        project.afterEvaluate() {
            project.tasks.classes.doLast { enhance() }
        }
    }

    void enhance() {
        project.pluginManager.apply(JavaPlugin)

        OpenJpaExtension openJpaConfiguration = project.extensions.findByType(OpenJpaExtension)
        EnhanceExtension configuration = openJpaConfiguration.extensions.findByType(EnhanceExtension)

        def openJpa = OpenJpa.openJpa(openJpaConfiguration.classpath, new Options([
            "addDefaultConstructor"      : configuration.addDefaultConstructor.toBoolean(),
            "enforcePropertyRestrictions": configuration.enforcePropertyRestrictions.toBoolean(),
            "tmpClassLoader"             : configuration.tmpClassLoader.toBoolean(),
            "propertiesFile"             : openJpaConfiguration.persistenceXmlFile
        ]))

        LOG.info("----Printing Classes----")
        (openJpaConfiguration.classes as String[]).each {
            LOG.info(it)
        }
        LOG.info("----End Classes----")
        openJpa.enhance(openJpaConfiguration.classes as String[])
        openJpa.dispose()
    }
}

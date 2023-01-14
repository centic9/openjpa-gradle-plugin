package com.radcortez.gradle.plugin.openjpa.metamodel

import com.radcortez.gradle.plugin.openjpa.OpenJpaExtension
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Description.
 *
 * @author Roberto Cortez
 */
class MetamodelTask extends JavaCompile {
    MetamodelTask() {
        project.afterEvaluate {
            MetamodelExtension configuration = project
                    .extensions.findByType(OpenJpaExtension)
                    .extensions.findByType(MetamodelExtension)

            SourceDirectorySet mainJava = project.sourceSets.main.java
            source(mainJava.srcDirs)

            // use "api" if "compile" is not available any more, e.g. on Gradle 7.x
            // https://github.com/radcortez/openjpa-gradle-plugin/issues/14
            try {
                setClasspath(project.configurations.compile)
            } catch (Throwable e) {
                setClasspath(project.configurations.compileClasspath)
            }
            setDestinationDir(project.file(configuration.metamodelOutputFolder))

            def openjpaConfig = project.configurations.detachedConfiguration(
                    project.dependencies.create(configuration.metamodelDependency),
                    project.dependencies.create("javax.annotation:jsr250-api:1.0"))

            options.annotationProcessorPath = openjpaConfig
            options.compilerArgs += [
                    "-Aopenjpa.source=" + sourceCompatibility[-1..-1],
                    "-Aopenjpa.metamodel=true",
                    "-proc:only",
                    "-processor", "org.apache.openjpa.persistence.meta.AnnotationProcessor6"
            ]

            mainJava.srcDir(destinationDir)
        }
    }
}

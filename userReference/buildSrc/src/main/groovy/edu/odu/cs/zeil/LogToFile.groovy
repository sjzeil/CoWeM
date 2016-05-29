package edu.odu.cs.zeil

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.StandardOutputListener
import org.gradle.logging.internal.LoggingOutputInternal

/**
 * A plugin for describing a course website.
 */
class LogToFile implements Plugin<Project> {

    void apply (Project project) {
    
        // Copy logging output to build/logs
        // Slightly modified from Sion Willis:
        //    http://willis7.github.io/blog/2013/gradle-output-to-log.html
        def tstamp = new Date().format('yyyy-MM-dd_HH-mm-ss')
        def buildLogDir = "${project.rootDir}/build/logs"
        project.mkdir("${buildLogDir}")
        def buildLog = new File("${buildLogDir}/gradle_${tstamp}.log")
        
        project.gradle.services.get(LoggingOutputInternal)
          .addStandardOutputListener (
            new StandardOutputListener () {
                void onOutput(CharSequence output) {
                    buildLog << output
                }
        })
        
        project.gradle.services.get(LoggingOutputInternal)
          .addStandardErrorListener (
            new StandardOutputListener () {
                void onOutput(CharSequence output) {
                    buildLog << output
                }
        })
    }    
}

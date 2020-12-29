package com.ceabie.dexknife

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * the spilt tools plugin.
 *
 * @author ceabie
 */
public class DexKnifePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.afterEvaluate {
            def for130 = new SplitToolsFor130(project)
            for (variant in project.android.applicationVariants) {
                for130.processSplitDex(variant)
            }
        }
    }
}
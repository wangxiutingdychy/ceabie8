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

            for (variant in project.android.applicationVariants) {
                if (isMultiDexEnabled(variant)) {
                    if (SplitToolsFor130.isCompat(variant)) {
                        SplitToolsFor130.processSplitDex(project, variant)
                    } else {
                        System.err.println("DexKnife Error: Android gradle plugin only <= 1.3.0. Or use DexKnife 1.5.2.");
                    }
                } else {
                    println("DexKnife : MultiDexEnabled is false, it's not work.");
                }
            }
        }
    }

    private static boolean isMultiDexEnabled(variant) {
        def is = variant.buildType.multiDexEnabled
        if (is != null) {
            return is;
        }

        is = variant.mergedFlavor.multiDexEnabled
        if (is != null) {
            return is;
        }

        return false
    }

}
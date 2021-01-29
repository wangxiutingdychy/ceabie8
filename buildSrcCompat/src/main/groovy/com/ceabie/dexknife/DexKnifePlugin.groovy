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
                    if (SplitToolsFor130.isCompat130(variant)) {
                        SplitToolsFor130.processSplitDex(project, variant)
                    } else {
                        SplitToolsFor150.processSplitDex(project, variant)
                    }

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
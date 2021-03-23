/*
 * Copyright (C) 2016 ceabie (https://github.com/ceabie/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ceabie.dexknife

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * the spilt tools plugin.
 *
 * @author ceabie
 */
public class DexKnifePlugin implements Plugin<Project> {

    public static boolean isCompat130(Object variant) {
        try {
            if (variant != null) {
                variant.dex

                return true
            }
        } catch (RuntimeException e) {
//            e.printStackTrace()
        }

        return false
    }

    @Override
    void apply(Project project) {

        project.afterEvaluate {

            for (variant in project.android.applicationVariants) {
                if (isMultiDexEnabled(variant)) {
                    if (isCompat130(variant)) {
                        System.err.println("DexKnife Error: This version is only support Android gradle plugin >= 1.5.0. Please use DexKnife 1.3.2.");
                    } else if (SplitToolsFor150.isCompat()) {
                        SplitToolsFor150.processSplitDex(project, variant)
                    } else {
                        println("DexKnife Error: Android gradle plugin only < 2.0.0.");
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
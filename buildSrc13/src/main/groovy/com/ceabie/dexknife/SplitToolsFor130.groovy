package com.ceabie.dexknife

import org.gradle.api.Project

/**
 * the spilt tools for plugin 1.3.0.
 *
 * @author ceabie
 */
public class SplitToolsFor130 extends AbstractSplitTools {

    public SplitToolsFor130(Project project) {
        super(project)
    }

//    public static boolean isCompat130(Object variant) {
//        try {
//            if (variant != null) {
//                variant.dex
//
//                return true
//            }
//        } catch (RuntimeException e) {
//            e.printStackTrace()
//        }
//
//        return false
//    }

    @Override
    public void processSplitDex(Object variant) {
        def dex = variant.dex
        if (dex.multiDexEnabled) {
            if (dex.additionalParameters == null) {
                dex.additionalParameters = []
            }

            dex.additionalParameters += '--main-dex-list=maindexlist.txt'
            dex.additionalParameters += '--minimal-main-dex'

            dex.inputs.file "second_dex_package_list.txt"
            dex.doFirst {
                processMainDexList(variant, null)
            }
        }
    }
}
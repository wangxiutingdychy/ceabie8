package com.ceabie.dexknife

import com.android.build.api.transform.Format
import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.internal.transforms.DexTransform
import org.gradle.api.Project
/**
 * the spilt tools for plugin 1.5.0.
 *
 * @author ceabie
 */
public class SplitToolsFor150 extends DexSplitTools {

    public static void processSplitDex(Project project, Object variant) {
        TransformTask dexTask
        TransformTask proGuardTask
        TransformTask jarMergingTask

        String name = variant.name.capitalize()
        boolean minifyEnabled = variant.buildType.minifyEnabled

        project.tasks.matching {
            ((it instanceof TransformTask) && it.name.endsWith(name)) // TransformTask
        }.each { TransformTask theTask ->
            Transform transform = theTask.transform
            String transformName = transform.name

            if (minifyEnabled && "proguard".equals(transformName)) { // ProGuardTransform
                proGuardTask = theTask
            } else if (!minifyEnabled && "jarMerging".equals(transformName)) {
                jarMergingTask = theTask
            } else if ("dex".equals(transformName)) { // DexTransform
                dexTask = theTask
            }
        }

        if (dexTask != null) {
            dexTask.inputs.file DEX_KNIFE_CFG_TXT

            dexTask.doFirst {
                DexTransform dexTransform = it.transform
                if (dexTransform.multiDex) {
                    TransformTask apkJarTask
                    String jarName
                    File mergedJar = null

                    if (variant.buildType.minifyEnabled) {
                        // 获得混淆后的jar
                        apkJarTask = proGuardTask
                        jarName = "main"
                    } else {
                        apkJarTask = jarMergingTask
                        jarName = "combined"
                    }

                    if (apkJarTask != null) {
                        Transform transform = apkJarTask.transform
                        def outputProvider = apkJarTask.outputStream.asOutput()
                        mergedJar = outputProvider.getContentLocation(jarName,
                                transform.getOutputTypes(),
                                transform.getScopes(), Format.JAR)
                    }

                    println ("========= DexKnife-MergedJar: " + mergedJar)

                    File fileMainList = dexTransform.mainDexListFile

                    if (mergedJar != null) {
                        File mappingFile = variant.mappingFile

                        processMainDexList(project, minifyEnabled,
                                mappingFile, mergedJar, fileMainList)

                        // 替换 AndroidBuilder
                        MultiDexAndroidBuilder.proxyAndroidBuilder(dexTransform)
                    }

                    // 替换这个文件
                    fileMainList.delete()
                    project.copy {
                        from 'maindexlist.txt'
                        into fileMainList.parentFile
                    }
                }
            }
        }
    }
}
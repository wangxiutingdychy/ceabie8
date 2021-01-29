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
//        TransformTask proGuardTask
        TransformTask jarMergingTask

        String name = variant.name.capitalize()
        boolean minifyEnabled = variant.buildType.minifyEnabled

        // find the task we want to process
        project.tasks.matching {
            ((it instanceof TransformTask) && it.name.endsWith(name)) // TransformTask
        }.each { TransformTask theTask ->
            Transform transform = theTask.transform
            String transformName = transform.name

//            if (minifyEnabled && "proguard".equals(transformName)) { // ProGuardTransform
//                proGuardTask = theTask
//            } else
            if (!minifyEnabled && "jarMerging".equals(transformName)) {
                jarMergingTask = theTask
            } else if ("dex".equals(transformName)) { // DexTransform
                dexTask = theTask
            }
        }

        if (dexTask != null && ((DexTransform) dexTask.transform).multiDex) {
            dexTask.inputs.file DEX_KNIFE_CFG_TXT

            dexTask.doFirst {
                startDexKnife()

                File mergedJar = null
                DexTransform dexTransform = it.transform
                DexKnifeConfig dexKnifeConfig = getDexKnifeConfig(project)

                if (!minifyEnabled && jarMergingTask != null) {
                    Transform transform = jarMergingTask.transform
                    def outputProvider = jarMergingTask.outputStream.asOutput()
                    mergedJar = outputProvider.getContentLocation("combined",
                            transform.getOutputTypes(),
                            transform.getScopes(), Format.JAR)
                }

                println("DexKnife-MergedJar: " + mergedJar)

                File fileMainList = dexTransform.mainDexListFile

                if (mergedJar != null) {
                    File mappingFile = variant.mappingFile

                    if (processMainDexList(project, minifyEnabled, mappingFile, mergedJar,
                            fileMainList, dexKnifeConfig)) {

                        // 替换 AndroidBuilder
                        MultiDexAndroidBuilder.proxyAndroidBuilder(dexTransform,
                                dexKnifeConfig.additionalParameters)
                    }
                }

                // 替换这个文件
                fileMainList.delete()
                project.copy {
                    from 'maindexlist.txt'
                    into fileMainList.parentFile
                }

                endDexKnife()
            }
        }
    }
}
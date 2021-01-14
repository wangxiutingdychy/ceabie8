package com.ceabie.dexknife;

import org.gradle.api.Project;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * the base of spilt tools.
 *
 * @author ceabie
 */
public abstract class AbstractSplitTools {
    protected Project mProject;

    public AbstractSplitTools(Project project) {
        mProject = project;
    }

    public abstract void processSplitDex(Object variant);

    protected void processMainDexList(File mergedJar) {
    }

    /**
     * 获得第二个分包的类过滤列表
     */
    private static PatternSet getMainDexPattern(Project project) throws Exception {
        File file = project.file("second_dex_package_list.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            line = line.trim().replace('.', '/');
            lines.add(line);
        }

        reader.close();

        return new PatternSet().exclude(lines);
    }

    /**
     * 生成主dex的类列表
     */
    private static void genMainDexList(Project project, boolean minifyEnabled,
                                       File mappingFile, File proguardOutputFile,
                                       File jarMergingOutputFile, File mainDexListFile,
                                       PatternSet mainDexPattern) throws Exception {
//        println(":genMainDexList");

        String[] secPackages = null;
        HashSet<String> secPackageSet = null;
        File mergedJar;

        if (minifyEnabled) {
            // 从mapping文件中收集混淆后的 class
            secPackageSet = getClassesFromMapping(mappingFile, mainDexPattern);

            mergedJar = proguardOutputFile;
        } else {
            mergedJar = jarMergingOutputFile;
        }

        File keepFile = project.file("maindexlist.txt");
        keepFile.delete();

        // 获得 ADT 推荐的 maindexlist
        File andMainDexList = mainDexListFile;
        HashSet<String> mainCls = getAdtMainDexClasses(andMainDexList);

        ZipFile clsfile = new ZipFile(mergedJar);

        final Spec<FileTreeElement> asSpec = mainDexPattern.getAsSpec();
        ClassFileTreeElement treeElement = new ClassFileTreeElement();

        Enumeration<? extends ZipEntry> entries = clsfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entryName.endsWith(".class")) {
                treeElement.setClassPath(entryName);
                boolean isMainDex = asSpec.isSatisfiedBy(treeElement);

                // 如果ADT的类在主dex，则不放在第二个dex
                if (!isMainDex && mainCls != null && mainCls.contains(entryName)) {
                    isMainDex = true;
                }

//                if (!isMainDex) {
//                    keepFile.withWriterAppend { w ->
//                        w << entryName << '\n'
//                    }
//                } else {
//                    println entryName
//                }
            }

        }

        clsfile.close();
    }

    private static HashSet<String> getClassesFromMapping(File mapping, PatternSet mainDexPattern) throws Exception {
        HashSet<String> secPackSet = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(mapping));

        String line;
        ArrayList<String> lines = new ArrayList<>();

        final Spec<FileTreeElement> asSpec = mainDexPattern.getAsSpec();
        ClassFileTreeElement treeElement = new ClassFileTreeElement();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.endsWith(":")) {
                int ip = line.indexOf(" -> ");
                if (ip != -1) {
                    String sOrg = line.substring(ip).replace('.', '/') + ".class";

                    treeElement.setClassPath(sOrg);
                    if (asSpec.isSatisfiedBy(treeElement)) {
                        String sMap = line.substring(ip + 4, line.length() - 1).replace('.', '/') + ".class";
                    }
                }

                lines.add(line);
            }
        }

        reader.close();

        return secPackSet;
    }

    /**
    * 获取系统推荐在主dex的列表
    */
    private static HashSet<String> getAdtMainDexClasses(File outputDir) throws Exception {
        HashSet<String> mainCls = new HashSet<>();

        BufferedReader reader = new BufferedReader(new FileReader(outputDir));

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.endsWith(".class")) {
                mainCls.add(line);
            }
        }

        reader.close();

        if (mainCls.size() == 0) {
            mainCls = null;
        }

        return mainCls;
    }
}
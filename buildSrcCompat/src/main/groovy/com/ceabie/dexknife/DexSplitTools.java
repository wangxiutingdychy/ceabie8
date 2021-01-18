package com.ceabie.dexknife;

import org.gradle.api.Project;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
public class DexSplitTools {

    public static final String DEX_KNIFE_CFG_TXT = "dexknife.txt";

    public static void processMainDexList(Project project, boolean minifyEnabled,
                                          File mappingFile, File jarMergingOutputFile,
                                          File andMainDexList) throws Exception {

        genMainDexList(project, minifyEnabled, mappingFile, jarMergingOutputFile, andMainDexList,
                getMainDexPattern(project));
    }

    /**
     * 获得第二个分包的类过滤列表
     */
    private static PatternSet getMainDexPattern(Project project) throws Exception {
        File file = project.file(DEX_KNIFE_CFG_TXT);
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
                                       File mappingFile, File jarMergingOutputFile,
                                       File andMainDexList, PatternSet mainDexPattern) throws Exception {

        System.out.println(":" + project.getName() + ":genMainDexList");

        // 获得 ADT 推荐的 maindexlist
        HashSet<String> mainCls = getAdtMainDexClasses(andMainDexList);

        File keepFile = project.file("maindexlist.txt");
        keepFile.delete();

        ArrayList<String> mainClasses;
        if (minifyEnabled) {
            // 从mapping文件中收集混淆后的 class
            mainClasses = getMainClassesFromMapping(mappingFile, mainDexPattern, mainCls);
        } else {
            // 从合并后的jar文件中收集 class
            mainClasses = getMainClassesFromJar(jarMergingOutputFile, mainDexPattern, mainCls);
        }

        if (mainClasses != null && mainClasses.size() > 0) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(keepFile));

            for (String mainClass : mainClasses) {
                writer.write(mainClass);
                writer.newLine();

                System.out.println(mainClass);
            }

            writer.close();
        }
    }

    private static ArrayList<String> getMainClassesFromJar(
            File jarMergingOutputFile, PatternSet mainDexPattern, HashSet<String> mainCls) throws Exception {
        ZipFile clsFile = new ZipFile(jarMergingOutputFile);

        final Spec<FileTreeElement> asSpec = mainDexPattern.getAsSpec();
        ClassFileTreeElement treeElement = new ClassFileTreeElement();

        ArrayList<String> mainDexList = new ArrayList<>();
        Enumeration<? extends ZipEntry> entries = clsFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entryName.endsWith(".class")) {
                treeElement.setClassPath(entryName);

                // 如果ADT的类在主dex，则不放在第二个dex
                if (asSpec.isSatisfiedBy(treeElement)
                        || (mainCls != null && mainCls.contains(entryName))) {
                    mainDexList.add(entryName);
                }
            }
        }

        clsFile.close();

        return mainDexList;
    }

    private static ArrayList<String> getMainClassesFromMapping(File mapping,
                                                               PatternSet mainDexPattern,
                                                               HashSet<String> mainCls) throws Exception {
        String line;
        ArrayList<String> mainDexList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(mapping));

        final Spec<FileTreeElement> asSpec = mainDexPattern.getAsSpec();
        ClassFileTreeElement treeElement = new ClassFileTreeElement();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.endsWith(":")) {
                int ip = line.indexOf(" -> ");
                if (ip != -1) {
                    String sOrg = line.substring(ip).replace('.', '/') + ".class";

                    treeElement.setClassPath(sOrg);
                    if (asSpec.isSatisfiedBy(treeElement)
                            || (mainCls != null && mainCls.contains(sOrg))) {
                        String sMap = line.substring(ip + 4, line.length() - 1)
                                .replace('.', '/') + ".class";

                        mainDexList.add(sMap);
                    }
                }
            }
        }

        reader.close();

        return mainDexList;
    }

    /**
     * get the maindexlist of android gradle plugin
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
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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * the base of spilt tools.
 *
 * @author ceabie
 */
public class DexSplitTools {

    public static final String DEX_KNIFE_CFG_TXT = "dexknife.txt";

    private static final String DEX_MINIMAL_MAIN_DEX = "--minimal-main-dex";

    private static final String DEX_KNIFE_CFG_DEX_PARAM = "-dex-param ";
    private static final String DEX_KNIFE_CFG_SPLIT = "-split ";
    private static final String DEX_KNIFE_CFG_KEEP = "-keep ";
    private static final String DEX_KNIFE_CFG_AUTO_MAINDEX = "-auto-maindex";
    private static final String DEX_KNIFE_CFG_MINIMAL_MAINDEX = "-minimal-maindex";
    private static final String DEX_KNIFE_CFG_DONOT_USE_SUGGEST = "-donot-use-suggest";
    private static final String DEX_KNIFE_CFG_LOG_MAIN_DEX = "-log-mainlist";
    public static final String MAINDEXLIST_TXT = "maindexlist.txt";

    public static void processMainDexList(Project project, boolean minifyEnabled, File mappingFile,
                                          File jarMergingOutputFile, File andMainDexList,
                                          DexKnifeConfig dexKnifeConfig) throws Exception {

        genMainDexList(project, minifyEnabled, mappingFile, jarMergingOutputFile, andMainDexList,
                dexKnifeConfig);
    }

    /**
     * get the config of dex knife
     */
    protected static DexKnifeConfig getDexKnifeConfig(Project project) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(project.file(DEX_KNIFE_CFG_TXT)));
        DexKnifeConfig dexKnifeConfig = new DexKnifeConfig();

        boolean minimalMainDex = true;

        Set<String> addParams = new HashSet<>();

        String line;
        ArrayList<String> splitToSecond = new ArrayList<>();
        ArrayList<String> keepMain = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            int rem = line.indexOf('#');
            if (rem != -1) {
                if (rem == 0) {
                    continue;
                } else {
                    line = line.substring(0, rem).trim();
                }
            }

            String cmd = line.toLowerCase();

            System.out.println("====== config: " + cmd);

            if (DEX_KNIFE_CFG_AUTO_MAINDEX.equals(cmd)) {
                minimalMainDex = false;
            } else if (DEX_KNIFE_CFG_MINIMAL_MAINDEX.equals(cmd)) {
                minimalMainDex = true;

            } else if (cmd.startsWith(DEX_KNIFE_CFG_DEX_PARAM)) {
                String param = line.substring(DEX_KNIFE_CFG_DEX_PARAM.length()).trim();
                if (!param.toLowerCase().startsWith("--main-dex-list")) {
                    addParams.add(param);
                }

            } else if (cmd.startsWith(DEX_KNIFE_CFG_SPLIT)) {
                String sPattern = line.substring(DEX_KNIFE_CFG_SPLIT.length()).trim();
                splitToSecond.add(sPattern.replace('.', '/'));

            } else if (cmd.startsWith(DEX_KNIFE_CFG_KEEP)) {
                String sPattern = line.substring(DEX_KNIFE_CFG_KEEP.length()).trim();
                keepMain.add(sPattern.replace('.', '/'));

            } else if (DEX_KNIFE_CFG_DONOT_USE_SUGGEST.equals(cmd)) {
                dexKnifeConfig.useSuggest = false;

            } else if (DEX_KNIFE_CFG_LOG_MAIN_DEX.equals(cmd)) {
                dexKnifeConfig.logMainList = true;

            } else if (!cmd.startsWith("-")) {
                splitToSecond.add(line.replace('.', '/'));
            }
        }

        reader.close();

        if (minimalMainDex) {
            addParams.add(DEX_MINIMAL_MAIN_DEX);
        }

        dexKnifeConfig.patternSet = new PatternSet()
                        .exclude(splitToSecond).include(keepMain);
        dexKnifeConfig.additionalParameters = addParams;
        return dexKnifeConfig;
    }

    /**
     * generate the main dex list
     */
    private static void genMainDexList(Project project, boolean minifyEnabled,
                                       File mappingFile, File jarMergingOutputFile,
                                       File andMainDexList, DexKnifeConfig dexKnifeConfig) throws Exception {

        System.out.println(":" + project.getName() + ":genMainDexList");

        // get the adt's maindexlist
        HashSet<String> mainCls = null;
        if (dexKnifeConfig.useSuggest) {
            mainCls = getAdtMainDexClasses(andMainDexList);
        }

        File keepFile = project.file(MAINDEXLIST_TXT);
        keepFile.delete();

        ArrayList<String> mainClasses;
        if (minifyEnabled) {
            // get classes from mapping
            mainClasses = getMainClassesFromMapping(mappingFile, dexKnifeConfig.patternSet, mainCls);
        } else {
            // get classes from merged jar
            mainClasses = getMainClassesFromJar(jarMergingOutputFile, dexKnifeConfig.patternSet, mainCls);
        }

        if (mainClasses != null && mainClasses.size() > 0) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(keepFile));

            for (String mainClass : mainClasses) {
                writer.write(mainClass);
                writer.newLine();

                if (dexKnifeConfig.logMainList) {
                    System.out.println(mainClass);
                }
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
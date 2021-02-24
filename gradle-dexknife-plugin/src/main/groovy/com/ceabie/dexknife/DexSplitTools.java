package com.ceabie.dexknife;

import com.android.builder.Version;

import org.gradle.api.Project;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.specs.Specs;
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
    private static final String DEX_KNIFE_CFG_DONOT_USE_SUGGEST = "-donot-use-suggest";
    private static final String DEX_KNIFE_CFG_LOG_MAIN_DEX = "-log-mainlist";

    private static final String MAINDEXLIST_TXT = "maindexlist.txt";
    public static final String MAPPING_FLAG = " -> ";
    public static final int MAPPING_FLAG_LEN = MAPPING_FLAG.length();

    private static long StartTime = 0;

    protected static void startDexKnife() {
        System.out.println("DexKnife Processing ...");
        StartTime = System.currentTimeMillis();
    }

    protected static void endDexKnife() {
        String time;
        long internal = System.currentTimeMillis() - StartTime;
        if (internal > 1000) {
            float i = internal / 1000;
            if (i >= 60) {
                i = i / 60;
                int min = (int) i;
                time = min + " min " + (i - min) + " s";
            } else {
                time = i + "s";
            }
        } else {
            time = internal + "ms";
        }

        System.out.println("DexKnife Finished: " + time);
    }

    static int getAndroidPluginVersion() {
        String version = Version.ANDROID_GRADLE_PLUGIN_VERSION;
        int size = version.length();
        int ver = 0;
        for (int i = 0; i < size; i++) {
            char c = version.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                if (c != '.') {
                    ver = ver * 10 + c - '0';
                }
            } else {
                break;
            }
        }

        return ver;
    }

    public static boolean processMainDexList(Project project, boolean minifyEnabled, File mappingFile,
                                             File jarMergingOutputFile, File andMainDexList,
                                             DexKnifeConfig dexKnifeConfig) throws Exception {

        return genMainDexList(project, minifyEnabled, mappingFile, jarMergingOutputFile,
                andMainDexList, dexKnifeConfig);
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

            System.out.println("DexKnife Config: " + cmd);

            if (DEX_KNIFE_CFG_AUTO_MAINDEX.equals(cmd)) {
                minimalMainDex = false;
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
    private static boolean genMainDexList(Project project, boolean minifyEnabled,
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
            System.out.println("DexKnife: From Mapping");
            // get classes from mapping
            mainClasses = getMainClassesFromMapping(mappingFile, dexKnifeConfig.patternSet, mainCls);
        } else {
            System.out.println("DexKnife: From Merged Jar");
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

            return true;
        }

        throw new Exception("DexKnife Warnning: Main dex is EMPTY ! Check your config and project!");
    }

    private static ArrayList<String> getMainClassesFromJar(
            File jarMergingOutputFile, PatternSet mainDexPattern, HashSet<String> mainCls) throws Exception {
        ZipFile clsFile = new ZipFile(jarMergingOutputFile);

        final Spec<FileTreeElement> asSpec = Specs.or(mainDexPattern.getAsIncludeSpec(),
                Specs.not(mainDexPattern.getAsExcludeSpec()));
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

    private static ArrayList<String> getMainClassesFromMapping(
            File mapping,
            PatternSet mainDexPattern,
            HashSet<String> mainCls) throws Exception {

        String line;
        ArrayList<String> mainDexList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(mapping));

        ClassFileTreeElement treeElement = new ClassFileTreeElement();
        Spec<FileTreeElement> asSpec = Specs.or(Specs.not(mainDexPattern.getAsExcludeSpec()),
                mainDexPattern.getAsIncludeSpec());

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.endsWith(":")) {
                int flagPos = line.indexOf(MAPPING_FLAG);
                if (flagPos != -1) {

                    String sOrg = line.substring(0, flagPos).replace('.', '/') + ".class";
                    treeElement.setClassPath(sOrg);

                    if (asSpec.isSatisfiedBy(treeElement)
                            || (mainCls != null && mainCls.contains(sOrg))) {
                        String sMap = line.substring(flagPos + MAPPING_FLAG_LEN, line.length() - 1)
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
        if (!outputDir.exists()) {
            System.out.println("DexKnife Warnning: Android recommand Main dex is no exist, try run again!");
            return null;
        }
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
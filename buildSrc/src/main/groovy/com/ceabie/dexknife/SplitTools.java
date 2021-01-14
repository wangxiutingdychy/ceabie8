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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * the base of spilt tools.
 *
 * @author ceabie
 */
public class SplitTools {
    protected Project mProject;

    public SplitTools(Project project) {
        mProject = project;
    }

    public static ArrayList<String> testPattern(Project project) throws Exception {
        ArrayList<String> secDexPackages = new ArrayList<>();

        File file = project.file("second_dex_package_list.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        ArrayList<String> lines = new ArrayList<>();
        String line;
        PatternSet patternSet = new PatternSet();
        while ((line = reader.readLine()) != null) {
            line = line.trim().replace('.', '/');
            lines.add(line);
        }
        reader.close();

        patternSet.exclude(lines);

        ZipFile clsfile = new ZipFile(project.file("combined.jar"));

        final Spec<FileTreeElement> asSpec = patternSet.getAsSpec();
        ClassFileTreeElement treeElement = new ClassFileTreeElement();

        Enumeration<? extends ZipEntry> entries = clsfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (entryName.endsWith(".class")) {
                treeElement.setClassPath(entryName);
                if (asSpec.isSatisfiedBy(treeElement)) {
                    System.out.println("====== " + entryName);
                }
            }
        }

        return secDexPackages;
    }
}
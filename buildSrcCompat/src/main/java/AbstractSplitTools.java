import org.gradle.api.Project;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


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


    /**
     */
    private static ArrayList<String> getSecondPackages(Project project) throws Exception {
        ArrayList<String> secDexPackages = new ArrayList<>();

        File file = project.file("second_dex_package_list.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
        }

        PatternSet patternSet = new PatternSet();
        Spec<FileTreeElement> asSpec = patternSet.getAsSpec();

        return secDexPackages;
    }
}
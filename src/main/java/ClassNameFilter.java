import java.io.File;
import java.io.FilenameFilter;

public class ClassNameFilter implements FilenameFilter {

    public ClassNameFilter() {

    }

    @Override
    public boolean accept(File dir, String name) {
        return name.indexOf(".class") > 0;
    }
}

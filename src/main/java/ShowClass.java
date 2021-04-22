import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Build Class view in right panel
 *
 * @author Mio
 * @version 1.0
 *
 * @see java.lang.reflect.Constructor
 * @see java.lang.reflect.Field
 * @see java.lang.reflect.Method
 * @see java.util.Hashtable
 * @see java.util.StringTokenizer
 */
public class ShowClass {
    Class c;
    String context = null;

    /**
     * Default Constructor
     * @param c class
     */
    public ShowClass(Class c) {
        this.c = c;
    }

    String findToken(String str) {
        StringTokenizer st = new StringTokenizer(str, ".");

        for(str = ""; st.hasMoreTokens(); str = st.nextToken()) {
        }

        return str;
    }

    /**
     *  set modifiers
     *
     * @param mod modifier val
     * @return modifier string
     */
    String checkMod(int mod) {
        String str = "";
        // set Mod
        if ((mod & 1) > 0) {
            str = str + "public ";
        } else if ((mod & 4) > 0) {
            str = str + "protected ";
        } else if ((mod & 2) > 0) {
            str = str + "private ";
        }

        if ((mod & 1024) > 0) {
            str = str + "abstract ";
        } else if ((mod & 8) > 0) {
            str = str + "static ";
        }

        if ((mod & 16) > 0) {
            str = str + "final ";
        }

        return str;
    }

    /**
     * Create class skeleton to display in GUI
     *
     * @param ht hashtable from GUI
     * @return skeleton strings with all components
     */
    String[] getSkeleton(Hashtable ht) {

        StringBuilder context = new StringBuilder();
        StringBuilder rowHeader = new StringBuilder();
        String[] retArr = new String[2];

        if (this.c != null) {
            Package p = this.c.getPackage();
            context.append(p.toString()).append(";\n\n");
            rowHeader.append("\n\n");
            context.append(this.checkMod(this.c.getModifiers()));
            context.append("class ").append(this.findToken(this.c.getName())).append(" {\n");
            rowHeader.append("\n");
            Field[] fds = this.c.getDeclaredFields();
            context.append("  // Fields\n");
            rowHeader.append("\n");
            // Grab Fields
            for (Field fd : fds) {
                context.append("  ").append(this.checkMod(fd.getModifiers()));
                context.append(this.findToken(fd.getType().getName())).append(" ");
                context.append(fd.getName()).append(";\n");
                rowHeader.append("\n");
            }

            context.append("\n");
            rowHeader.append("\n");

            Constructor[] cst = this.c.getDeclaredConstructors();
            context.append("  // Constructors\n");
            rowHeader.append("\n");

            for (Constructor constructor : cst) {
                String key = constructor.toString();
                key = key.substring(key.lastIndexOf(" ") + 1);
                key = key.replaceFirst(constructor.getName(), constructor.getName() + ".<init>");
                key = key.replaceAll(",", ", ");
                Integer curRuns = (Integer) ht.get(key);
                if (curRuns != null) {
                    rowHeader.append(curRuns).append("\n");
                } else {
                    rowHeader.append("0\n");
                }

                context.append("  ").append(this.checkMod(constructor.getModifiers()));
                context.append(this.findToken(this.c.getName()));
                Class[] ca = constructor.getParameterTypes();
                context.append("(");

                for (int j = 0; j < ca.length; ++j) {
                    Class carray = ca[j].getComponentType();
                    String str = "";
                    if (carray == null) {
                        str = this.findToken(ca[j].getName());
                    } else {
                        str = this.findToken(carray.getName()) + "[]";
                    }

                    context.append(str);
                    if (j < ca.length - 1) {
                        context.append(", ");
                    }
                }

                context.append(") { }\n");
            }

            context.append("\n");
            rowHeader.append("\n");
            Method[] md = this.c.getDeclaredMethods();
            context.append("  // Methods\n");
            rowHeader.append("\n");

            for (Method method : md) {
                String key = method.toString();
                key = key.substring(key.lastIndexOf(" ") + 1);
                key = key.replaceAll(",", ", ");
                Integer curRuns = (Integer) ht.get(key);
                if (curRuns != null) {
                    rowHeader.append(curRuns);
                } else {
                    rowHeader.append("0");
                }

                context.append("  ").append(this.checkMod(method.getModifiers()));
                context.append(this.findToken(method.getReturnType().getName())).append(" ");
                context.append(method.getName());
                Class[] ca = method.getParameterTypes();
                context.append("(");

                for (int j = 0; j < ca.length; ++j) {
                    Class carray = ca[j].getComponentType();
                    String str = "";
                    if (carray == null) {
                        str = this.findToken(ca[j].getName());
                    } else {
                        str = this.findToken(carray.getName()) + "[]";
                    }

                    context.append(str);
                    if (j < ca.length - 1) {
                        context.append(", ");
                    }
                }

                context.append(")\n");
                rowHeader.append("\n");
            }

            context.append("}\n\n");
            rowHeader.append("\n\n");
        }

        retArr[0] = context.toString();
        retArr[1] = rowHeader.toString();

        return retArr;
    }

    /**
     * Build class skeleton to display in GUI
     *
     * @return string with all components
     */
    String[] getSkeleton() {

        StringBuilder context = new StringBuilder();
        StringBuilder rowHeader = new StringBuilder();
        String[] retArr = new String[2];

        if (this.c != null) {
            Package p = this.c.getPackage();
            context.append(p.toString()).append(";\n\n");
            rowHeader.append("\n\n");
            context.append(this.checkMod(this.c.getModifiers()));
            context.append("class ").append(this.findToken(this.c.getName())).append(" {\n");
            rowHeader.append("\n");
            Field[] fds = this.c.getDeclaredFields();
            context.append("  // Fields\n");
            rowHeader.append("\n");

            for (Field fd : fds) {
                context.append("  ").append(this.checkMod(fd.getModifiers()));
                context.append(this.findToken(fd.getType().getName())).append(" ");
                context.append(fd.getName()).append(";\n");
                rowHeader.append("\n");
            }

            context.append("\n");
            rowHeader.append("\n");
            Constructor[] cst = this.c.getDeclaredConstructors();
            context.append("  // Constructors\n");
            rowHeader.append("\n");

            for (Constructor constructor : cst) {
                context.append("  ").append(this.checkMod(constructor.getModifiers()));
                context.append(this.findToken(this.c.getName())).append("() { }\n");
                rowHeader.append("0\n");
            }

            context.append("\n");
            rowHeader.append("\n");
            Method[] md = this.c.getDeclaredMethods();
            context.append("  // Methods\n");
            rowHeader.append("\n");

            for (Method method : md) {
                context.append("  ").append(this.checkMod(method.getModifiers()));
                context.append(this.findToken(method.getReturnType().getName())).append(" ");
                context.append(method.getName());
                Class[] ca = method.getParameterTypes();
                context.append("(");

                for (int j = 0; j < ca.length; ++j) {
                    Class carray = ca[j].getComponentType();
                    String str = "";
                    if (carray == null) {
                        str = this.findToken(ca[j].getName());
                    } else {
                        str = this.findToken(carray.getName()) + "[]";
                    }

                    context.append(str);
                    if (j < ca.length - 1) {
                        context.append(", ");
                    }
                }

                context.append(")\n");
                rowHeader.append("0\n");
            }

            context.append("}\n\n");
            rowHeader.append("\n\n");
        }

        retArr[0] = context.toString();
        retArr[1] = rowHeader.toString();

        return retArr;
    }
}

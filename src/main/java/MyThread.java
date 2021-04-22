import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;

import java.util.Hashtable;
import java.util.List;

/**
 * Code provided by professor
 * Added in requested code to complete
 * required functions
 *
 * @author Mio
 * @version 1.1
 */
public class MyThread extends Thread {
    GUI gui;
    Hashtable hash = new Hashtable(40);

    VirtualMachine vm;
    boolean stopOnVMStart;
    boolean connected = true;
    int numClasses;
    ReferenceType rt;
    String pkgName;

    /**
     * Constructor MyThread with args
     * @param vm virtual machine
     * @param stopVM val for boolean
     * @param name name of package
     * @param length number of classes
     * @param window instance of GUI window
     */
    public MyThread(VirtualMachine vm, boolean stopVM, String name, int length, GUI window) {
        this.vm = vm;
        this.stopOnVMStart = stopVM;
        this.start();
        this.numClasses = length;
        this.pkgName = name;
        this.gui = window;
    }

    /**
     * Default constructor MyThread
     */
    public MyThread() {
        try {
            createWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void run() {
        EventQueue queue = vm.eventQueue();
        for (int i = 1; i <= numClasses; i++) {
            ClassPrepareRequest cpr = vm.eventRequestManager().createClassPrepareRequest();
            cpr.addClassFilter(pkgName + ".*");
            cpr.addCountFilter(i);
            cpr.enable();
        }

        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                boolean resumeStoppedApp = false;

                EventIterator it = eventSet.eventIterator();
                while (it.hasNext()) {
                    Event e = it.nextEvent();
                    resumeStoppedApp |= !handleEvent(e);
                }
                if (resumeStoppedApp) {
                    eventSet.resume();
                }
            } catch (InterruptedException ignored) {
            } catch (VMDisconnectedException |
                    ClassNotFoundException var8) {
                break;
            }
        }
    }

    public Hashtable getHashTable() {
        return this.hash;
    }

    private boolean handleEvent(Event event) throws ClassNotFoundException {
        if (event instanceof ExceptionEvent) {
            return exceptionEvent(event);
        } else if (event instanceof BreakpointEvent) {
            return breakpointEvent(event);
        } else if (event instanceof WatchpointEvent) {
            return fieldWatchEvent(event);
        } else if (event instanceof StepEvent) {
            return stepEvent(event);
        } else if (event instanceof MethodEntryEvent) {
            return methodEntryEvent(event);
        } else if (event instanceof MethodExitEvent) {
            return methodExitEvent(event);
        } else if (event instanceof ClassPrepareEvent) {
            /*provide code
             The goal is to utilize the event object to identify methods in
             the the reference type class. Find the locations of the methods
             in the code. Use the virtual machine object to set breakpoints
             for each location and enable them.
             */
            //     :
            //     :

            ClassPrepareEvent cpe = (ClassPrepareEvent)event;
            this.rt = cpe.referenceType();

            try {
                List methodList = this.rt.methods();
                Object[] o = methodList.toArray();

                for (Object value : o) {
                    Method metd = (Method) value;
                    Location loc = metd.location();
                    BreakpointRequest req = this.vm.eventRequestManager()
                            .createBreakpointRequest(loc);
                    req.enable();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return classPrepareEvent(event);
        } else if (event instanceof ClassUnloadEvent) {
            return classUnloadEvent(event);
        } else if (event instanceof ThreadStartEvent) {
            return threadStartEvent(event);
        } else if (event instanceof ThreadDeathEvent) {
            return threadDeathEvent(event);
        } else if (event instanceof VMStartEvent) {
            return vmStartEvent(event);
        } else {
            return handleExitEvent(event);
        }
    }

    private boolean vmDied = false;

    private boolean handleExitEvent(Event event) {
        if (event instanceof VMDeathEvent) {
            vmDied = true;
            return vmDeathEvent(event);
        } else if (event instanceof VMDisconnectEvent) {
            connected = false;
            if (!vmDied) {
                vmDisconnectEvent(event);
            }
            return false;
        } else {
            throw new InternalError("Unexpected event type");
        }
    }

    private boolean vmStartEvent(Event event) {
        VMStartEvent se = (VMStartEvent) event;
        return stopOnVMStart;
    }

    private boolean breakpointEvent(Event event) throws ClassNotFoundException {
           /* provide code
           This method is called when a break point is encountered.
           Take advantage of this break point event to identify the location.
           The method and class can be identified from the location. Make an
           update to the number of times of execution for the class's method
           in real time.
         */
        //     :
        //     :
        //     :
        BreakpointEvent be = (BreakpointEvent)event;
        if (this.hash.containsKey(be.location().method().toString())) {
            Integer i = (Integer)this.hash.get(be.location().method().toString());
            int j = i + 1;
            this.hash.put(be.location().method().toString(), j);
        } else {
            this.hash.put(be.location().method().toString(), 1);
        }
        // update numbers in real time
        this.gui.addNumbers();

        return false;
    }

    private boolean methodEntryEvent(Event event) {
        MethodEntryEvent me = (MethodEntryEvent) event;
        System.out.println("MethodEntryEvent");
        System.out.println(me.method().toString());
        System.out.println(me.location().lineNumber());
        return true;
    }

    private boolean methodExitEvent(Event event) {
        MethodExitEvent me = (MethodExitEvent) event;
        return true;
    }

    private boolean fieldWatchEvent(Event event) {
        WatchpointEvent fwe = (WatchpointEvent) event;
        return true;
    }

    private boolean stepEvent(Event event) {
        StepEvent se = (StepEvent) event;
        return true;
    }

    private boolean classPrepareEvent(Event event) {
        ClassPrepareEvent cle = (ClassPrepareEvent) event;
        return false;
    }

    private boolean classUnloadEvent(Event event) {
        ClassUnloadEvent cue = (ClassUnloadEvent) event;
        return false;
    }

    private boolean exceptionEvent(Event event) {
        ExceptionEvent ee = (ExceptionEvent) event;
        return true;
    }

    private boolean threadDeathEvent(Event event) {
        ThreadDeathEvent tee = (ThreadDeathEvent) event;
        return false;
    }

    private boolean threadStartEvent(Event event) {
        ThreadStartEvent tse = (ThreadStartEvent) event;
        return false;
    }

    public boolean vmDeathEvent(Event event) {
        return false;
    }

    public boolean vmDisconnectEvent(Event event) {
        System.out.println("VMDisconnectEvent");
        return false;
    }

    private void createWindow() {
    }

}
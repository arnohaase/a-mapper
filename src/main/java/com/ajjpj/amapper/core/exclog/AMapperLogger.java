package com.ajjpj.amapper.core.exclog;

import com.ajjpj.afoundation.function.AFunction0;
import com.ajjpj.afoundation.function.AFunction0NoThrow;
import com.ajjpj.amapper.core.path.APath;
import org.apache.log4j.Logger;


/**
 * @author arno
 */
public abstract class AMapperLogger {
    public abstract void debug(AFunction0NoThrow<String> msg);

    protected abstract void warn(String msg);

    public void deferredWithoutInitial(final APath path) {
        debug (new AFunction0NoThrow<String> () {
            @Override public String apply () {
                return "Object mapped as 'deferred' without previously being mapped in the primary hierarchy @ " + path;
            }
        });
    }
    public void severalExistingTargetsForSource(APath path, Object s) {
        warn("Several existing target elements for source element " + s + "@" + path);
    }
    public void diffPreProcessMismatch(APath path) {
        warn("Mismatch in diff operation: One source object skipped by preprocessor, the other not skipped. Skipping. @" + path);
    }
    public void duplicateRegistration(APath path, Object s) {
        warn("The object " + s + " was visited twice while navigating primary references. Maybe one of the references should be deferred, maybe the object is just referenced from two 'parents' as a child.");
    }
    public void generatedCompiledMappingDef(final String code) {
        debug(new AFunction0NoThrow<String>() {
            @Override public String apply() throws RuntimeException {
                return "------------------------- Generated Code --------------------------\n" + code;
            }
        });
    }


    public static AMapperLogger defaultLogger() {
        try {
            return Log4J;
        }
        catch (Throwable th) {
            return StdOut;
        }
    }


    /**
     * controls output of StdOut and StdErr
     */
    public static volatile boolean showDebug = false;

    public static AMapperLogger StdOut = new AMapperLogger() {
        String preamble() {
            return Thread.currentThread().getName() + "@" + System.currentTimeMillis() + ": ";
        }

        @Override public void debug(AFunction0NoThrow<String> msg) {
            if(showDebug) System.out.println(preamble() + "DEBUG " + msg.apply());
        }

        @Override protected void warn(String msg) {
            System.out.println(preamble() + "WARN  " + msg);
        }
    };

    public static AMapperLogger StdErr = new AMapperLogger() {
        String preamble() {
            return Thread.currentThread().getName() + "@" + System.currentTimeMillis() + ": ";
        }

        @Override public void debug(AFunction0NoThrow<String> msg) {
            if(showDebug) System.err.println(preamble() + "DEBUG " + msg.apply());
        }

        @Override protected void warn(String msg) {
            System.err.println(preamble() + "WARN  " + msg);
        }
    };

    public static AMapperLogger Log4J = new AMapperLogger() {
        final Logger log = Logger.getLogger("org.ajjpj.amapper");

        @Override
        public void debug(AFunction0NoThrow<String> msg) {
            if(log.isDebugEnabled()) {
                log.debug(msg.apply());
            }
        }

        @Override
        protected void warn(String msg) {
            log.warn(msg);
        }
    };
}

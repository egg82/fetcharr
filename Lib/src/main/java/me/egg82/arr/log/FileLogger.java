package me.egg82.arr.log;

import me.egg82.arr.config.LogConfigVars;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

import java.io.File;
import java.io.IOException;

public class FileLogger implements Logger {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Logger impl;
    private final boolean silent;
    private final boolean clean;
    private final boolean file;

    private final LogFile traceFile;
    private final LogFile debugFile;
    private final LogFile infoFile;
    private final LogFile warnFile;
    private final LogFile errorFile;
    private final LogFile combinedFile;

    public FileLogger(@NotNull Logger impl) {
        this(impl, LogConfigVars.getFile(LogConfigVars.LOG_DIR), !LogConfigVars.getBool(LogConfigVars.USE_STDOUT), LogConfigVars.getBool(LogConfigVars.CLEAN_STDOUT), LogConfigVars.getBool(LogConfigVars.USE_LOG_FILES));
    }

    public FileLogger(@NotNull Logger impl, @NotNull File logDir) {
        this(impl, logDir, false, false, true);
    }

    public FileLogger(@NotNull Logger impl, @NotNull File logDir, boolean silent, boolean clean, boolean file) {
        this.impl = impl;
        this.silent = silent;
        this.clean = clean;
        this.file = file;

        LogFile traceFile = null;
        try {
            traceFile = new LogFile(new File(logDir, "trace.log"));
        } catch (IOException ex) {
            logger.error("Could not create file {}", new File(logDir, "trace.log").getAbsolutePath(), ex);
        }
        this.traceFile = traceFile;

        LogFile debugFile = null;
        try {
            debugFile = new LogFile(new File(logDir, "debug.log"));
        } catch (IOException ex) {
            logger.error("Could not create file {}", new File(logDir, "debug.log").getAbsolutePath(), ex);
        }
        this.debugFile = debugFile;

        LogFile infoFile = null;
        try {
            infoFile = new LogFile(new File(logDir, "info.log"));
        } catch (IOException ex) {
            logger.error("Could not create file {}", new File(logDir, "info.log").getAbsolutePath(), ex);
        }
        this.infoFile = infoFile;

        LogFile warnFile = null;
        try {
            warnFile = new LogFile(new File(logDir, "warn.log"));
        } catch (IOException ex) {
            logger.error("Could not create file {}", new File(logDir, "warn.log").getAbsolutePath(), ex);
        }
        this.warnFile = warnFile;

        LogFile errorFile = null;
        try {
            errorFile = new LogFile(new File(logDir, "error.log"));
        } catch (IOException ex) {
            logger.error("Could not create file {}", new File(logDir, "error.log").getAbsolutePath(), ex);
        }
        this.errorFile = errorFile;

        LogFile combinedFile = null;
        try {
            combinedFile = new LogFile(new File(logDir, "combined.log"));
        } catch (IOException ex) {
            logger.error("Could not create file {}", new File(logDir, "combined.log").getAbsolutePath(), ex);
        }
        this.combinedFile = combinedFile;
    }

    @Override
    public String getName() {
        return impl.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return impl.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if (!silent) {
            impl.trace(msg);
        }
        if (file) {
            log(traceFile, msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (!silent) {
            impl.trace(format, arg);
        }
        if (file) {
            log(traceFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.trace(format, arg1, arg2);
        }
        if (file) {
            log(traceFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (!silent) {
            impl.trace(format, arguments);
        }
        if (file) {
            log(traceFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.trace(msg, t);
            } else {
                impl.trace(msg);
            }
        }
        if (file) {
            log(traceFile, msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return impl.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (!silent) {
            impl.trace(marker, msg);
        }
        if (file) {
            log(traceFile, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (!silent) {
            impl.trace(marker, format, arg);
        }
        if (file) {
            log(traceFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.trace(marker, format, arg1, arg2);
        }
        if (file) {
            log(traceFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (!silent) {
            impl.trace(marker, format, argArray);
        }
        if (file) {
            log(traceFile, MessageFormatter.format(format, argArray).getMessage());
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.trace(marker, msg, t);
            } else {
                impl.trace(marker, msg);
            }
        }
        if (file) {
            log(traceFile, msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return impl.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (!silent) {
            impl.debug(msg);
        }
        if (file) {
            log(debugFile, msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (!silent) {
            impl.debug(format, arg);
        }
        if (file) {
            log(debugFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.debug(format, arg1, arg2);
        }
        if (file) {
            log(debugFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!silent) {
            impl.debug(format, arguments);
        }
        if (file) {
            log(debugFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.debug(msg, t);
            } else {
                impl.debug(msg);
            }
        }
        if (file) {
            log(debugFile, msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return impl.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (!silent) {
            impl.debug(marker, msg);
        }
        if (file) {
            log(debugFile, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (!silent) {
            impl.debug(marker, format, arg);
        }
        if (file) {
            log(debugFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.debug(marker, format, arg1, arg2);
        }
        if (file) {
            log(debugFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (!silent) {
            impl.debug(marker, format, arguments);
        }
        if (file) {
            log(debugFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.debug(marker, msg, t);
            } else {
                impl.debug(marker, msg);
            }
        }
        if (file) {
            log(debugFile, msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return impl.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if (!silent) {
            impl.info(msg);
        }
        if (file) {
            log(infoFile, msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (!silent) {
            impl.info(format, arg);
        }
        if (file) {
            log(infoFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.info(format, arg1, arg2);
        }
        if (file) {
            log(infoFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (!silent) {
            impl.info(format, arguments);
        }
        if (file) {
            log(infoFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.info(msg, t);
            } else {
                impl.info(msg);
            }
        }
        if (file) {
            log(infoFile, msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return impl.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (!silent) {
            impl.info(marker, msg);
        }
        if (file) {
            log(infoFile, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (!silent) {
            impl.info(marker, format, arg);
        }
        if (file) {
            log(infoFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.info(marker, format, arg1, arg2);
        }
        if (file) {
            log(infoFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (!silent) {
            impl.info(marker, format, arguments);
        }
        if (file) {
            log(infoFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.info(marker, msg, t);
            } else {
                impl.info(marker, msg);
            }
        }
        if (file) {
            log(infoFile, msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return impl.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if (!silent) {
            impl.warn(msg);
        }
        if (file) {
            log(warnFile, msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (!silent) {
            impl.warn(format, arg);
        }
        if (file) {
            log(warnFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (!silent) {
            impl.warn(format, arguments);
        }
        if (file) {
            log(warnFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.warn(format, arg1, arg2);
        }
        if (file) {
            log(warnFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.warn(msg, t);
            } else {
                impl.warn(msg);
            }
        }
        if (file) {
            log(warnFile, msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return impl.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (!silent) {
            impl.warn(marker, msg);
        }
        if (file) {
            log(warnFile, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (!silent) {
            impl.warn(marker, format, arg);
        }
        if (file) {
            log(warnFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.warn(marker, format, arg1, arg2);
        }
        if (file) {
            log(warnFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (!silent) {
            impl.warn(marker, format, arguments);
        }
        if (file) {
            log(warnFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.warn(marker, msg, t);
            } else {
                impl.warn(marker, msg);
            }
        }
        if (file) {
            log(warnFile, msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return impl.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if (!silent) {
            impl.error(msg);
        }
        if (file) {
            log(errorFile, msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (!silent) {
            impl.error(format, arg);
        }
        if (file) {
            log(errorFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.error(format, arg1, arg2);
        }
        if (file) {
            log(errorFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (!silent) {
            impl.error(format, arguments);
        }
        if (file) {
            log(errorFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.error(msg, t);
            } else {
                impl.error(msg);
            }
        }
        if (file) {
            log(errorFile, msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return impl.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (!silent) {
            impl.error(marker, msg);
        }
        if (file) {
            log(errorFile, msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (!silent) {
            impl.error(marker, format, arg);
        }
        if (file) {
            log(errorFile, MessageFormatter.format(format, arg).getMessage());
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (!silent) {
            impl.error(marker, format, arg1, arg2);
        }
        if (file) {
            log(errorFile, MessageFormatter.format(format, arg1, arg2).getMessage());
        }
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (!silent) {
            impl.error(marker, format, arguments);
        }
        if (file) {
            log(errorFile, MessageFormatter.format(format, arguments).getMessage());
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (!silent) {
            if (!clean) {
                impl.error(marker, msg, t);
            } else {
                impl.error(marker, msg);
            }
        }
        if (file) {
            log(errorFile, msg, t);
        }
    }

    private void log(@Nullable LogFile file, @Nullable String msg) {
        if (file != null) {
            try {
                traceFile.log(msg);
            } catch (IOException ex) {
                logger.error("Could not write to log {}", file, ex);
            }
        }
        if (combinedFile != null) {
            try {
                combinedFile.log(msg);
            } catch (IOException ex) {
                logger.error("Could not write to log {}", combinedFile.absolutePath(), ex);
            }
        }
    }

    private void log(@Nullable LogFile file, @Nullable String msg, @Nullable Throwable t) {
        if (file != null) {
            try {
                traceFile.log(msg, t);
            } catch (IOException ex) {
                logger.error("Could not write to log {}", file, ex);
            }
        }
        if (combinedFile != null) {
            try {
                combinedFile.log(msg, t);
            } catch (IOException ex) {
                logger.error("Could not write to log {}", combinedFile.absolutePath(), ex);
            }
        }
    }
}

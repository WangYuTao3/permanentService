/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.administrator.topactivity.utils.log;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Logging helper class.
 */
public class NgdsLog {

    protected static NgdsLogFileHandler sNgdsLogHandler;

    /**
     * 初始化日志本地化输出渠道(调用此方法后日志将输出到文件中)
     *
     * @param context
     * @param logFileName 请用模块名称加上版本号作为唯一文件名
     */
    public static void initFileLoger(Context context, String logFileName) {
        sNgdsLogHandler = NgdsLogFileHandler.getInstance();
        sNgdsLogHandler.init(context, logFileName);
    }

    public static void v(String tag, String content) {
        Log.v(tag, content);
        log2File("v", tag, content);
    }


    public static void d(String tag, String content) {
        Log.d(tag, content);
        log2File("d", tag, content);
    }

    public static void e(String tag, String content) {
        Log.e(tag, content);
        log2File("e", tag, content);
    }

    public static void e(String tag, Throwable tr) {
        String content = tr.toString();
        Log.e(tag, content, tr);
        log2File("e", tag, content);
    }

    public static void e(String tag, Throwable tr, String content) {
        Log.e(tag, content, tr);
        log2File("e", tag, content);
    }

    private static void log2File(String prefix, String tag, String content) {
        if (null != sNgdsLogHandler) {
            sNgdsLogHandler.log2File(prefix + " " + tag + ":" + content);
        }
    }

    public static void l(String tag, Throwable tr) {
        e(tag, tr);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        Log.wtf(tag, msg, tr);
    }

    public static void wtf(String tag, String content) {
        Log.wtf(tag, content);
    }


    /**
     * Formats the caller's provided message and prepends useful info like
     * calling thread ID and method name.
     */
    private static String buildMessage(String format, Object... args) {
        String msg =
                (args == null || args.length == 0) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        // Walk up the stack looking for the first caller outside of VolleyLog.
        // It will be at least two frames up, so start there.
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(NgdsLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "-->" + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }

    /**
     * A simple event log with records containing a name, thread ID, and timestamp.
     */
    static class MarkerLog {

        /**
         * Minimum duration from first marker to last in an marker log to warrant logging.
         */
        private static final long MIN_DURATION_FOR_LOGGING_MS = 0;


        private static class Marker {
            public final String name;
            public final long thread;
            public final long time;

            public Marker(String name, long thread, long time) {
                this.name = name;
                this.thread = thread;
                this.time = time;
            }
        }


        private final List<Marker> mMarkers = new ArrayList<Marker>();
        private boolean mFinished = false;

        /**
         * Adds a marker to this log with the specified name.
         */
        public synchronized void add(String name, long threadId) {
            if (mFinished) {
                throw new IllegalStateException("Marker added to finished log");
            }

            mMarkers.add(new Marker(name, threadId, SystemClock.elapsedRealtime()));
        }

        /**
         * Closes the log, dumping it to logcat if the time difference between
         * the first and last markers is greater than {@link #MIN_DURATION_FOR_LOGGING_MS}.
         *
         * @param header Header string to print above the marker log.
         */
        public synchronized void finish(String tag, String header) {
            mFinished = true;

            long duration = getTotalDuration();
            if (duration <= MIN_DURATION_FOR_LOGGING_MS) {
                return;
            }

            long prevTime = mMarkers.get(0).time;
            d(tag, buildMessage("(%-4d ms) %s", duration, header));
            for (Marker marker : mMarkers) {
                long thisTime = marker.time;
                d(tag, buildMessage("(+%-4d) [%2d] %s", (thisTime - prevTime), marker.thread,
                        marker.name));
                prevTime = thisTime;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            // Catch requests that have been collected (and hence end-of-lifed)
            // but had no debugging output printed for them.
            if (!mFinished) {
                finish("Marker log", "Request on the loose");
                e("Marker log",
                        "Marker log finalized without finish() - uncaught exit point for request");
            }
        }

        /**
         * Returns the time difference between the first and last events in this log.
         */
        private long getTotalDuration() {
            if (mMarkers.size() == 0) {
                return 0;
            }

            long first = mMarkers.get(0).time;
            long last = mMarkers.get(mMarkers.size() - 1).time;
            return last - first;
        }
    }
}

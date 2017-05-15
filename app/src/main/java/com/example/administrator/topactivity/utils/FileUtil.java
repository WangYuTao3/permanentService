package com.example.administrator.topactivity.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * 功能描述：文件工具类
 */
public class FileUtil {

    /**
     * 写入text到日志文件（采用多线程）
     *
     * @param logName <p>
     *                写入log的文件名，如果没有指定，则使用默认的当天日期（2013-9-25）作为文件名
     *                如果有指定则写入的文件名是logName+"("+当天日期")"
     *                </p>
     * @deprecated 这个方法已经过期, 请使用
     */
    public static void writeLogtoSdcard(final String logName, String text, boolean... printLog) {// 新建或打开日志文件
        FileLogUtil.writeLogtoSdcard(logName, text, printLog);
    }

    /**
     * @return sdcard是否挂载
     */
    public static boolean isSdcardMounted() {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDPATH() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public static File creatSDFile(String fileName) throws IOException {
        File file = new File(getSDPATH() + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(getSDPATH() + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(getSDPATH() + fileName);
        return file.exists();
    }


    /**
     * 根据提供的文件夹名称，返回所要创建目录的绝对路径,如果sdcard不存在，则返回null
     * <p>
     * 例如，提供"txtwDownload"，则返回/mnt/sdcard/txtwDownload
     */
    public static File getRootFolder(String folder) {
        if (isSdcardMounted()) {
            File sdDir = Environment.getExternalStorageDirectory();
            File dirFolder = sdDir;
            String[] paths = folder.split("/");
            int length = paths.length;
            for (int i = 0; i < length; i++) {
                dirFolder = new File(dirFolder, paths[i]);
                if (!dirFolder.exists()) {
                    boolean value = dirFolder.mkdir();
                    if (value) {
                    }
                } else {
                }
            }
            return dirFolder;
        }
        return null;
    }

    /**
     * @return boolean
     * @throws
     * @Title: isHashFile
     * @Description: 判断文件是否存在
     */
    public static boolean isHashFile(File file) {
        return file.exists();
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isHashFile(String filePath) {
        return !StringUtil.isEmpty(filePath) && new File(filePath).exists();
    }

    /**
     * 文件保存到指定的内存空间路径
     *
     * @param ctx
     * @param resourceId
     * @param filename
     */
    public static void writeFileToMemory(Context ctx, int resourceId,
                                         String filename) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            File file = ctx.getFileStreamPath(filename);
            if (isHashFile(file)) {
                return;
            }
            is = ctx.getResources().openRawResource(resourceId);
            if (is == null)
                throw new RuntimeException("stream is null");
            fos = ctx.openFileOutput(filename, Context.MODE_WORLD_READABLE);
            byte buf[] = new byte[8 * 1024];
            int numRead = is.read(buf);
            while (numRead != -1) {
                fos.write(buf, 0, numRead);
                numRead = is.read(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                is = null;

            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }

    }

    /**
     * 通过file获取文件数据 add by ouyang
     */
    public static String getFileDataStr(String path) {
        File file = new File(path);
        InputStream inputStream = null;
        StringBuffer retBuffer = new StringBuffer();
        try {
            inputStream = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(inputStream);

            int line = 0;
            byte[] buffer = new byte[1026];
            while ((line = dis.read(buffer)) != -1) {
                retBuffer.append(new String(Base64Helper.encode(buffer, 0, line, Base64Helper.DEFAULT)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retBuffer.toString();
    }

    /**
     * 功能描述:日志工具类 <br />
     * 创建时间:2013-10-10 下午02:33:14
     *
     * @author Dave
     */
    public static class FileLogUtil {
        private static final String TAG = "FileLogUtil";
        /**
         * 日志文件在sdcard中的路径
         */
        private static String ROOT_FOLDER = "txtw/HAPPY_LOG";
        private static boolean isWriteLog = true;

        private static FileLogUtil INSTANCE;
        private ThreadPoolExecutor writeLogExecutor;
        private BlockingQueue<Runnable> writeLogBlock;
        private static int mDateNumber;

        private FileLogUtil() {
            writeLogBlock = new LinkedBlockingQueue<Runnable>();
            writeLogExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 120, TimeUnit.SECONDS, writeLogBlock);
        }

        /**
         * 单例模式
         */
        public static FileLogUtil getInstant() {
            if (INSTANCE == null) {
                synchronized (FileLogUtil.class) {
                    // when more than two threads run into the first null check same
                    // time, to avoid instanced more than one time, it needs to be
                    // checked again.
                    if (INSTANCE == null) {
                        INSTANCE = new FileLogUtil();
                    }
                }
            }
            return INSTANCE;
        }

        private ThreadPoolExecutor getWriteLogExecutor() {
            if (writeLogExecutor == null ||
                    writeLogExecutor.isShutdown() ||
                    writeLogExecutor.isTerminated() ||
                    writeLogExecutor.isTerminating()
                    ) {
                writeLogBlock = new LinkedBlockingQueue<Runnable>();
                writeLogExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 30, TimeUnit.SECONDS, writeLogBlock);
            }
            return writeLogExecutor;
        }


        /**
         * 写入text到日志文件(采用多线程)
         *
         * @param logName <p>
         *                写入log的文件名，如果没有指定，则使用默认的当天日期（2013-9-25）作为文件名
         *                如果有指定则写入的文件名是logName+"("+当天日期")"
         *                </p>
         */
        public static void writeLogtoSdcard(final String logName, final String text, final boolean... printLog) {// 新建或打开日志文件
            String replaceLogName = logName;
            // wusq 文件名中包含":"或者"："会出现"open failed einval"
            replaceLogName = replaceLogName.replaceAll(":", "");
            replaceLogName = replaceLogName.replaceAll("：", "");
            final String willLogFileName = replaceLogName;
            if (printLog.length > 0) {
                boolean shouldPrintLog = printLog[0];
                if (shouldPrintLog) {
                    Log.v(willLogFileName, text);
                }
            }
            FileLogUtil.getInstant().getWriteLogExecutor().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            realWriteLog(willLogFileName, text, printLog);
                        }
                    });
        }

        private static void realWriteLog(final String logName, String text,
                                         boolean... printLog) {
            if (isWriteLog && FileUtil.isSdcardMounted()) {
                String filename;
                String date = DateTimeUtil.getNowTime(DateTimeUtil.dateFormat);
                if (logName == null) {
                    filename = date;
                } else {
                    filename = date + "/" + logName;
                }
                // wusq 清除一周之前的日志
                cleanLogBeforeOneWeek(logName);
                mDateNumber = StringUtil.stringToInt(date.replace("-", ""), 0);
                clearLogBeforeOneWeekDir(FileUtil.getRootFolder(ROOT_FOLDER));
                String willWriteMessage = DateTimeUtil.getTime() + "    "
                        + text;
                beginWriteLog(filename, willWriteMessage);
            }
        }

        private static void beginWriteLog(String filename, String willWriteMessage) {
            FileWriter writer = null;
            BufferedWriter bufWriter = null;
            try {
                File logFolder = FileUtil.getRootFolder(ROOT_FOLDER);
                File file = null;
                if (logFolder != null) {
                    file = new File(logFolder, filename + ".log");
                }
                if (file != null) {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    writer = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                    bufWriter = new BufferedWriter(writer);
                    bufWriter.write(willWriteMessage);
                    bufWriter.write("\n");//增加一个分行
                    bufWriter.newLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufWriter != null) {
                        bufWriter.close();
                        bufWriter = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static File getLogFileDir() {
            File logFolder = FileUtil.getRootFolder(ROOT_FOLDER);
            return logFolder;
        }

        /**
         * 清除一周之前的日志
         *
         * @param fileTag 如果null则清除默认的文件,非null则清除fileTag对应的文件名
         */
        private static void cleanLogBeforeOneWeek(String fileTag) {
            File logFolder = getLogFileDir();
            if (logFolder != null) {
                File[] files = logFolder.listFiles(new LogFilenameFilter(fileTag));
                int fileSize = files == null ? 0 : files.length;
                if (fileSize > 0) {
                    String today = DateTimeUtil.getNowTime(DateTimeUtil.dateFormat);
                    String tmpDate;
                    File tmpFile;
                    for (int i = 0; i < fileSize; i++) {
                        tmpFile = files[i];
                        tmpDate = StringUtil.getDate(tmpFile.getName());
                        if (!DateTimeUtil.computeTwoDaysWithInSpecified(tmpDate,
                                today, 6)) {// 如果存储的文件名的时间戳和今天的间隔在7天之外，则删除文件
                            if (files[i].exists()) {
                                files[i].delete();
                            }
                        }
                    }
                }
            }
        }

        private static void clearLogBeforeOneWeekDir(File dir) {
            if (dir == null || !dir.exists()) {
                return;
            }
            //删除旧的日志文件
            File[] files = dir.listFiles(logFilenameFilter);
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            File[] fileDirs = dir.listFiles(dirFileFilter);
            if (fileDirs != null) {
                for (File file : fileDirs) { //迭代删除
                    delete(file);
                }
            }
        }

        private static FileFilter dirFileFilter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {

                if (pathname.isDirectory()) {
                    String strName = pathname.getName().replace("-", "");
                    if (!StringUtil.isEmpty(strName)) {
                        int dateNumber = StringUtil.stringToInt(strName, 0);
                        return mDateNumber - dateNumber > 7;
                    }
                }

                return false;
            }
        };

        private static FilenameFilter logFilenameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        };

        /**
         * 等待线程池关闭
         */
        public void shutdownAndAwaitTermination() {
            INSTANCE = null;
            if (writeLogExecutor != null) {
                writeLogExecutor.purge();
                writeLogExecutor.shutdown();
            }

            // Disable new tasks from being submitted
            // try {
            // // Wait a while for existing tasks to terminate
            // if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            // executor.shutdownNow(); // Cancel currently executing tasks
            // // Wait a while for tasks to respond to being cancelled
            // if (!executor.awaitTermination(60, TimeUnit.SECONDS))
            // Log.e("AsyncImageLoader", "Pool did not terminate");
            // }
            // } catch (InterruptedException ie) {
            // // (Re-)Cancel if current thread also interrupted
            // executor.shutdownNow();
            // // Preserve interrupt status
            // Thread.currentThread().interrupt();
            // }
        }

        private static class LogFilenameFilter implements FilenameFilter {
            private String fileTag;

            private LogFilenameFilter(String fileTag) {
                this.fileTag = fileTag;
            }

            @Override
            public boolean accept(File dir, String filename) {
                if (fileTag != null) {
                    return filename.startsWith(fileTag + "(");
                } else {
                    return isOnlyDate(filename);
                }
            }

            /**
             * 之所以加上(txtw)*是为了兼容旧版本
             *
             * @param filename
             * @return
             */
            private boolean isOnlyDate(String filename) {
                return Pattern.matches(
                        "(txtw)*(\\d{4}-\\d{1,2}-\\d{1,2})(\\.log)", filename);
            }

        }
    }

    /**
     * @param ctx
     * @param fileName
     */
    public static void changeFileMode(Context ctx, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isPicture(String url) {
        if (StringUtil.isEmpty(url)) {
            return false;
        }
        String strSuffix = "";
        if (url.indexOf(".") > -1) {
            strSuffix = url.substring(url.lastIndexOf("."), url.length());
        }
        if (".jpg".equalsIgnoreCase(strSuffix) || ".png".equalsIgnoreCase(strSuffix) || ".jpeg".equalsIgnoreCase(strSuffix)) {
            return true;
        }
        return false;
    }

    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {

            toFile.getParentFile().mkdirs();

        }

        if (toFile.exists() && rewrite) {

            toFile.delete();

        }

        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;

        try {

//			chmodeFile(toFile.getParentFile());
//			chmodeFile(toFile);
            fosfrom = new FileInputStream(
                    fromFile);

            fosto = new FileOutputStream(toFile);

            byte bt[] = new byte[1024];

            int c;

            while ((c = fosfrom.read(bt)) > 0) {

                fosto.write(bt, 0, c); // 将内容写到新文件当中

            }


        } catch (Exception ex) {

            Log.e("readfile", ex.getMessage());

            ex.printStackTrace();
        } finally {
            try {
                if (fosfrom != null) {
                    fosfrom.close();
                    fosfrom = null;
                }

                if (fosto != null) {
                    fosto.close();
                    fosto = null;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void chmodeFile(File file) {
        try {
            String path = file.getAbsolutePath();
            String chmodString = "chmod 777 " + path;
            Runtime run = Runtime.getRuntime();
            run.exec(chmodString);

            chmodString = "chmod u+s " + path;
            run.exec(chmodString);

            chmodString = "chmod g+s " + path;
            run.exec(chmodString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sd卡路径的方法   <br>
     *
     * @return
     */
    public static ArrayList<String> getExternalStorageDirectory(Context ctx) {
        File file = Environment.getDataDirectory();
        ArrayList<String> list = new ArrayList<String>();
        if (file != null && file.exists()) {
            list.add(file.getPath());
        }
        if (isSdcardMounted()) {
            list.add(getSDPATH());
        }

        return list;
//
//		StorageManager mStorageManager = (StorageManager)ctx.getSystemService(Context.STORAGE_SERVICE);
//		try {
//			Class<?> claz = Class.forName("android.os.storage.StorageManager");
//			Method expand = claz.getMethod("getVolumePaths");
//			return (String[]) expand.invoke(mStorageManager);
//		} catch (Exception e) {
//			if (android.os.Environment.getExternalStorageState().equals(
//					android.os.Environment.MEDIA_MOUNTED)) {
//				File sdcard = Environment.getExternalStorageDirectory();
//				if (sdcard != null) {
//					return new String[]{sdcard.getPath()};
//				}
//			}
//
//		}
//		return null;
    }

    /**
     * 根据路径获取存储空间，例如 ："/storage/sdcard0"，"/storage/sdcard1"
     *
     * @param mContext
     * @param strPath
     * @return
     */
    public static String getExternalStorageSize(Context mContext, String strPath) {
        try {
            StatFs stat = new StatFs(strPath);
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            if (totalBlocks * blockSize == 0) {
                return "0";
            }
            return Formatter.formatFileSize(mContext, totalBlocks * blockSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }


    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destDirName + srcFile.getName()));
    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public static boolean moveDirectory(String srcDirName, String destDirName) {

        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile())
                moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            else if (sourceFile.isDirectory())
                moveDirectory(sourceFile.getAbsolutePath(),
                        destDir.getAbsolutePath() + File.separator + sourceFile.getName());
            else
                ;
        }
        return srcDir.delete();
    }


    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static boolean DeleteFile(Context context, File file) {
        return delete(file);
    }

    private static boolean delete(File file) {
        if (file.exists() == false) {
            /*mHandler.sendEmptyMessage(0); */
            return false;
        } else {
            if (file.isFile()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }
                for (File f : childFile) {
                    delete(f);
                }
                return file.delete();
            }
        }
        return false;
    }

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            creatSDDir(path);
            file = creatSDFile(path + "/" + fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int ret;
            while ((ret = input.read(buffer)) != -1) {
                output.write(buffer, 0, ret);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File write2SDFromInput(File file, InputStream input, long contentLength) {
        if (file == null) {
            return null;
        }

        long downloadLength = 0l;
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];

            int ret = 0;
            while ((ret = input.read(buffer)) != -1) {
                output.write(buffer, 0, ret);
                downloadLength += ret;
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (downloadLength < contentLength) {
            // 下载失败
            file.delete();
            return null;
        }
        return file;
    }

    /**
     * Bitmap转成文件
     *
     * @param bitmap
     */
    public static String saveBitmapFile(Bitmap bitmap, Context context) {
        if (bitmap != null) {
            String fileName = System.currentTimeMillis() + ".png";
            try {
                createCellDir("", context);
                File file = createCellFile(File.separator + fileName, context);
                FileOutputStream fos;
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 在Cell Phone上创建目录
     *
     * @param dirName
     */
    private static File createCellDir(String dirName, Context context) {
        File dir = new File(getCELLPATH(context) + dirName);
        dir.mkdir();
        return dir;
    }

    private static String getCELLPATH(Context context) {
        return context.getFilesDir().getPath();
    }

    /**
     * 在Cell Phone上创建文件
     *
     * @throws IOException
     */
    private static File createCellFile(String fileName, Context context)
            throws IOException {
        File file = new File(getCELLPATH(context) + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 传入目录与文件名创建文件
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static File createFileWithDir(String dir, String fileName) {
        return new File(dir, fileName);
    }

    public static File getSdCardPath() {
        if (!Build.MODEL.equalsIgnoreCase("HUAWEI P6-T00")) {
            return Environment.getExternalStorageDirectory();
        }
        String sdcard_path = null;
        String sd_default = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        Log.d("text", sd_default);
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sdcard_path == null)
            return Environment.getExternalStorageDirectory();
        Log.d("text", sdcard_path);
        return new File(sdcard_path);
    }

}

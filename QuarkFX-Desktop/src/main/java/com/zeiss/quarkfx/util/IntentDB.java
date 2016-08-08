package com.zeiss.quarkfx.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zeiss.quarkfx.QuarkFXApplication;
import com.zeiss.quarkfx.logging.Log;
import com.zeiss.quarkfx.platformindependant.IntentP;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URLDecoder;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The Intent database collects all needed information to be able to send desktop intents
 */
public class IntentDB {

    private final List<IntentApplication> fas = new LinkedList<>();

    @Nullable
    private File jarFolder = null;

    private static String escapeStringForJavaArgs(@NotNull String s) {
        StringBuilder escaped = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '"') {
                escaped.append("\\");
                escaped.append(c);
                continue;
            }
            escaped.append(c);
        }
        escaped.insert(0, '"');
        escaped.append('"');
        return escaped.toString();
    }

    private static boolean isMimeTypeRight(@NotNull String filter, @NotNull String mimetype) {
        if ("*".equals(mimetype)) {
            return true;
        }
        String[] filterSplits = filter.split("/");
        if (filterSplits.length != 2) {
            throw new IllegalArgumentException("mimetype not in format string/string: " + filter);
        }
        String[] mimetypesplits = filter.split("/");
        if (mimetypesplits.length != 2) {
            throw new IllegalArgumentException("mimetype not in format string/string: " + mimetype);
        }
        if ("*".equals(mimetypesplits[0])) {
            return true;
        }
        if (!"*".equals(filterSplits[0]) && !mimetypesplits[0].equals(filterSplits[0])) {
            return false;
        }
        if ("*".equals(mimetypesplits[1])) {
            return true;
        }
        return "*".equals(filterSplits[1]) || mimetypesplits[1].equals(filterSplits[1]);
    }

    @NotNull
    private static String parseCurrentWindow() {
        Window window = QuarkFXApplication.getInstance().getScene().getWindow();
        double height = window.getHeight();
        double width = window.getWidth();
        double x = window.getX();
        double y = window.getY();
        return x + ":" + y + ":" + width + ":" + height;
    }

    /**
     * method to set the new position and size of the window
     *
     * @param encodedProperties String encoding the size and position (x:y:width:height)
     */
    public static void alterCurrentWindow(@NotNull String encodedProperties) {
        String[] splits = encodedProperties.split(":");
        if (splits.length != 4) {
            Log.error(IntentDB.class.getSimpleName(), "Malformed Window placement");
            return;
        }
        try {
            double x = Double.parseDouble(splits[0]);
            double y = Double.parseDouble(splits[1]);
            double width = Double.parseDouble(splits[2]);
            double height = Double.parseDouble(splits[3]);
            Window window = QuarkFXApplication.getInstance().getScene().getWindow();
            window.setX(x);
            window.setY(y);
            window.setWidth(width);
            window.setHeight(height);
        } catch (NumberFormatException e) {
            Log.error(IntentDB.class.getSimpleName(), "Malformed Window parameters");
        }

    }

    /**
     * fetches all jar files to update the internal database
     */
    public void refresh() {

        fas.clear();

        String path = QuarkFXApplication.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.error(IntentDB.class.getSimpleName(), "Could not refresh, url decode failed");
            e.printStackTrace();
            return;
        }

        File jarfile = new File(path);
        if (!jarfile.exists()) {
            Log.error(IntentDB.class.getSimpleName(), "Cannot find my own JAR :'( path: " + path);
            return;
        }
        File jarFolder = jarfile.getParentFile();
        if (!jarFolder.exists()) {
            Log.error(IntentDB.class.getSimpleName(), "Cannot find my own Parent Folder:'(");
            return;
        }
        this.jarFolder = jarFolder;
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //TODO: Dereference of jarFolder.listFiles() may produce NullPointerException?!
        final File[] files = jarFolder.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.toString().endsWith(".jar")) {
                try (JarFile jar = new JarFile(f);) {
                    Log.info(IntentDB.class.getSimpleName(), "Scanning " + f.toString() + " ...");
                    String main = jar.getManifest().getMainAttributes().getValue("Main-Class");
                    JarEntry entry = jar.getJarEntry("DesktopManifest.json");
                    if (entry == null) {
                        Log.error(IntentDB.class.getSimpleName(), "Cannot find Manifest :'(");
                        continue;
                    }
                    InputStream is = jar.getInputStream(entry);
                    Scanner scanner = new Scanner(is).useDelimiter("\\A");
                    String json = scanner.hasNext() ? scanner.next() : null;
                    if (json == null) {
                        Log.error(IntentDB.class.getSimpleName(), "DesktopManifest.json is empty");
                        continue;
                    }
                    scanner.close();
                    is.close();
                    try {
                        IntentApplication fa = gson.fromJson(json, IntentApplication.class);
                        fa.jarfilename = f.getName();
                        fa.main = main;
                        fas.add(fa);
                        String message = "Loaded IntentApplication: \n" +
                                "   label: " + fa.label +
                                "   version: " + fa.version +
                                "   icon: " + fa.iconPath +
                                "   description: " + fa.description +
                                "   main: " + fa.main +
                                "   jarfileName: " + fa.jarfilename;
                        Log.debug(IntentDB.class.getSimpleName(), message);
                    } catch (JsonSyntaxException e) {
                        Log.error(IntentDB.class.getSimpleName(), "DesktopManifest.json is malformed");
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //TODO: why return a LinkedList?? -> never used

    /**
     * launches an intent :D
     * @param intent the intent object to be transferred
     * @param main the main class, packagename inclusive
     * @return
     */
    @NotNull
    public List<String> launchIntent(IntentP intent, String main) {
        if (jarFolder == null) {
            Log.error(IntentDB.class.getSimpleName(), "You handled the Intent wrong!");
            return new LinkedList<>();
        }
        IntentApplication fa = getForMain(main);
        try {
            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.create();

            String json = gson.toJson(intent);

            json = escapeStringForJavaArgs(json);

            //FIXME unsafe
            //ProcessBuilder builder = new ProcessBuilder("java -jar \""+new File(jarFolder, fa.jarfilename).getAbsolutePath()+"\" "+json);
            //builder.redirectErrorStream(true);
            //Process proc = builder.start();
            Runtime rt = Runtime.getRuntime();
            String windowInformation = parseCurrentWindow();

            Stage stage = (Stage) QuarkFXApplication.getInstance().getScene().getWindow();
            stage.close();
            Process proc = rt.exec("java -jar \"" + new File(jarFolder, fa.jarfilename).getAbsolutePath() + "\" " + json + " " + windowInformation);

            InputStream stdin = proc.getInputStream();
            InputStream stderr = proc.getErrorStream();
            //SequenceInputStream sis = new SequenceInputStream(stdin, stderr);

            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);

            LinkedList<String> lines = new LinkedList<>();
            String line;
            while ((line = br.readLine()) != null)
                lines.addLast(line);
            int exitVal = proc.waitFor();
            if (exitVal != 0) {
                Log.error(IntentDB.class.getSimpleName(), "Process exited with exitvalue other than 0: " + exitVal);
            }
            br.close();
            isr.close();
            stdin.close();

            //TODO remove
            for (String line1 : lines) {
                System.out.println(fa.label + " -> " + line1);
            }
            Stage newstage = new Stage();
            newstage.setScene(QuarkFXApplication.getInstance().getScene());
            //TODO get where window was moved!
            alterCurrentWindow(windowInformation);
            newstage.show();
            return lines;
        } catch (Exception t) {
            t.printStackTrace();
        }
        return new LinkedList<>();
    }

    private @Nullable IntentApplication getForMain(String main) {
        return getSafeForMain(main, 0);
    }

    /**
     * If this Intent can theoretically be sent
     * @param main
     * @return
     */
    public boolean isCallable(@NotNull IntentP main) {
        return !getPossibleReceivers(main).isEmpty();
    }

    //FIXME
    private @Nullable IntentApplication getSafeForMain(String main, int loop) {
        loop++;
        try {
            for (IntentApplication fa : fas) {
                if (fa.main.equals(main)) {
                    return fa;
                }
            }
        } catch (ConcurrentModificationException e) {
            Log.info(IntentDB.class.getSimpleName(), "Whoops, we've just refreshed, lets redo this; loop: " + loop);
            if (loop >= 10) {
                throw new RuntimeException("Well we've repeated this a little toooooo much");
            }
            return getSafeForMain(main, loop);
        }
        return null;
    }

    /**
     * collects all the possible applications able to receive this intent
     * @param intent the intent object to be checked
     * @return
     */
    @NotNull
    public List<IntentApplication> getPossibleReceivers(@NotNull IntentP intent) {
        System.out.println("### CALLABLE ###");
        LinkedList<IntentApplication> apps = new LinkedList<>();
        if (intent.hasSpecificReceiver()) {
            System.out.println("### SHOULD WORK ###");
            IntentApplication app = getForMain(intent.getReceiverMainClass());
            apps.add(app);
            return apps;
        }
        //FIXME not Thread safe
        for (IntentApplication fa : fas) {
            for (IntentP filter : fa.intentfilters) {
                if (filter.getAction().equals(intent.getAction())) {
                    //TODO category
                    if (isMimeTypeRight(filter.getData(), intent.getMimeType())) {
                        apps.add(fa);
                    } else {
                        System.out.println("+++ " + fa.label + " doesnt have mimetype: " + intent.getMimeType());
                    }
                } else {
                    System.out.println("+++ " + fa.label + " doesnt have action: " + intent.getAction());
                }
            }
        }
        return apps;
    }
}

package main;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Klass som hämtar blankett-mallen och sköter ändringar av platshållare.
 */
public class Blankett {
    private String blankett;

    public Blankett() {
        blankett = readFile("/mall.rtf");
    }

    public void replace(String remove, String add) {
        String update = blankett.replaceFirst("%%" + remove + "%%", add);
        blankett = update;
    }

    public void removePlaceHolders() {
        String update = blankett.replaceAll("\\%%.*?\\%% ?", "");
        blankett = update;
    }

    public String readFile(String path) {
        try {
            InputStream in = this.getClass().getResourceAsStream(path);

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public void printFile(String filename) {
        String pathString = filename + ".rtf";
        int i = 2;
        while (Files.exists(Paths.get(pathString))) {
            pathString = filename + "-" + i + ".rtf";
            i++;
        }
        try (PrintStream out = new PrintStream(new FileOutputStream(pathString))) {
            out.print(blankett);
        } catch (Exception e) {
            System.out.println("FEL " + e);
        }
    }
}

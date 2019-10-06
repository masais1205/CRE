package cre.algorithm.tool;
import java.io.*;

public class TemporaryFileManager {

    /**
     * Release file to temporary folder.
     * @param filePath path of source file
     * @return path of new File in temporary folder.
     * @throws Exception if can not file source file.
     */
    public File releasePackedFile(String filePath) throws Exception {
        File result;
        File newFile = File.createTempFile("cre.algorithm.tool_eca890876c0ef3237_", ".R");
        InputStream is = getClass().getResourceAsStream(filePath);
        if (is == null) {
            throw new Exception("Can not find resource: " + filePath);
        }
        try (BufferedInputStream bis = new BufferedInputStream(is); DataOutputStream bos = new DataOutputStream(new FileOutputStream(newFile))) {
            byte[] buf = new byte[1024 * 1024];
            int count;
            while ((count = bis.read(buf)) != -1) {
                bos.write(buf, 0, count);
            }
            result = newFile;
            newFile.deleteOnExit();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static TemporaryFileManager getInstance() {
        if (instance == null) {
            instance = new TemporaryFileManager();
        }
        return instance;
    }

    private TemporaryFileManager() {
    }

    private static TemporaryFileManager instance;
}

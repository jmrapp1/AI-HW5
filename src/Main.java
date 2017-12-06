import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        int[][][] faces = readBitmaps();
        System.out.println("Read in " + faces.length + " people's faces");
    }

    public static int[][][] readBitmaps() {
        File[] people = new File("facesDB/").listFiles(f -> f.isDirectory()); // Get all files that are dirs
        int[][][] matrix = new int[people.length][][];

        for (int i = 0; i < people.length; i++) {
            File dir = people[i];
            File[] faces = dir.listFiles(f -> !f.isDirectory() && f.getName().contains(".BMP") && !f.getName().startsWith("10")); // Get all face images, don't include 10
            matrix[i] = new int[faces.length][]; // create array to hold the images

            for (int j = 0; j < faces.length; j++) {
                File face = faces[j];
                try {
                    matrix[i][j] = BitMapLoader.loadbitmap(new FileInputStream(face));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return matrix;
    }

}

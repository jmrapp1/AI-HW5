import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        int[][] faces = readBitmaps();
        System.out.println("Read in " + faces.length + " people's faces");
    }

    private static int[][] getStatFeatures(int[][] faces) {
        int[][] statFeatures = new int[faces.length][];
        for (int i = 0; i < faces.length; i++) {
            statFeatures[i] = new int[5];
            for (int j = 0; j < faces[i].length; j++) {

            }
        }
        return statFeatures;
    }

    private static void getAllBitmapFiles(ArrayList<File> files, File directory) {
        File[] allFiles = directory.listFiles();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                getAllBitmapFiles(files, f);
            } else if (f.getName().contains(".BMP") && !f.getName().startsWith("10")) {
                files.add(f);
            }
        }
    }

    public static int[][] readBitmaps() {
        ArrayList<File> faces = new ArrayList<>();
        getAllBitmapFiles(faces, new File("facesDB/"));

        int[][] matrix = new int[faces.size()][];

        for (int i = 0; i < faces.size(); i++) {
            File face = faces.get(i);
            try {
                matrix[i] = BitMapLoader.loadbitmap(new FileInputStream(face));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return matrix;
    }

    //this works under the assumption that the array is even in size, each image being represented by an array of size 48*48

    public static double median(int[] image) {
        Arrays.sort(image);
        double v1 = image[image.length / 2];
        double v2 = image[(image.length / 2) - 1];
        double median = (v1 + v2) / 2;
        return median;
    }//end median

    public static double findMean(int[] array) {
        double mean = 0;
        int sum = 0;

        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i];
        }
        mean = sum / (array.length);
        return mean;
    }

    public static double findVariance(int[] array) {
        double variance = 0;
        double sum = 0;
        double mean = findMean(array);
        for (int i = 0; i < array.length; i++) {
            sum = sum + Math.pow(array[i] - mean, 2);
        }
        variance = sum / array.length;
        return variance;

    }

    public static double skewness(int[] array) {
        double skewness = 0;
        double mean = findMean(array);
        double sumNumerator = 0;
        double sumDenominator = 0;

        for (int i = 0; i < array.length; i++) {
            sumNumerator = sumNumerator + Math.pow(array[i] - mean, 3);
        }
        sumNumerator = sumNumerator / array.length;

        for (int i = 0; i < array.length; i++) {
            sumDenominator = sumDenominator + Math.pow(array[i] - mean, 2);
        }
        sumDenominator = Math.pow((sumDenominator / 2), 3 / 2);

        return sumNumerator / sumDenominator;
    }

}

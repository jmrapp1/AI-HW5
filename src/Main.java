import java.util.Arrays;

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

    //this works under the assumption that the array is even in size, each image being represented by an array of size 48*48
    public static double median (int[]image){
        Arrays.sort(image);
        double v1 = image[image.length/2];
        double v2 = image[(image.length/2)-1];
        double median = (v1+v2)/2;
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

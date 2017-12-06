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
        double[][] statFeatures = getStatFeatures(faces);

    }

    private static double[][] getStatFeatures(int[][] faces) {
        double[][] statFeatures = new double[faces.length][];
        for (int i = 0; i < faces.length; i++) {
            int[] face = faces[i];
            statFeatures[i] = new double[5];
            statFeatures[i][0] = median(face);
            statFeatures[i][1] = findMean(face);
            statFeatures[i][2] = findVariance(face);
            statFeatures[i][3] = skewness(face);
            statFeatures[i][4] = kurtosis(face);
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
        int sum = 0;

        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i];
        }
        return sum / (array.length);
    }

    public static double findVariance(int[] array) {
        double sum = 0;
        double mean = findMean(array);
        for (int i = 0; i < array.length; i++) {
            sum = sum + Math.pow(array[i] - mean, 2);
        }
        return sum / array.length;
    }

    public static double skewness(int[] array) {
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

    public static double kurtosis(int[]image){
        double kurtosis = 0;
        double sumNumerator = 0;
        double sumDenominator = 0;
        double mean = findMean(image);

        for (int i = 0; i < image.length; i++) {
            sumNumerator = sumNumerator + Math.pow(image[i] - mean, 4);
        }
        sumNumerator = sumNumerator / image.length;

        for (int i = 0; i < image.length; i++) {
            sumDenominator = sumDenominator + Math.pow(image[i] - mean, 2);
        }
        sumDenominator = (Math.pow((sumDenominator / 2), 2));
        kurtosis = sumNumerator / sumDenominator;
        return kurtosis-3;
    }//end


    //***************************************************//
    //              Maximum Values                       //
    //***************************************************//

    public static double median_max(int[][]images){
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[0][i] > max){
                max = images[0][i];
            }
        }
        return max;
    }
    public static double mean_max(int[][]images){
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[1][i] > max){
                max = images[1][i];
            }
        }
        return max;
    }
    public static double variance_max(int[][]images){
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[2][i] > max){
                max = images[2][i];
            }
        }
        return max;
    }
    public static double skewness_max(int[][]images){
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[3][i] > max){
                max = images[3][i];
            }
        }
        return max;
    }
    public static double kurtosis_max(int[][]images){
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[4][i] > max){
                max = images[4][i];
            }
        }
        return max;
    }


    //***************************************************//
    //              Minimum Values                       //
    //***************************************************//

    public static double median_min(int[][]images){
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[0][i] > min){
                min = images[0][i];
            }
        }
        return min;
    }

    public static double mean_min(int[][]images){
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[1][i] > min){
                min = images[1][i];
            }
        }
        return min;
    }

    public static double variance_min(int[][]images){
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[2][i] > min){
                min = images[2][i];
            }
        }
        return min;
    }

    public static double skewness_min(int[][]images){
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[3][i] > min){
                min = images[3][i];
            }
        }
        return min;
    }

    public static double kurtosis_min(int[][]images){
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 360; i++){
            if(images[4][i] > min){
                min = images[4][i];
            }
        }
        return min;
    }

    /*
        int mean_min = Integer.MAX_VALUE;
        int variance_min = Integer.MAX_VALUE;
        int skewness_min = Integer.MAX_VALUE;
        int kurtosis_min = Integer.MAX_VALUE;
    */

}

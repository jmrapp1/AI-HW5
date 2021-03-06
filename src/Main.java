import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) {
        outputFaceData("faces-data", false, false);
        outputFaceData("faces-subject", false, true);
        outputFaceData("targets-data", true, false);
        outputFaceData("targets-subject", true, true);
    }

    public static void outputFaceData(String name, boolean findOnlyTarget, boolean onlySubject) {
        FacesData data = readBitmaps(findOnlyTarget);
        int[] subjects = data.getSubjects();
        int[][] faces = data.getFaces();

        double[][] statFeatures = getStatFeatures(faces);
        double[][] normalizedFeatures = getNormalizedFeatures(statFeatures);
        System.out.println("Processed " + faces.length + " people's faces");
        outputFile(name, normalizedFeatures, subjects, onlySubject);
    }

    private static String getBinarySubject(int subject, boolean csv) {
        String binary = Integer.toBinaryString(subject);
        String str = "";
        String[] split = binary.split("");
        if (split.length < 6) {
            int total = 6 - split.length;
            for (int i = 0; i < total; i++) {
                str += "0.1" + (csv ? ", " : " ");
            }
        }
        for (int i = 0; i < split.length; i++) {
            String type = split[i];
            if (type.equals("0")) type = "0.1";
            else if (type.equals("1")) type = "0.9";
            str += type;
            if (i + 1 < split.length) {
                str += (csv ? ", " : " ");
            }
        }
        return str;
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

    private static double[][] getNormalizedFeatures(double[][] faces) {
        double[][] norm = new double[faces.length][];
        for (int i = 0; i < faces.length; i++) {
            double[] face = faces[i];
            norm[i] = new double[5];
            for (int j = 0; j < norm[i].length; j++) {
                norm[i][j] = getNormal(face[j], findMin(faces, j), findMax(faces, j));
            }
        }
        return norm;
    }

    private static void outputFile(String fileName, double[][] faces, int[] subjects, boolean onlySubject) {
        try {
            PrintWriter outDat = new PrintWriter(fileName + ".dat");
            PrintWriter outCsv = new PrintWriter(fileName + ".csv");
            for (int i = 0; i < faces.length; i++) {
                String rowDat = "";
                String rowCsv = "";
                if (!onlySubject) {
                    for (int j = 0; j < faces[i].length; j++) {
                        rowDat += faces[i][j] + (j + 1 == faces[i].length ? "" : " ");
                        rowCsv += faces[i][j] + (j + 1 == faces[i].length ? "" : ", ");
                    }
                }
                if (onlySubject) {
                    rowDat += getBinarySubject(subjects[i], false);
                    rowCsv += getBinarySubject(subjects[i], true);
                }
                outDat.println(rowDat);
                outCsv.println(rowCsv);
            }
            outDat.flush();
            outDat.close();
            outCsv.flush();
            outCsv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getNormal(double x, double min, double max) {
        return (x - min) / (max - min);
    }

    private static void getAllBitmapFiles(ArrayList<File> files, File directory, boolean findOnlyTarget) {
        File[] allFiles = directory.listFiles();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                getAllBitmapFiles(files, f, findOnlyTarget);
            } else if (f.getName().contains(".BMP") && ((findOnlyTarget && f.getName().startsWith("10")) || (!findOnlyTarget && !f.getName().startsWith("10")))) {
                files.add(f);
            }
        }
    }

    public static FacesData readBitmaps(boolean findOnlyTarget) {
        ArrayList<File> faces = new ArrayList<>();
        getAllBitmapFiles(faces, new File("facesDB/"), findOnlyTarget);

        int[][] matrix = new int[faces.size()][];
        int[] subjects = new int[faces.size()];

        for (int i = 0; i < faces.size(); i++) {
            File face = faces.get(i);
            try {
                int subject = Integer.parseInt(face.getParentFile().getName().replace("S", ""));
                matrix[i] = BitMapLoader.loadbitmap(new FileInputStream(face));
                subjects[i] = subject;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new FacesData(subjects, matrix);
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

    public static double kurtosis(int[] image) {
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
        return kurtosis - 3;
    }//end


    //***************************************************//
    //              Maximum Values                       //
    //***************************************************//

    public static double findMax(double[][] images, int index) {
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < images.length; i++) {
            if (images[i][index] > max) {
                max = images[i][index];
            }
        }
        return max;
    }


    //***************************************************//
    //              Minimum Values                       //
    //***************************************************//

    public static double findMin(double[][] images, int index) {
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < images.length; i++) {
            if (images[i][index] < min) {
                min = images[i][index];
            }
        }
        return min;
    }

}

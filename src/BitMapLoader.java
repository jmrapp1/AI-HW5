/*
   ***************************************************************
   BitMapLoader.java reads in an 8-bit grey-scale bitmap file

   Input:  Name of the image file passed in command line argument

   Output: The integer array ndata8[0..nwidth*nheight] contrains
           the integer representations of the image pixels

   Note:   Feature extraction for neural learning must be
           performed on image data contained in ndata8[]

   Sample Usage:
          Case 1:
             java BitMapLoader white.bmp

             Image width and height: 16, 16
             Image padding:0
             Image length:256

             255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255
             ...
             255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255



          Case 3:
             java BitMapLoader black.bmp

             Image width and height: 16, 16
             Image padding:0
             Image length:256

             0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
             ...
             0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0



   Source: http://forums-beta.sun.com/thread.jspa?messageID=1383663
   ****************************************************************
*/

import java.io.*;

public class BitMapLoader {

    //*****************************************************
    public static void main(String[] args){


        try {
            loadbitmap (new FileInputStream(args[0]));
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    } //main



    //*****************************************************
    public static void loadbitmap (InputStream fs) {

        try {

            //*** 14 byte BITMAPFILEHEADER
            int bflen=14;
            byte bf[]=new byte[bflen];
            fs.read(bf,0,bflen);

            //*** 40 byte BITMAPFILEHEADER
            int bilen=40;
            byte bi[]=new byte[bilen];
            fs.read(bi,0,bilen);

            //*** Interperet data
            int nsize = (((int)bf[5]&0xff) << 24) | (((int)bf[4]&0xff) << 16) | (((int)bf[3]&0xff) << 8) | (int)bf[2]&0xff;
            int nbisize = (((int)bi[3]&0xff) << 24) | (((int)bi[2]&0xff) << 16) | (((int)bi[1]&0xff) << 8) | (int)bi[0]&0xff;
            int nwidth = (((int)bi[7]&0xff) << 24) | (((int)bi[6]&0xff) << 16) | (((int)bi[5]&0xff) << 8) | (int)bi[4]&0xff;
            int nheight = (((int)bi[11]&0xff) << 24) | (((int)bi[10]&0xff) << 16) | (((int)bi[9]&0xff) << 8) | (int)bi[8]&0xff;
            int nplanes = (((int)bi[13]&0xff) << 8) | (int)bi[12]&0xff;
            int nbitcount = (((int)bi[15]&0xff) << 8) | (int)bi[14]&0xff;


            //*** Look for non-zero values to indicate compression
            int ncompression = (((int)bi[19]) << 24) | (((int)bi[18]) << 16) | (((int)bi[17]) << 8) | (int)bi[16];
            int nsizeimage = (((int)bi[23]&0xff) << 24) | (((int)bi[22]&0xff) << 16) | (((int)bi[21]&0xff) << 8) | (int)bi[20]&0xff;
            int nxpm = (((int)bi[27]&0xff) << 24) | (((int)bi[26]&0xff) << 16) | (((int)bi[25]&0xff) << 8) | (int)bi[24]&0xff;
            int nypm = (((int)bi[31]&0xff) << 24) | (((int)bi[30]&0xff) << 16) | (((int)bi[29]&0xff) << 8) | (int)bi[28]&0xff;
            int nclrused = (((int)bi[35]&0xff) << 24) | (((int)bi[34]&0xff) << 16) | (((int)bi[33]&0xff) << 8) | (int)bi[32]&0xff;
            int nclrimp = (((int)bi[39]&0xff) << 24) | (((int)bi[38]&0xff) << 16) | (((int)bi[37]&0xff) << 8) | (int)bi[36]&0xff;

            //*** non 8-bit images
            if (nbitcount!=8) {
                System.out.println("Error: Can only handle 8-bit grey-scale bitmaps");
                System.exit(0);
            }


            //*** from here on nbitcount = 8
            int nNumColors = 0;
            if (nclrused > 0)
                nNumColors = nclrused;
            else
                nNumColors = (1&0xff) << nbitcount;

            if (nsizeimage == 0) {
                nsizeimage = ((((nwidth*nbitcount)+31) & ~31 ) >> 3);
                nsizeimage *= nheight;
            }

            //*** Read the palatte colors.
            int npalette[] = new int [nNumColors];
            byte bpalette[] = new byte [nNumColors*4];
            fs.read (bpalette, 0, nNumColors*4);

            int nindex8 = 0;
            for (int n = 0; n < nNumColors; n++) {
                npalette[n] = (255&0xff) << 24 | (((int)bpalette[nindex8+2]&0xff) << 16) | (((int)bpalette[nindex8+1]&0xff) << 8) | (int)bpalette[nindex8]&0xff;
                nindex8 += 4;
            }


            //*** Read the image data (actually indices into the palette)
            //*** Scan lines are still padded out to even 4-byte boundaries.
            int npad8 = (nsizeimage / nheight) - nwidth;

            //*** integer data
            int ndata8[] = new int [nwidth*nheight];

            //*** read in the binary image date
            byte bdata[] = new byte [(nwidth+npad8)*nheight];
            fs.read (bdata, 0, (nwidth+npad8)*nheight);

            nindex8 = 0;
            for (int j8 = 0; j8 < nheight; j8++) {
                for (int i8 = 0; i8 < nwidth; i8++) {
                    ndata8 [nwidth*(nheight-j8-1)+i8] = npalette [((int)bdata[nindex8]&0xff)];
                    nindex8++;
                }
                nindex8 += npad8;
            }


            //***********************************************************************************
            //*** Print image information
            //*** ndata8[0..nwidth*nheight] contains the image in 8bit grey scale
            //***********************************************************************************
            System.out.println("Image width and height: " + nwidth + ", " + nheight);
            System.out.println("Image padding: "+npad8);
            System.out.println("Image length: " + bdata.length);

            //*** print the entire array of nwidth*nheight pixels
            int k = 0;
            for (int i=0; i<nheight; i++){
                for (int j=0; j<nwidth; j++){
                    ndata8[k] = ndata8[k] & 0xff;

                    //*** HERE HERE HERE HERE HERE HERE HERE HERE HERE
                    //*** perform feature extraction on ndata8[]
                    System.out.print(ndata8[k] + " ");
                    k++;
                }
                System.out.println();
            }

            //*** close the image file
            fs.close();
        }

        catch (Exception e) {
            System.out.println("Caught exception in loadbitmap!");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    } //loadbitmap

public static double findMean(int [] array){
    double mean=0;
    int sum =0;

    for(int i=0; i<array.length; i++){
        sum = sum+ array[i];
    }
    mean = sum/(array.length);
    return mean;
}

public static double findVariance (int [] array){
    double variance =0;
    double sum =0;
    double mean = findMean(array);
    for(int i=0; i<array.length; i++){
        sum = sum + Math.pow(array[i]-mean, 2);
    }
    variance = sum/array.length;
    return variance;

}

public static double skewness (int [] array){
    double skewness =0;
    double mean = findMean(array);
    double sumNumerator=0;
    double sumDenominator = 0;

    for(int i =0; i <array.length; i++){
        sumNumerator = sumNumerator + Math.pow(array[i]-mean, 3);
    }
    sumNumerator = sumNumerator/array.length;

    for (int i=0; i <array.length; i++){
        sumDenominator = sumDenominator + Math.pow(array[i]-mean, 2);
    }
    skewness = Math.pow((sumDenominator/2), 3/2);

    return skewness;
}

} //BitMapLoader
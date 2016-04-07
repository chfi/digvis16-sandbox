/**
 * Created by christian on 31/03/16.
 */

import com.leapmotion.leap.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

public class Sample {

    static {
        // Load the native OpenCV library
        //System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        System.load("/usr/local/opt/opencv3/share/OpenCV/java/lib" + Core.NATIVE_LIBRARY_NAME + ".so");
    }

    public static void main(String[] args) {


        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);

        Controller leapController = new Controller();
        ImageList currentImages = leapController.images();

        while ((!currentImages.get(0).isValid())) {
            currentImages = leapController.images();

        }

        DisplayFrame frame_left = new DisplayFrame();
        frame_left.setVisible(true);

        DisplayFrame frame_right = new DisplayFrame();
        frame_right.setVisible(true);


        // why 400x400?
        int w = 400;
        int h = 400;

        Mat target = Mat.zeros(w,h,CvType.CV_8UC3);

        Image img_left = currentImages.get(0);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                byte[] bgrPixel = new byte[3];
                target.get(y,x,bgrPixel);

                int[] brightness = new int[3];

                Vector input = new Vector(((float)x)/w, ((float)y)/h, 0.f);

                input.setX((input.getX() - img_left.rayOffsetX()) / img_left.rayScaleX());
                input.setY((input.getY() - img_left.rayOffsetY()) / img_left.rayScaleY());

                Vector pixel = img_left.warp(input);


                if(pixel.getX() >= 0 && pixel.getX() < img_left.width() &&
                        pixel.getY() >= 0 && pixel.getY() < img_left.height()) {
                    int data_index = (int)(Math.floor(pixel.getY()) * img_left.width() + Math.floor(pixel.getX())); //xy to buffer index
                    brightness[0] = img_left.data()[data_index]; //Look up brightness value
                    brightness[2] = brightness[1] = brightness[0]; //Greyscale
                } else {
                    brightness[0] = 255; //Display invalid pixels as red
                    brightness[2] = brightness[1] = 0;
                }


                bgrPixel[2] = (byte)brightness[0];
                bgrPixel[1] = (byte)brightness[1];
                bgrPixel[0] = (byte)brightness[2];

                //byte[] bgrPixel = { 0, 0, 0 };
                target.put(y,x,bgrPixel);
            }

            System.out.println("row " + y);

        }

        System.out.println("fart");


        while (true) {
            // Read current camera frame into matrix
            currentImages = leapController.images();

            img_left = currentImages.get(0);
            Image img_right = currentImages.get(1);



            frame_left.renderMatrix(target);

            /*
            Mat mat_left = leapImageToMatrix(img_left);
            Mat mat_right = leapImageToMatrix(img_right);

            float[] dist_buf_left = img_left.distortion();

            frame_left.renderMatrix(mat_left);
            frame_right.renderMatrix(mat_right);*/
        }

        /*

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Remove the sample listener when done
        controller.removeListener(listener);
        */
    }

    static public Mat leapImageToMatrix(Image img) {
        int w = img.width();
        int h = img.height();
        Mat mat = Mat.zeros(h,w,CvType.CV_8UC1);
        byte[] buf = img.data();
        mat.put(0,0,buf);
        return mat;
    }


}

class SampleListener extends Listener {

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
    }

    public void onFrame(Controller controller) {
        Frame frame = controller.frame();

        frame.gestures().forEach(gesture ->
            System.out.println("Detected " + gesture.type().toString()));

    }
}
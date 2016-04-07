import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;

public class DisplayFrame {

    private final JFrame frame;
    private final DisplayPanel panel;

    public DisplayFrame() {
        // JFrame which holds JPanel
        frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JPanel which is used for drawing image
        panel = new DisplayPanel();
        frame.getContentPane().add(panel);
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public void renderMatrix(Mat image) {

        Image i = toBufferedImage(image);
        panel.setImage(i);
        panel.repaint();
        frame.pack();
    }

    public static Image toBufferedImage(Mat m){

        int type;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        } else {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }

        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);

        // copy from matrix into buffer
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] buffer = new byte[bufferSize];
        m.get(0,0,buffer);

        // copy from buffer into image
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, bufferSize);

        return image;
    }
}
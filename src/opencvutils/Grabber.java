package opencvutils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Basic class for {@link CameraGrabber} and {@link VideoGrabber}.<br>
 * It contains methods for conversion between different objects that hold image data.
 *
 * @author PGRF FIM UHK
 * @version 1.0
 * @since 2019-09-01
 */
public class Grabber {

    /**
     * Object that contains data (one frame) from camera or video
     */
    private final Mat frame;

    /**
     * Object that is used to access camera or video file
     */
    final VideoCapture capture;

    /**
     * Constructor for accessing camera
     */
    Grabber() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        frame = new Mat();
        capture = new VideoCapture();
    }

    /**
     * Constructor for accessing video files by given filename
     *
     * @param filename filename of a video file
     */
    Grabber(String filename) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        frame = new Mat();
        capture = new VideoCapture(filename);
    }

    /**
     * Release the camera or video source
     */
    public void release() {
        capture.release();
    }

    /**
     * Get width of the last captured frame
     *
     * @return width of the last captured frame
     */
    public int getWidth() {
        return frame.cols();
    }

    /**
     * Get height of the last captured frame
     *
     * @return height of the last captured frame
     */
    public int getHeight() {
        return frame.rows();
    }

    /**
     * Grabs next frame or image and returns it as {@link ByteBuffer} instance
     *
     * @return ByteBuffer instance
     */
    public ByteBuffer grabImage() {
        if (capture.read(frame)) {
            return mat2Buffer(frame);
        }
        return null;
    }

    /**
     * Grabs next frame or image and returns it as {@link ByteBuffer} instance
     *
     * @param flipCode flip image - see {@link FlipCode}
     * @return ByteBuffer instance
     */
    public ByteBuffer grabImage(int flipCode) {
        if (capture.read(frame)) {
            Mat flippedFrame = new Mat();
            Core.flip(frame, flippedFrame, flipCode);
            return mat2Buffer(flippedFrame);
        }
        return null;
    }

    /**
     * Grabs next frame or image and returns it as {@link Mat} instance
     *
     * @return raw Mat instance
     */
    public Mat grabImageRaw() {
        if (capture.read(frame)) {
            return frame;
        }
        return null;
    }

    /**
     * Grabs next frame or image and returns it as {@link Mat} instance
     *
     * @param flipCode flip image - see {@link FlipCode}
     * @return raw Mat instance
     */
    public Mat grabImageRaw(int flipCode) {
        if (capture.read(frame)) {
            Mat flippedFrame = new Mat();
            Core.flip(frame, flippedFrame, flipCode);
            return flippedFrame;
        }
        return null;
    }

    /**
     * Method for converting {@link Mat} instance to {@link ByteBuffer} instance
     *
     * @param m mat instance
     * @return ByteBuffer instance
     */
    public static ByteBuffer mat2Buffer(Mat m) {
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] byteArray = new byte[bufferSize];
        m.get(0, 0, byteArray);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteArray.length * Float.BYTES); // 4 bytes per float
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(byteArray);
        byteBuffer.position(0);
        return byteBuffer;
    }

    /**
     * Get FPS (frames per second) for given source - video file or camera input.
     *
     * @return FPS
     */
    public double getFPS() {
        return capture.get(Videoio.CAP_PROP_FPS);
    }

    /**
     * Get property of the capture. Use constants in {@link org.opencv.videoio.Videoio}.
     * For example getProperty(Videoio.CAP_PROP_FPS)
     *
     * @param propertyID use constants in {@link org.opencv.videoio.Videoio}.
     * @return value
     */
    public double getProperty(int propertyID) {
        return capture.get(propertyID);
    }

    /**
     * Set property of the capture. Use constants in {@link org.opencv.videoio.Videoio}.
     * For example setProperty(Videoio.CAP_PROP_FRAME_WIDTH, 1280)
     *
     * @param propertyID use constants in {@link org.opencv.videoio.Videoio}.
     * @param value      value to be set for given property
     * @return if successful
     */
    public boolean setProperty(int propertyID, double value) {
        return capture.set(propertyID, value);
    }

}

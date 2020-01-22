package opencvutils;

import org.opencv.videoio.Videoio;

/**
 * Grabber for access to cameras. Contains different constructors to access them.
 *
 * @author PGRF FIM UHK
 * @version 1.0
 * @since 2019-09-01
 */
public class CameraGrabber extends Grabber {

    /**
     * Constructor that opens camera by given ID and tries to open it with requested resolution.
     *
     * @param width  requested width
     * @param height requested height
     * @param source opens camera with given ID (zero indexed)
     */
    public CameraGrabber(int width, int height, int source) {
        super();
        capture.open(source);

        // https://docs.opencv.org/3.1.0/d8/dfe/classcv_1_1VideoCapture.html#a8c6d8c2d37505b5ca61ffd4bb54e9a7c
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
    }

    /**
     * Constructor that opens default camera and tries to open it with requested resolution.
     *
     * @param width  requested width
     * @param height requested height
     */
    public CameraGrabber(int width, int height) {
        this(width, height, 0);
    }

    /**
     * Basic constructor. It opens camera by given ID with default resolution.
     *
     * @param source opens camera with given ID (zero indexed)
     */
    public CameraGrabber(int source) {
        super();
        capture.open(source);
    }

    /**
     * Basic constructor. It opens default camera with default resolution.
     */
    public CameraGrabber() {
        super();
        capture.open(0);
    }

}

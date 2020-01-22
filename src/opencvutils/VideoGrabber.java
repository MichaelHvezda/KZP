package opencvutils;

import org.opencv.videoio.Videoio;

/**
 * Grabber for video files. Contains direct access to useful properties of a video file.
 * <br>
 * For properties explanation see documentation:
 * https://docs.opencv.org/4.1.1/d4/d15/group__videoio__flags__base.html#gaeb8dd9c89c10a5c63c139bf7c4f5704d
 *
 * @author PGRF FIM UHK
 * @version 1.0
 * @since 2019-09-01
 */
public class VideoGrabber extends Grabber {

    static {
        try {
            /*
            VM options:
            -Djava.library.path=D:\\Java\\lib\\opencv-411\\build\\java\\x64\\
             */
            System.load("D:\\JavaProgramko\\KZPJ-muj\\opencv\\build\\bin\\opencv_videoio_ffmpeg412_64.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public VideoGrabber(String filename) {
        super(filename);
    }

    /**
     * Get current time in video for current frame in seconds.
     *
     * @return current video time in seconds
     */
    public double getCurrentVideoTime() {
        // Current position of the video file in milliseconds.
        return capture.get(Videoio.CAP_PROP_POS_MSEC) / 1000.0; // convert to seconds
    }

    /**
     * Get total time of the video file in seconds.
     *
     * @return total video time in seconds, NaN if no frame was read
     */
    public double getTotalVideoTime() {
        return (getTotalFrameCount() * getCurrentVideoTime()) / getCurrentFrameCount();
    }

    /**
     * Get the order count of the last read frame
     *
     * @return current frame count
     */
    public double getCurrentFrameCount() {
        // 0-based index of the frame to be decoded/captured next.
        return capture.get(Videoio.CAP_PROP_POS_FRAMES);
    }

    /**
     * Get total frames count of the whole video.
     *
     * @return frames count
     */
    public double getTotalFrameCount() {
        // Number of frames in the video file.
        return capture.get(Videoio.CAP_PROP_FRAME_COUNT);
    }

    /**
     * Set current video time to 0 - the beginning.
     */
    public void rewind() {
        capture.set(Videoio.CAP_PROP_POS_MSEC, 0);
    }

}

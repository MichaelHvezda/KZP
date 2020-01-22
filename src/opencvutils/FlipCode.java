package opencvutils;

/**
 * @author PGRF FIM UHK
 * @version 1.0
 * @since 2019-09-01
 */
public final class FlipCode {
    // https://docs.opencv.org/4.1.1/d2/de8/group__core__array.html#gaca7be533e3dac7feb70fc60635adf441
    //    0 means flipping around the x-axis
    //    Positive value (for example, 1) means flipping around y-axis
    //    Negative value (for example, -1) means flipping around both axes
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    public static final int X_Y_AXIS = -1;
}

package CTRLX.vision;
public class VisionConfig {
    // HSV thresholds (tune under your lighting)
    public static final int[] LOWER_GREEN = {40, 70, 70};
    public static final int[] UPPER_GREEN = {80, 255, 255};

    public static final int[] LOWER_BLACK = {0, 0, 0};
    public static final int[] UPPER_BLACK = {180, 255, 50};

    // Behavior tuning
    public static final double BASE_SPEED = 0.25;   // forward speed
    public static final double STEER_SCALE = 1.5;   // px → motor power
    public static final double LOST_TIMEOUT = 1.0;  // seconds
    public static final double TURN_TIME = 0.8;     // seconds for a branch turn
}

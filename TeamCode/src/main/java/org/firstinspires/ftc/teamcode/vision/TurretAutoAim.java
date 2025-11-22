package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

/**
 * Computes a desired absolute servo position (0..1) from Limelight Ty.
 * - Call enable() / disable() to control behavior.
 * - Call getDesiredServoPosition() each loop; returns Double.NaN when no valid target.
 *
 * Tuning parameters:
 *   MAX_TAG_ANGLE_DEG   --> map +/- deg to full servo travel around center
 *   SMOOTHING_ALPHA     --> simple exponential smoothing to avoid jumpiness (0..1)
 */
public class TurretAutoAim {

    private final Limelight3A limelight;
    private boolean enabled = false;

    // tuning: how many degrees of Ty correspond to the full half-range of servo (center->edge)
    private final double MAX_TAG_ANGLE_DEG = 30.0; // change to match your geometry
    // smoothing: 0.0 = no smoothing (jump), 1.0 = freeze. use something like 0.15
    private final double SMOOTHING_ALPHA = 0.15;

    // last-smoothed servo position
    private double lastServoPos = 0.5;

    public TurretAutoAim(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void enable() { enabled = true; }

    public void disable() { enabled = false; }

    public boolean isEnabled() { return enabled; }

    /**
     * Returns: desired servo absolute position in [0,1], or Double.NaN if no valid target
     * Call this each loop; if disabled or no target seen returns NaN
     */
    public double getDesiredServoPosition() {
        if (!enabled) return Double.NaN;

        LLResult res = limelight.getLatestResult();
        if (res == null || !res.isValid()) {
            return Double.NaN;
        }

        // Ty = vertical offset (degrees). Positive means target is above center.
        double ty = res.getTy();

        // Map ty (deg) --> servo position (0..1), center = 0.5
        // position = 0.5 + (ty / MAX_TAG_ANGLE_DEG) * 0.5
        double rawPos = 0.5 + (ty / MAX_TAG_ANGLE_DEG) * 0.5;

        // clamp
        rawPos = Math.max(0.0, Math.min(1.0, rawPos));

        // smooth toward rawPos
        lastServoPos = lastServoPos + SMOOTHING_ALPHA * (rawPos - lastServoPos);

        return lastServoPos;
    }
}

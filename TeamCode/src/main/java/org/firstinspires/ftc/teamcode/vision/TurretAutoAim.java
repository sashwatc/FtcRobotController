package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

/**
 * TurretAutoAim computes an absolute servo position [0..1] from Limelight Ty.
 * - enable()/disable() control whether it produces a value
 * - getDesiredServoPosition() returns Double.NaN when disabled or no valid target
 *
 * Mapping: Ty (deg) -> servo position with center = 0.5.
 * Tune MAX_TAG_ANGLE_DEG to match geometry (how many degrees Ty => half servo travel).
 */
public class TurretAutoAim {

    private final Limelight3A limelight;
    private boolean enabled = false;

    // TUNING
    private final double MAX_TAG_ANGLE_DEG = 30.0; // degrees -> half servo travel
    private final double SMOOTHING_ALPHA = 0.12;   // 0..1 (higher = snappier, lower = smoother)

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
     * Returns an absolute [0..1] servo position or Double.NaN if disabled/no target.
     * Call every loop.
     */
    public double getDesiredServoPosition() {
        if (!enabled) return Double.NaN;

        LLResult res = limelight.getLatestResult();
        if (res == null || !res.isValid()) return Double.NaN;

        // Ty is vertical offset in degrees (positive => target above center)
        double ty = res.getTy();

        // Map Ty to [0..1] (center 0.5). Ty = +MAX_TAG_ANGLE_DEG -> pos = 1.0
        double rawPos = 0.5 + (ty / MAX_TAG_ANGLE_DEG) * 0.5;

        // Clamp
        rawPos = Math.max(0.0, Math.min(1.0, rawPos));

        // Smooth toward rawPos
        lastServoPos = lastServoPos + SMOOTHING_ALPHA * (rawPos - lastServoPos);

        return lastServoPos;
    }
}

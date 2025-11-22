package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Turret subsystem:
 * - controls two mirrored pitch servos (left increases to go up, right mirrored)
 * - controls two flywheel motors (left/right row motors)
 * - provides manual incremental turretPitch(stickY) and absolute setPitchAbsolute(pos)
 */
public class KitTurretControl {

    private DcMotor leftRowMotor, rightRowMotor;
    private Servo leftPitchServo, rightPitchServo;

    // pitch is stored as absolute servo position (0..1). Center = 0.5.
    private double pitchPosition = 0.5;
    private final double PITCH_SPEED = 0.01; // incremental speed per loop for manual control

    public void init(HardwareMap hwMap) {
        // motors
        leftRowMotor = hwMap.get(DcMotor.class, "leftRowMotor");
        rightRowMotor = hwMap.get(DcMotor.class, "rightRowMotor");
        leftRowMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRowMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftRowMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRowMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // servos (names must match your DS config)
        leftPitchServo  = hwMap.get(Servo.class, "leftPitchServo");
        rightPitchServo = hwMap.get(Servo.class, "rightPitchServo");

        // initialize to centered position
        setPitchPosition(pitchPosition);
    }

    /** Stop flywheels */
    public void stop() {
        if (leftRowMotor != null) leftRowMotor.setPower(0);
        if (rightRowMotor != null) rightRowMotor.setPower(0);
    }

    /** Flywheel control (left/right rollers) using triggers (0..1) */
    public void runWithTriggers(double leftTrigger, double rightTrigger) {
        if (leftTrigger > 0.05 && rightTrigger <= 0.05) {
            leftRowMotor.setPower(leftTrigger);
            rightRowMotor.setPower(-leftTrigger);
        } else if (rightTrigger > 0.05 && leftTrigger <= 0.05) {
            leftRowMotor.setPower(-rightTrigger);
            rightRowMotor.setPower(rightTrigger);
        } else {
            stop();
        }
    }

    /** Internal: apply mirrored positions to real servos.
     *  LEFT: position (increase => CCW => turret up)
     *  RIGHT: mirrored (1 - position) so it rotates CW when LEFT goes CCW */
    private void setPitchPosition(double position) {
        position = Math.max(0.0, Math.min(1.0, position));
        leftPitchServo.setPosition(position);
        rightPitchServo.setPosition(1.0 - position);
        pitchPosition = position;
    }

    /** Manual incremental turret control (stickY from -1..1). Uses internal PITCH_SPEED. */
    public void turretPitch(double stickY) {
        double delta = -stickY * PITCH_SPEED; // invert if needed
        double newPos = pitchPosition + delta;
        newPos = Math.max(0.0, Math.min(1.0, newPos));
        setPitchPosition(newPos);
    }

    /** Called by auto-aim: set an absolute position (0..1). */
    public void setPitchAbsolute(double absolutePosition) {
        if (Double.isNaN(absolutePosition)) return;
        absolutePosition = Math.max(0.0, Math.min(1.0, absolutePosition));
        setPitchPosition(absolutePosition);
    }

    /** Get the current stored pitch position (0..1). */
    public double getPitchPosition() {
        return pitchPosition;
    }
}
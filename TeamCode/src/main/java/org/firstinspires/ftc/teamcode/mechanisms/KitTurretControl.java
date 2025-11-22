package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class KitTurretControl {

    private DcMotor leftRowMotor, rightRowMotor;
    private Servo leftPitchServo, rightPitchServo;
    private double pitchPosition = 0.5; // servo position (0..1)
    private final double PITCH_SPEED = 0.01; // manual sensitivity

    public void stop() {
        if (leftRowMotor != null) leftRowMotor.setPower(0);
        if (rightRowMotor != null) rightRowMotor.setPower(0);
    }

    private void setPitchPosition(double position) {
        position = Math.max(0.0, Math.min(1.0, position));
        // left servo normal, right servo mirrored
        leftPitchServo.setPosition(position);
        rightPitchServo.setPosition(1.0 - position);
        pitchPosition = position;
    }

    public void init(HardwareMap hwMap) {
        leftRowMotor = hwMap.get(DcMotor.class, "leftRowMotor");
        rightRowMotor = hwMap.get(DcMotor.class, "rightRowMotor");

        rightRowMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftRowMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftRowMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRowMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftPitchServo = hwMap.get(Servo.class, "leftPitchServo");
        rightPitchServo = hwMap.get(Servo.class, "rightPitchServo");

        // start centered
        setPitchPosition(pitchPosition);

        stop();
    }

    /**
     * Flywheel control (left/right rollers) using triggers.
     */
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

    /**
     * Manual pitch control called with joystick Y. Keeps internal state.
     * stickY typically from -1..1 (gamepad stick)
     */
    public void turretPitch(double stickY) {
        double delta = -stickY * PITCH_SPEED; // invert if needed
        double newPos = pitchPosition + delta;
        newPos = Math.max(0.0, Math.min(1.0, newPos));
        setPitchPosition(newPos);
    }

    /**
     * Called by auto-aim. Sets **absolute** servo position (0..1).
     * This avoids the servos fighting each other.
     */
    public void setPitchAbsolute(double absolutePosition) {
        if (Double.isNaN(absolutePosition)) return;
        absolutePosition = Math.max(0.0, Math.min(1.0, absolutePosition));
        setPitchPosition(absolutePosition);
    }

    /**
     * Returns the last-known servo position (0..1).
     */
    public double getPitchPosition() {
        return pitchPosition;
    }
}

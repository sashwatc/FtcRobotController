package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class KitTurretControl {
    // 4 MOTORS (2 left, 2 right)
    private DcMotor leftMotor1, leftMotor2, rightMotor1, rightMotor2;
    private Servo leftPitchServo, rightPitchServo;

    // Power multiplier - set this once, triggers control speed
    private double powerMultiplier = 1.5;  // Fixed multiplier (1.5 = 150% power)
    private final double MIN_POWER = 0.05; // Deadzone threshold
    private final double PITCH_SPEED = 0.01;

    private double pitchPosition = 0.5;

    public void init(HardwareMap hwMap) {
        // Initialize 4 motors
        leftMotor1 = hwMap.get(DcMotor.class, "leftMotor1");
        leftMotor2 = hwMap.get(DcMotor.class, "leftMotor2");
        rightMotor1 = hwMap.get(DcMotor.class, "rightMotor1");
        rightMotor2 = hwMap.get(DcMotor.class, "rightMotor2");

        // Set motor modes for max speed
        leftMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Let flywheels spin down naturally
        leftMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Set directions
        leftMotor1.setDirection(DcMotorSimple.Direction.FORWARD);
        leftMotor2.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor1.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor2.setDirection(DcMotorSimple.Direction.FORWARD);

        // Pitch servos
        leftPitchServo = hwMap.get(Servo.class, "leftPitchServo");
        rightPitchServo = hwMap.get(Servo.class, "rightPitchServo");

        setPitchPosition(pitchPosition);
    }

    // Triggers control speed (0 to 1), powerMultiplier scales it
    public void runWithTriggers(double leftTrigger, double rightTrigger) {
        // Triggers are already 0-1, just apply the fixed multiplier
        leftTrigger *= powerMultiplier;
        rightTrigger *= powerMultiplier;

        // Clamp to valid range
        leftTrigger = Math.min(1.0, Math.max(0.0, leftTrigger));
        rightTrigger = Math.min(1.0, Math.max(0.0, rightTrigger));

        if (leftTrigger > MIN_POWER && rightTrigger <= MIN_POWER) {
            // Left trigger: left motors FORWARD, right motors REVERSE
            leftMotor1.setPower(leftTrigger);
            leftMotor2.setPower(leftTrigger);
            rightMotor1.setPower(-leftTrigger);
            rightMotor2.setPower(-leftTrigger);
        } else if (rightTrigger > MIN_POWER && leftTrigger <= MIN_POWER) {
            // Right trigger: left motors REVERSE, right motors FORWARD
            leftMotor1.setPower(-rightTrigger);
            leftMotor2.setPower(-rightTrigger);
            rightMotor1.setPower(rightTrigger);
            rightMotor2.setPower(rightTrigger);
        } else {
            stop();
        }
    }

    // Set fixed power multiplier
    public void setPowerMultiplier(double multiplier) {
        this.powerMultiplier = Math.max(0.5, Math.min(2.0, multiplier));
    }

    // Get current power multiplier
    public double getPowerMultiplier() {
        return powerMultiplier;
    }

    // Full power shooting (uses max trigger value 1.0 * multiplier)
    public void shootAtMaxPower() {
        leftMotor1.setPower(1.0 * powerMultiplier);
        leftMotor2.setPower(1.0 * powerMultiplier);
        rightMotor1.setPower(-1.0 * powerMultiplier);
        rightMotor2.setPower(-1.0 * powerMultiplier);
    }

    // Stop all motors
    public void stop() {
        if (leftMotor1 != null) leftMotor1.setPower(0);
        if (leftMotor2 != null) leftMotor2.setPower(0);
        if (rightMotor1 != null) rightMotor1.setPower(0);
        if (rightMotor2 != null) rightMotor2.setPower(0);
    }

    private void setPitchPosition(double position) {
        position = Math.max(0.0, Math.min(1.0, position));
        leftPitchServo.setPosition(position);
        rightPitchServo.setPosition(1.0 - position);
        pitchPosition = position;
    }

    public void turretPitch(double stickY) {
        double delta = -stickY * PITCH_SPEED;
        double newPos = pitchPosition + delta;
        newPos = Math.max(0.0, Math.min(1.0, newPos));
        setPitchPosition(newPos);
    }

    public void setPitchAbsolute(double absolutePosition) {
        if (Double.isNaN(absolutePosition)) return;
        absolutePosition = Math.max(0.0, Math.min(1.0, absolutePosition));
        setPitchPosition(absolutePosition);
    }

    public double getPitchPosition() {
        return pitchPosition;
    }
}
package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class KitTurretControl {
    private DcMotor leftMotor1, leftMotor2, rightMotor1, rightMotor2;
    private Servo leftPitchServo, rightPitchServo;

    // Left trigger: leftMotor1/rightMotor1 at 20%, leftMotor2/rightMotor2 at full speed
    // Right trigger: all 4 motors normal speed
    private final double LEFT_TRIGGER_MOTOR1_SPEED = 0.5; // Fixed 20% for motor1 pair
    private double rightTriggerMultiplier = 1.0;           // Multiplier for right trigger
    private final double MIN_POWER = 0.05;
    private final double PITCH_SPEED = 0.01;

    private double pitchPosition = 0.5;

    public void init(HardwareMap hwMap) {
        leftMotor1 = hwMap.get(DcMotor.class, "leftMotor1");
        leftMotor2 = hwMap.get(DcMotor.class, "leftMotor2");
        rightMotor1 = hwMap.get(DcMotor.class, "rightMotor1");
        rightMotor2 = hwMap.get(DcMotor.class, "rightMotor2");

        leftMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        leftMotor1.setDirection(DcMotorSimple.Direction.FORWARD);
        leftMotor2.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor1.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor2.setDirection(DcMotorSimple.Direction.FORWARD);

        leftPitchServo = hwMap.get(Servo.class, "leftPitchServo");
        rightPitchServo = hwMap.get(Servo.class, "rightPitchServo");

        setPitchPosition(pitchPosition);
    }

    public void runWithTriggers(double leftTrigger, double rightTrigger) {
        if (leftTrigger > MIN_POWER && rightTrigger <= MIN_POWER) {
            // LEFT TRIGGER: All 4 motors run
            // leftMotor1 and rightMotor1 at 20% speed
            // leftMotor2 and rightMotor2 at full trigger speed
            double power1 = LEFT_TRIGGER_MOTOR1_SPEED; // Fixed 20% for motor1 pair
            double power2 = leftTrigger;               // Variable speed for motor2 pair

            // Apply right trigger multiplier if you want motor2 pair to also be scaled
            // power2 *= rightTriggerMultiplier; // Uncomment if needed

            leftMotor1.setPower(power1);
            leftMotor2.setPower(power2);
            rightMotor1.setPower(-power1);
            rightMotor2.setPower(-power2);

        } else if (rightTrigger > MIN_POWER && leftTrigger <= MIN_POWER) {
            // RIGHT TRIGGER: All 4 motors normal speed
            double power = rightTrigger * rightTriggerMultiplier;
            power = Math.min(1.0, Math.max(0.0, power));

            leftMotor1.setPower(-power);
            leftMotor2.setPower(-power);
            rightMotor1.setPower(power);
            rightMotor2.setPower(power);

        } else {
            stop();
        }
    }

    // Set right trigger power multiplier (affects motor2 pair on left trigger if uncommented above)
    public void setRightTriggerMultiplier(double multiplier) {
        this.rightTriggerMultiplier = Math.max(0.5, Math.min(2.0, multiplier));
    }

    public double getRightTriggerMultiplier() {
        return rightTriggerMultiplier;
    }

    // Set left trigger motor1 speed (20% default)
    public void setLeftTriggerMotor1Speed(double speed) {
        // This would need to be not final to change it
        // For now, it's fixed at 20%
    }

    // Max power shooting (right trigger direction)
    public void shootAtMaxPower() {
        double power = 1.0 * rightTriggerMultiplier;
        leftMotor1.setPower(-power);
        leftMotor2.setPower(-power);
        rightMotor1.setPower(power);
        rightMotor2.setPower(power);
    }

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
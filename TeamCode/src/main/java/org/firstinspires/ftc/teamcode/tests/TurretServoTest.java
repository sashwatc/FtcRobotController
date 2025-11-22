package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="SimpleServoDirectTest")
public class TurretServoTest extends OpMode {

    private Servo leftPitchServo, rightPitchServo;

    @Override
    public void init() {
        leftPitchServo  = hardwareMap.get(Servo.class, "leftPitchServo");
        rightPitchServo = hardwareMap.get(Servo.class, "rightPitchServo");

        telemetry.addLine("Loaded Simple Servo Test");
        telemetry.addLine("Right Stick Y = Servo position");
        telemetry.addLine("Stick up -> 1.0, Stick down -> 0.0");
        telemetry.update();
    }

    @Override
    public void loop() {

        // Convert stick from [-1..1] to [0..1]
        double raw = gamepad2.right_stick_y;
        double pos = (raw + 1) / 2.0;

        // Set servos directly
        leftPitchServo.setPosition(pos);
        rightPitchServo.setPosition(pos);

        telemetry.addData("Stick Raw", raw);
        telemetry.addData("Servo Position", pos);
        telemetry.update();
    }
}

package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "List Hardware")
public class ListHardware extends OpMode {
    @Override
    public void init() {
        // List all DC motors
        for (String name : hardwareMap.getAllNames(DcMotor.class)) {
            telemetry.addData("Motor", name);
        }

        // List all servos
        for (String name : hardwareMap.getAllNames(Servo.class)) {
            telemetry.addData("Servo", name);
        }

        telemetry.update();
    }

    @Override
    public void loop() {}
}
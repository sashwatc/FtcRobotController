package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.vision.Limelight;

@TeleOp(name = "Limelight Test")
public class LimelightTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        Limelight limelight = new Limelight("172.29.0.2");

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Has Target", limelight.hasTarget());
            telemetry.addData("TY", limelight.getTY());
            telemetry.addData("Target ID", limelight.getTargetID());
            telemetry.update();
            sleep(100);
        }
    }
}

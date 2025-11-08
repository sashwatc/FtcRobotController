package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Limelight Test")
public class LimelightTest extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() {
        // Initialize Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // poll 100 times/sec
        limelight.start();            // start the Limelight

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                // Target is visible
                telemetry.addData("Target Visible", true);
                telemetry.addData("X Offset (tx)", result.getTx());
                telemetry.addData("Y Offset (ty)", result.getTy());
                telemetry.addData("Area (ta)", result.getTa());
            } else {
                // No target
                telemetry.addData("Target Visible", false);
            }

            telemetry.update();
        }
    }
}

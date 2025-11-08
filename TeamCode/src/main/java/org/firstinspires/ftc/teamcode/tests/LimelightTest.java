package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

@TeleOp(name = "Limelight Test")
public class LimelightTest extends OpMode {

    private Limelight3A limelight;

    @Override
    public void init() {
        // Get the Limelight from the hardware map.
        // Make sure the name "limelight" matches what you configured in the Driver Station.
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Optional: Set how often data is requested from the Limelight (e.g., 100 times per second).
        limelight.setPollRateHz(100);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        // Start the Limelight vision processing.
        limelight.start();

        // Switch to a specific pipeline if needed (e.g., pipeline 0).
        // Pipelines are configured in the Limelight's web interface.
        limelight.pipelineSwitch(0);
    }

    @Override
    public void loop() {
        // Get the latest result from the Limelight.
        LLResult result = limelight.getLatestResult();

        // Check if the result is valid before using it.
        if (result != null && result.isValid()) {
            telemetry.addData("Pipeline Index", result.getPipelineIndex());
            telemetry.addData("Target X", result.getTx()); // Horizontal offset from crosshair
            telemetry.addData("Target Y", result.getTy()); // Vertical offset from crosshair
            telemetry.addData("Target Area", result.getTa()); // Area of the target
        } else {
            telemetry.addData("Result", "No valid data");
        }
        telemetry.update();
    }

    @Override
    public void stop() {
        // Stop the Limelight when the OpMode is finished.
        limelight.stop();
    }
}

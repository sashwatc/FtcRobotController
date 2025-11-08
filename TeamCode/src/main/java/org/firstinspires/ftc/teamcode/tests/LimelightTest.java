package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@TeleOp(name="Limelight Test - AprilTag JSON")
public class LimelightTest extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() {
        // Initialize Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                telemetry.addData("Target Visible", true);
                telemetry.addData("X Offset (tx)", result.getTx());
                telemetry.addData("Y Offset (ty)", result.getTy());
                telemetry.addData("Area (ta)", result.getTa());

                // Parse the JSON string to get AprilTag ID
                try {
                    JSONObject json = new JSONObject(result.toString());
                    JSONArray fiducials = json.optJSONArray("Fiducial"); // array of tags
                    if (fiducials != null && fiducials.length() > 0) {
                        JSONObject firstTag = fiducials.getJSONObject(0);
                        int tagId = firstTag.optInt("id", -1);
                        telemetry.addData("AprilTag ID", tagId);
                    } else {
                        telemetry.addData("AprilTag ID", "None detected");
                    }
                } catch (JSONException e) {
                    telemetry.addData("AprilTag ID", "Error parsing JSON");
                }

            } else {
                telemetry.addData("Target Visible", false);
                telemetry.addData("AprilTag ID", "N/A");
            }

            telemetry.update();
        }
    }
}

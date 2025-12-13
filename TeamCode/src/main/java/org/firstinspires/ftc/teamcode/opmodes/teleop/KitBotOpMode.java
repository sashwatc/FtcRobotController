package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;
import org.firstinspires.ftc.teamcode.mechanisms.MechanumDrive;
import org.firstinspires.ftc.teamcode.vision.TurretAutoAim;

@TeleOp(name = "KitBotOpModeDecode")
public class KitBotOpMode extends OpMode {

    private MechanumDrive drive = new MechanumDrive();
    private KitTurretControl turret = new KitTurretControl();
    private TurretAutoAim autoAim;

    private ElapsedTime buttonTimer = new ElapsedTime();

    private boolean autoAimEnabled = false;
    private boolean fieldRelative = true;
    private double driveSpeed = 0.8;
    private double rightTriggerMultiplier = 1.0;

    @Override
    public void init() {
        drive.init(hardwareMap);
        turret.init(hardwareMap);
        autoAim = new TurretAutoAim(hardwareMap);

        drive.setSpeedMultiplier(driveSpeed);
        turret.setRightTriggerMultiplier(rightTriggerMultiplier);

        buttonTimer.reset();

        telemetry.addLine("KitBot Initialized - 4 Motor Flywheel");
        telemetry.addData("Drive Mode", fieldRelative ? "Field Relative" : "Robot Relative");
        telemetry.addData("Drive Speed", String.format("%.0f%%", driveSpeed * 100));
        telemetry.addData("Right Trigger Power", String.format("%.0f%%", rightTriggerMultiplier * 100));
        telemetry.addLine("Left Trigger: Motor1=20%, Motor2=Variable");
        telemetry.addLine("Right Trigger: All motors=Variable");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Drive control
        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        // Drive speed adjustment
        if (gamepad1.dpad_up && buttonTimer.milliseconds() > 200) {
            driveSpeed = Math.min(1.0, driveSpeed + 0.05);
            drive.setSpeedMultiplier(driveSpeed);
            buttonTimer.reset();
        }
        if (gamepad1.dpad_down && buttonTimer.milliseconds() > 200) {
            driveSpeed = Math.max(0.2, driveSpeed - 0.05);
            drive.setSpeedMultiplier(driveSpeed);
            buttonTimer.reset();
        }

        // Right trigger power adjustment
        if (gamepad2.dpad_up && buttonTimer.milliseconds() > 200) {
            rightTriggerMultiplier = Math.min(2.0, rightTriggerMultiplier + 0.1);
            turret.setRightTriggerMultiplier(rightTriggerMultiplier);
            buttonTimer.reset();
        }
        if (gamepad2.dpad_down && buttonTimer.milliseconds() > 200) {
            rightTriggerMultiplier = Math.max(0.5, rightTriggerMultiplier - 0.1);
            turret.setRightTriggerMultiplier(rightTriggerMultiplier);
            buttonTimer.reset();
        }

        // Toggle field-relative mode
        if (gamepad1.back && buttonTimer.milliseconds() > 300) {
            fieldRelative = !fieldRelative;
            buttonTimer.reset();
        }

        // Apply drive
        if (fieldRelative) {
            drive.driveFeildRelative(forward, strafe, rotate);
        } else {
            drive.drive(forward, strafe, rotate);
        }

        // Turret control
        double leftTrigger = gamepad2.left_trigger;
        double rightTrigger = gamepad2.right_trigger;

        // The new runWithTriggers handles:
        // - Left trigger: leftMotor1/rightMotor1 at 20%, leftMotor2/rightMotor2 at variable
        // - Right trigger: all motors at variable speed
        turret.runWithTriggers(leftTrigger, rightTrigger);

        // Auto-aim toggle
        if (gamepad2.square && buttonTimer.milliseconds() > 300) {
            autoAimEnabled = true;
            autoAim.enable();
            buttonTimer.reset();
        }
        if (gamepad2.circle && buttonTimer.milliseconds() > 300) {
            autoAimEnabled = false;
            autoAim.disable();
            buttonTimer.reset();
        }

        // Pitch control
        double desiredServoPos = Double.NaN;
        if (autoAimEnabled) {
            desiredServoPos = autoAim.getDesiredServoPosition();
        }

        if (autoAimEnabled && !Double.isNaN(desiredServoPos)) {
            turret.setPitchAbsolute(desiredServoPos);
        } else {
            turret.turretPitch(gamepad2.right_stick_y);
        }

        // Quick manual pitch test
        if (gamepad2.triangle) {
            turret.setPitchAbsolute(1.0);
        } else if (gamepad2.cross) {
            turret.setPitchAbsolute(0.0);
        }

        // Max power shooting (right trigger direction)
        if (gamepad2.right_bumper) {
            turret.shootAtMaxPower();
        } else if (!gamepad2.right_bumper && rightTrigger <= 0.05 && leftTrigger <= 0.05) {
            // Only stop if no triggers or bumper are pressed
            turret.stop();
        }

        // Reset heading
        if (gamepad1.start && buttonTimer.milliseconds() > 300) {
            drive.resetHeading();
            buttonTimer.reset();
        }

        // Telemetry
        telemetry.addLine("=== DRIVE ===");
        telemetry.addData("Mode", fieldRelative ? "FIELD RELATIVE" : "ROBOT RELATIVE");
        telemetry.addData("Speed", String.format("%.0f%%", driveSpeed * 100));
        telemetry.addData("Heading", String.format("%.1f°", drive.getHeadingDegrees()));

        telemetry.addLine("\n=== 4-MOTOR FLYWHEEL ===");
        telemetry.addData("Right Trigger Power", String.format("%.0f%%", rightTriggerMultiplier * 100));
        telemetry.addData("Left Trigger", String.format("%.0f%%", leftTrigger * 100));
        telemetry.addData("Right Trigger", String.format("%.0f%%", rightTrigger * 100));

        telemetry.addLine("\nMotor Speeds (Left Trigger):");
        telemetry.addData("leftMotor1/rightMotor1", "20% fixed");
        telemetry.addData("leftMotor2/rightMotor2", String.format("%.0f%%", leftTrigger * 100));

        telemetry.addLine("\nMotor Speeds (Right Trigger):");
        telemetry.addData("All 4 motors", String.format("%.0f%%", rightTrigger * rightTriggerMultiplier * 100));

        telemetry.addLine("\n=== TURRET ===");
        telemetry.addData("Auto Aim", autoAimEnabled ? "ENABLED (Square)" : "DISABLED (Circle)");
        telemetry.addData("Pitch Position", String.format("%.3f", turret.getPitchPosition()));

        telemetry.addLine("\n=== CONTROLS ===");
        telemetry.addLine("Gamepad1 Back: Toggle field/robot mode");
        telemetry.addLine("Gamepad1 Start: Reset heading");
        telemetry.addLine("Gamepad1 Dpad: Adjust drive speed");
        telemetry.addLine("Gamepad2 Dpad: Adjust right trigger power");
        telemetry.addLine("Gamepad2 Square/Circle: Toggle auto-aim");
        telemetry.addLine("Gamepad2 Right Bumper: Max power shoot");
        telemetry.addLine("Gamepad2 Triggers: Control flywheel speed");
        telemetry.update();
    }
}
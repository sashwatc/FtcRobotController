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
    private double flywheelPower = 1.5; // Fixed multiplier

    @Override
    public void init() {
        drive.init(hardwareMap);
        turret.init(hardwareMap);
        autoAim = new TurretAutoAim(hardwareMap);

        drive.setSpeedMultiplier(driveSpeed);
        turret.setPowerMultiplier(flywheelPower);

        buttonTimer.reset();

        telemetry.addLine("KitBot Initialized - 4 Motor Flywheel");
        telemetry.addData("Drive Mode", fieldRelative ? "Field Relative" : "Robot Relative");
        telemetry.addData("Drive Speed", String.format("%.0f%%", driveSpeed * 100));
        telemetry.addData("Flywheel Power", String.format("%.0f%%", flywheelPower * 100));
        telemetry.update();
    }

    @Override
    public void loop() {
        // Drive control
        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        // Drive speed adjustment (optional)
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

        // Flywheel power adjustment (optional - set once, not during play)
        if (gamepad2.dpad_up && buttonTimer.milliseconds() > 200) {
            flywheelPower = Math.min(2.0, flywheelPower + 0.1);
            turret.setPowerMultiplier(flywheelPower);
            buttonTimer.reset();
        }
        if (gamepad2.dpad_down && buttonTimer.milliseconds() > 200) {
            flywheelPower = Math.max(0.5, flywheelPower - 0.1);
            turret.setPowerMultiplier(flywheelPower);
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

        // Turret control - TRIGGERS CONTROL SPEED DIRECTLY
        double leftTrigger = gamepad2.left_trigger;  // 0 to 1
        double rightTrigger = gamepad2.right_trigger; // 0 to 1

        // The more you press trigger, the faster it goes
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
        if (gamepad2.triangle) turret.setPitchAbsolute(1.0);
        if (gamepad2.cross) turret.setPitchAbsolute(0.0);

        // Max power shooting (optional button)
        if (gamepad2.right_bumper) {
            turret.shootAtMaxPower();
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

        telemetry.addLine("\n=== FLYWHEEL ===");
        telemetry.addData("Power Multiplier", String.format("%.0f%%", flywheelPower * 100));
        telemetry.addData("Left Trigger", String.format("%.0f%%", leftTrigger * 100));
        telemetry.addData("Right Trigger", String.format("%.0f%%", rightTrigger * 100));
        telemetry.addData("Actual Speed", String.format("%.0f%%", Math.max(leftTrigger, rightTrigger) * flywheelPower * 100));
        telemetry.addData("Auto Aim", autoAimEnabled ? "ENABLED" : "DISABLED");
        telemetry.addData("Pitch Pos", String.format("%.3f", turret.getPitchPosition()));

        telemetry.addLine("\n=== CONTROLS ===");
        telemetry.addLine("Triggers: Control flywheel speed (more press = faster)");
        telemetry.addLine("Back: Toggle field/robot mode");
        telemetry.addLine("Start: Reset heading");
        telemetry.addLine("Dpad: Adjust drive/flywheel power");
        telemetry.update();
    }
}
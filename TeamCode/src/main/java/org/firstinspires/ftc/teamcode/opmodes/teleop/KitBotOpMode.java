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
    private double driveSpeed = 0.9;

    @Override
    public void init() {
        drive.init(hardwareMap);
        turret.init(hardwareMap);
        autoAim = new TurretAutoAim(hardwareMap);

        drive.setSpeedMultiplier(driveSpeed);
        buttonTimer.reset();

        telemetry.addLine("✅ KitBot Initialized (Player-Centric)");
        telemetry.addLine("Drive: Robot Relative");
        telemetry.update();
    }

    @Override
    public void loop() {

        /* ===================== DRIVE (PLAYER-CENTRIC) ===================== */
        double forward = -gamepad1.left_stick_y;
        double strafe  =  gamepad1.left_stick_x;
        double rotate  =  gamepad1.right_stick_x;

        // Drive speed adjust
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

        drive.drive(forward, strafe, rotate);

        /* ===================== FLYWHEEL ===================== */
        double leftTrigger  = gamepad2.left_trigger;
        double rightTrigger = gamepad2.right_trigger;
        turret.runWithTriggers(leftTrigger, rightTrigger);

        /* ===================== AUTO AIM TOGGLE ===================== */
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

        /* ===================== TURRET PITCH ===================== */
        double desiredServoPos = Double.NaN;
        if (autoAimEnabled) {
            desiredServoPos = autoAim.getDesiredServoPosition();
        }

        if (autoAimEnabled && !Double.isNaN(desiredServoPos)) {
            turret.setPitchAbsolute(desiredServoPos);
        } else {
            turret.turretPitch(gamepad2.right_stick_y);
        }

        // Manual hard limits test
        //if (gamepad2.dpad_up) turret.setPitchAbsolute(1.0);
        //if (gamepad2.dpad_down) turret.setPitchAbsolute(0.0);

        /* ===================== TELEMETRY ===================== */
        telemetry.addLine("=== DRIVE ===");
        telemetry.addData("Speed", "%.0f%%", driveSpeed * 100);

        telemetry.addLine("\n=== FLYWHEEL ===");
        telemetry.addData("Left Trigger", "%.2f", leftTrigger);
        telemetry.addData("Right Trigger", "%.2f", rightTrigger);

        telemetry.addLine("\n=== TURRET ===");
        telemetry.addData("Auto Aim", autoAimEnabled ? "ENABLED" : "DISABLED");
        telemetry.addData("Pitch Position", "%.3f", turret.getPitchPosition());

        telemetry.update();
    }
}

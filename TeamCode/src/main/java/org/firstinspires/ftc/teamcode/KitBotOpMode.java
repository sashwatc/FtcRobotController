package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;
import org.firstinspires.ftc.teamcode.mechanisms.MechanumDrive;
import org.firstinspires.ftc.teamcode.vision.TurretAutoAim;

@TeleOp(name ="KitBotOpModeDecode")
public class KitBotOpMode extends OpMode {

    private MechanumDrive drive = new MechanumDrive();
    private KitTurretControl turret = new KitTurretControl();
    private TurretAutoAim autoAim;

    private double forward, strafe, rotate;
    private boolean autoAimEnabled = false;

    @Override
    public void init() {
        drive.init(hardwareMap);
        turret.init(hardwareMap);
        autoAim = new TurretAutoAim(hardwareMap);

        telemetry.addLine("✅ KitBot Initialized — ready to run");
        telemetry.update();
    }

    @Override
    public void loop() {
        // ---------- DRIVE (gamepad1) ----------
        forward = gamepad1.left_stick_y;
        strafe  = gamepad1.left_stick_x;
        rotate  = gamepad1.right_stick_x;
        drive.driveFeildRelative(forward, strafe, rotate);

        // ---------- FLYWHEEL (gamepad2 triggers) ----------
        double leftTrigger  = gamepad2.left_trigger;
        double rightTrigger = gamepad2.right_trigger;
        turret.runWithTriggers(leftTrigger, rightTrigger);

        // ---------- AUTO-AIM TOGGLE (gamepad2) ----------
        if (gamepad2.square) {
            autoAimEnabled = true;
            autoAim.enable();
        }
        if (gamepad2.circle) {
            autoAimEnabled = false;
            autoAim.disable();
        }

        // ---------- PITCH CONTROL ----------
        // If auto-aim enabled and has valid target, apply absolute position.
        double desiredServoPos = Double.NaN;
        if (autoAimEnabled) {
            desiredServoPos = autoAim.getDesiredServoPosition();
        }

        if (autoAimEnabled && !Double.isNaN(desiredServoPos)) {
            // auto-aim drives pitch (absolute)
            turret.setPitchAbsolute(desiredServoPos);
        } else {
            // manual pitch (gamepad1 right stick)
            turret.turretPitch(gamepad2.right_stick_y);
        }

        // Quick manual full travel test (useful during initial tuning)
        if (gamepad2.dpad_up) turret.setPitchAbsolute(1.0);
        if (gamepad2.dpad_down) turret.setPitchAbsolute(0.0);

        // ---------- TELEMETRY ----------
        telemetry.addData("Drive", "F: %.2f  S: %.2f  R: %.2f", forward, strafe, rotate);
        telemetry.addData("Flywheel", "LTrig: %.2f  RTrig: %.2f", leftTrigger, rightTrigger);
        telemetry.addData("Auto Aim", autoAimEnabled ? "ENABLED" : "DISABLED");
        telemetry.addData("PitchPos", "%.3f", turret.getPitchPosition());
        telemetry.update();
    }
}

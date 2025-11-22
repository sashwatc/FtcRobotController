package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;
import org.firstinspires.ftc.teamcode.mechanisms.MechanumDrive;
import org.firstinspires.ftc.teamcode.vision.TurretAutoAim;

@TeleOp(name ="KitBotOpModeDecode")
public class KitBotOpMode extends OpMode {

    MechanumDrive drive = new MechanumDrive();
    KitTurretControl turret = new KitTurretControl();
    TurretAutoAim autoAim;

    double forward, strafe, rotate;

    boolean autoAimEnabled = false;

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

        // ------------------ DRIVE ------------------
        forward = gamepad1.left_stick_y;
        strafe  = gamepad1.left_stick_x;
        rotate  = gamepad1.right_stick_x;
        drive.driveFeildRelative(forward, strafe, rotate);

        // ------------------ FLYWHEEL ------------------
        double leftTrigger  = gamepad2.left_trigger;
        double rightTrigger = gamepad2.right_trigger;
        turret.runWithTriggers(leftTrigger, rightTrigger);

        // ------------------ AUTO AIM TOGGLE ------------------
        if (gamepad2.square) {
            autoAimEnabled = true;
            autoAim.enable();
        }
        if (gamepad2.circle) {
            autoAimEnabled = false;
            autoAim.disable();
        }

        // ------------------ PITCH CONTROL ------------------
        double desiredServoPos = Double.NaN;

        if (autoAimEnabled) {
            desiredServoPos = autoAim.getDesiredServoPosition();
        }

        if (autoAimEnabled && !Double.isNaN(desiredServoPos)) {
            // auto-aim is active & valid
            turret.setPitchAbsolute(desiredServoPos);
        } else {
            // *** FIXED: manual pitch now uses GAMEPAD1 right stick ***
            turret.turretPitch(gamepad1.right_stick_y);
        }

        // ------------------ TELEMETRY ------------------
        telemetry.addData("Drive", "F: %.2f  S: %.2f  R: %.2f", forward, strafe, rotate);
        telemetry.addData("Flywheel", "LTrig: %.2f  RTrig: %.2f", leftTrigger, rightTrigger);
        telemetry.addData("Auto Aim", autoAimEnabled ? "ENABLED" : "DISABLED");
        telemetry.addData("PitchPos", "%.3f", turret.getPitchPosition());
        telemetry.update();
    }
}

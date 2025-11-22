package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;

@TeleOp(name="TurretPitchServoTest")
public class TurretServoTest extends OpMode {

    KitTurretControl turret = new KitTurretControl();

    @Override
    public void init() {
        turret.init(hardwareMap);

        telemetry.addLine("✅ Turret Pitch Servo Test Loaded");
        telemetry.addLine("Use GAMEPAD2 Right Stick Y to move servos");
        telemetry.addLine("Servos should tilt together (mirrored)");
        telemetry.update();
    }

    @Override
    public void loop() {

        // manual test input
        double stickY = gamepad2.right_stick_y;

        // move the pitch servos
        turret.turretPitch(stickY);

        // show feedback
        telemetry.addData("StickY", stickY);
        telemetry.addData("PitchPosition", turret.getPitchPosition());
        telemetry.update();
    }
}

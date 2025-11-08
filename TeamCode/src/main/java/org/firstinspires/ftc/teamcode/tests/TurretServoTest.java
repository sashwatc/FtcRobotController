package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;

@TeleOp(name="TurretServoTest")
public class TurretServoTest extends OpMode {
    KitTurretControl turret = new KitTurretControl();

    @Override
    public void init() {
        turret.init(hardwareMap);
        telemetry.addLine("✅ Turret Servo Test Ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        double stick = -gamepad2.right_stick_y;
        turret.turretPitch(stick);
        telemetry.addData("Stick", stick);
        telemetry.update();
    }
}



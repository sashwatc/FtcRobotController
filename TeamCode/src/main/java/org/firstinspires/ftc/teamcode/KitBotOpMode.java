package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;
import org.firstinspires.ftc.teamcode.mechanisms.MechanumDrive;
@TeleOp(name ="KitBotOpModeDecode")
public class KitBotOpMode extends OpMode {

    MechanumDrive drive = new MechanumDrive();
    KitTurretControl turret = new KitTurretControl();

    double forward, strafe, rotate;

    @Override
    public void init() {
        drive.init(hardwareMap);
        turret.init(hardwareMap);
        telemetry.addLine("✅ KitBot Initialized — ready to run");
        telemetry.update();
    }

    @Override
    public void loop() {
        //drive
        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;
        drive.driveFeildRelative(forward, strafe, rotate);
        //turret
        double leftTrigger  = gamepad2.left_trigger;
        double rightTrigger = gamepad2.right_trigger;
        double pitchStickY  = gamepad2.right_stick_y;

        turret.runWithTriggers(leftTrigger, rightTrigger);
        turret.turretPitch(pitchStickY);
        // telemetry
        telemetry.addData("Drive", "F: %.2f  S: %.2f  R: %.2f", forward, strafe, rotate);
        telemetry.addData("Turret", "LeftTrig: %.2f  RightTrig: %.2f  StickY: %.2f", leftTrigger, rightTrigger, pitchStickY);
        telemetry.update();

    }
}

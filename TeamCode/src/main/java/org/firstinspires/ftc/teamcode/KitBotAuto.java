package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;
import org.firstinspires.ftc.teamcode.mechanisms.MechanumDrive;

@Autonomous(name = "KitBotAuto")
public class KitBotAuto extends LinearOpMode {

    private MechanumDrive drive = new MechanumDrive();
    private KitTurretControl turret = new KitTurretControl();

    @Override
    public void runOpMode() throws InterruptedException {

        // ---------- INIT ----------
        drive.init(hardwareMap);
        turret.init(hardwareMap);

        telemetry.addLine("✅ Auto Initialized");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // ---------- AUTONOMOUS SEQUENCE ----------

        // 1️⃣ Drive forward for 1 second
        drive.drive(0.5, 0, 0);
        sleep(1000);
        drive.drive(0, 0, 0);

        // 2️⃣ Aim turret up
        turret.setPitchAbsolute(0.7);
        sleep(600);

        // 3️⃣ Spin flywheel (shoot)
        turret.runWithTriggers(1.0, 0.0);
        sleep(1500);
        turret.stop();

        // 4️⃣ Lower turret
        turret.setPitchAbsolute(0.5);
        sleep(500);

        // ---------- END ----------
        drive.drive(0, 0, 0);
    }
}

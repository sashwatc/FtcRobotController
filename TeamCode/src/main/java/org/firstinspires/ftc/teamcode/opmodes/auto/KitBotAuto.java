package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.KitTurretControl;
import org.firstinspires.ftc.teamcode.mechanisms.MechanumDrive;
import org.firstinspires.ftc.teamcode.vision.TurretAutoAim;

@Autonomous(name = "auto", group = "Competition")
public class KitBotAuto extends LinearOpMode {

    // ========== STATE DEFINITIONS ==========
    private enum AutoState {
        INIT,
        DRIVE_TO_POSITION,
       SCAN_FOR_TARGET,
        AIM_AT_TARGET,
        SHOOT,
        RESET_TURRET,
        PARK,
        COMPLETE
    }

    private AutoState currentState = AutoState.INIT;

    // ========== HARDWARE ==========
    private MechanumDrive drive;
    private KitTurretControl turret;
    private TurretAutoAim autoAim;

    // ========== TIMERS ==========
    private ElapsedTime stateTimer = new ElapsedTime();
    private ElapsedTime scanTimer = new ElapsedTime();
    private ElapsedTime shootTimer = new ElapsedTime();

    // ========== STATE VARIABLES ==========
    private boolean targetAcquired = false;
    private double lastAimPosition = 0.5;
    private int scanCycles = 0;

    @Override
    public void runOpMode() {
        // ---------- INITIALIZATION ----------
        drive = new MechanumDrive();
        turret = new KitTurretControl();

        drive.init(hardwareMap);
        turret.init(hardwareMap);
        autoAim = new TurretAutoAim(hardwareMap);

        telemetry.addLine("✅ State Machine Auto Initialized");
        telemetry.addData("Current State", currentState);
        telemetry.update();

        waitForStart();
        stateTimer.reset();

        // ========== MAIN STATE MACHINE LOOP ==========
        while (opModeIsActive()) {
            switch (currentState) {

                // ===== STATE 1: INIT =====
                case INIT:
                    telemetry.addData("State", "INIT → Setting up");

                    // Reset all timers
                    stateTimer.reset();
                    scanTimer.reset();
                    shootTimer.reset();

                    // Move to next state immediately
                    currentState = AutoState.DRIVE_TO_POSITION;
                    break;

                // ===== STATE 2: DRIVE TO SHOOTING POSITION =====
                case DRIVE_TO_POSITION:
                    telemetry.addData("State", "DRIVE_TO_POSITION");
                    telemetry.addData("Time", "%.1f/1.5 sec", stateTimer.seconds());

                    // Drive forward at 50% power
                    drive.drive(0.5, 0, 0);

                    // Check if we've driven long enough (1.5 seconds)
                    if (stateTimer.seconds() > 1.5) {
                        drive.drive(0, 0, 0); // Stop
                        currentState = AutoState.SCAN_FOR_TARGET;
                        stateTimer.reset();
                        scanTimer.reset();
                    }
                    break;

       /*         // ===== STATE 3: SCAN FOR APRILTAG =====
                case SCAN_FOR_TARGET:
                    telemetry.addData("State", "SCAN_FOR_TARGET");
                    telemetry.addData("Scan Time", "%.1f/3.0 sec", scanTimer.seconds());
                    telemetry.addData("Scan Cycles", scanCycles);

                    // Enable auto-aim
                    autoAim.enable();

                    // Get target position from Limelight
                    double desiredPos = autoAim.getDesiredServoPosition();

                    if (!Double.isNaN(desiredPos)) {
                        // Target found!
                        targetAcquired = true;
                        lastAimPosition = desiredPos;
                        turret.setPitchAbsolute(desiredPos);

                        telemetry.addLine("✅ TARGET ACQUIRED!");
                        telemetry.addData("Aim Position", "%.3f", desiredPos);

                        // Move to aiming state
                        currentState = AutoState.AIM_AT_TARGET;
                        stateTimer.reset();

                    } else {
                        // No target yet - scan back and forth
                        scanCycles++;
                        double scanPos = 0.5 + 0.25 * Math.sin(scanTimer.seconds() * 2.0);
                        turret.setPitchAbsolute(scanPos);

                        telemetry.addLine("🔍 Scanning...");
                        telemetry.addData("Scan Position", "%.3f", scanPos);

                        // Timeout after 3 seconds
                        if (scanTimer.seconds() > 3.0) {
                            telemetry.addLine("⚠️ SCAN TIMEOUT - Using preset");
                            currentState = AutoState.AIM_AT_TARGET;
                            stateTimer.reset();
                        }
                    }
                    break;

           /*     // ===== STATE 4: AIM AT TARGET =====
                case AIM_AT_TARGET:
                    telemetry.addData("State", "AIM_AT_TARGET");

                    if (targetAcquired) {
                        // Use Limelight position
                        desiredPos = autoAim.getDesiredServoPosition();
                        if (!Double.isNaN(desiredPos)) {
                            turret.setPitchAbsolute(desiredPos);
                            lastAimPosition = desiredPos;
                            telemetry.addData("Aiming", "Limelight: %.3f", desiredPos);
                        } else {
                            // Lost target, use last known position
                            turret.setPitchAbsolute(lastAimPosition);
                            telemetry.addData("Aiming", "Last known: %.3f", lastAimPosition);
                        }
                    } else {
                        // No target found - use preset position
                        turret.setPitchAbsolute(0.7);
                        telemetry.addData("Aiming", "Preset: 0.7");
                    }

                    // Wait for turret to settle (0.5 seconds)
                    if (stateTimer.seconds() > 0.5) {
                        currentState = AutoState.SHOOT;
                        shootTimer.reset();
                    }
                    break;

           /*     // ===== STATE 5: SHOOT =====
                case SHOOT:
                    telemetry.addData("State", "SHOOT");
                    telemetry.addData("Shoot Time", "%.1f/1.5 sec", shootTimer.seconds());

                    // Start flywheel
                    turret.runWithTriggers(1.0, 0.0);

                    // Shoot for 1.5 seconds
                    if (shootTimer.seconds() > 1.5) {
                        turret.stop();
                        currentState = AutoState.RESET_TURRET;
                        stateTimer.reset();
                    }
                    break;

                // ===== STATE 6: RESET TURRET =====
                /*  case RESET_TURRET:
                    telemetry.addData("State", "RESET_TURRET");

                    // Disable auto-aim
                    autoAim.disable();

                    // Lower turret to safe position
                    turret.setPitchAbsolute(0.3);

                    // Wait for turret to move (0.3 seconds)
                    if (stateTimer.seconds() > 0.3) {
                        currentState = AutoState.PARK;
                        stateTimer.reset();
                    }
                    break;
*/
                // ===== STATE 7: PARK =====
                case PARK:
                    telemetry.addData("State", "PARK");

                    // Strafe right into parking zone
                    drive.drive(0, 0.3, 0);

                    // Park for 0.8 seconds
                    if (stateTimer.seconds() > 0.8) {
                        drive.drive(0, 0, 0);
                        currentState = AutoState.COMPLETE;
                    }
                    break;

                // ===== STATE 8: COMPLETE =====
                case COMPLETE:
                    telemetry.addData("State", "COMPLETE - Auto Finished!");
                    // Do nothing, auto is done
                    break;
            }

            // Update telemetry every loop
            telemetry.update();
        }
    }
}
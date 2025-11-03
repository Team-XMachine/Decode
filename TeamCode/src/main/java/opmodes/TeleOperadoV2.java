package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import enums.IntakeStates;
import enums.ShooterStates;
import hardware.Robot;
import systems.GamepadX;
import systems.IntakeSystem;
import systems.LimeLightImageTools;
import systems.ShooterSystem;
import systems.TransferSystem;

@TeleOp(name = "Cyclops")
public class TeleOperadoV2 extends LinearOpMode {

    Robot robot;
    IntakeSystem intakeSystem;
    TransferSystem transferSystem;
    ShooterSystem shooterSystem;

    GamepadX gamepad;

    @Override
    public void runOpMode() throws InterruptedException {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        robot = new Robot(hardwareMap);
        intakeSystem = new IntakeSystem();
        transferSystem = new TransferSystem();
        shooterSystem = new ShooterSystem();

        gamepad = new GamepadX(gamepad1);

        telemetry.setMsTransmissionInterval(11);

        robot.getLimelight().pipelineSwitch(0);

        LimeLightImageTools lime = new LimeLightImageTools(robot.getLimelight());
        lime.setDriverStationStreamSource();
        lime.forwardAll();

        dashboard.startCameraStream(lime.getStreamSource(), 30);

        waitForStart();
        while (opModeIsActive()) {
            gamepad.readGamepad(gamepad1);
            intakeSystem.update(robot);
            transferSystem.update(robot);
            shooterSystem.update(robot, telemetry);

            robot.runDrive(gamepad1);

            double ta = robot.getLimelight().getLatestResult() != null ? robot.getLimelight().getLatestResult().getTa() : 0;


            telemetry.addLine("========== STATES ==========");
            telemetry.addData("INTAKE CS", IntakeSystem.CS);
            telemetry.addData("TRANSFER CS", TransferSystem.CS);
            telemetry.addData("SHOOTING CS", ShooterSystem.CS);
            telemetry.addLine("------------------------");
            telemetry.addData("STORAGE 1", IntakeSystem.storage[0]);
            telemetry.addData("STORAGE 2", IntakeSystem.storage[1]);
            telemetry.addLine("========== DATA ==========");
            telemetry.addData("INTAKE CURRENT", robot.getMotor("intake").getCurrent(CurrentUnit.MILLIAMPS));
            telemetry.addData("ENTRY SENSOR DISTANCE (CM)", robot.getDistanceSensor("entrySensor").getDistance(DistanceUnit.CM));
            telemetry.addData("ITK SENSOR DISTANCE (CM)", robot.getDistanceSensor("itk").getDistance(DistanceUnit.CM));
            telemetry.addData("SHOOTER1 VELOCITY", robot.getMotor("LS").getVelocity());
            telemetry.addData("SHOOTER2 VELOCITY", robot.getMotor("RS").getVelocity());
            telemetry.addData("TA", ta);
            telemetry.update();

            if (gamepad.ONEwasBPressed()) {
                IntakeSystem.CS = IntakeStates.CATCH_2;
            }

            if (gamepad.TWOwasBPressed()) {
                IntakeSystem.CS = IntakeStates.EJECT_1;
            }

            if (gamepad.ONEwasAPressed()) {
                ShooterSystem.CS = ShooterStates.SHOOTING;
            }
            if (gamepad.TWOwasAPressed()) {
                ShooterSystem.CS = ShooterStates.IDLE;
            }
        }
    }
}
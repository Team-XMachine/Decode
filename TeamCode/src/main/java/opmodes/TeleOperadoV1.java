package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.limelightvision.LLFieldMap;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.List;

import drive.SampleMecanumDrive;
import enums.IntakeStates;
import systems.GamepadX;
import systems.IntakeSystem;
import util.Globals;

@TeleOp(name="Logan")
public class TeleOperadoV1 extends LinearOpMode {

    SampleMecanumDrive drive;
    DcMotorEx intake, shooter;
    CRServo transfer;

    Rev2mDistanceSensor checker, itk;

    GamepadX gamepad;

    Limelight3A limelight;

    boolean c4tch = false;
    boolean GOTCHA = false;

    @Override
    public void runOpMode() throws InterruptedException {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        gamepad = new GamepadX(gamepad1);

        drive = new SampleMecanumDrive(hardwareMap);
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        transfer = hardwareMap.get(CRServo.class, "transfer");

        checker = hardwareMap.get(Rev2mDistanceSensor.class, "checker");
        itk = hardwareMap.get(Rev2mDistanceSensor.class, "itk");

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        limelight.start();

        while (opModeIsActive()) {

            double ta = limelight.getLatestResult() == null ? 0 : limelight.getLatestResult().getTa();

            telemetry.addData("CHECKER", itk.getDistance(DistanceUnit.CM));
            telemetry.addData("Transfer Servo Power", transfer.getPower());
            telemetry.addData("Current", intake.getCurrent(CurrentUnit.MILLIAMPS));
            telemetry.addData("TA", ta);

            telemetry.update();

            gamepad.readGamepad(gamepad1);

            c4tch = itk.getDistance(DistanceUnit.CM) <= Globals.ITK_DISTANCE;
            GOTCHA = intake.getCurrent(CurrentUnit.MILLIAMPS) >= Globals.MAX_CURRENT;

            drive.setWeightedDrivePower(
                    new Pose2d(
                            gamepad1.left_stick_y,
                            gamepad1.left_stick_x,
                            (gamepad1.right_stick_x - gamepad1.right_stick_y)
                    )
            );

            if (gamepad.ONEwasBPressed()) {
                if (!c4tch) {
                    transfer.setPower(Globals.TRANSFER_POWER);
                    intake.setPower(Globals.INTAKE_POWER);
                } else {
                    intake.setPower(0);
                    transfer.setPower(0);
                }
            }

            if (c4tch) {
                transfer.setPower(0);
                if(GOTCHA) {
                    intake.setPower(0);
                }
            }



            if (gamepad.TWOwasBPressed()) {
                transfer.setPower(-Globals.TRANSFER_POWER);
                intake.setPower(-Globals.INTAKE_POWER);
                sleep(700);
                transfer.setPower(0);
                intake.setPower(0);
            }

            if (gamepad.ONEwasAPressed()) {
                shooter.setPower(ta / Globals.SHOOTER_KP);
            }


            if (gamepad.ONEwasYPressed()) {
                transfer.setPower(Globals.TRANSFER_POWER);
                sleep(Globals.TRANSFER_TIME);
                shooter.setPower(0);
                transfer.setPower(0);
            }
        }
    }
}
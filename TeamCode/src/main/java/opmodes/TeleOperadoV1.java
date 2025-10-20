package opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import drive.SampleMecanumDrive;
import systems.GamepadX;
import util.Globals;

@TeleOp(name="Logan")
public class TeleOperadoV1 extends LinearOpMode {

    SampleMecanumDrive drive;
    DcMotorEx intake, shooter;

    CRServo transfer;

    Rev2mDistanceSensor checker, itk;

    GamepadX gamepad;

    boolean c4tch = false;

    @Override
    public void runOpMode() throws InterruptedException {

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
        while (opModeIsActive()) {

            telemetry.addData("CHECKER", itk.getDistance(DistanceUnit.CM));
            telemetry.addData("Transfer Servo Power", transfer.getPower());
            telemetry.update();

            gamepad.readGamepad(gamepad1);

            c4tch = itk.getDistance(DistanceUnit.CM) <= Globals.ITK_DISTANCE;

            drive.setWeightedDrivePower(
                    new Pose2d(
                            gamepad1.left_stick_y,
                            gamepad1.left_stick_x,
                            (gamepad1.right_stick_x - gamepad1.right_stick_y)
                    )
            );

            if (gamepad.ONEwasBPressed()) {
                if (!c4tch) {
                    intake.setPower(Globals.INTAKE_POWER);
                } else {
                    intake.setPower(0);
                }
            }

            if (c4tch) {
                intake.setPower(0);
            }

            if (gamepad.TWOwasBPressed()) {
                transfer.setPower(-Globals.TRANSFER_POWER);
                intake.setPower(-Globals.INTAKE_POWER);
                sleep(700);
                transfer.setPower(0);
                intake.setPower(0);
            }

            if (gamepad.ONEwasAPressed()) {
                shooter.setPower(Globals.SHOOTER_POWER);
            }

            if (gamepad.ONEwasYPressed()) {
                transfer.setPower(Globals.TRANSFER_POWER);
                sleep(600);
                shooter.setPower(0);
                transfer.setPower(0);
            }
        }
    }
}
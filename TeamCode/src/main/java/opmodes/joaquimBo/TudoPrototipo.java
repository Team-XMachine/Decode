package opmodes.joaquimBo;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import drive.SampleMecanumDrive;

@Config
@TeleOp(name = "Tudo Prototipo")

public class TudoPrototipo extends LinearOpMode {

    DcMotorEx lancador;
    DcMotorEx intake;

    SampleMecanumDrive Drive;

    @Override
    public void runOpMode() throws InterruptedException {

        lancador = hardwareMap.get(DcMotorEx.class, "Lançador");
        telemetry = FtcDashboard.getInstance().getTelemetry();
        Drive = new SampleMecanumDrive(hardwareMap);
        intake = hardwareMap.get(DcMotorEx.class, "Intake");
        waitForStart();

        while (opModeIsActive()) {

            Drive.setWeightedDrivePower(
                    new Pose2d(
                            gamepad1.left_stick_y,
                            gamepad1.left_stick_x,
                            gamepad1.right_stick_y
                    ));

            lancador.setPower(-gamepad1.right_trigger);
            intake.setPower(-gamepad1.left_trigger);

            telemetry.addData(" Left Stick Y: ", gamepad1.left_stick_y);
            telemetry.addData(" Left trigger: ", gamepad1.left_trigger);
            telemetry.addData(" Gamepad Right Trigger: ", gamepad1.right_trigger);
            telemetry.addData(" Velocidade do Motor: ", lancador.getVelocity());
            telemetry.addData(" Left Stick X", gamepad1.left_stick_x);
            telemetry.update();

        }
    }
}
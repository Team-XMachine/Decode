package opmodes.joaquimBo;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Config
@TeleOp
public class testeIntake extends LinearOpMode {

    DcMotorEx intake, transfer, shooter, core;

    public static double N = -1, M = 1, X = -1, Y = 1;

    @Override
    public void runOpMode() throws InterruptedException {

        intake = hardwareMap.get(DcMotorEx.class, "TE");
        transfer = hardwareMap.get(DcMotorEx.class, "FE");
        shooter = hardwareMap.get(DcMotorEx.class, "TD");
        core = hardwareMap.get(DcMotorEx.class, "FD");


        waitForStart();
        while (opModeIsActive()) {
            intake.setPower((gamepad1.right_trigger - gamepad1.left_trigger) * N);
            shooter.setPower((gamepad1.right_trigger - gamepad1.left_trigger) * X);
            transfer.setPower((gamepad1.right_trigger - gamepad1.left_trigger) * M);
        }
    }
}

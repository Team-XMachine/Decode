package opmodes.joaquimBo;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "intake")
public class Intake1 extends LinearOpMode {
    private DcMotorEx intake;


    @Override
    public void runOpMode() {
        intake = hardwareMap.get(DcMotorEx.class, "motor4");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            intake.setPower(0.5);


        }
    }
}
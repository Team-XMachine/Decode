package CTRLX.tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous
public class motors_test extends LinearOpMode {
    DcMotorEx left, right;

    Servo intake;

    @Override
    public void runOpMode() throws InterruptedException {

        left = hardwareMap.get(DcMotorEx.class, "motor1");
        right = hardwareMap.get(DcMotorEx.class, "motor2");

        intake = hardwareMap.get(Servo.class, "itk");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left.setDirection(DcMotorSimple.Direction.REVERSE);
        right.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setPosition(0);

        waitForStart();

        if(opModeIsActive()) {
            left.setPower(0.5);
            right.setPower(-0.5);
            sleep(3000);
            left.setPower(0);
            right.setPower(0);
        }
    }
}

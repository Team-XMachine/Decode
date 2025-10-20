package test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import systems.GamepadX;

@TeleOp(name = "dia da família")
@Config
public class diadafamilia extends LinearOpMode {

    public static double kp = 0.01;

    public static int tp = 50;

    Servo claw;

    DcMotorEx left, right, arm;

    GamepadX gamepad;

    @Override
    public void runOpMode() throws InterruptedException {

        left = hardwareMap.get(DcMotorEx.class, "left");
        right = hardwareMap.get(DcMotorEx.class, "right");

        arm = hardwareMap.get(DcMotorEx.class, "arm");

        claw = hardwareMap.get(Servo.class, "claw");

        gamepad = new GamepadX(gamepad1);

        waitForStart();
        while (opModeIsActive()) {

            arm.setPower(PID(tp, arm.getCurrentPosition()));

            double drive = -gamepad1.right_stick_x; // Forward/backward
            double turn = gamepad1.left_stick_y;  // Turning

            // Calculate motor power
            double leftPower = drive + turn;
            double rightPower = drive - turn;

            // Clip values to [-1.0, 1.0]
            leftPower = Range.clip(leftPower, -1.0, 1.0);
            rightPower = Range.clip(rightPower, -1.0, 1.0);

            // Apply power
            left.setPower(leftPower);
            right.setPower(rightPower);

            if(gamepad1.a) {
                claw.setPosition(1);
            }

            if(gamepad1.b) {
                claw.setPosition(0);
            }

            if (gamepad1.dpad_up) {
                tp = 100;
            }

            if (gamepad1.dpad_down) {
                tp = 50;
            }
        }
    }

    public double PID(double reference, double state) {
        double error = reference - state;

        return error * kp;
    }
}

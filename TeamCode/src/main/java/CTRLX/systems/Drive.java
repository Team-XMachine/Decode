package CTRLX.systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class Drive {

    DcMotorEx left, right;

    private ElapsedTime runtime = new ElapsedTime();

    // PIDF coefficients (tune these!)
    public static double kP = 0.01, kI = 0, kD = 0.001, kF = 0;
    private double integral = 0, lastError = 0;

    public Drive(HardwareMap hwMap) {

        left = hwMap.get(DcMotorEx.class, "motor1");
        right = hwMap.get(DcMotorEx.class, "motor2");

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        left.setDirection(DcMotorSimple.Direction.REVERSE);
        right.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void followLine(double lp, double rp) {
        left.setPower(lp);
        right.setPower(rp);
    }

    public void stopMotors() {
        left.setPower(0);
        right.setPower(0);
    }

    // Time-based turns
    public void turnLeft(double timeSec) {
        ElapsedTime timer = new ElapsedTime();
        while (timer.seconds() < timeSec) {
            left.setPower(-0.4);
            right.setPower(0.4);
        }
        stopMotors();
    }

    public void turnRight(double timeSec) {
        ElapsedTime timer = new ElapsedTime();
        while (timer.seconds() < timeSec) {
            left.setPower(0.4);
            right.setPower(-0.4);
        }
        stopMotors();
    }

    public void uTurn(double timeSec) {
        ElapsedTime timer = new ElapsedTime();
        while (timer.seconds() < timeSec) {
            left.setPower(0.4);
            right.setPower(-0.4);
        }
        stopMotors();
    }
}
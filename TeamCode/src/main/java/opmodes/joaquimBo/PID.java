package opmodes.joaquimBo;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class PID extends LinearOpMode {

    DcMotorEx motor;

    double integralSum = 0;
    double kp = 0;
    double ki = 0;
    double kd = 0;

    ElapsedTime timer = new ElapsedTime();
    private double lastError;

    @Override
    public void runOpMode() throws InterruptedException{

        motor = hardwareMap.get(DcMotorEx.class, "motor");
        waitForStart();
        while (opModeIsActive()) {
            double Power = (PIDcontrol( 100, motor.getCurrentPosition()));
            motor.setPower(Power);
        }

    }

    public double PIDcontrol(double reference, double state){
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivate = (error - lastError) / timer.seconds();
        timer.reset();

        double output = (error * kp) + ( derivate * kd) + (integralSum * ki);
        lastError = error;
        return output;
    }
}
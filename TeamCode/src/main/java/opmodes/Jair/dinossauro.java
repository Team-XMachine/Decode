package opmodes.Jair;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="magneto")
@Config
public class dinossauro extends LinearOpMode {

    public static final double shooterAngle = 75;
    double lastError = 0;
    double integralSum = 0;

    ElapsedTime time = new ElapsedTime();

    Limelight3A limelight;
    DcMotorEx shooter;
    double Ta = 0;
    public static double Kp = 0, Kd = 0;



    double targetVel = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);

        waitForStart();
        limelight.start();
        while (opModeIsActive()) {
            telemetry.addData("TA", limelight.getLatestResult() == null ? 0 : limelight.getLatestResult().getTa());
            telemetry.addData("TY", limelight.getLatestResult() == null ? 0 : limelight.getLatestResult().getTy());
            telemetry.addData("TX", limelight.getLatestResult() == null ? 0 : limelight.getLatestResult().getTx());
            telemetry.update();
            Ta = limelight.getLatestResult() == null ? 0 : limelight.getLatestResult().getTa();


            //shooter.setPower(Ta / Kp);

            shooter.setVelocity(targetVel, AngleUnit.DEGREES);
        }
    }

    public double pid(double reference, double state) {
        double error = reference - state;
        double derivative = (error - lastError) / time.seconds();
        lastError = error;

        return (Kp * error) + (Kd * derivative);
    }
}

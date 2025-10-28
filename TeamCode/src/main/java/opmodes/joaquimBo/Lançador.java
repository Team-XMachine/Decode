package opmodes.joaquimBo;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcontroller.mechanics.PIDControl;

@Config
@TeleOp(name = "lançador prototipo")
public class Lançador extends LinearOpMode {

    DcMotorEx motor1;

    public static double p = 0;
    public static double i = 0;
    public static double d = 0;

    public static double targetShooterVelocity = 1200;

    @Override
    public void runOpMode() throws InterruptedException {
        motor1 = hardwareMap.get(DcMotorEx.class, "motorLanç");
        telemetry.addData("motor", "pronto");
        telemetry = FtcDashboard.getInstance().getTelemetry();
        PIDControl PID = new PIDControl();


        waitForStart();

        while (opModeIsActive()) {
            PID.setKp(p);
            PID.setKi(i);
            PID.setKd(d);

            if(gamepad1.right_trigger != 0){
                motor1.setVelocity(PID.PIDcontrol(targetShooterVelocity, motor1.getVelocity()));
            }else{
                motor1.setPower(0);
            }

            telemetry.addData("Gamepad left stick Y", gamepad1.right_trigger);
            telemetry.addData("velocidade do motor", motor1.getVelocity());
            telemetry.update();
        }
    }
}

package opmodes.joaquimBo;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
@TeleOp(name = "testarMotores")
public class TestaMotores extends LinearOpMode {

    private DcMotorEx FD;
    private DcMotorEx FE;
    private DcMotorEx TD;
    private DcMotorEx TE;

    @Override
    public void runOpMode() throws InterruptedException {

        FD = hardwareMap.get(DcMotorEx.class, "FD");
        FD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FD.setVelocity(0.5);
        FD.setDirection(DcMotorSimple.Direction.FORWARD);

        FE = hardwareMap.get(DcMotorEx.class, "FE");
        FE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FE.setVelocity(0.5);
        FE.setDirection(DcMotorSimple.Direction.REVERSE);

        TD = hardwareMap.get(DcMotorEx.class, "TD");
        TD.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TD.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TD.setVelocity(0.5);
        TD.setDirection(DcMotorSimple.Direction.FORWARD);

        TE = hardwareMap.get(DcMotorEx.class, "TE");
        TE.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TE.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        TE.setVelocity(0.5);
        TE.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {

            if(gamepad1.a) {
                FD.setPower(0.5);
            } else {
                FD.setPower(0.0);
            }

            if(gamepad1.x) {
                FE.setPower(0.5);
            } else {
                FE.setPower(0.0);
            }

            if(gamepad1.y) {
                TD.setPower(0.5);
            } else {
                TD.setPower(0.0);
            }
            if(gamepad1.b) {
                TE.setPower(0.5);
            } else {
                TE.setPower(0.0);
            }
        }
    }
}

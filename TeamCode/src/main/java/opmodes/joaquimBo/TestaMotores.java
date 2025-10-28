package opmodes.joaquimBo;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "testarMotores")
public class TestaMotores extends LinearOpMode {

    private DcMotorEx FrenteDireita;
    private DcMotorEx FrenteEsquerda;
    private DcMotorEx AtrasDireita;
    private DcMotorEx AtrasEsquerda;

    @Override
    public void runOpMode() throws InterruptedException {

        FrenteDireita = hardwareMap.get(DcMotorEx.class, "front1");
        FrenteDireita.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrenteDireita.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrenteDireita.setVelocity(0.5);
        FrenteDireita.setDirection(DcMotorSimple.Direction.FORWARD);


        FrenteEsquerda = hardwareMap.get(DcMotorEx.class, "front2");
        FrenteEsquerda.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrenteEsquerda.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrenteEsquerda.setVelocity(0.5);
        FrenteEsquerda.setDirection(DcMotorSimple.Direction.REVERSE);

        AtrasDireita = hardwareMap.get(DcMotorEx.class, "back1");
        AtrasDireita.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        AtrasDireita.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        AtrasDireita.setVelocity(0.5);
        AtrasDireita.setDirection(DcMotorSimple.Direction.FORWARD);

        AtrasEsquerda = hardwareMap.get(DcMotorEx.class, "back2");
        AtrasEsquerda.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        AtrasEsquerda.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        AtrasEsquerda.setVelocity(0.5);
        AtrasEsquerda.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            if(gamepad1.a) {
                FrenteDireita.setPower(0.5);
            } else {
                FrenteDireita.setPower(0.0);
            }

            if(gamepad1.x) {
                FrenteEsquerda.setPower(0.5);
            } else {
                FrenteEsquerda.setPower(0.0);
            }

            if(gamepad1.y) {
                AtrasDireita.setPower(0.5);
            } else {
                AtrasDireita.setPower(0.0);
            }
            if(gamepad1.b) {
                AtrasEsquerda.setPower(0.5);
            } else {
                AtrasEsquerda.setPower(0.0);
            }
        }
    }
}

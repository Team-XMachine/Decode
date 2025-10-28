package opmodes.joaquimBo;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Tentativa Um Mexer Motor")
public class Tentativa extends LinearOpMode {

    // definição de motores
    private DcMotorEx frontLeftMotor;
    private DcMotorEx backLeftMotor;
    private DcMotorEx frontRightMotor;
    private DcMotorEx backRightMotor;



    @Override
    public void runOpMode() throws InterruptedException {
        // Configurar motor


        frontLeftMotor = hardwareMap.get(DcMotorEx.class, "front1");
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setVelocity(0.5);
        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);


        frontRightMotor = hardwareMap.get(DcMotorEx.class, "front2");
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setVelocity(0.5);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        backLeftMotor = hardwareMap.get(DcMotorEx.class, "back1");
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setVelocity(0.5);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        backRightMotor = hardwareMap.get(DcMotorEx.class, "back2");
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setVelocity(0.5);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        //telemetria inicial
        telemetry.addData("Status", "Robô configurado!");
        telemetry.addData("Controles:", "");
        telemetry.addData(" RIGHT TRIGGER", "FRENTE");
        telemetry.addData(" LEFT TRIGGER", "TRÁS");
        telemetry.addData(" Joystick Esquerdo X", "LATERAL");
        telemetry.addData(" Joystick Direito X", "ROTAÇÃO");

        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            //definir triggers

            double rightTrigger = gamepad1.right_trigger;
            double leftTrigger = gamepad1.left_trigger;

            //definir lados
            double y = leftTrigger - rightTrigger;
            double x = gamepad1.left_stick_x * 1.1; // Compensa imperfeições no strafing
            double z = gamepad1.right_stick_x; // Rotação


            double denominator = Math.max(Math.abs(x) + Math.abs(y) + Math.abs(z), 1);
            double frontLeftPower = (x + y + z) / denominator;
            double backLeftPower = (x - y + z) / denominator;
            double frontRightPower = (x - y - z) / denominator;
            double backRightPower = (x + y - z) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            //telemetria

            telemetry.addData(" Right Trigger (Frente)", "%.2f", rightTrigger);
            telemetry.addData(" Left Trigger (Trás)", "%.2f", leftTrigger);
            telemetry.addData(" Lateral (Joystick Esq X)", "%.2f", gamepad1.left_stick_x);
            telemetry.addData(" Rotação (Joystick Dir X)", "%.2f", z);
            telemetry.addData("", "=== RESULTADOS ===");
            telemetry.addData(" Drive Final (Y)", "%.2f", y);
            telemetry.addData(" Strafe Final (X)", "%.2f", x);
            telemetry.addData(" Rotate Final (RX)", "%.2f", z);
            telemetry.addData("I", "=== POTÊNCIAS ===");
            telemetry.addData("⚡ Frente Esquerdo", "%.2f", frontLeftPower);
            telemetry.addData("⚡ Frente Direito", "%.2f", frontRightPower);
            telemetry.addData("⚡ Trás Esquerdo", "%.2f", backLeftPower);
            telemetry.addData(" Trás Direito", "%.2f", backRightPower);
            telemetry.addData("", "=== ENCODERS ===");
            telemetry.addData(" FL Posição", frontLeftMotor.getCurrentPosition());
            telemetry.addData(" FR Posição", frontRightMotor.getCurrentPosition());
            telemetry.addData(" BL Posição", backLeftMotor.getCurrentPosition());
            telemetry.addData(" BR Posição", backRightMotor.getCurrentPosition());
            telemetry.update();
        }
    }
}



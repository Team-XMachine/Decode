package opmodes.joaquimBo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="ServoTest")
public class servo1 extends LinearOpMode {

    private Servo Servo;
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        Servo = hardwareMap.get(Servo.class, "servo1");

        Servo.setPosition(0.0);

        telemetry.addData("Status", "Inicializado");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {

            if (gamepad1.a) {
                Servo.setPosition(0.5);
                telemetry.addData("Posição", "Horizontal 90°");
            }

            if (gamepad1.b) {
                Servo.setPosition(1.0);
                telemetry.addData("Posição", "Máxima 180°");
            }

            telemetry.update();
        }
    }
}
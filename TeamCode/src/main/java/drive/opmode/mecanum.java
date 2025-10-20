package drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import drive.Robot;

@TeleOp
public class mecanum extends LinearOpMode {

    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException {

        robot = new Robot(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            robot.moveMecanumWheels(gamepad1);
        }
    }
}

package test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.xmachine.parsec.core.Dashboard;

@TeleOp
public class dash extends LinearOpMode {

    public static double x, y, h = 0;

    ElapsedTime time = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        Dashboard.init(8082);

        waitForStart();
        while (opModeIsActive()) {
            Dashboard.setRobotPose(x, y, Math.toRadians(h));
            telemetry.addData("Time", time.seconds());
            telemetry.update();
        }
    }
}

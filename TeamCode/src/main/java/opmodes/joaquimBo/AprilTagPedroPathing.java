package opmodes.joaquimBo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;

import hardware.Robot;
import systems.LimeLightImageTools;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import pedroPathing.constants.Constants;
import systems.LimeLightImageTools;

@Autonomous(name = "apriltag+pedropathing")
public class AprilTagPedroPathing extends LinearOpMode {


    Robot robot;
    private Limelight3A camera;
    private Follower follower;
    private boolean following = false;
    private final Pose TARGET_LOCATION = new Pose();


    @Override
    public void runOpMode() throws InterruptedException {


        camera = hardwareMap.get(Limelight3A.class, "limelight");
        camera.setPollRateHz(100);
        camera.pipelineSwitch(0);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0));


        LimeLightImageTools lime = new LimeLightImageTools(camera);


        camera.start();

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        lime.setDriverStationStreamSource();
        lime.forwardAll();

        dashboard.startCameraStream(lime.getStreamSource(), 30);

        follower.update();

        if (following) {
            follower.followPath(follower.pathBuilder()
                    .addPath(new BezierLine(follower.getPose(), TARGET_LOCATION))
                    .setLinearHeadingInterpolation(follower.getHeading(), TARGET_LOCATION.minus(follower.getPose()).getAsVector().getTheta())
                    .build());


            follower.setPose(getRobotPoseFromCamera());
        }

        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("coordenadas", getRobotPoseFromCamera());
            telemetry.update();
        }


    }

    private Pose getRobotPoseFromCamera() {


        //getpitch sentido x
        //getroll sentido y
        //getyaw sentido z
        LLResult result = camera.getLatestResult();

        Pose3D botpose = result.getBotpose();
        double x = botpose.getPosition().x;
        double y = botpose.getPosition().y;
        double h = botpose.getOrientation().getPitch(AngleUnit.DEGREES);
        if (result == null) {
            x = 0;
            y = 0;
            h = 0;
        }


        return new Pose(x, y, h, FTCCoordinates.INSTANCE).getAsCoordinateSystem(PedroCoordinates.INSTANCE);
    }
}
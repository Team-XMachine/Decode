package opmodes.joaquimBo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.Constants;

public class autonomous extends OpMode {
    private Follower follower;
    public static MecanumConstants driveConstants = new MecanumConstants();

    private final Pose startPose = new Pose(0, 0, Math.toRadians(180)); // Start Pose of our robot.
    private final Pose scorePose = new Pose(7, 8, Math.toRadians(135)); // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    private final Pose pickup1Pose = new Pose(10, 12, Math.toRadians(0)); // Highest (First Set) of Artifacts from the Spike Mark.
    private final Pose pickup2Pose = new Pose(15, 16, Math.toRadians(0)); // Middle (Second Set) of Artifacts from the Spike Mark.
    private final Pose pickup3Pose = new Pose(20, 21, Math.toRadians(90));
    private final Pose pickup4Pose = new Pose(26, 27, Math.toRadians(0));

    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;

    private Path scorePreload;

    private PathChain grabPickup1, scorePickup1, grabPickup2, scorePickup2, grabPickup3, scorePickup3,grabPickup4, scorePickup4;


    public void BuildPath() {
        scorePreload = new Path(new BezierLine(startPose, scorePose));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

        grabPickup1 = follower.pathBuilder()

                .addPath(new BezierLine(scorePose, pickup1Pose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();

        scorePickup1 = follower.pathBuilder()

                .addPath(new BezierLine(pickup1Pose, scorePose))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup2 = follower.pathBuilder()

                .addPath(new BezierLine(scorePose, pickup2Pose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading())
                .build();

        scorePickup2 = follower.pathBuilder()

                .addPath(new BezierLine(pickup2Pose, scorePose))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup3 = follower.pathBuilder()

                .addPath(new BezierLine(scorePose, pickup3Pose))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), pickup3Pose.getHeading())
                .build();

        scorePickup3 = follower.pathBuilder()

                .addPath(new BezierLine(scorePose, pickup3Pose))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup4 = follower.pathBuilder()

                .addPath(new BezierLine(scorePose, pickup4Pose))
                .setLinearHeadingInterpolation(pickup4Pose.getHeading(), pickup4Pose.getHeading())
                .build();

        scorePickup4 = follower.pathBuilder()

                .addPath(new BezierLine(scorePose, pickup4Pose))
                .setLinearHeadingInterpolation(pickup4Pose.getHeading(), scorePose.getHeading())
                .build();
    }
    public void AutonomousPathUpdate(){
        switch (pathState){
            case 0:
                follower.followPath(scorePreload);
                setPathState(1);
                break;


            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(grabPickup1, true);
                    setPathState(2);
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(scorePickup1, true);
                    setPathState(3);
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(grabPickup2,true);
                    setPathState(4);
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    follower.followPath(scorePickup2, true);
                    setPathState(5);
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    follower.followPath(grabPickup3, true);
                    setPathState(6);
                }
                break;

            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(scorePickup3, false);
                    setPathState(7);
                }
                break;


            case 7:
                if (follower.isBusy()){
                    follower.followPath(scorePickup4, false);
                    setPathState(8);
                }
                break;
            case 8:
                if (follower.isBusy()){
                    follower.followPath(grabPickup4, false);
                    setPathState(9);
                }
                break;
        }
    }
    @Override
    public void loop(){
        follower.update();
        AutonomousPathUpdate();
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        telemetry.addData(" path state ", pathState);
        telemetry.addData(" x ", follower.getPose().getX());
        telemetry.addData(" y ", follower.getPose().getY());
        telemetry.addData(" heading ", follower.getPose().getHeading());
        telemetry.update();
    }
    @Override
    public void init_loop(){}

    @Override
    public void init(){
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        BuildPath();
        follower = Constants.createFollower(hardwareMap);
        BuildPath();
        follower.setStartingPose(startPose);
        follower.setStartingPose(startPose);
    }
    @Override
    public void start(){
        opmodeTimer.resetTimer();
        setPathState(0);
    }
    public void setPathState(int pState) {

        pathState = pState;
        pathTimer.resetTimer();}

}

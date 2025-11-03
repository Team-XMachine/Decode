package pedroPathing.constants;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class Constants {

        public static FollowerConstants followerConstants = new FollowerConstants()
                .mass(10)
                .forwardZeroPowerAcceleration(-41.278)
                .lateralZeroPowerAcceleration(-59.7819)
                .useSecondaryTranslationalPIDF(false)
                .useSecondaryHeadingPIDF(false)
                .useSecondaryDrivePIDF(false)
                .centripetalScaling(0.0005)
                .translationalPIDFCoefficients(new com.pedropathing.control.PIDFCoefficients(0.1, 0, 0.01, 0))
                .headingPIDFCoefficients(new PIDFCoefficients(2, 0, 0.1, 0))
                .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.1, 0, 0, 0.6, 0));

        public static MecanumConstants driveConstants = new MecanumConstants()
                .leftFrontMotorName("FE")
                .leftRearMotorName("TE")
                .rightFrontMotorName("FD")
                .rightRearMotorName("TD")
                .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
                .xVelocity(57.8741)
                .yVelocity(52.295);

        public static PathConstraints pathConstraints = new PathConstraints(
                0.995,
                500,
                1,
                1
        );

        public static Follower createFollower(HardwareMap hardwareMap) {
            return new FollowerBuilder(followerConstants, hardwareMap)
                    .mecanumDrivetrain(driveConstants)
                    .pathConstraints(pathConstraints)
                    .twoWheelLocalizer(localizerConstants)
                    .build();
        }

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName("leftFront")
            .strafeEncoder_HardwareMapName("rightRear")
            .IMU_HardwareMapName("imu")
            .IMU_Orientation(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                    )
            );
    }

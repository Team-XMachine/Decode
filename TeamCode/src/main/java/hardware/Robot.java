package hardware;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import drive.SampleMecanumDrive;

public class Robot {

    SampleMecanumDrive drive;

    DcMotorEx intake, shooter;

    CRServo transfer;

    Rev2mDistanceSensor checker;

    Rev2mDistanceSensor entryDetector;

    HardwareMap hwMap;

    Limelight3A limelight;

    public Robot(HardwareMap hwMap) {
        this.hwMap = hwMap;
        drive = new SampleMecanumDrive(hwMap);
        intake = hwMap.get(DcMotorEx.class, "intake");
        shooter = hwMap.get(DcMotorEx.class, "shooter");

        transfer = hwMap.get(CRServo.class, "transfer");

        limelight = hwMap.get(Limelight3A.class,"limelight");

        checker = hwMap.get(Rev2mDistanceSensor.class, "itk");
        entryDetector = hwMap.get(Rev2mDistanceSensor.class, "entrySensor");

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        limelight.setPollRateHz(100);
        limelight.start();
        limelight.deleteSnapshots();
    }

    // getters

    public void runDrive(Gamepad gamepad) {
        drive.setWeightedDrivePower(
                new Pose2d(
                        gamepad.left_stick_y,
                        gamepad.left_stick_x,
                        (gamepad.right_stick_y - gamepad.right_stick_x)
                )
        );
    }

    public DcMotorEx getMotor(String motor) {
        return hwMap.get(DcMotorEx.class, motor);
    }

    public CRServo getCRServo(String servo) {
        return hwMap.get(CRServo.class, servo);
    }

    public Rev2mDistanceSensor getDistanceSensor(String sensor) {
        return hwMap.get(Rev2mDistanceSensor.class, sensor);
    }

    public Limelight3A getLimelight() {
        return limelight;
    }
}

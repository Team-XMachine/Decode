package drive;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;



public class Robot {
    private HardwareMap hwMap;

    public SampleMecanumDrive drive;

    public Robot(HardwareMap hwMap) {
        this.hwMap = hwMap;
        drive = new SampleMecanumDrive(hwMap);
    }

    protected void init() {

    }

    public void moveMecanumWheels(Gamepad gamepad1) {
        drive.setWeightedDrivePower(
                new Pose2d(
                        -gamepad1.left_stick_y / 1,
                        -gamepad1.left_stick_x / 1,
                        -gamepad1.right_stick_x / 1
                )
        );

        drive.update();
    }
}

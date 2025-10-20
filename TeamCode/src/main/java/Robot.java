import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import drive.SampleMecanumDrive;

public class Robot {

    SampleMecanumDrive drive;

    DcMotorEx intake, shooter;

    CRServo transfer;

    Rev2mDistanceSensor checker;

    HardwareMap hwMap;

    public Robot(HardwareMap hwMap) {
        this.hwMap = hwMap;
        drive = new SampleMecanumDrive(hwMap);
        intake = hwMap.get(DcMotorEx.class, "intake");
        shooter = hwMap.get(DcMotorEx.class, "shooter");

        transfer = hwMap.get(CRServo.class, "transfer");

        checker = hwMap.get(Rev2mDistanceSensor.class, "itk");
    }

    // getters

    public DcMotorEx getMotor(String motor) {
        return hwMap.get(DcMotorEx.class, motor);
    }

    public CRServo getCRServo(String servo) {
        return hwMap.get(CRServo.class, servo);
    }
}

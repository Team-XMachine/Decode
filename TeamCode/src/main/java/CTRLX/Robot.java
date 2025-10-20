package CTRLX;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import CTRLX.systems.Drive;

public class Robot {

    Drive drive;

    Rev2mDistanceSensor lsensor, rsensor;

    Servo intake, d1, d2;

    HardwareMap hwMap;

    public Robot(HardwareMap hwMap) {
        this.hwMap = hwMap;
        drive = new Drive(hwMap);

//        lsensor = hwMap.get(Rev2mDistanceSensor.class, "left");
//        rsensor = hwMap.get(Rev2mDistanceSensor.class, "right");

        intake = hwMap.get(Servo.class, "itk");
        d1 = hwMap.get(Servo.class, "d1");
        d2 = hwMap.get(Servo.class, "d2");
    }

    public Drive getDrive() {
        return drive;
    }

    public DcMotorEx getMotor(String motor) {
        return hwMap.get(DcMotorEx.class, motor);
    }

    public Servo getServo(String servo) {
        return hwMap.get(Servo.class, servo);
    }
}

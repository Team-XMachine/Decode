package opmodes.joaquimBo;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.mechanics.servoconfig;
@TeleOp(name = "servo")
public class servo extends OpMode {
    servoconfig servo = new servoconfig();

    @Override
    public void init(){
        servo.init(hardwareMap);
    }

    @Override
    public void loop(){
       servo.setServoRot(1.0);

    }
}

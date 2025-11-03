package opmodes.Jair;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp (name = "Jogador")
public class Shooter extends OpMode {

    DcMotorEx RigthShooter;
    DcMotorEx LeftShooter;

    @Override
    public void init() {
        RigthShooter = hardwareMap.get(DcMotorEx.class, "RS");
        LeftShooter = hardwareMap.get(DcMotorEx.class, "LS");
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            LeftShooter.setPower(1);
        } else {
            
            LeftShooter.setPower(0);
        }
        if (gamepad1.a) {
            RigthShooter.setPower(1);
        } else {
            RigthShooter.setPower(0);
        }
    }
}

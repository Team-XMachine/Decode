package opmodes.joaquimBo;

import org.firstinspires.ftc.robotcontroller.mechanics.DistanceConfig;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp (name = "sensor de distancia")
public class DistanceTest extends OpMode {

    DistanceConfig config = new DistanceConfig();

    @Override
    public void init(){
        config.init(hardwareMap);

    }

    @Override
    public void loop(){
        telemetry.addData("distance",config.getDistance());

    }
}

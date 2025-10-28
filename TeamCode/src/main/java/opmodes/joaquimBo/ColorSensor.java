package opmodes.joaquimBo;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.mechanics.ColorConfig;
@TeleOp (name = "sensor color")
public class ColorSensor extends OpMode {
    ColorConfig Config = new ColorConfig();

    @Override
    public void init(){
        Config.init(hardwareMap);
    }

    @Override
    public void loop(){
        Config.getDetectedColor(telemetry);

    }
}

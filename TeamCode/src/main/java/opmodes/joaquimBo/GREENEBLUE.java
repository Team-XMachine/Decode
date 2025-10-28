package opmodes.joaquimBo;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.mechanics.ColorGreenAndPurple;

@TeleOp (name = "GREEN e PURPLE")
public class GREENEBLUE extends OpMode {
    ColorGreenAndPurple Config = new ColorGreenAndPurple();

    @Override
    public void init(){
        Config.init(hardwareMap);
        telemetry = FtcDashboard.getInstance().getTelemetry();

    }

    @Override
    public void loop(){
        Config.getDetectedColor(telemetry);

    }
}


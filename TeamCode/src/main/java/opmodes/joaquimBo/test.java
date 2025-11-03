package opmodes.joaquimBo;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "testeMotores")
public class test extends LinearOpMode {

    DcMotorEx motor,motor1, motor2, motor3;

    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotorEx.class,"FE");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setVelocity(1);

        motor1 = hardwareMap.get(DcMotorEx.class,"FD");
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor1.setVelocity(1);

        motor2 = hardwareMap.get(DcMotorEx.class,"TE");
        motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor2.setVelocity(1);

        motor3 = hardwareMap.get(DcMotorEx.class,"TD");
        motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor3.setVelocity(1);

        waitForStart();
        while (opModeIsActive()){
            if (gamepad1.a){
                motor.setPower(1);
            }else{
                motor.setPower(0);
            }

            if (gamepad1.b){
                motor1.setPower(1);
            }else{
                motor1.setPower(0);
            }

            if (gamepad1.x){
                motor2.setPower(1);
            }else{
                motor2.setPower(0);
            }

            if (gamepad1.y){
                motor3.setPower(1);
            }else{
                motor3.setPower(0);
            }
        }
    }
}

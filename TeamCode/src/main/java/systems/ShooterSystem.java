package systems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import enums.IntakeStates;
import enums.ShooterStates;
import enums.TransferStates;
import hardware.Robot;
import util.Globals;

public class ShooterSystem {

    public static ShooterStates PS, CS = ShooterStates.INITIALIZE;

    double targetVel = 0;

    double lastError = 0;

    ElapsedTime time = new ElapsedTime();

    public ShooterSystem() {
        PS = ShooterStates.INITIALIZE;
        CS = ShooterStates.INITIALIZE;
    }

    public void update(Robot robot, Telemetry telemetry) {
        DcMotorEx shooter = robot.getMotor("LS");
        DcMotorEx shooter2 = robot.getMotor("RS");

        switch (CS) {
            case INITIALIZE:
            case IDLE:
                shooter.setVelocity(0);
                break;

            case SHOOTING:
                if (PS != CS) {
                    time.reset();
                }

                double ta = robot.getLimelight().getLatestResult() != null ? robot.getLimelight().getLatestResult().getTa() : 0;

                targetVel = ta > 0 ? (Globals.SHOOTER_VEL / ta) : Globals.TARGET_VEL;


                double power = pid(targetVel, shooter.getVelocity());

                shooter.setPower(Math.max(power, 1.0));
                shooter2.setPower(-shooter.getPower());

                telemetry.addData("SHOOTER TARGET VEL", targetVel);
                telemetry.update();

                if (!IntakeSystem.storage[0] && IntakeSystem.storage[1]) {
                    IntakeSystem.CS = IntakeStates.CATCH_1;
                    IntakeSystem.storage[1] = false;
                    time.reset();
                }


                if (IntakeSystem.storage[0] && shooter.getVelocity() >= targetVel && robot.getLimelight().getLatestResult() != null) {
                    TransferSystem.CS = TransferStates.SHOOTING;
                    if (time.seconds() >= Globals.CYCLE_SHOOTING_TIME) {
                        IntakeSystem.storage[0] = false;
                        TransferSystem.CS = TransferStates.IDLE;
                        CS = ShooterStates.IDLE;
                    }
                }

                break;
        }
        PS = CS;
    }

    double pid(double reference, double state) {
        double error = reference - state;
        lastError = error;

        return error * Globals.SHOOTER_KP;
    }
}

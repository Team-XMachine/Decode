package systems;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import enums.TransferStates;
import hardware.Robot;
import util.Globals;

public class TransferSystem {

    public static TransferStates PS, CS = TransferStates.INITIALIZE;

    public TransferSystem() {
        PS = TransferStates.INITIALIZE;
        CS = TransferStates.INITIALIZE;
    }

    public void update(Robot robot) {

        Rev2mDistanceSensor checker = robot.getDistanceSensor("itk");

        boolean GOTCHA = checker.getDistance(DistanceUnit.CM) <= Globals.ITK_DISTANCE;

        DcMotorEx transfer = robot.getMotor("transfer");

        switch (CS) {
            case INITIALIZE:
            case IDLE:
                transfer.setPower(0);
                break;
            case EJECTING:
                transfer.setPower(-Globals.TRANSFER_POWER);
                break;
            case SHOOTING:
                transfer.setPower(Globals.TRANSFER_POWER);
                break;
            case CATCH:
                if (!GOTCHA) {
                    transfer.setPower(Globals.TRANSFER_POWER);
                } else {
                    transfer.setPower(0);
                    CS = TransferStates.IDLE;
                }
                break;
        }
        PS = CS;
    }
}

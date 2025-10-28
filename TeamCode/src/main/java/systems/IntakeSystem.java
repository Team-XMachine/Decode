package systems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import enums.IntakeStates;
import enums.TransferStates;
import hardware.Robot;
import util.Globals;

public class IntakeSystem {

    public static Boolean[] storage = {false, false};

    double maxCurrent = 0;

    public static IntakeStates PS, CS = IntakeStates.INITIALIZE;

    ElapsedTime time = new ElapsedTime();

    TransferSystem transferSystem;

    public IntakeSystem() {
        storage[0] = false;
        storage[1] = false;
        CS = IntakeStates.INITIALIZE;
        PS = IntakeStates.INITIALIZE;
    }

    public void update(Robot robot) {
        DcMotorEx intake = robot.getMotor("intake");

        switch (CS) {
            case INITIALIZE:
            case IDLE:
                intake.setPower(0);
                break;
            case RUNNING:
                intake.setPower(Globals.INTAKE_POWER);
                break;
            case EJECT:
                intake.setPower(-Globals.INTAKE_POWER);
                break;
            case EJECT_1:

                if(!storage[0] && !storage[1]) {
                    CS = IntakeStates.IDLE;
                    return;
                }

                if (PS != CS) {
                    maxCurrent = 0;
                    time.reset();
                }
                if (storage[1]) {
                    CS = IntakeStates.EJECT_2;
                    return;
                }
                TransferSystem.CS = TransferStates.EJECTING;
                intake.setPower(-Globals.INTAKE_POWER);
                maxCurrent = Math.max(intake.getCurrent(CurrentUnit.MILLIAMPS), maxCurrent);
                if (/*maxCurrent >= Globals.EJECT_CURRENT && intake.getCurrent(CurrentUnit.MILLIAMPS) <= Globals.AVERAGE_CURRENT*/
                        time.seconds() > Globals.EJECT_TIME) {
                    TransferSystem.CS = TransferStates.IDLE;
                    CS = IntakeStates.IDLE;
                    storage[0] = false;
                }
                break;
            case EJECT_2:
                if (PS != CS) {
                    maxCurrent = 0;
                }
                intake.setPower(-Globals.INTAKE_POWER);

                if (robot.getDistanceSensor("entrySensor").getDistance(DistanceUnit.CM) > Globals.ENTRY_CHECKER_DISTANCE) {
                    TransferSystem.CS = TransferStates.IDLE;
                    CS = IntakeStates.IDLE;
                    storage[1] = false;
                }

                break;
            case CATCH_1:
                if (CS != PS) {
                    TransferSystem.CS = TransferStates.CATCH;
                    intake.setPower(Globals.INTAKE_POWER);
                }
                if (TransferSystem.CS == TransferStates.IDLE) {
                    storage[0] = true;
                    CS = IntakeStates.IDLE;
                }
                break;
            case CATCH_2:

                if(storage[0] && storage[1]) {
                    CS = IntakeStates.IDLE;
                    return;
                }

                if (!storage[0]) {
                    CS = IntakeStates.CATCH_1;
                    return;
                }

                if (PS == IntakeStates.IDLE) {
                    time.reset();
                }
                intake.setPower(Globals.INTAKE_POWER * 0.75);

                if (robot.getDistanceSensor("entrySensor").getDistance(DistanceUnit.CM) <= Globals.ENTRY_CHECKER_DISTANCE) {
                    storage[1] = true;
                    CS = IntakeStates.IDLE;
                }
                break;
        }
        PS = CS;
    }
}

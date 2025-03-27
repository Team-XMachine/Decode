package systems;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.HardwareMap;

import enums.DriveStates;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import util.Globals;

public class DriveSystem {

    public static DriveStates CS, PS = DriveStates.INITIALIZE;
    Follower follower;

    public DriveSystem(HardwareMap hwMap) {
        follower = new Follower(hwMap, FConstants.class, LConstants.class);
        CS = DriveStates.INITIALIZE;
        PS = DriveStates.INITIALIZE;
    }

    public boolean closerTo(Vector2d marker) {
        Vector2d currentPose = new Vector2d(follower.getPose().getX(), follower.getPose().getY());
        double distance = marker.distTo(currentPose);

        return distance < Globals.MARKER_MAX_DISTANCE;
    }

    public void BUILD_PATHS() {

    }
}

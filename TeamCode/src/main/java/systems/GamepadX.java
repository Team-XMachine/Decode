package systems;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;

public class GamepadX {
    private final Gamepad mainGamepad;
    private final Gamepad gamepadOne;
    private final Gamepad gamepadTwo;
    private final Gamepad gamepadThree;

    private final Map<String, Boolean> currentState = new HashMap<>();
    private final Map<String, Boolean> previousState = new HashMap<>();

    private final String[] buttonsToTrack = {"b", "y", "a", "x", "left_bumper", "dpad_up", "dpad_down"}; // Add more buttons if needed

    public GamepadX(Gamepad gamepad) {
        this.mainGamepad = gamepad;
        this.gamepadOne = new Gamepad();
        this.gamepadTwo = new Gamepad();
        this.gamepadThree = new Gamepad();

        // Initialize button states
        for (String button : buttonsToTrack) {
            currentState.put(button + "_ONE", false);
            previousState.put(button + "_ONE", false);
            currentState.put(button + "_TWO", false);
            previousState.put(button + "_TWO", false);
        }
    }

    private boolean wasButtonPressed(String button, String gamepadId) {
        return currentState.get(button + "_" + gamepadId) && !previousState.get(button + "_" + gamepadId);
    }



    ////////////////////////////// GAMEPAD ONE //////////////////////////////
    public boolean ONEwasBPressed() {
        return wasButtonPressed("b", "ONE");
    }
    public boolean ONEwasYPressed() {
        return wasButtonPressed("y", "ONE");
    }
    public boolean ONEwasAPressed() {
        return wasButtonPressed("a", "ONE");
    }
    public boolean ONEwasXPressed() {
        return wasButtonPressed("x", "ONE");
    }
    public boolean ONEwasDpadUpPressed() {
        return wasButtonPressed("dpad_up", "ONE");
    }
    public boolean ONEwasDpadDownPressed() {
        return wasButtonPressed("dpad_up", "ONE");
    }



    ////////////////////////////// GAMEPAD TWO //////////////////////////////

    public boolean TWOwasDpadDOwnPressed() {
        return wasButtonPressed("dpad_down", "TWO");
    }
    public boolean TWOwasBPressed() {
        return wasButtonPressed("b", "TWO");
    }
    public boolean TWOwasYPressed() {
        return wasButtonPressed("y", "TWO");
    }
    public boolean TWOwasAPressed() {
        return wasButtonPressed("a", "TWO");
    }
    public boolean TWOwasXPressed() {
        return wasButtonPressed("x", "TWO");
    }
    public boolean TWOwasDpadUpPressed() {
        return wasButtonPressed("dpad_up", "TWO");
    }


    ////////////////////////////// GAMEPAD THREE //////////////////////////////

    public boolean THREEwasDpadDOwnPressed() {
        return wasButtonPressed("dpad_down", "THREE");
    }
    public boolean THREEwasBPressed() {
        return wasButtonPressed("b", "THREE");
    }
    public boolean THREEwasYPressed() {
        return wasButtonPressed("y", "THREE");
    }
    public boolean THREEwasAPressed() {
        return wasButtonPressed("a", "THREE");
    }
    public boolean THREEwasXPressed() {
        return wasButtonPressed("x", "THREE");
    }
    public boolean THREEwasDpadUpPressed() {
        return wasButtonPressed("dpad_up", "THREE");
    }


    public void readGamepad(Gamepad gamepad) {
        // Update previous state
        for (String button : buttonsToTrack) {
            previousState.put(button + "_ONE", currentState.get(button + "_ONE"));
            previousState.put(button + "_TWO", currentState.get(button + "_TWO"));
            previousState.put(button + "_THREE", currentState.get(button + "_THREE"));
        }

        if (gamepad.right_bumper && !gamepad.left_bumper) {
            gamepadOne.reset();
            gamepadThree.reset();
            gamepadTwo.copy(gamepad);
        } else if (gamepad.left_bumper && !gamepad.right_bumper) {
            gamepadOne.reset();
            gamepadTwo.reset();
            gamepadThree.copy(gamepad);
        } else {
            gamepadTwo.reset();
            gamepadThree.reset();
            gamepadOne.copy(gamepad);
        }

        // Update current state
        for (String button : buttonsToTrack) {
            boolean buttonPressedOne = getButtonState(button, gamepadOne);
            boolean buttonPressedTwo = getButtonState(button, gamepadTwo);
            boolean buttonPressedThree = getButtonState(button, gamepadThree);

            currentState.put(button + "_ONE", buttonPressedOne);
            currentState.put(button + "_TWO", buttonPressedTwo);
            currentState.put(button + "_THREE", buttonPressedThree);
        }
    }

    private boolean getButtonState(String button, Gamepad gamepad) {
        switch (button) {
            case "a":
                return gamepad.a;
            case "b":
                return gamepad.b;
            case "x":
                return gamepad.x;
            case "y":
                return gamepad.y;
            case "left_bumper":
                return gamepad.left_bumper;
            case "right_bumper":
                return gamepad.right_bumper;
            case "dpad_up":
                return gamepad.dpad_up;
            case "dpad_down":
                return gamepad.dpad_down;
            default:
                return false;
        }
    }

    public Gamepad getGamepadOne() {
        readGamepad(this.mainGamepad);
        return gamepadOne;
    }

    public Gamepad getGamepadTwo() {
        readGamepad(this.mainGamepad);
        return gamepadTwo;
    }

    public Gamepad getGamepadThree() {
        return gamepadThree;
    }
}

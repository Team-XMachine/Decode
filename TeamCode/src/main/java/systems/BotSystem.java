package systems;

import enums.BotStates;

public class BotSystem {

    public static BotStates CS, PS = BotStates.INITIALIZE;

    public BotSystem() {
        CS = BotStates.INITIALIZE;
        PS = BotStates.INITIALIZE;
    }

    public void update() {
        switch (CS) {
            case INITIALIZE:
            case IDLE:
                break;
        }
        PS = CS;
    }
}

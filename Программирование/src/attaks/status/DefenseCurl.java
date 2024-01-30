package attaks.status;

import ru.ifmo.se.pokemon.*;

public class DefenseCurl extends StatusMove {
    public DefenseCurl() {
        super(Type.NORMAL, 0, 100);
    }

    @Override
    protected void applySelfEffects(Pokemon pk) {
        pk.setMod(Stat.DEFENSE, 1);
    }

    @Override
    protected String describe() {
        return "uses Defense Curl";

    }
}

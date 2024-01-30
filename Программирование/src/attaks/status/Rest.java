package attaks.status;

import ru.ifmo.se.pokemon.*;

public class Rest extends StatusMove {
    public Rest() {
        super(Type.PSYCHIC, 0, 100);

    }

    @Override
    protected void applySelfEffects(Pokemon pk) {
        pk.setMod(Stat.HP, (int) (pk.getHP() - pk.getStat(Stat.HP)));
        pk.setCondition((new Effect()).condition(Status.SLEEP).turns(2));
    }

    @Override
    protected String describe() {
        return "uses Rest";
    }
}

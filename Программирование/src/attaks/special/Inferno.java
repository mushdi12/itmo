package attaks.special;

import ru.ifmo.se.pokemon.*;

public class Inferno extends SpecialMove {
    public Inferno() {
        super(Type.FIRE, 100, 50);
    }

    @Override
    protected void applyOppDamage(Pokemon pk, double at) {
        pk.setMod(Stat.HP, (int) Math.round(at));
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        Effect.burn(p);
    }

    @Override
    protected String describe() {
        return "uses Inferno";
    }
}

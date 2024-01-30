package attaks.special;

import ru.ifmo.se.pokemon.*;

public class EnergyBall extends SpecialMove {
    public EnergyBall() {
        super(Type.GRASS, 90, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon p, double v) {
        p.setMod(Stat.HP, (int) Math.round(v));
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        if ((int) Math.round(Math.random() * 100) <= 10) {
            p.setMod(Stat.SPECIAL_DEFENSE, -1);
        }
    }

    @Override
    protected String describe() {
        return "uses Energy Ball";

    }
}

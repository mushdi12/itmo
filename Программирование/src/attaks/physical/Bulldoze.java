package attaks.physical;

import ru.ifmo.se.pokemon.*;

public class Bulldoze extends PhysicalMove {
    public Bulldoze() {
        super(Type.GROUND, 60, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon pk, double at) {
        pk.setMod(Stat.HP, (int) Math.round(at));
    }

    @Override
    protected void applyOppEffects(Pokemon pk) {
        pk.setMod(Stat.SPEED, -1);
    }

    @Override
    protected String describe() {
        return "uses Bulldoze";
    }
}

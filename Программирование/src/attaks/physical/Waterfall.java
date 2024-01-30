package attaks.physical;

import ru.ifmo.se.pokemon.*;

public class Waterfall extends PhysicalMove {
    public Waterfall() {
        super(Type.WATER, 80, 100);
    }

    @Override
    protected void applyOppDamage(Pokemon pk, double at) {
        pk.setMod(Stat.HP, (int) Math.round(at));
    }

    @Override
    protected void applyOppEffects(Pokemon pk) {
        if ((int) Math.round(Math.random() * 100) <= 20) {
            Effect.flinch(pk);
        }
    }

    @Override
    protected String describe() {
        return "uses Sludge Bomb";
    }
}

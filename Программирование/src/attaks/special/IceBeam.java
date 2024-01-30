package attaks.special;

import ru.ifmo.se.pokemon.*;

public class IceBeam extends SpecialMove {
    public IceBeam() {
        super(Type.ICE, 90, 100);

    }

    @Override
    protected void applyOppDamage(Pokemon pk, double at) {
        pk.setMod(Stat.HP, (int) Math.round(at));
    }

    @Override
    protected void applyOppEffects(Pokemon pk) {
        if ((int) Math.round(Math.random() * 100) <= 10) {
            Effect.freeze(pk);
        }
    }

    @Override
    protected String describe() {
        return "uses Ice Beam";
    }
}

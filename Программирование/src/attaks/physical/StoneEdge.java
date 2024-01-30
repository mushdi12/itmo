package attaks.physical;

import ru.ifmo.se.pokemon.*;

public class StoneEdge extends PhysicalMove {
    public StoneEdge() {
        super(Type.ROCK, 100, 80);
    }

    @Override
    protected void applyOppDamage(Pokemon pk, double at) {
        pk.setMod(Stat.HP, (int) Math.round(at));
    }

    @Override
    protected double calcCriticalHit(Pokemon att, Pokemon def) {
        if ((att.getStat(Stat.SPEED) / 512.0) * 3 > Math.random()) {
            System.out.println(Messages.getBundle("critical"));
            return 2.0;
        } else {
            return 1.0;
        }
    }

    @Override
    protected String describe() {
        return "uses Stone Edge";
    }
}

package pokemons;

import attaks.status.Confide;
import ru.ifmo.se.pokemon.*;

public class Chandelure extends Lampent {
    public Chandelure(String p, int lvl) {
        super(p, lvl);
        setStats(60,55,90,145,90,80);
        addMove(new Confide());

    }
}

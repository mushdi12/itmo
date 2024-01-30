package pokemons;

import attaks.special.Inferno;
import ru.ifmo.se.pokemon.*;

public class Lampent extends Litwick {
    public Lampent(String p, int lvl) {
        super(p, lvl);
        setStats(60, 40, 60, 95, 60, 55);
        addMove(new Inferno());
    }
}

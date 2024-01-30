package pokemons;

import ru.ifmo.se.pokemon.*;
import attaks.special.EnergyBall;
import attaks.status.ConfuseRay;


public class Litwick extends Pokemon {
    public Litwick(String p, int lvl) {
        super(p, lvl);
        setStats(50, 30, 55, 65, 55, 20);
        setMove(new EnergyBall(), new ConfuseRay());
    }
}

package pokemons;

import attaks.physical.Bulldoze;
import attaks.physical.Waterfall;
import attaks.special.IceBeam;
import ru.ifmo.se.pokemon.*;

public class Lugia extends Pokemon {
    public Lugia(String p, int lvl) {
        super(p, lvl);
        setStats(106, 90, 130, 90, 154, 110);
        setMove(new Bulldoze(), new IceBeam(), new Waterfall());
    }

}


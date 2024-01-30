package pokemons;

import attaks.physical.StoneEdge;

import ru.ifmo.se.pokemon.*;

public class Sudowoodo extends Bonsly {
    public Sudowoodo(String p, int lvl) {
        super(p, lvl);
        setStats(70, 100, 115, 30, 65, 30);
        addMove(new StoneEdge());


    }
}

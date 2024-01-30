package pokemons;

import attaks.status.DefenseCurl;
import attaks.status.Rest;
import attaks.status.RockPolish;
import ru.ifmo.se.pokemon.*;

public class Bonsly extends Pokemon {
    public Bonsly(String p, int lvl) {
        super(p, lvl);
        setStats(50, 80, 95, 10, 45, 10);
        setMove(new DefenseCurl(), new Rest(), new RockPolish());
    }

}

import pokemons.*;
import ru.ifmo.se.pokemon.*;

public class Main {
    public static void main(String[] args) {
        Battle b = new Battle(); // СОЗДАНИЕ НОВОЙ БИТВЫ
        Pokemon p1 = new Lugia("Лугия", 15);
        Pokemon p2 = new Bonsly("Бонсли", 15);
        Pokemon p3 = new Sudowoodo("Судовудо", 15);
        Pokemon p4 = new Litwick("Литвик", 15);
        Pokemon p5 = new Lampent("Светильник", 15);
        Pokemon p6 = new Chandelure("Канделябр", 15);
        b.addAlly(p1);
        b.addFoe(p2);
        b.addFoe(p3);
        b.addFoe(p4);
        b.addFoe(p5);
        b.addFoe(p6);
        b.go();//ЗАПУСК БИТВЫ
    }
}

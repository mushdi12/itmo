% Зелья и их свойства
potion(swallow,  toxicity(20), description('Ускоряет регенерацию здоровья.')).
potion(thunderbolt, toxicity(25), description('Повышает урон.')).
potion(cat, toxicity(10), description('Позволяет видеть в темноте.')).
potion(black_blood, toxicity(35), description('Полезно против вампиров.')).

% Пример логики подбора
best_combo(Witcher, Monster, Limit, Combo) :-
    findall(P, potion(P, _, _), Potions),
    suitable_potions(Monster, Potions, Limit, Combo).

suitable_potions(vampire, _, Limit, [swallow, black_blood]) :-
    Total is 20 + 35,
    Total =< Limit.

suitable_potions(griffin, _, Limit, [swallow, thunderbolt]) :-
    Total is 20 + 25,
    Total =< Limit.

suitable_potions(drowner, _, Limit, [cat, swallow]) :-
    Total is 10 + 20,
    Total =< Limit.

witcher(geralt).
witcher(vesemir).
witcher(lambert).
witcher(eskel).
witcher(gaetan).
witcher(kaya).
witcher(thorden).
witcher(erland).
witcher(letho).
witcher(serp).

% Школы ведьмаков
witcher_school(wolf).
witcher_school(cat).
witcher_school(bear).
witcher_school(griffin).
witcher_school(vipern).

% Люди
human(ciri).
human(dandelion).
human(emhyr).
human(radowid).

% Гномы
dwarf(zoltan).

% Маги
mage(yennefer).
mage(triss).
mage(vilgefortz).

% Монстры
monster(griffin).
monster(leshen).
monster(drowner).
monster(vampire).
monster(wyvern).
monster(ghoul).

% Королевства
kingdom(redania).
kingdom(nilfgaard).
kingdom(temeria).
kingdom(kaedwen).
kingdom(skellige).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Дружба
friend(geralt, dandelion).
friend(geralt, zoltan).
friend(geralt, ciri).
friend(geralt, vesemir).
friend(geralt, zoltan).

friend(zoltan, triss).
friend(zoltan, ciri).
friend(zoltan, vesemir).
friend(zoltan, geralt).

friend(lambert, geralt).

friend(ciri, yennefer).
friend(ciri, triss).
friend(ciri, vesemir).
friend(ciri, geralt).
friend(ciri, zoltan).

% Любовь
love(geralt, yennefer).
love(yennefer, geralt).
love(triss, geralt).

% Враги
enemy(geralt, vilgefortz).
enemy(emhyr, radowid).

% Наставничество
mentor(vesemir, geralt).
mentor(geralt, ciri).

% Школа Волка
belongs_to(geralt, wolf).
belongs_to(vesemir, wolf).
belongs_to(lambert, wolf).
belongs_to(eskel, wolf).

% Школа Кота
belongs_to(gaetan, cat).
belongs_to(kaya, cat).

% Школа Медведя
belongs_to(thorden, bear).

% Школа Грифона
belongs_to(erland, griffin).

% Школа Змеи 
belongs_to(letho, viper).
belongs_to(serp, viper).

% Проживание (в королевствах)
lives_in(yennefer, redania).
lives_in(triss, redania).
lives_in(zoltan, redania).
lives_in(dandelion, temeria).
lives_in(emhyr, nilfgaard).

% Кто кого победил
killed(geralt, griffin).
killed(geralt, leshen).
killed(geralt, vilgefortz).
killed(geralt, vampire).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ЗЕЛЬЯ
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% potion(Название, Эффект, Значение_бонуса, Интоксикация).
potion(swallow, heal, 30, 20).
potion(thunderbolt, attack, 40, 30).
potion(tawny_owl, stamina, 25, 25).
potion(cat, night_vision, 0, 10).
potion(white_honey, detox, -100, 0).
potion(blizzard, speed, 20, 25).
potion(black_blood, anti_vampire, 50, 35).
potion(necrophage_oil, anti_undead, 40, 20).
potion(golden_oriole, poison_resist, 30, 25).
potion(killer_whale, breath, 0, 15).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ЭФФЕКТИВНОСТЬ ПРОТИВ МОНСТРОВ
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% effective_against(Зелье, Монстр).
effective_against(thunderbolt, griffin).
effective_against(thunderbolt, wyvern).
effective_against(black_blood, vampire).
effective_against(necrophage_oil, drowner).
effective_against(necrophage_oil, ghoul).
effective_against(swallow, griffin).
effective_against(swallow, leshen).
effective_against(blizzard, leshen).
effective_against(golden_oriole, vampire).
effective_against(cat, drowner).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ОБЩИЕ ХАРАКТЕРИСТИКИ МОНСТРОВ (для гибкости логики)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

monster_type(griffin, flying).
monster_type(wyvern, flying).
monster_type(leshen, forest).
monster_type(drowner, undead).
monster_type(ghoul, undead).
monster_type(vampire, undead).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ПРАВИЛА РЕКОМЕНДАЦИЙ
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Рекомендуем одиночное зелье, если оно эффективно против монстра
recommend_single(Witcher, Monster, Potion) :-
    witcher(Witcher),
    monster(Monster),
    effective_against(Potion, Monster),
    potion(Potion, _, _, _).

% Суммируем интоксикацию
total_toxicity([], 0).
total_toxicity([P|Rest], Total) :-
    potion(P, _, _, T1),
    total_toxicity(Rest, T2),
    Total is T1 + T2.

% Проверяем, не превышает ли комбинация лимит
valid_combo(Potions, Limit) :-
    total_toxicity(Potions, Total),
    Total =< Limit.

% Проверяем, чтобы хотя бы одно зелье было эффективно против монстра
combo_effective(Monster, Potions) :-
    member(P, Potions),
    effective_against(P, Monster).

% Генерируем комбинации из 1–3 зелий
combo([A]) :- potion(A, _, _, _).
combo([A,B]) :- potion(A, _, _, _), potion(B, _, _, _), A \= B.
combo([A,B,C]) :- potion(A, _, _, _), potion(B, _, _, _), potion(C, _, _, _), all_different([A,B,C]).

all_different([]).
all_different([H|T]) :- not(member(H, T)), all_different(T).

% Главная рекомендация: найти комбинацию зелий для ведьмака
recommend_combo(Witcher, Monster, Limit, Potions) :-
    witcher(Witcher),
    monster(Monster),
    combo(Potions),
    valid_combo(Potions, Limit),
    combo_effective(Monster, Potions).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ЭВРИСТИКА — выбрать лучшую по эффекту комбинацию
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

combo_power([], 0).
combo_power([P|Rest], Power) :-
    potion(P, _, Bonus, _),
    combo_power(Rest, R),
    Power is Bonus + R.

best_combo(Witcher, Monster, Limit, BestCombo) :-
    setof((Power, C),
          (recommend_combo(Witcher, Monster, Limit, C),
           combo_power(C, Power)),
          Combos),
    last(Combos, (_, BestCombo)).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Союзники: если персонажи друзья или имеют общего врага
ally(X, Y) :-
    friend(X, Y).
ally(X, Y) :-
    enemy(X, Z),
    enemy(Y, Z),
    X \= Y.

% Старший наставник: цепочка наставничества
grandmentor(X, Y) :-
    mentor(X, Z),
    mentor(Z, Y).

% Ведьмак является охотником на монстров, если он убил монстра
monster_hunter(X) :-
    witcher(X),
    killed(X, M),
    monster(M).

% Жители одного королевства
same_kingdom(X, Y) :-
    lives_in(X, K),
    lives_in(Y, K),
    X \= Y.

% Соперники: два человека-врага
rival(X, Y) :-
    enemy(X, Y),
    human(X),
    human(Y).

% Ведьмак защищает королевство, если он живёт там и убил монстра
protects(X, Kingdom) :-
    witcher(X),
    lives_in(X, Kingdom),
    killed(X, M),
    monster(M).

% Опасный персонаж: если он враг ведьмака или монстр
dangerous(X) :-
    enemy(X, geralt);
    monster(X).

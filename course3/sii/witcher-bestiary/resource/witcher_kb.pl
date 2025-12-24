%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% МОНСТРЫ
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% monster(Название_монстра)
monster(griffin).
monster(leshen).
monster(drowner).
monster(vampire).
monster(wyvern).
monster(ghoul).
monster(nekker).
monster(archespore).
monster(alghoul).
monster(wraith).
monster(noonwraith).
monster(nightwraith).
monster(ekimmara).
monster(bruxa).
monster(cockatrice).
monster(werewolf).
monster(fiend).
monster(troll).
monster(centipede).
monster(harpy).

% monster_traits(Монстр, Сильные_стороны, Слабые_стороны)
monster_traits(griffin, [flight, claw_attack], [thunderbolt, aard]).
monster_traits(leshen, [forest_magic, strength], [swallow, decoction_leshen]).
monster_traits(drowner, [water_attack, group], [necrophage_oil, cat]).
monster_traits(vampire, [speed, regeneration], [black_blood, golden_oriole]).
monster_traits(wyvern, [flight, venom], [thunderbolt, decoction_wyvern]).
monster_traits(ghoul, [strength, group], [necrophage_oil, decoction_fiend]).
monster_traits(nekker, [group, stealth], [necrophage_oil, decoction_fiend]).
monster_traits(archespore, [poison, size], [swallow, decoction_leshen]).
monster_traits(alghoul, [undead, bite], [necrophage_oil, decoction_fiend]).
monster_traits(wraith, [ghost, speed], [petri_philter, yrden]).
monster_traits(noonwraith, [ghost, curse], [petri_philter, yrden]).
monster_traits(nightwraith, [ghost, stealth], [petri_philter, yrden]).
monster_traits(ekimmara, [vampiric, attack], [decoction_ekimmara, igni]).
monster_traits(bruxa, [speed, stealth], [igni, thunder]).
monster_traits(cockatrice, [poison, flight], [thunderbolt, decoction_wyvern]).
monster_traits(werewolf, [strength, speed], [silver, decoction_fiend]).
monster_traits(fiend, [strength, fear], [thunder, decoction_fiend]).
monster_traits(troll, [strength, regen], [fire, quen]).
monster_traits(centipede, [poison, speed], [blizzard, decoction_fiend]).
monster_traits(harpy, [flight, agility], [thunderbolt, aard]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ЗЕЛЬЯ
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% potion(Название, Эффект, Значение_бонуса, Интоксикация)
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
potion(full_moon, max_health, 35, 30).
potion(maribor_forest, stamina_regen, 20, 25).
potion(petri_philter, sign_power, 25, 25).
potion(thunder, damage_boost, 45, 35).
potion(decoction_ekimmara, lifesteal, 50, 40).
potion(decoction_fiend, endurance, 30, 30).
potion(decoction_griffin, magic_boost, 40, 35).
potion(decoction_leshen, forest_power, 45, 30).
potion(decoction_troll, regen, 25, 20).
potion(decoction_wyvern, aerial_boost, 35, 30).

% potion_description(Название, Описание)
potion_description(swallow, "Ускоряет регенерацию здоровья. Полезно в длительных боях.").
potion_description(thunderbolt, "Увеличивает урон от атак. Эффективно против крупных врагов.").
potion_description(tawny_owl, "Ускоряет восстановление выносливости. Полезно для частого использования знаков.").
potion_description(cat, "Позволяет видеть в темноте. Необходима в пещерах и ночью.").
potion_description(white_honey, "Полностью выводит токсины и снимает эффекты зелий.").
potion_description(blizzard, "Замедляет время при уклонении. Эффективно против быстрых врагов.").
potion_description(black_blood, "Отравляет вампиров, когда они пьют кровь ведьмака.").
potion_description(necrophage_oil, "Повышает урон против утопцев и гулей.").
potion_description(golden_oriole, "Повышает устойчивость к ядам.").
potion_description(killer_whale, "Увеличивает запас дыхания под водой.").
potion_description(full_moon, "Увеличивает максимальное здоровье ведьмака.").
potion_description(maribor_forest, "Ускоряет восстановление энергии.").
potion_description(petri_philter, "Усиливает знаки. Эффективен против духов.").
potion_description(thunder, "Повышает физическую силу ведьмака.").
potion_description(decoction_ekimmara, "Дарует вампиризм: здоровье восстанавливается при ударе.").
potion_description(decoction_fiend, "Повышает выносливость в долгих боях.").
potion_description(decoction_griffin, "Усиливает силу магических знаков.").
potion_description(decoction_leshen, "Увеличивает силу атак в лесных областях.").
potion_description(decoction_troll, "Повышает регенерацию здоровья.").
potion_description(decoction_wyvern, "Повышает урон по воздушным врагам.").

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ЗНАКИ ВЕДЬМАКА
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% sign(Название_знака, Эффект, Значение_бонуса)
sign(aard, knockdown, 30).
sign(igni, fire_damage, 40).
sign(yrden, trap, 25).
sign(quen, shield, 35).
sign(axii, charm, 20).

% sign_description(Название_знака, Описание)
sign_description(aard, "Телепатический толчок, сбивает с ног врагов и монстров.").
sign_description(igni, "Огненный знак. Поджигает монстров и врагов.").
sign_description(yrden, "Создает магическую ловушку, замедляет врагов и призыв духов.").
sign_description(quen, "Щит, который поглощает урон и защищает ведьмака.").
sign_description(axii, "Манипуляция разумом, временное подчинение противника.").

% sign_effective_against(Знак, Монстр)
sign_effective_against(aard, griffin).
sign_effective_against(aard, wyvern).
sign_effective_against(igni, wraith).
sign_effective_against(igni, bruxa).
sign_effective_against(yrden, noonwraith).
sign_effective_against(yrden, nightwraith).
sign_effective_against(quen, troll).
sign_effective_against(axii, human).
sign_effective_against(axii, dwarf).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ПРАВИЛА КОМБИНАЦИЙ ЗЕЛИЙ И ЗНАКОВ
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

effective_against(Potion, Monster) :-
    monster_traits(Monster, _, Weaknesses),
    member(Potion, Weaknesses).

% total_toxicity(Список_зелей, Общая_интоксикация)
total_toxicity([], 0).
total_toxicity([P|Rest], Total) :-
    potion(P, _, _, T1),
    total_toxicity(Rest, T2),
    Total is T1 + T2.

% valid_combo(Список_зелей, Лимит_интоксикации)
valid_combo(Potions, Limit) :-
    total_toxicity(Potions, Total),
    Total =< Limit.

% combo_effective(Монстр, Список_зелей) — хотя бы одно зелье эффективно против монстра
combo_effective(Monster, Potions) :-
    member(P, Potions),
    effective_against(P, Monster).

% combo(Список_зелей) — генерирует комбинации из 1–3 зелий
combo([A]) :- potion(A, _, _, _).
combo([A,B]) :- potion(A, _, _, _), potion(B, _, _, _), A \= B.
combo([A,B,C]) :- potion(A, _, _, _), potion(B, _, _, _), potion(C, _, _, _), all_different([A,B,C]).

% all_different(Список_элементов) — все элементы различны
all_different([]).
all_different([H|T]) :- not(member(H, T)), all_different(T).

% recommend_combo(Монстр, Лимит_интоксикации, Список_зелей)
recommend_combo(Monster, Limit, Potions) :-
    monster(Monster),
    combo(Potions),
    valid_combo(Potions, Limit),
    combo_effective(Monster, Potions).

% combo_power(Список_зелей, Общая_эффективность)
combo_power([], 0).
combo_power([P|Rest], Power) :-
    potion(P, _, Bonus, _),
    combo_power(Rest, R),
    Power is Bonus + R.

% best_combo(Монстр, Лимит_интоксикации, Лучший_список_зелей)
best_combo(Monster, Limit, BestCombo) :-
    findall((Power, C),
        (recommend_combo(Monster, Limit, C),
         combo_power(C, Power)),
        Combos),
    sort(Combos, Sorted),
    reverse(Sorted, [(MaxPower, BestCombo)|_]).


% recommend_signs(Монстр, Список_знаков)
recommend_signs(Monster, Signs) :-
    monster(Monster),
    findall(S, sign_effective_against(S, Monster), Signs).

% hunt_advice(Монстр, Лимит_интоксикации, Сильные_стороны, Слабые_стороны, Зелья, Знаки)
hunt_advice(Monster, Limit, Strong, Weak, Potions, Signs) :-
    monster(Monster),
    best_combo(Monster, Limit, Potions),
    recommend_signs(Monster, Signs),
    monster_traits(Monster, Strong, Weak).



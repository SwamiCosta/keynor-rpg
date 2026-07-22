package com.keynor.rpg.domain.model;

/**
 * A combat action tracked by the board's Unit of Time (UT) clock — 1 UT = 0.1 second. Each
 * constant maps to one entry of the UT balancing report (2026-07-14): a fixed {@code utBase}
 * (see {@link CombatTimingCoefficients}) and a weighted combination of derived attributes
 * ({@link CombatActionTimeCalculator}) that together determine how long the action takes for a
 * given character. Only {@link #WALK_1M} is currently wired to any REST-consuming client
 * (the board's "Passo de Combate" movement) — every other constant exists so the backend has a
 * single, complete source of truth for combat timing, ahead of the frontend gaining a way to
 * trigger them.
 */
public enum CombatActionType {
    /** Ação 01 — Deslocamento de 1m em Ação (passo de combate cuidadoso). S = Speed. */
    WALK_1M,
    /** Ação 02 — Deslocamento de 1m em Corrida. S = Speed. */
    RUN_1M,
    /** Ação 03 — Golpe Corporal Rápido (Jab). S = 0.7×Speed + 0.3×MeleeDexterity. */
    JAB,
    /** Ação 04 — Golpe Corporal Padrão (soco cruzado, chute). S = 0.6×Speed + 0.4×MeleeDexterity. */
    BODY_STRIKE,
    /** Ação 05 — Ataque Perfurante (rapieira, lança). S = 0.7×Speed + 0.3×MeleeDexterity. */
    PIERCING_ATTACK,
    /** Ação 06 — Ataque de Balanço Leve (machadinha, espada, martelo). S = 0.7×Speed + 0.3×MeleeDexterity. */
    LIGHT_SWING_ATTACK,
    /** Ação 07 — Ataque de Balanço Pesado (martelo de guerra, espadão). S = 0.65×Speed + 0.35×MeleeDexterity. */
    HEAVY_SWING_ATTACK,
    /** Ação 08 — Beber uma Poção (já na mão). S = Speed. */
    DRINK_POTION,
    /** Ação 09A — Sacar Arma Corpo a Corpo Leve/Média. S = 0.6×Speed + 0.4×MeleeDexterity. */
    DRAW_MELEE_WEAPON,
    /** Ação 09B — Sacar Arma de Longa Distância (arco, arma de fogo). S = Speed. */
    DRAW_RANGED_WEAPON,
    /** Ação 10 — Sacar Objeto da Mochila. S = Speed. */
    DRAW_FROM_BACKPACK,
    /** Ação 11 — Recarregar Pistola. S = Speed. */
    RELOAD_PISTOL,
    /** Ação 12 — Recarregar Arma de Fogo Maior (rifles, fuzis). S = Speed. */
    RELOAD_LONG_GUN,
    /** Ação 13 — Evasão (sair da frente de um ataque). S = 0.7×Evasion + 0.3×Speed. */
    EVASION,
    /** Ação 14 — Aparar/Bloquear (escudo ou arma). S = 0.4×CognitiveSpeed + 0.3×MeleeDexterity + 0.3×Speed. */
    BLOCK,
    /** Ação 15 — Levantar-se do Chão. S = Speed. */
    STAND_UP,
    /** Ação 16 — Mirar (ajustar a mira para o próximo disparo). S = Speed. */
    AIM,
    /** Ação 17 — Sacar Arma Pesada (espadões, martelos de guerra). S = 0.7×Speed + 0.3×MeleeDexterity. */
    DRAW_HEAVY_WEAPON,
    /** Ação 18 — Dar Meia Volta (pivô rápido sobre o próprio eixo). S = Speed. */
    TURN_AROUND,
    /** Ação 19 — Analisar o Inimigo/Olhar ao Redor. S = CognitiveSpeed. */
    ANALYZE_SURROUNDINGS,
    /** Ação 20 — Conjurar Magia (feitiço básico). S = Speed. */
    CAST_SPELL
}

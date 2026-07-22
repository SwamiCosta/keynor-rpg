# Game Rules

**Scope:** The tabletop-style ruleset that governs how `keynor-rpg` characters interact with the world — tests, contested rolls, damage, combat timing, and (pending) bonds and stress/sanity. This is gameplay/design content, not a backend implementation guide — it documents the rules a Dungeon Master and players follow at the table, and the reference domain knowledge for the `Doraxes` and `Clown` agents.

**Maintained by:** Doraxes (primary — flags gaps, proposes clarifications, keeps this current as new rules land) and Gaemes (structural changes, cross-references to the attribute/domain-model docs). Referenced from `doraxes.md`'s Mandatory reading (full) and `clown.md`'s Mandatory reading (contextual — Clown does not need to master combat/damage resolution, only enough to understand what each attribute is *for*).

**Status:** Work in progress. This document is being built incrementally with the user. Sections marked `*TODO*` are known-empty and intentionally not filled in — never invent content for them. Sections marked **OPEN QUESTION** contain a rule that is real but underspecified in a way that changes how an agent should act — these must be resolved with the user before being treated as settled, per the workspace's Skill 14 (Ask Before Inferring). Doraxes' standing job is to keep this list current and never let it silently go stale.

---

## Tests

A test resolves an uncertain action. Roll a random number from 1 to 100 and add the relevant attribute. **If the total exceeds 100, the test succeeds.**

Conditions can add bonuses or penalties to the roll. Difficulty can also add bonuses or penalties (rather than changing the success threshold, which stays fixed at "total > 100").

**Example:** A character with 60 points in Survival Skills tries to start a fire by rubbing sticks together. They add 60 to the random roll. If the wood is wet, they might take a -40 penalty. If they have flint and steel, they might get a +20 bonus. Because it's a difficult activity, a further -10 penalty might apply.

**There is no critical success or critical failure mechanic.** The random roll's raw value is never special-cased — a character who rolls a natural 1 but has enough bonus to exceed 100 succeeds exactly like anyone else who exceeds 100; a character who rolls a natural 100 but carries enough penalty to fall at or below 100 fails exactly like anyone else who does. Only the final total (after every bonus and penalty) against the fixed ">100" threshold matters — confirmed by the user 2026-07-08, resolving the prior open question about whether exactly 100 succeeds: it does not (the threshold is strictly ">100", not "≥100"), and there is no alternate "tie" outcome to consider since nothing is ever compared to the roll itself, only to 100.

### Contested tests

Two characters test against each other; bonuses or penalties can favor either side depending on the situation.

**Example:** A character tries to bluff another during a card game. They roll Discretion; the other rolls Behavior Reading. Both add their attribute to their own random roll, and whoever gets the higher total wins. Being drunk might penalize a character; heavy noise or distraction might affect either side.

**Ties are circumstantial, but the character *resisting* an effect wins by default.** When a contested test produces a numeric outcome that matters even on a loss (e.g. a graze), a tie can be resolved as a halved result rather than an outright win/loss. As a general rule, though, whichever side of the contest is *resisting* something is considered the winner on a tie. Example: a character resisting a mind-control effect succeeds if their result ties the mage casting it — the resister wins ties, not the initiator.

### Combined tests

Some tests require combining attributes. Moving without being noticed might need Hiding, Sneaking, and Discretion combined; attacking an enemy might need Swing Power combined with Melee Dexterity. **Each attribute rolls its own random number independently — its own d100 plus that attribute — and the resulting totals are then averaged** (with different weights per attribute where the situation calls for it), not the raw attribute values averaged before a single roll. Confirmed by the user 2026-07-08.

### Assistance

A character can be assisted by another while attempting a test, when the situation allows it. Assistance requires its own test, either in the same attribute or in a complementary one. Each side's test rolls independently (own d100 plus own attribute, same convention as Combined tests above) and the results are then combined — an exact average, or different weights depending on who is more involved in the task, depending on the situation. Synergy bonuses can apply here if the characters share a bond.

> **OPEN QUESTION — helper's own success, and synergy.** Does the helper's own test need to independently succeed (>100) for the assistance to count, or is only the combined total checked against the threshold? Separately, synergy bonuses depend on the still-`*TODO*` Bonds section — expected to stay open until that section exists.

---

## Attribute reference — what each attribute represents

This section documents the narrative/gameplay meaning of each derived attribute, for Doraxes' rulings and Clown's character-creation suggestions. It complements, not replaces, `additive-attribute-formulas.md`'s formula-level detail — this file explains *what an attribute is for*, that file explains *how its number is computed*.

| Attribute | What it represents |
|---|---|
| **Push Strength / Upper Strike** | Pushing force, from the chest muscles. Affects straight-forward movements — direct punches, thrusts. Increases damage for punches, spear thrusts, rapier strikes, etc. |
| **Leg Drive** | Leg strength. Affects projection movements or kicks. Increases damage from shoves or kicks. Used (with Push Strength's help) to push, or (with Grappling Strength's help) to drag, loads above Max Capacity and below Drag Capacity — a contested test if the target resists. |
| **Grip Strength** | Constricting force, both in grapples and in fists/hands/fingers. Contributes to Grappling / Self Lifting. Used to execute or resist disarm attempts, or to deal crushing damage. |
| **Lift / Pull Strength** | Pulling force, from the back muscles. Contributes to Grappling / Self Lifting. Used as the requirement for firing a bow (each bow has its own weight requirement). |
| **Swing Power** | The character's ability to generate circular movement. Increases damage from axes, mauls, swords, hooks/cross punches, thrown weapons, etc. A hanging character can use this to swing themselves. |
| **Grappling / Self Lifting** | Average of Grip and Pull Strength — the combination of grip and pull. Used to move objects or enemies. |
| **Speed** | The character's general ability to apply speed to their body's movements. Affects Evasion and Movement Speed. In chase situations, this attribute determines how quickly a character accelerates toward their top speed. |
| **Movement Speed** | Average travel speed. Governs movement in combat. A character can choose to run, doubling this speed but costing stamina per movement. In chase situations, tests of this attribute determine the character's top speed. |
| **Stamina Pool** | Total stamina available to the character, derived from cardiovascular capacity. Determines how many actions a character can take before tiring. |
| **Fatigue Resistance** | Resistance to tiring, derived from cardiovascular capacity. Determines how slowly a character tires. |
| **Stamina Recovery** | Rest quality, derived from cardiovascular capacity. Determines how quickly a character rests mid-combat or in a dangerous situation. |
| **Soft Tissue Durability** | Resistance of the character's soft-tissue body components — muscle, skin. Represents natural protection against damage. |
| **Bone Durability** | Resistance of the character's bones. Represents natural protection against damage. |
| **Total Mass** | The character's approximate total weight. Has little use beyond determining how easily the character can be displaced by others. |
| **Physical Integrity** | 0-100% summary of overall body condition, from the wound tree. Reaching 0% kills the character; a destroyed vital organ (e.g. Heart) can kill outright regardless of the rest. See the Damage section's own subsection below. |
| **Valor** | A character's resolve to keep fighting rather than give up. Falls 1:1 with Physical Integrity loss; reaching 0 or below makes the character "fall" (afraid, avoids conflict) without necessarily dying. See the Combat section's own subsection below. |

> **OPEN QUESTION — table coverage.** Only the attributes the user has described so far are listed above. As Clown and Doraxes are used, every derived attribute in `additive-attribute-formulas.md` should eventually get an entry here (Sight/Hearing/Smell, the Cognitive group, the Mind-driven Skills/Social/Supernatural attributes, etc.) — currently absent, not yet a gap to chase down proactively, just an acknowledged incompleteness.

---

## Damage

### Damage types

| Type | Caused by |
|---|---|
| **Chop** | Impactful blade cuts — axes, swords |
| **Slice** | Blades sliding across a surface — sabers |
| **Blunt** | Concussive blows — hammers |
| **Piercing** | Puncturing — arrows, spears |
| **Burning** | High temperature — fire, heat |
| **Frost** | Low temperature — ice, cold |
| **Corrosive** | Corrosive effects — acid |
| **Tear** | Tearing, pulling, or ripping-apart effects |
| **Compress** | Crushing effects |

Mental damage is measured in stress, not here (see the `*TODO*` Stress and Sanity section). Spiritual damage is also a separate case, not yet documented.

Every `Material` (see `keynor-rpg`'s `Material`/`DamageType` domain catalog) has its own resistance to each damage type — e.g. steel is very resistant to Slice but less resistant to Blunt, and quite vulnerable to Burning; padded linen, despite being frailer overall, handles Blunt better than Slice.

### Resolving a hit's damage value

**Resolved (2026-07-20) — supersedes the 2026-07-08 "contested test against Evasion" decision.** For melee, thrown, firearm/crossbow, and bow attacks, the hit check is the fixed-threshold Special Attack Test roll (e.g. `Tmd > 40`) described in `special-attack-test.md` — **not** a contested roll against the target's Evasion. Evasion still matters, but only as the target's *optional reaction*, not as part of the attacker's own hit check:

A damage value comes from either a test or a fixed value. Example: an axe swing uses a test combining Swing Power + Melee Dexterity; a falling rock or a fired bullet has a fixed damage value. Damage tests do **not** follow the "success or failure" (>100) rule of an ordinary test — only the final total is applied directly. A character who rolls 70 and has +60 Swing Power deals 130 points of damage. See the Special Attack Test section below for the concrete melee/thrown/firearm/bow resolution mechanics that supersede this general example.

See `special-attack-test.md`'s "Evasion reaction" section for the implementation (`EvasionResolver`).

A damage value comes from either a test or a fixed value. Example: an axe swing uses a test combining Swing Power + Melee Dexterity; a falling rock or a fired bullet has a fixed damage value. Damage tests do **not** follow the "success or failure" (>100) rule of an ordinary test — only the final total is applied directly. A character who rolls 70 and has +60 Swing Power deals 130 points of damage. See `special-attack-test.md` for the concrete melee/thrown/firearm/bow resolution mechanics that supersede this general example.

### Damage vs. resistance — outcome categories

Once a raw damage value and a target material/damage-type pair are known, a calculation is applied to determine how much of that damage actually matters. The result falls into one of three categories:

1. **Irrelevant damage** — the material's resistance is far superior, or the initial damage is too low; no change to the material.
2. **Significant damage** — the damage overcomes the material's resistance and causes deformation or alteration: cuts, burns, bends, cracks, etc. The amount of deformation depends on the damage dealt. This is where negative effects begin to apply — a cut arm applies less force, a bent sword deals less damage, a shattered shield has lower durability, etc. Significant damage **accumulates**: a new instance of significant damage is added to the prior total to check whether the object's limit is exceeded.
3. **Irreversible damage** — the final damage value exceeds the target's limit and it is completely destroyed: broken weapon blades, destroyed shields, severed limbs, etc. Afterward the object is no longer functional, or at minimum imposes a severe penalty when used in tests.

**Resolved (2026-07-20) — see `special-attack-test.md`.** The calculation this `*TODO*` used to defer now exists: `Protection (P) = Vb × Md × Dm` (reusing `Material.baseDurability`/`getMultiplier` for Vb/Md, plus a new per-equipment `Dm`) and `FinalDamage = (Db − P) × Ae` if `Db > P`, else `0`. This maps directly onto the three categories above: `FinalDamage == 0` is Irrelevant; a nonzero `FinalDamage` that keeps the object under its irreversible-damage limit is Significant (still accumulates, per the rule above); a `FinalDamage` that exceeds that limit is Irreversible. `Db` (raw damage) itself now comes from one of four concrete per-attack-type formulas (melee, thrown, firearm/crossbow, bow) — see that file for the full detail. Not yet wired into a REST endpoint or `PlayableCharacter` — reference data and pure calculators only, for now.

**Combined damage types:** an attack can combine more than one damage type, but each type is calculated separately. Example: an explosion might deal 30 Blunt, 40 Burning, and 20 Piercing (from shrapnel) — each application goes through its own resistance calculation, and the amounts that get through are summed to determine whether the total damage is Irrelevant, Significant, or Irreversible.

### Damage to the body

Each body component defined in the Wound Tree has its own irreversible-damage limit based on its size, plus its own natural resistance values based on its material (bone, flesh, or both). When a body component is damaged, it starts to penalize the attributes that depend on it.

**Biological damage:** characters with biological bodies can suffer direct damage to internal organs tied to disease, poisoning, and similar effects. Here the deterioration can simulate a damage application (e.g. wasting muscle, corroding organs) or adverse effects depending on the situation (e.g. exhaustion from lack of air, paralysis, etc.)

### Physical Integrity (2026-07-18)

A single 0-100% summary of the character's overall body condition, derived from the wound tree (`PlayableCharacter.getPhysicalIntegrity()`). 100% at full health; cannot exceed 100%. Reaching 0% kills the character.

- **Severity:** irreversible damage counts far more against Integrity than reversible damage on the same component (a severed, irreversibly-damaged arm is worse than dozens of reversible bruises spread across the body) — implemented as a weighted per-component severity (`kIntegrityReversibleSeverity` vs. `kIntegrityIrreversibleSeverity`, see `additive-attribute-formulas.md`).
- **Component importance:** not every component matters equally — a hit to the stomach costs more Integrity than the same raw damage to a thigh. Implemented as an importance tier derived from each component's `CascadeRelation` + `vital` flag (protected-internal-and-vital > protected-internal > structural > attached-appendage), not 45 hand-authored per-node numbers.
- **Instant death by vital organ:** a character can sit at 100% Integrity everywhere else and still die outright the instant a `vital` component (Heart, Brain, etc.) is fully, irreversibly destroyed — independent of the weighted-average calculation above.
- **Resistance slows time-based loss, but never stops it (settled design rule, 2026-07-19):** whenever Integrity drains over time from an active status (bleeding, poison, disease, starvation, dehydration — see the still-open mechanic below), the matching resistance (Bleeding Resistance, Disease Resistance, Poison Resistance, etc.) reduces how much is lost per tick, but the loss can never be reduced below **1**. No resistance value, however high, fully negates ongoing Integrity loss from an active status — this floor is a confirmed design decision, independent of the still-undetermined damage-vs-resistance formula below, and must not be lost inside that open question.

> **OPEN QUESTION / linked TODOs — bleeding, poison, disease, starvation, dehydration as time-based Integrity loss (2026-07-18).** The user's design:
> - Any of these statuses drains Integrity every 5 UT, mitigated per the settled floor-of-1 rule above.
> - **Accumulated wound count is the dominant factor** — a character with several gunshot wounds bleeds out faster than one with a single cut, regardless of either character's resistance value.
> - **Slice, Pierce, and Chop damage can each trigger a bleeding status even when the damage itself is reversible (not just on irreversible hits) — of the three, Slice has the highest chance of triggering it.** Separately, and unconditionally, all irreversible damage of any type always triggers some bleeding rate.
> - The bleeding rate applied by a given hit is scaled by the damage that hit actually dealt, **after resistance is applied** to it.
> - A component already fully, irreversibly damaged should still cost Integrity on a further hit to it, even though no more HP is lost on that specific component (it's already at its floor).
>
> **None of this mechanic is implemented** — it depends directly on the damage-vs-resistance formula in the "Damage vs. resistance — outcome categories" `*TODO*` above (2026-07-08, "do not invent this formula"), which is itself still unresolved. **These two TODOs are deliberately kept cross-referenced — do not resolve one without revisiting the other**, since the bleeding/tick design is unbuildable until the damage-vs-resistance formula exists, and that formula's shape should account for what the bleeding mechanic will need from it (a post-resistance damage value) once it's defined.

### Protection

If there is cover or protection between the character and the source of damage, the damage calculation is applied to the protection first, and only the leftover damage passes through. The same then applies again to the character's own body components.

**Example:** a character wearing steel armor is struck in the chest. The damage is first calculated against the steel. If it's Irrelevant, nothing happens. If it's Significant or Irreversible, a calculation determines how much damage the steel protection absorbs and how much passes through to the character's chest. Damage is then applied against the chest again and, if that is overcome, may or may not transfer to the heart.

### Recoil damage

Every contact strike has a recoil effect that damages the object used to attack. It works as follows:

A steel sword strikes a bronze breastplate, and the damage (before resistance is applied) is 100.
- Calculate the Bronze's resistance to the sword's Chop attack — call this value **RB**.
- Calculate the Steel's resistance to the armor's Blunt recoil — call this value **RA**.
- Sum RB and RA to get the total system resistance (**RT**), which determines which object receives the larger share of the divided damage.
- Damage to the breastplate = `100 × (RA / RT)`. Damage to the sword = `100 × (RB / RT)`.

Recoil-type mapping (extended by the user 2026-07-08):

| Attacking type | Recoils as |
|---|---|
| Chop | Blunt |
| Piercing | Blunt |
| Blunt | Blunt |
| Slice | Corrosive |
| Burning | Frost |
| Frost | Burning |
| Tear | Compress |
| Compress | Tear |
| Corrosive | *(not yet specified)* |

> `*TODO*` — Corrosive's own recoil type is still unspecified. Given the "skip when no object involved" rule just below, this may end up moot in practice (acid attacks may rarely involve a damageable attacking object), but don't assume that — wait for the user to confirm rather than inferring it from the pattern of the other 8.

**In practice, recoil is calculated in only a minority of cases — skip it entirely whenever it doesn't apply.** Recoil only exists for impact- or friction-based attacks, i.e. attacks delivered through a physical object that itself can take damage. If the attack has no such object, there is nothing to apply recoil to and the recoil step is skipped outright — it is not "zero damage," it simply never runs. Example: a fireball deals Burning damage but has no recoil, because fire itself has no matter to damage.

---

## Combat

### Time tracking

Combat proceeds as an increasing time count. There is a count for each character involved in combat, plus a general count. The general count increases by 1 every time nothing else happens. When the general count equals a character's own count, that character may act or defer. When a character takes an action, their own count increases by that action's duration.

**Example:** general count = 10, Character A's count = 11, Character B's count = 16. Nothing happens; general count increments. General count = 11, matching Character A — they act, choosing to move; the move takes 6 time units, so their count becomes 17. Nothing happens for 5 more units. Now the count is 16, matching Character B — they act, choosing to fire an arrow at Character A. And so on.

**Time units are called UT (Unidade de Tempo) — 1 UT = 0.1 second.** This resolves the prior open question: an action's time cost is derived from the acting character's attributes via the formula below (confirmed by the user 2026-07-14, UT balancing report).

**UT formula:**

```
S  = Σ (attribute_i × weight_i)      — the action's attribute score, a weighted combination
UT = max(1, floor(UT_Base × (60 / S)))
```

`floor` rounds the result down; `max(1, ...)` guarantees no active combat action ever costs 0 UT. When every attribute feeding an action sits at 60 (the baseline every additive-standard attribute equals at its inputs' neutral values — see `additive-attribute-formulas.md`), `S = 60` and `UT` resolves to exactly `UT_Base`, regardless of how many attributes the action combines (every action's weights sum to 1.0).

**Per-action UT_Base and formula:**

| Action | UT_Base | Score (S) formula |
|---|---|---|
| Move 1m, careful step | 5 | Speed |
| Move 1m, running | 2 | Speed |
| Jab (quick body strike) | 2 | 0.7×Speed + 0.3×Melee Dexterity |
| Standard body strike (cross, kick) | 4 | 0.6×Speed + 0.4×Melee Dexterity |
| Piercing attack (rapier, spear) | 3 | 0.7×Speed + 0.3×Melee Dexterity |
| Light swing attack (hatchet, sword, hammer) | 4 | 0.7×Speed + 0.3×Melee Dexterity |
| Heavy swing attack (war hammer, greatsword) | 8 | 0.65×Speed + 0.35×Melee Dexterity |
| Drink a potion (already in hand) | 12 | Speed |
| Draw light/medium melee weapon | 5 | 0.6×Speed + 0.4×Melee Dexterity |
| Draw ranged weapon (bow, firearm) | 5 | Speed |
| Draw item from backpack | 40 | Speed |
| Reload pistol | 15 | Speed |
| Reload long gun (rifle) | 25 | Speed |
| Evasion (dodge an incoming attack) | 3 | 0.7×Evasion + 0.3×Speed |
| Block/parry (shield or weapon) | 2 | 0.4×Cognitive Speed + 0.3×Melee Dexterity + 0.3×Speed |
| Stand up from the ground | 10 | Speed |
| Aim (adjust aim before next shot) | 4 | Speed |
| Draw heavy weapon (greatsword, war hammer) | 8 | 0.7×Speed + 0.3×Melee Dexterity |
| Turn around (quick pivot) | 2 | Speed |
| Analyze surroundings / assess the enemy | 3 | Cognitive Speed |
| Cast a spell (basic) | 10 | Speed |

Only movement (the first two rows) is currently reachable from the board's frontend client; every other action above is implemented backend-side (`POST /api/v1/combat/action-time`, `CombatActionType`) ahead of the frontend gaining a way to trigger them.

**Long Range Combat removed (2026-07-18).** The attribute itself is gone (see `additive-attribute-formulas.md`'s Removals section); the four actions that used to read it (Draw ranged weapon, Reload pistol, Reload long gun, Aim) now score on Speed alone, at weight 1.0 — not the old partial Speed weight left standing on its own. No game-balance rebalancing beyond that was requested; revisit if the four actions feel mistimed in play.

**Close Combat and Short Range Combat merged into Melee Dexterity (2026-07-20).** Both attributes read by the table above under two different names for the same underlying concept — melee proficiency — are gone; every action that used to weight either one now reads the single `Melee Dexterity` (MD) attribute instead, at the same weights as before (only the attribute name changed, not the formula shape or its numbers). `Melee Dexterity` (renamed from `Melee Accuracy`) is also the attribute rolled for every melee attack's hit-check test — see the new Special Attack Test section below.

**Shared actions:** if two or more characters are tied on count, all of them choose their action and submit it simultaneously. This can be done secretly, so as not to grant an advantage, if they are opponents.

**Reactions:** reactions such as dodging, blocking, or parrying can only be executed if the character is at rest (not currently mid-action). A character in the middle of an attack or movement cannot dodge or block incoming attacks. It is entirely possible for two characters to attack each other at the same time and for both to land their hits simultaneously. This is why it matters for characters to know when to wait and defer their actions, in order to react to enemy actions.

**Concurrent actions:** some actions can be combined, like walking and attacking, or blocking and counter-attacking. These combined actions require Agility tests. If executed, the longer action's duration is used for the time count.

> `*TODO*` — the Agility test's difficulty and its failure consequence depend on which pair of actions is being combined, and will be defined per action rather than as one universal rule (confirmed by the user 2026-07-08). Do not invent a single universal threshold or consequence in the meantime — wait for the per-action definitions.

**Actions available to everyone:**
- **Lunge Attack** — the character makes a short forward movement (up to 2 meters) and an attack at the same time.
- **Charge Attack** — the character runs a longer distance (more than 2 meters) and executes an attack at the end of the run. If the distance is greater than 4 meters, damage increases by +5.

**Combat start:** at the start of combat, every character makes a Cognitive Speed test to determine turn order. Divide each result by 10 and round down.

**Example:** if one character gets 110, another gets 109, and another gets 100, the order is: the first character starts at count 0, and the other two start at count 1.

**Surprise:** if one or more characters are surprising another, they may take any number of actions — including attacks — until detected. At the moment of detection, the count starts at 0 for every character.

**Detection is a contested test between whichever attributes fit the situation** — e.g. Hiding vs. Sight, Sneaking vs. Hearing, or Bluffing vs. Behavior Reading. Any attribute pairing that could plausibly let one side surprise an unaware other side is valid; this is not a fixed pair, it's chosen per situation. As already stated above, the moment a surprised, unaware target is struck, their surprise ends. Any other nearby enemy who can see or hear the one who was just struck also stops being surprised at that point. Confirmed by the user 2026-07-08.

**Fatigue:** every action a character takes in combat costs stamina, drawn from the Stamina Pool as combat unfolds. When a character reaches a quarter of their total stamina or less, they are considered tired: their speed and strength become compromised, and they can only take an action if they have enough stamina to pay at least half that action's cost.

**Example:** a character with 7 stamina remaining can take an action that costs 14 stamina, but not one that costs 15.

**Tired penalty (confirmed by the user 2026-07-08): -25% on any physical test, -10% on any non-physical test.** Being tired doesn't only compromise the body — reasoning gets foggy and even casting spells becomes harder, hence the smaller but real penalty on non-physical tests too.

When a character's stamina is driven to 0 or below, they are considered exhausted and cannot take any further action until stamina is restored, except for actions that don't consume stamina. A character automatically restores stamina while standing still; the rate is determined by their Stamina Recovery and how long they rest. The maximum stamina recoverable this way is one third of the character's total pool — recovering beyond that requires several minutes of rest after combat ends.

**Actions that don't consume stamina:** walking (not running), blocking (only raising or moving the arms for protection — anything beyond that requires stamina), dropping or picking up small objects, drinking potions, etc.

### Valor (2026-07-18; collapse-vs-conscious distinction added 2026-07-19)

A Combat/Competition pool attribute (`PlayableCharacter.getValorAttribute()`) representing a character's resolve to keep fighting rather than give up. Baseline 60, raised by the `Bellicose` trait and the testosterone modifier.

- **Coupled 1:1 to Physical Integrity loss:** every point of Integrity lost costs Valor the same amount (lose 40 Integrity, lose 40 Valor) — implemented directly, not as a triggered event. `current` is allowed to go negative.
- **Falling vs. dying:** Valor at 0 or below means the character's will to fight gives out — but this is **not necessarily dead** (that's Physical Integrity reaching 0). A character whose max Valor exceeds 100 can, in principle, keep fighting all the way to death by Integrity loss alone.
- **Two different collapse states, depending on what caused the drop to 0 (settled design rule, 2026-07-19):**
  - **Driven to 0 by a physical blow** (i.e. by the Integrity-coupled Valor loss from a strong hit): the character's body physically gives out — they collapse to the ground and may faint outright, unable to keep a combat stance, even though they are still alive.
  - **Driven to 0 by anything other than a blow** (the still-undesigned Fear/Demoralization/Confusion/pain-without-wound effects below): the character stays conscious and standing, but thoroughly discouraged — becomes easily frightened and does everything in their power to avoid conflict or continued pain.
- **Resisting once fallen or discouraged:** a player who wants their character to resist this impulse (stay on the field, withhold information under torture) can still attempt Will tests, at escalating penalties, regardless of which of the two collapse states above applies.
- **Current implementation only covers the "physical blow" state:** `PlayableCharacter.hasFallen()` is a single boolean (`current <= 0`), with no cause tracking — but since the Integrity coupling is the *only* implemented way to move Valor today (every other drain listed below is still undesigned), every `hasFallen()` today is, by construction, the "driven to 0 by a blow" case. The "conscious but discouraged" case cannot occur yet and needs no code today; it becomes relevant only once a Fear/Demoralization/Confusion/pain-without-wound mechanic exists to actually trigger it.

> **OPEN QUESTION — everything beyond the Integrity coupling (2026-07-18).** Fear, Demoralization, Confusion, Affection, and Intense Pain effects are all meant to drain Valor independently of Integrity loss, resisted by Fear Resistance/Will/etc. Pain Threshold is the most important of these: an attack that causes significant pain should reduce Valor via a pain effect if the target's Pain Threshold isn't high enough to resist — and pain can occur without a severe wound (torture scenarios: heavy pain, light wounds). **None of this is implemented** — no formulas or thresholds have been given yet; only the baseline Integrity-coupled formula above is built. The exact size of "escalating penalties" for a fallen character's resistance Will tests is likewise undefined — a DM-adjudicated call for now (Doraxes' domain), not a fixed table.

> `*TODO*` — no action has a stated stamina cost yet (the "14 stamina" example above is illustrative, not a real value). The user has confirmed (2026-07-08) that, like the time-cost table, an action's stamina cost will be **derived from the character's Fatigue Resistance**, with the exact formula to follow in a future delta. The exact formula relating Stamina Recovery + rest duration to stamina restored is likewise still pending. Do not invent either in the meantime.

---

## Bonds

*TODO*

---

## Stress and Sanity

*TODO*

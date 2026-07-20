# Special Attack Test

**Scope:** The d20-based attack-resolution mechanic (2026-07-20) that replaces the generic "a damage value comes from a test" example in `game-rules.md`'s Damage section with concrete, per-attack-type formulas — melee, thrown, firearm/crossbow, and bow. This file is the formula-level detail; `game-rules.md` carries the narrative rules. Read it before touching `MeleeAttackProfile`, `ThrownWeaponProfile`, `FirearmProfile`, `BowProfile`, `ArmorProtection`, `SpecialAttackTestResolver`, or any of the four `*AttackResolver` domain services.

**Status:** Domain model and formulas implemented (`domain.model`/`domain.service`) as reference data and pure calculators — same "not yet consumed by any use case" precedent as `Material.java` (rpg-21). No REST endpoint exists yet; wiring this into `PlayableCharacter`/a real attack-resolution use case is a future delta.

---

## The Special Attack Test roll

Unlike the ordinary d100 Test (`game-rules.md`'s "Tests" section, `>100` succeeds), every attribute this mechanic reads is rolled once via:

```
result = attribute - 10 + 1d20
```

A d20 only ever shifts the attribute by up to 10 points either way — there is no independent success/failure threshold baked into the roll itself, and (same rule as the ordinary Test) **no critical success or critical failure**: the raw d20 value is never special-cased. `SpecialAttackTestResolver.roll(attributeValue)` implements this, via the existing `RandomSource` output port (the same one `BodyCascadeResolver` uses) — `d20 = randomSource.nextInt(20) + 1`.

Each attack type below rolls this once per attribute it needs (Tmd, Tstr-variant, Taim, Tsp, Tpst) — each roll is independent, same "own roll per attribute" convention as `game-rules.md`'s Combined Tests rule.

---

## Melee attacks

The player rolls a Melee Dexterity test (**Tmd**) and a Strength test (**Tstr**) — which specialized strength depends on the weapon's attack type:

- **Estocada (thrust)** attacks read **Upper Strike** (`MeleeForceAttribute.UPPER_STRIKE`).
- Every other attack type (chop, slice, crush, tear, lash, etc.) reads **Swing Power** (`MeleeForceAttribute.SWING_POWER`).
- No entry in the current table uses **Leg Drive** — it stays in `MeleeForceAttribute` for a future unarmed/kick profile.

**Hit check:** `Tmd > 40` (or `> 55` for long-hafted weapons — spears, tridents, naginatas, war scythes, halberds — flagged via `MeleeAttackProfile.isLongHafted()`). This makes it effectively impossible to miss an adjacent target barring severe debilitation.

**Raw damage:** `Db = (Tstr × Mf) + (Tmd × Fp)`, where `Mf` (Force Multiplier) and `Fp` (Proficiency Factor) come from the weapon/attack-type's `MeleeAttackProfile` entry.

`MeleeAttackResolver.resolveHit(tmd, profile)` / `.resolveRawDamage(tstr, tmd, profile)` implement these. `MeleeAttackProfile` has 45 constants (one per weapon/attack-type combination from the design table), each carrying `damageType`, `forceAttribute`, `forceMultiplier` (Mf), `proficiencyFactor` (Fp), `areaOfEffect` (Ae), and `longHafted`.

---

## Thrown attacks

The player rolls an Aim test (**Taim**) and a Swing Power test (**Tsp**).

**Hit check:** `Taim > 45 + distance(m) + Whnd` (Weapon Handling — 0 for weapons designed to be thrown, higher for improvised objects). A person of average skill will not miss a throw at a target within 5m unless throwing an improper object or debilitated.

**Raw damage:** `Db = (Tsp × Mf) + (Taim × Fp) − (Ded × distance)`, where `Ded` (Energy Degradation per Distance) is how much the throw loses per meter traveled.

**Maximum range:** the moment `Ded × distance` exceeds `(Tsp × Mf) + (Taim × Fp)`, the object has lost all its energy and falls to the ground — `ThrownAttackResolver.isWithinRange(...)` checks this; `.resolveRawDamage(...)` floors at 0 past that point rather than going negative. `ThrownWeaponProfile` has 4 constants (throwing knife, throwing hatchet, javelin, thrown rock).

---

## Firearm / trigger-weapon attacks

Crossbows (historical) and firearms (historical and modern) share one formula — grouped as "trigger weapons" since neither rolls a Strength attribute at the moment of firing (a crossbow's draw strength was already spent loading it). The player rolls only an Aim test (**Taim**).

**Hit check:** `Taim > Whnd + 40 + distance(m)`.

**Raw damage:** `Db = Wdmg − (Ded × distance)` — no attribute involved, `Wdmg` (Weapon Damage) is a flat per-weapon value.

**Maximum range:** same "loses all energy" rule as thrown weapons, at the point `Ded × distance > Wdmg`. **Modern firearms have `Ded = 0`** (the design table's `"-"` entries) — they never fall off within this game's scale (listed range "1000+m"), so `FirearmAttackResolver.isWithinRange(...)` always returns `true` for them. `FirearmProfile` has 9 constants: 4 historical (hand crossbow, heavy crossbow, black-powder pistol, heavy musket) and 5 modern (9mm pistol, .357 Magnum revolver, 5.56 assault rifle, .308 sniper rifle, buckshot shotgun).

---

## Bow attacks

The player rolls an Aim test (**Taim**) and a Pull Strength test (**Tpst**).

**Draw check (before the hit check):** a bow has a minimum pull (**Tmin**) — if `Tpst` is not strictly greater than `Tmin`, the character fails to draw the string at all. `BowAttackResolver.canDrawBow(tpst, profile)`.

**Hit check:** `Taim > 40 + distance(m)`.

**Effective pull strength:** `Tpstm = min(Tpst, Tmax)` — a bow also has a maximum pull (**Tmax**); the character can never apply more draw force than the bow's own physical limit, no matter how high their Pull Strength roll is. **This corrects the original design note's literal "highest value between Tmax or Tpst" wording** — confirmed with the user (2026-07-20) that the intent is the cap described in the very same sentence ("the character can only pull the string up to its limit"), not the literal maximum.

**Raw damage:** `Db = (Tpstm × Mf) − (Ded × distance)`.

**Maximum range:** same "loses all energy" rule, at the point `Ded × distance > Tpstm × Mf`. `BowProfile` has 5 constants (short bow, recurve bow, composite bow, longbow, flight bow).

---

## Evasion reaction

**Clarifies `game-rules.md`'s "Resolving a hit's damage value" section (2026-07-20).** The fixed-threshold hit checks above (`Tmd > 40`, etc.) are the complete attacker-side resolution — they do **not** involve the target's Evasion. Evasion only enters as the target's own *optional reaction*, and only if they are at rest (their combat clock is at or below the attacker's, per `game-rules.md`'s "Reactions" rule).

If the target chooses to evade: they roll their own Evasion via a Special Attack Test (`Evasion - 10 + 1d20`) and it is compared **directly against the attacker's already-resolved attack-test result** — the same Tmd/Taim/Tpst-derived roll the hit check used, not a fresh attacker roll. `EvasionResolver.resolve(attackTestResult, evasionTestResult)` returns one of three `EvasionOutcome`s:

- **`EVADED`** — the target's roll was higher; the attack misses entirely.
- **`GRAZING_HIT`** — a tie; the blow grazes the target. **This deliberately does not follow `game-rules.md`'s general Contested Tests tie rule** (resister wins) — here a tie instead halves the final damage. `EvasionResolver.applyToFinalDamage(finalDamage, outcome)` implements the halving.
- **`FULL_HIT`** — the attacker's roll was higher; the attack lands at full damage, unchanged.

The "is the target even eligible to evade" check (clock comparison) is a combat-timing concern and intentionally lives outside `EvasionResolver` — same separation-of-concerns precedent as `CombatActionTimeCalculator` owning UT costs independently of attack resolution.

## Protection and final damage

Once a raw damage value (Db) is known, it is checked against the struck object's protection:

```
P = Vb × Md × Dm
```

- **Vb** (Material Base Value) and **Md** (the material's per-`DamageType` modifier) come straight from the existing `Material` enum (rpg-21) — no new material catalog needed.
- **Dm** (Material Dimension) is new: an arbitrary per-equipment multiplier, carried by the new `ArmorProtection` enum (10 constants: common clothes, gambeson, light/hardened leather armor, chainmail, bronze/steel breastplate, legendary plate armor, wooden buckler, infantry tower shield). `ProtectionCalculator.calculate(armor, damageType)` implements this.

```
FinalDamage = (Db − P) × Ae   if Db > P
FinalDamage = 0                otherwise
```

**Ae** (Area of Effect) measures the severity of the wound once protection is overcome, and comes from the weapon/attack profile (not the target) — every `*AttackProfile`/`*WeaponProfile` enum above carries its own `areaOfEffect`. `DamageResolver.finalDamage(rawDamage, protection, areaOfEffect)` implements this.

**This closes `game-rules.md`'s previously-open "damage vs. resistance" `*TODO*`** (2026-07-08, "the calculation that turns (raw damage, Material.baseDurability, Material.getMultiplier(damageType)) into one of the three categories... is explicitly deferred") — `FinalDamage` is exactly that missing computation. It stays compatible with the existing Irrelevant/Significant/Irreversible framing: `FinalDamage == 0` is Irrelevant; a nonzero `FinalDamage` that keeps the object under its irreversible-damage limit is Significant (and still accumulates across hits, per that section's existing rule); a `FinalDamage` that pushes the object over its limit is Irreversible. **Still open:** the bleeding/Integrity periodic-drain mechanic that was cross-referenced against this same TODO (`game-rules.md`'s Physical Integrity section) is not implemented by this delta — it now has the "post-resistance damage value" it needed (`FinalDamage`), but the drain-per-tick wiring itself is future work.

---

## Deferred inputs — Bellicose, Fighting, WeaponPracticing, Fencing

`CloseCombat` and `LowRangeCombat` (both deleted, see `additive-attribute-formulas.md`'s Removal section) used to read the `Bellicose` trait, `Fighting` (training), `WeaponPracticing` (training), and `Fencing` (Knowledge). These four inputs are **not** wired into `MeleeDexterity`'s own additive formula. Per explicit user decision (2026-07-20), they are intended to become **situational modifiers on individual Special Attack Test rolls** instead — e.g. `Fencing` boosting the Tmd roll specifically for sword-type attacks, not every melee attack uniformly. This is documented intent only: none of the four attack resolvers above read any of these inputs yet, and no per-weapon/per-attack-type modifier mechanism exists. Do not implement this until the user defines which inputs modify which specific weapons/attack types and by how much.

---

## Extending this table

Adding a new weapon/attack-type entry: add one constant to the relevant `*AttackProfile`/`*WeaponProfile`/`ArmorProtection` enum with its damage type and coefficients — no resolver code changes needed, since every resolver reads coefficients off the profile rather than switching on specific constants. If a new weapon needs a genuinely new coefficient (not already modeled by `Mf`/`Fp`/`Ded`/`Whnd`/`Wdmg`/`Tmin`/`Tmax`/`Ae`/`Dm`), that is a formula-shape change and needs the user's input first, same as any other additive-standard extension.

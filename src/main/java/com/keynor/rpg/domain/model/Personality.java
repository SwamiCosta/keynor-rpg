package com.keynor.rpg.domain.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Third data group of the {@link Mind} pillar, added in rpg-19: the Values-linked {@link Trait}s
 * a character has selected. Unlike {@link Erudition}, there is no shared point budget here — each
 * trait is gated purely by its own prerequisite ({@link Trait#prerequisitesMet(PlayableCharacter)}
 * — a concern sitting at its default value, or the pair's base trait already being selected).
 *
 * <p>Selecting a base trait permanently forces its linked {@link Values} field to 0 (see
 * {@link Trait#applyForcedValue(Values)}) — this is a one-way personality commitment, not a
 * reversible slider tweak, so {@link #select(Trait, PlayableCharacter)} applies it immediately
 * and {@link #deselect(Trait)} does not undo it.
 */
public class Personality {

    private final Set<Trait> selectedTraits;

    public Personality(Set<Trait> selectedTraits) {
        this.selectedTraits = new LinkedHashSet<>(selectedTraits);
    }

    public static Personality defaults() {
        return new Personality(Set.of());
    }

    public boolean canSelect(Trait trait, PlayableCharacter character) {
        return selectedTraits.contains(trait) || trait.prerequisitesMet(character);
    }

    public void select(Trait trait, PlayableCharacter character) {
        if (!canSelect(trait, character)) {
            throw new IllegalStateException("Cannot select trait " + trait + ": prerequisites not met");
        }
        selectedTraits.add(trait);
        trait.applyForcedValue(character.getMind().getValues());
    }

    public void deselect(Trait trait) {
        selectedTraits.remove(trait);
    }

    public boolean hasTrait(Trait trait) {
        return selectedTraits.contains(trait);
    }

    public Set<Trait> getSelectedTraits() {
        return Collections.unmodifiableSet(selectedTraits);
    }

    /** Sum of every selected trait's {@link Trait#getKnowledgePointsModifier()}. */
    public int getKnowledgePointsModifier() {
        return selectedTraits.stream().mapToInt(Trait::getKnowledgePointsModifier).sum();
    }

    /** Sum of every selected trait's {@link Trait#getLabourPointsModifier()}. */
    public int getLabourPointsModifier() {
        return selectedTraits.stream().mapToInt(Trait::getLabourPointsModifier).sum();
    }
}

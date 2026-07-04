package com.keynor.rpg.domain.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Second data group of the {@link Mind} pillar: the knowledge {@link Trait}s a character has
 * selected. A character gets {@link #FREE_TRAIT_SLOTS} knowledge traits for free; selecting
 * beyond that is documented intent only (would spend from {@link Mind#getEventPoints()} once
 * per-trait costs are defined, the same deferred-cost precedent as the genetic/training pools)
 * — not implemented yet, so {@link #canSelect(Trait)} simply caps selection at the free slots.
 *
 * <p>Every {@link Trait} constant today belongs to Erudition; if a future pillar introduces
 * traits of its own, this class should only ever hold the subset that are actually knowledge
 * traits (currently all of them).
 */
public class Erudition {

    public static final int FREE_TRAIT_SLOTS = 2;

    private final Set<Trait> selectedTraits;

    public Erudition(Set<Trait> selectedTraits) {
        this.selectedTraits = new LinkedHashSet<>(selectedTraits);
    }

    public static Erudition defaults() {
        return new Erudition(Set.of());
    }

    /**
     * A trait can be selected if it is already selected (no-op), or a free slot remains and its
     * prerequisites are met.
     */
    public boolean canSelect(Trait trait, PlayableCharacter character) {
        if (selectedTraits.contains(trait)) {
            return true;
        }
        return selectedTraits.size() < FREE_TRAIT_SLOTS && trait.prerequisitesMet(character);
    }

    public void select(Trait trait, PlayableCharacter character) {
        if (!canSelect(trait, character)) {
            throw new IllegalStateException("Cannot select trait " + trait + ": no free slot or prerequisites not met");
        }
        selectedTraits.add(trait);
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
}

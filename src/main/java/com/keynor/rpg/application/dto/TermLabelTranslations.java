package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Language;
import java.util.Map;

/**
 * Portuguese translations for every {@code AttributeBreakdown.Term} label used across
 * {@code PlayableCharacter}'s formulas — the only backend-sourced text in this project today
 * (rpg-23, i18n). English labels are the literal strings already baked into the domain layer's
 * formula code (rpg-21); this table translates them at the response layer, in
 * {@link AttributeBreakdownResponse#from(com.keynor.rpg.domain.model.AttributeBreakdown, Language)},
 * without touching {@code PlayableCharacter.java} itself. If a label isn't found here for `PT`,
 * the English label is used as a fallback (should only happen if a new formula term ships without
 * a matching translation added in the same delta — see the mandatory-sync note on
 * {@code additive-attribute-formulas.md}).
 */
final class TermLabelTranslations {

    private TermLabelTranslations() {
    }

    private static final Map<String, String> EN_TO_PT = Map.ofEntries(
            Map.entry("Adrenal Glands", "Glândulas Adrenais"),
            Map.entry("Agility", "Agilidade"),
            Map.entry("Amygdala and Cingulum", "Amígdala e Cíngulo"),
            Map.entry("Anarchist", "Anarquista"),
            Map.entry("Anti Naturalist", "Anti-Naturalista"),
            Map.entry("Archery", "Arco e Flecha"),
            Map.entry("Art", "Arte"),
            Map.entry("Astral Atrium", "Átrio Astral"),
            Map.entry("Astral Ventriculum", "Ventrículo Astral"),
            Map.entry("Backstabber", "Traidor"),
            Map.entry("Bellicose", "Belicoso"),
            Map.entry("Biology", "Biologia"),
            Map.entry("Blood Thickness", "Espessura do Sangue"),
            Map.entry("Body Fat", "Gordura Corporal"),
            Map.entry("Bone Density", "Densidade Óssea"),
            Map.entry("Cardiac Output", "Débito Cardíaco"),
            Map.entry("Cellular Health", "Saúde Celular"),
            Map.entry("Cerebral Capacity", "Capacidade Cerebral"),
            Map.entry("Chemistry", "Química"),
            Map.entry("Clean Vessel", "Vaso Puro"),
            Map.entry("Computer Science", "Ciência da Computação"),
            Map.entry("Conservative", "Conservador"),
            Map.entry("Coordination", "Coordenação"),
            Map.entry("Dancing", "Dança"),
            Map.entry("Digestive Absorption", "Absorção Digestiva"),
            Map.entry("Discretion", "Discrição"),
            Map.entry("Divinity", "Divindade"),
            Map.entry("Dog Eat Dog", "Lei da Selva"),
            Map.entry("Dominant", "Dominante"),
            Map.entry("Ears Sensitivity", "Sensibilidade Auditiva"),
            Map.entry("Ecology", "Ecologia"),
            Map.entry("Ectomorphy", "Ectomorfia"),
            Map.entry("Ego", "Ego"),
            Map.entry("Endomorphy", "Endomorfia"),
            Map.entry("Engineering", "Engenharia"),
            Map.entry("Expatriated", "Expatriado"),
            Map.entry("Eyes Sensitivity", "Sensibilidade Visual"),
            Map.entry("Fencing", "Esgrima"),
            Map.entry("Fiber Type", "Tipo de Fibra"),
            Map.entry("Fighting", "Luta"),
            Map.entry("Flexibility", "Flexibilidade"),
            Map.entry("Focus", "Foco"),
            Map.entry("Freedom", "Liberdade"),
            Map.entry("Height", "Altura"),
            Map.entry("Hippocampus", "Hipocampo"),
            Map.entry("Hypothalamus", "Hipotálamo"),
            Map.entry("Illiterate", "Analfabeto"),
            Map.entry("Immunity", "Imunidade"),
            Map.entry("Impurity Cleaning", "Limpeza de Impurezas"),
            Map.entry("Intensity", "Intensidade"),
            Map.entry("Inventor", "Inventor"),
            Map.entry("Justice", "Justiça"),
            Map.entry("Ketosis Efficiency", "Eficiência de Cetose"),
            Map.entry("Knowledge", "Conhecimento"),
            Map.entry("Leg Drive", "Impulso de Pernas"),
            Map.entry("Limb Ratio", "Proporção de Membros"),
            Map.entry("Lone Wolf", "Lobo Solitário"),
            Map.entry("Loyalty", "Lealdade"),
            Map.entry("Mass Penalty", "Penalidade de Massa"),
            Map.entry("Mass", "Massa"),
            Map.entry("Medicine", "Medicina"),
            Map.entry("Mesomorphy", "Mesomorfia"),
            Map.entry("Morality", "Moralidade"),
            Map.entry("Muscle Distribution", "Distribuição Muscular"),
            Map.entry("Muscle Mass", "Massa Muscular"),
            Map.entry("Nature", "Natureza"),
            Map.entry("Neural Drive", "Impulso Neural"),
            Map.entry("Neuromuscular Efficiency", "Eficiência Neuromuscular"),
            Map.entry("Nihilist", "Niilista"),
            Map.entry("Noetic Plexus", "Plexo Noético"),
            Map.entry("Nose Sensitivity", "Sensibilidade Olfativa"),
            Map.entry("Organization", "Organização"),
            Map.entry("Orphan Mind", "Mente Órfã"),
            Map.entry("Outdoor Lifestyle", "Estilo de Vida ao Ar Livre"),
            Map.entry("Oxygen Carrying Capacity", "Capacidade de Transporte de Oxigênio"),
            Map.entry("Pagan", "Pagão"),
            Map.entry("Past Eraser", "Apagador do Passado"),
            Map.entry("Peace", "Paz"),
            Map.entry("Peacekeeper", "Pacificador"),
            Map.entry("Phaxic Cerebelum", "Cerebelo Fáxico"),
            Map.entry("Philosopher", "Filósofo"),
            Map.entry("Possessive", "Possessivo"),
            Map.entry("Practicalist", "Praticalista"),
            Map.entry("Precision", "Precisão"),
            Map.entry("Profane", "Profano"),
            Map.entry("Progesterone Modifier", "Modificador de Progesterona"),
            Map.entry("Progress", "Progresso"),
            Map.entry("Protagonist", "Protagonista"),
            Map.entry("Pulmonary Capacity", "Capacidade Pulmonar"),
            Map.entry("Realitic", "Realista"),
            Map.entry("Reasoning", "Raciocínio"),
            Map.entry("Reflexes", "Reflexos"),
            Map.entry("Relativist", "Relativista"),
            Map.entry("Reliable", "Confiável"),
            Map.entry("Religion Practitioner", "Praticante Religioso"),
            Map.entry("Resilience", "Resiliência"),
            Map.entry("Self Sacrifice", "Autossacrifício"),
            Map.entry("Shape Aesthetics", "Estética Corporal"),
            Map.entry("Shooting", "Tiro"),
            Map.entry("Skin Thickness", "Espessura da Pele"),
            Map.entry("Society", "Sociedade"),
            Map.entry("Subtle Epiphyseal Gland", "Glândula Epifisária Sutil"),
            Map.entry("Suicidal", "Suicida"),
            Map.entry("Synapsis Quality", "Qualidade Sináptica"),
            Map.entry("Tendons and Ligaments", "Tendões e Ligamentos"),
            Map.entry("Testosterone Modifier", "Modificador de Testosterona"),
            Map.entry("Thalamus", "Tálamo"),
            Map.entry("Thyroid", "Tireoide"),
            Map.entry("Tradition", "Tradição"),
            Map.entry("Truth", "Verdade"),
            Map.entry("Vanity", "Vaidade"),
            Map.entry("Vigor", "Vigor"),
            Map.entry("Weapon Practicing", "Treino com Armas"),
            Map.entry("Wizardry", "Feitiçaria")
    );

    static String translate(String englishLabel, Language language) {
        if (language == Language.EN) {
            return englishLabel;
        }
        return EN_TO_PT.getOrDefault(englishLabel, englishLabel);
    }
}

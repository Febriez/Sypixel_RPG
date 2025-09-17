package com.febrie.rpg.util.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Job-related language key enum
 */
public enum JobLangKey implements ILangKey {

    // Job messages
    JOB_SELECTED("job.selected", "You have selected the %s class!"),
    JOB_ALREADY_HAS("job.already_has", "You already have this class!"),
    JOB_LEVEL_UP("job.level_up", "Your job level has increased to %d!"),
    JOB_MAX_LEVEL("job.max_level", "You have reached the maximum job level!"),
    JOB_REQUIREMENT_NOT_MET("job.requirement_not_met", "You do not meet the requirements for this job!"),
    JOB_SKILL_LEARNED("job.skill_learned", "You have learned the skill: %s"),
    JOB_SKILL_UPGRADED("job.skill_upgraded", "Skill upgraded: %s (Level %d)"),
    JOB_SKILL_MAX_LEVEL("job.skill_max_level", "This skill is already at maximum level!"),
    JOB_NOT_ENOUGH_SKILL_POINTS("job.not_enough_skill_points", "Not enough skill points!");

    private final String key;
    private final String defaultValue;

    JobLangKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    JobLangKey(String key) {
        this.key = key;
        this.defaultValue = "";
    }

    @Override
    @NotNull
    public String key() {
        return key;
    }

    @Override
    @NotNull
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the language key for a job's name
     * @param jobKey The job key (e.g., "warrior", "mage", "archer")
     * @return The corresponding ILangKey
     */
    public static ILangKey jobName(@NotNull String jobKey) {
        return new DynamicLangKey("job.job." + jobKey.toLowerCase() + ".name");
    }

    /**
     * Get the language key for a job's description
     * @param jobKey The job key (e.g., "warrior", "mage", "archer")
     * @return The corresponding ILangKey
     */
    public static ILangKey jobDescription(@NotNull String jobKey) {
        return new DynamicLangKey("job.job." + jobKey.toLowerCase() + ".description");
    }

    /**
         * Dynamic language key implementation for runtime-generated keys
         */
        private record DynamicLangKey(String key) implements ILangKey {

        @Override
            @NotNull
            public String key() {
                return key;
            }

            @Override
            @NotNull
            public String getDefaultValue() {
                return "";
            }
        }
}
package com.febrie.rpg.util.lang;

/**
 * Island-related language keys
 */
public enum IslandLangKey implements ILangKey {
    // Island creation biomes
    ISLAND_GUI_CREATION_BIOMES_PLAINS("island.gui.creation.biomes.plains", "Plains"),
    ISLAND_GUI_CREATION_BIOMES_FOREST("island.gui.creation.biomes.forest", "Forest"),
    ISLAND_GUI_CREATION_BIOMES_DESERT("island.gui.creation.biomes.desert", "Desert"),
    ISLAND_GUI_CREATION_BIOMES_JUNGLE("island.gui.creation.biomes.jungle", "Jungle"),
    ISLAND_GUI_CREATION_BIOMES_TAIGA("island.gui.creation.biomes.taiga", "Taiga"),
    ISLAND_GUI_CREATION_BIOMES_SNOWY_PLAINS("island.gui.creation.biomes.snowy_plains", "Snowy Plains"),
    ISLAND_GUI_CREATION_BIOMES_SAVANNA("island.gui.creation.biomes.savanna", "Savanna"),
    ISLAND_GUI_CREATION_BIOMES_SWAMP("island.gui.creation.biomes.swamp", "Swamp"),
    ISLAND_GUI_CREATION_BIOMES_MUSHROOM_FIELDS("island.gui.creation.biomes.mushroom_fields", "Mushroom Fields"),
    ISLAND_GUI_CREATION_BIOMES_BEACH("island.gui.creation.biomes.beach", "Beach"),
    ISLAND_GUI_CREATION_BIOMES_FLOWER_FOREST("island.gui.creation.biomes.flower_forest", "Flower Forest"),
    ISLAND_GUI_CREATION_BIOMES_CHERRY_GROVE("island.gui.creation.biomes.cherry_grove", "Cherry Grove"),
    ISLAND_GUI_CREATION_BIOMES_OCEAN("island.gui.creation.biomes.ocean", "Ocean"),

    // Island creation templates
    ISLAND_GUI_CREATION_TEMPLATES_BASIC_NAME("island.gui.creation.templates.basic.name", "Basic Island"),
    ISLAND_GUI_CREATION_TEMPLATES_BASIC_DESC("island.gui.creation.templates.basic.desc", "A simple starting island"),
    ISLAND_GUI_CREATION_TEMPLATES_SKYBLOCK_NAME("island.gui.creation.templates.skyblock.name", "SkyBlock Island"),
    ISLAND_GUI_CREATION_TEMPLATES_SKYBLOCK_DESC("island.gui.creation.templates.skyblock.desc", "Classic skyblock island with limited resources"),
    ISLAND_GUI_CREATION_TEMPLATES_LARGE_NAME("island.gui.creation.templates.large.name", "Large Island"),
    ISLAND_GUI_CREATION_TEMPLATES_LARGE_DESC("island.gui.creation.templates.large.desc", "Spacious island with plenty of room"),
    ISLAND_GUI_CREATION_TEMPLATES_WATER_NAME("island.gui.creation.templates.water.name", "Water Island"),
    ISLAND_GUI_CREATION_TEMPLATES_WATER_DESC("island.gui.creation.templates.water.desc", "Island surrounded by water features"),

    // Island creation colors
    ISLAND_GUI_CREATION_COLORS_RED("island.gui.creation.colors.red", "Red"),
    ISLAND_GUI_CREATION_COLORS_BLUE("island.gui.creation.colors.blue", "Blue"),
    ISLAND_GUI_CREATION_COLORS_GREEN("island.gui.creation.colors.green", "Green"),
    ISLAND_GUI_CREATION_COLORS_YELLOW("island.gui.creation.colors.yellow", "Yellow"),
    ISLAND_GUI_CREATION_COLORS_PURPLE("island.gui.creation.colors.purple", "Purple"),
    ISLAND_GUI_CREATION_COLORS_ORANGE("island.gui.creation.colors.orange", "Orange"),
    ISLAND_GUI_CREATION_COLORS_WHITE("island.gui.creation.colors.white", "White"),
    ISLAND_GUI_CREATION_COLORS_BLACK("island.gui.creation.colors.black", "Black"),
    ISLAND_GUI_CREATION_COLORS_GRAY("island.gui.creation.colors.gray", "Gray"),
    ISLAND_GUI_CREATION_COLORS_LIGHT_GRAY("island.gui.creation.colors.light_gray", "Light Gray"),
    ISLAND_GUI_CREATION_COLORS_CYAN("island.gui.creation.colors.cyan", "Cyan"),
    ISLAND_GUI_CREATION_COLORS_PINK("island.gui.creation.colors.pink", "Pink"),
    ISLAND_GUI_CREATION_COLORS_LIME("island.gui.creation.colors.lime", "Lime"),
    ISLAND_GUI_CREATION_COLORS_BROWN("island.gui.creation.colors.brown", "Brown"),
    ISLAND_GUI_CREATION_COLORS_LIGHT_BLUE("island.gui.creation.colors.light_blue", "Light Blue"),
    ISLAND_GUI_CREATION_COLORS_MAGENTA("island.gui.creation.colors.magenta", "Magenta"),

    // Island general
    ISLAND_CONTRIBUTE_AMOUNT_TOO_LOW("island.contribute.amount_too_low", "Amount too low"),
    ISLAND_CONTRIBUTE_INVALID_AMOUNT("island.contribute.invalid_amount", "Invalid amount"),
    ISLAND_DEFAULT_NAME("island.default_name", "Island"),
    ISLAND_DELETE_CONFIRM_WORD("island.delete_confirm_word", "DELETE"),
    ISLAND_DELETE_INPUT_ERROR("island.delete_input_error", "Input error"),
    ISLAND_DELETE_INPUT_TEXT("island.delete_input_text", "Type DELETE to confirm"),
    ISLAND_DELETE_INPUT_TITLE("island.delete_input_title", "Delete Island"),
    ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TEXT("island.gui.contribute.contribution_input.text", "Enter amount"),
    ISLAND_GUI_CONTRIBUTE_CONTRIBUTION_INPUT_TITLE("island.gui.contribute.contribution_input.title", "Contribute"),
    ISLAND_GUI_CREATION_CLICK_TO_SELECT("island.gui.creation.click_to_select", "Click to select"),
    ISLAND_GUI_CREATION_SELECTED("island.gui.creation.selected", "Selected"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_CONTACT_ADMIN("island.gui.main.create_island.contact_admin", "Contact admin"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_DESCRIPTION("island.gui.main.create_island.description", "Create your own island"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_FEATURE_("island.gui.main.create_island.feature_", "Feature"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_NO_ISLAND("island.gui.main.create_island.no_island", "No island"),
    ISLAND_GUI_MAIN_CREATE_ISLAND_TITLE("island.gui.main.create_island.title", "Create Island"),
    ISLAND_GUI_MAIN_TITLE("island.gui.main.title", "Island"),
    ISLAND_GUI_MAIN_TITLE_WITH_NAME("island.gui.main.title_with_name", "Island: %s"),
    ISLAND_MEMBER_KICK_CONFIRM_WORD("island.member_kick.confirm_word", "KICK"),
    ISLAND_MEMBER_KICK_INPUT_ERROR("island.member_kick.input_error", "Input error"),
    ISLAND_MEMBER_KICK_INPUT_TEXT("island.member_kick.input_text", "Type KICK to confirm"),
    ISLAND_MEMBER_KICK_INPUT_TITLE("island.member_kick.input_title", "Kick Member"),
    ISLAND_ROLES_MEMBER("island.roles.member", "Member"),
    ISLAND_ROLES_SUB_OWNER("island.roles.sub_owner", "Sub Owner"),
    ISLAND_ROLES_WORKER("island.roles.worker", "Worker"),

    // Island Settings
    ISLAND_GUI_CREATION_ISLAND_NAME_INPUT_TITLE("island.gui.creation.island_name_input.title", "Enter Island Name"),
    ISLAND_SETTINGS_NAME_ERROR("island.settings.name_error", "Invalid name"),
    ISLAND_SETTINGS_NAME_INPUT_ERROR("island.settings.name_input_error", "Name input error");

    private final String key;
    private final String defaultValue;

    IslandLangKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get biome lang key by biome name
     */
    public static ILangKey getBiomeKey(String biomeName) {
        String upperBiome = biomeName.toUpperCase();
        try {
            return IslandLangKey.valueOf("ISLAND_GUI_CREATION_BIOMES_" + upperBiome);
        } catch (IllegalArgumentException e) {
            // Return a default implementation if not found
            return new ILangKey() {
                @Override
                public String key() {
                    return "island.gui.creation.biomes." + biomeName.toLowerCase();
                }

                @Override
                public String getDefaultValue() {
                    return biomeName;
                }
            };
        }
    }

    /**
     * Get template name key by template name
     */
    public static ILangKey getTemplateNameKey(String templateName) {
        String upperTemplate = templateName.toUpperCase();
        try {
            return IslandLangKey.valueOf("ISLAND_GUI_CREATION_TEMPLATES_" + upperTemplate + "_NAME");
        } catch (IllegalArgumentException e) {
            return new ILangKey() {
                @Override
                public String key() {
                    return "island.gui.creation.templates." + templateName.toLowerCase() + ".name";
                }

                @Override
                public String getDefaultValue() {
                    return templateName;
                }
            };
        }
    }

    /**
     * Get template description key by template name
     */
    public static ILangKey getTemplateDescKey(String templateName) {
        String upperTemplate = templateName.toUpperCase();
        try {
            return IslandLangKey.valueOf("ISLAND_GUI_CREATION_TEMPLATES_" + upperTemplate + "_DESC");
        } catch (IllegalArgumentException e) {
            return new ILangKey() {
                @Override
                public String key() {
                    return "island.gui.creation.templates." + templateName.toLowerCase() + ".desc";
                }

                @Override
                public String getDefaultValue() {
                    return templateName + " island";
                }
            };
        }
    }

    /**
     * Get color key by color name
     */
    public static ILangKey getColorKey(String colorName) {
        String upperColor = colorName.toUpperCase();
        try {
            return IslandLangKey.valueOf("ISLAND_GUI_CREATION_COLORS_" + upperColor);
        } catch (IllegalArgumentException e) {
            return new ILangKey() {
                @Override
                public String key() {
                    return "island.gui.creation.colors." + colorName.toLowerCase();
                }

                @Override
                public String getDefaultValue() {
                    return colorName;
                }
            };
        }
    }
}
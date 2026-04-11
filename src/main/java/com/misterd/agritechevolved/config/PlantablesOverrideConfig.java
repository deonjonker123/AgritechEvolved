package com.misterd.agritechevolved.config;

import com.misterd.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantablesOverrideConfig {
    private static final Logger MAIN_LOGGER = LogUtils.getLogger();
    private static org.apache.logging.log4j.Logger ERROR_LOGGER = null;
    private static boolean HAS_LOGGED_ERRORS = false;
    private static Path ERROR_LOG_PATH = null;

    private static final Pattern TABLE_PATTERN     = Pattern.compile("\\[(\\w+)\\.([\\w]+)\\]");
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(.+)");
    private static final Pattern ARRAY_PATTERN     = Pattern.compile("\\[\\s*(.*)\\s*\\]");
    private static final Pattern STRING_PATTERN    = Pattern.compile("\"([^\"]*)\"");

    private static final Map<String, Integer> cropLineNumbers = new HashMap<>();
    private static final Map<String, Integer> treeLineNumbers = new HashMap<>();
    private static final Map<String, Integer> soilLineNumbers = new HashMap<>();

    private static void setupErrorLogger() {
        ERROR_LOGGER = LogManager.getLogger(PlantablesOverrideConfig.class);
    }

    private static synchronized void createLogFileIfNeeded() {
        if (HAS_LOGGED_ERRORS) return;
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String logFileName = "plantables_config_overrides_errors_" + timestamp + ".log";
            ERROR_LOG_PATH = FMLPaths.CONFIGDIR.get()
                    .resolve("agritechevolved/plantables_overrides/plantables_config_logs")
                    .resolve(logFileName);
            Files.createDirectories(ERROR_LOG_PATH.getParent());

            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            LoggerConfig existing = config.getLoggerConfig("PlantablesOverrideErrorLogger");
            if (existing != null) {
                existing.getAppenders().forEach((name, appender) -> {
                    existing.removeAppender(name);
                    appender.stop();
                });
                config.removeLogger("PlantablesOverrideErrorLogger");
            }

            PatternLayout layout = PatternLayout.newBuilder()
                    .withPattern("%d{yyyy-MM-dd HH:mm:ss} [%p] %m%n")
                    .build();
            FileAppender appender = FileAppender.newBuilder()
                    .setName("PlantablesOverrideErrorAppender")
                    .withFileName(ERROR_LOG_PATH.toString())
                    .setLayout(layout)
                    .setConfiguration(config)
                    .build();
            appender.start();

            config.addAppender(appender);
            LoggerConfig loggerConfig = new LoggerConfig("PlantablesOverrideErrorLogger", Level.INFO, false);
            loggerConfig.addAppender(appender, Level.INFO, null);
            config.addLogger("PlantablesOverrideErrorLogger", loggerConfig);
            context.updateLoggers();

            ERROR_LOGGER = LogManager.getLogger("PlantablesOverrideErrorLogger");
            MAIN_LOGGER.info("Created override config error log file: {}", ERROR_LOG_PATH);
            HAS_LOGGED_ERRORS = true;
        } catch (Exception e) {
            MAIN_LOGGER.error("Failed to set up dedicated error logger: {}", e.getMessage());
        }
    }

    private static void logError(String message, Object... params) {
        createLogFileIfNeeded();
        ERROR_LOGGER.error(message, params);
    }

    private static void logWarning(String message, Object... params) {
        createLogFileIfNeeded();
        ERROR_LOGGER.warn(message, params);
    }

    public static void loadOverrides(
            Map<String, PlantablesConfig.CropInfo> crops,
            Map<String, PlantablesConfig.TreeInfo> trees,
            Map<String, PlantablesConfig.SoilInfo> cropSoils,
            Map<String, PlantablesConfig.SoilInfo> treeSoils,
            Map<String, PlantablesConfig.FertilizerInfo> fertilizers) {

        Path configDir = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/plantables_overrides");
        Path overridePath = configDir.resolve("plantables_config_overrides.toml");
        setupErrorLogger();

        if (!Files.exists(overridePath)) {
            createDefaultOverrideFile(configDir, overridePath);
        }

        try {
            MAIN_LOGGER.info("Loading plantables overrides from {}", overridePath);
            cropLineNumbers.clear();
            treeLineNumbers.clear();
            soilLineNumbers.clear();

            Map<String, Map<String, Map<String, Object>>> tables = parseTomlFile(overridePath);
            int cropCount       = processCropEntries(tables.getOrDefault("crops", Collections.emptyMap()), crops);
            int treeCount       = processTreeEntries(tables.getOrDefault("trees", Collections.emptyMap()), trees);
            int soilCount       = processSoilEntries(tables.getOrDefault("soils", Collections.emptyMap()), cropSoils, treeSoils);
            int fertilizerCount = processFertilizerEntries(tables.getOrDefault("fertilizers", Collections.emptyMap()), fertilizers);
            MAIN_LOGGER.info("Successfully loaded {} crop, {} tree, {} soil, and {} fertilizer overrides",
                    cropCount, treeCount, soilCount, fertilizerCount);
        } catch (Exception e) {
            MAIN_LOGGER.error("Failed to load override.toml file: {}", e.getMessage());
            logError("Failed to load override.toml file: {}", e.getMessage());
            logError("The override file will be ignored, but the mod will continue to function");
        }
    }

    private static Map<String, Map<String, Map<String, Object>>> parseTomlFile(Path filePath) throws IOException {
        Map<String, Map<String, Map<String, Object>>> result = new HashMap<>();
        String currentSection = null;
        Map<String, Map<String, Object>> currentSectionMap = null;
        Map<String, Object> currentTableMap = null;
        StringBuilder multilineValue = null;
        String pendingKey = null;
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                int commentPos = findUnquotedChar(line, '#');
                if (commentPos >= 0) line = line.substring(0, commentPos);
                line = line.trim();
                if (line.isEmpty()) continue;

                if (multilineValue != null) {
                    multilineValue.append(line);
                    if (countOccurrences(multilineValue.toString(), '[') == countOccurrences(multilineValue.toString(), ']')
                            && countOccurrences(multilineValue.toString(), '{') == countOccurrences(multilineValue.toString(), '}')) {
                        currentTableMap.put(pendingKey, parseValue(multilineValue.toString()));
                        multilineValue = null;
                        pendingKey = null;
                    }
                    continue;
                }

                Matcher tableMatcher = TABLE_PATTERN.matcher(line);
                if (tableMatcher.matches()) {
                    currentSection = tableMatcher.group(1);
                    String currentTable = tableMatcher.group(2);

                    switch (currentSection) {
                        case "crops" -> cropLineNumbers.put(currentTable, lineNumber);
                        case "trees" -> treeLineNumbers.put(currentTable, lineNumber);
                        case "soils" -> soilLineNumbers.put(currentTable, lineNumber);
                    }

                    currentSectionMap = result.computeIfAbsent(currentSection, k -> new HashMap<>());
                    currentTableMap = currentSectionMap.computeIfAbsent(currentTable, k -> new HashMap<>());
                    continue;
                }

                if (currentTableMap == null) continue;

                Matcher kvMatcher = KEY_VALUE_PATTERN.matcher(line);
                if (kvMatcher.matches()) {
                    String key = kvMatcher.group(1);
                    String valueStr = kvMatcher.group(2).trim();
                    boolean balanced = countOccurrences(valueStr, '[') == countOccurrences(valueStr, ']')
                            && countOccurrences(valueStr, '{') == countOccurrences(valueStr, '}');
                    if ((!valueStr.startsWith("[") || valueStr.endsWith("]")) && balanced) {
                        currentTableMap.put(key, parseValue(valueStr));
                    } else {
                        multilineValue = new StringBuilder(valueStr);
                        pendingKey = key;
                    }
                }
            }
        }

        return result;
    }

    private static int findUnquotedChar(String str, char target) {
        boolean inQuotes = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            else if (c == target && !inQuotes) return i;
        }
        return -1;
    }

    private static int countOccurrences(String str, char target) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == target) count++;
        }
        return count;
    }

    private static Object parseValue(String valueStr) {
        if (valueStr.startsWith("[") && valueStr.endsWith("]") && valueStr.contains("{")) {
            List<Map<String, Object>> items = new ArrayList<>();
            String content = valueStr.substring(1, valueStr.length() - 1).trim();
            int startIdx = 0;
            while (startIdx < content.length()) {
                int openBrace = content.indexOf('{', startIdx);
                if (openBrace == -1) break;
                int closeBrace = findMatchingCloseBrace(content, openBrace);
                if (closeBrace == -1) break;
                items.add(parseObject(content.substring(openBrace + 1, closeBrace).trim()));
                startIdx = closeBrace + 1;
            }
            return items;
        }

        if (valueStr.startsWith("[") && valueStr.endsWith("]")) {
            Matcher m = ARRAY_PATTERN.matcher(valueStr);
            if (m.matches()) {
                List<String> items = new ArrayList<>();
                Matcher sm = STRING_PATTERN.matcher(m.group(1));
                while (sm.find()) items.add(sm.group(1));
                return items;
            }
        }

        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            return valueStr.substring(1, valueStr.length() - 1);
        }

        try {
            return valueStr.contains(".") ? Double.parseDouble(valueStr) : Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            if (valueStr.equalsIgnoreCase("true")) return true;
            if (valueStr.equalsIgnoreCase("false")) return false;
            return valueStr;
        }
    }

    private static int findMatchingCloseBrace(String content, int openBracePos) {
        int depth = 0;
        for (int i = openBracePos; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                if (--depth == 0) return i;
            }
        }
        return -1;
    }

    private static Map<String, Object> parseObject(String objectStr) {
        Map<String, Object> result = new HashMap<>();
        for (String part : objectStr.split(",")) {
            part = part.trim();
            if (part.isEmpty()) continue;
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String valueStr = kv[1].trim();
            Object value;
            if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
                value = valueStr.substring(1, valueStr.length() - 1);
            } else if (valueStr.equals("true")) {
                value = true;
            } else if (valueStr.equals("false")) {
                value = false;
            } else {
                try {
                    value = valueStr.contains(".") ? Double.parseDouble(valueStr) : Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    value = valueStr;
                }
            }
            result.put(key, value);
        }
        return result;
    }

    private static int processCropEntries(Map<String, Map<String, Object>> entries, Map<String, PlantablesConfig.CropInfo> crops) {
        return processPlantEntries(entries, crops, null, cropLineNumbers, "Crop", "seed");
    }

    private static int processTreeEntries(Map<String, Map<String, Object>> entries, Map<String, PlantablesConfig.TreeInfo> trees) {
        return processPlantEntries(entries, null, trees, treeLineNumbers, "Tree", "sapling");
    }

    private static String lineInfo(Map<String, Integer> lineNumbers, String name) {
        int n = lineNumbers.getOrDefault(name, -1);
        return n > 0 ? " (line " + n + ")" : "";
    }

    private static <T> int processPlantEntries(
            Map<String, Map<String, Object>> plantEntries,
            Map<String, PlantablesConfig.CropInfo> crops,
            Map<String, PlantablesConfig.TreeInfo> trees,
            Map<String, Integer> lineNumbers,
            String plantType,
            String seedKey) {

        int count = 0;
        boolean isCrop = crops != null;

        for (Map.Entry<String, Map<String, Object>> entry : plantEntries.entrySet()) {
            String plantName = entry.getKey();
            Map<String, Object> plantConfig = entry.getValue();

            try {
                Object seedObj = plantConfig.get(seedKey);
                if (seedObj == null) {
                    String info = lineInfo(lineNumbers, plantName);
                    MAIN_LOGGER.warn("{} override '{}'{} is missing a {} ID, skipping", plantType, plantName, info, seedKey);
                    logWarning("{} override '{}'{} is missing a {} ID, skipping", plantType, plantName, info, seedKey);
                    continue;
                }

                String seedId = seedObj.toString();
                if (RegistryHelper.getItem(seedId) == null) {
                    String info = lineInfo(lineNumbers, plantName);
                    MAIN_LOGGER.warn("{} override '{}'{} uses non-existent {} item: {}, skipping", plantType, plantName, info, seedKey, seedId);
                    logWarning("{} override '{}'{} uses non-existent {} item: {}, skipping", plantType, plantName, info, seedKey, seedId);
                    continue;
                }

                // Merge soils into existing entry if present, otherwise start fresh
                List<String> existingValidSoils = isCrop
                        ? (crops.containsKey(seedId) ? crops.get(seedId).validSoils : new ArrayList<>())
                        : (trees.containsKey(seedId) ? trees.get(seedId).validSoils : new ArrayList<>());

                List<String> validSoils = new ArrayList<>(existingValidSoils);
                Object soilsObj = plantConfig.get("soil");
                if (soilsObj instanceof List<?> soilList) {
                    for (Object soilObj : soilList) {
                        String soilId = soilObj.toString();
                        if (RegistryHelper.getBlock(soilId) == null) {
                            String info = lineInfo(lineNumbers, plantName);
                            MAIN_LOGGER.warn("{} override '{}'{} references non-existent soil block: {}, skipping this soil", plantType, plantName, info, soilId);
                            logWarning("{} override '{}'{} references non-existent soil block: {}, skipping this soil", plantType, plantName, info, soilId);
                        } else if (!validSoils.contains(soilId)) {
                            validSoils.add(soilId);
                        }
                    }
                }

                if (validSoils.isEmpty()) {
                    String info = lineInfo(lineNumbers, plantName);
                    MAIN_LOGGER.warn("{} override '{}'{} has no valid soils, skipping", plantType, plantName, info);
                    logWarning("{} override '{}'{} has no valid soils, skipping", plantType, plantName, info);
                    continue;
                }

                // Use overridden drops if provided, otherwise fall back to existing drops
                List<PlantablesConfig.DropInfo> drops = null;
                if (plantConfig.containsKey("drops")) {
                    drops = processDrops(plantConfig, plantName, lineNumbers, plantType);
                    if (drops.isEmpty()) {
                        String info = lineInfo(lineNumbers, plantName);
                        MAIN_LOGGER.warn("{} override '{}'{} has no valid drops, skipping", plantType, plantName, info);
                        logWarning("{} override '{}'{} has no valid drops, skipping", plantType, plantName, info);
                        continue;
                    }
                }

                if (isCrop) {
                    PlantablesConfig.CropInfo cropInfo = crops.getOrDefault(seedId, new PlantablesConfig.CropInfo());
                    cropInfo.validSoils = validSoils;
                    if (drops != null) cropInfo.drops = drops;
                    crops.put(seedId, cropInfo);
                } else {
                    PlantablesConfig.TreeInfo treeInfo = trees.getOrDefault(seedId, new PlantablesConfig.TreeInfo());
                    treeInfo.validSoils = validSoils;
                    if (drops != null) treeInfo.drops = drops;
                    trees.put(seedId, treeInfo);
                }

                count++;
                MAIN_LOGGER.info("Added {} override for '{}' with {} {}", plantType.toLowerCase(), plantName, seedKey, seedId);

            } catch (Exception e) {
                String info = lineInfo(lineNumbers, plantName);
                MAIN_LOGGER.error("Error processing {} override '{}'{}: {}", plantType.toLowerCase(), plantName, info, e.getMessage());
                logError("Error processing {} override '{}'{}: {}", plantType.toLowerCase(), plantName, info, e.getMessage());
            }
        }

        return count;
    }

    private static List<PlantablesConfig.DropInfo> processDrops(
            Map<String, Object> plantConfig,
            String plantName,
            Map<String, Integer> lineNumbers,
            String plantType) {

        Object dropsObj = plantConfig.get("drops");
        if (!(dropsObj instanceof List<?> dropsList)) return Collections.emptyList();

        int defaultMin      = plantConfig.get("min_count") instanceof Number n ? n.intValue()   : 1;
        int defaultMax      = plantConfig.get("max_count") instanceof Number n ? n.intValue()   : 1;
        float defaultChance = plantConfig.get("chance")    instanceof Number n ? n.floatValue() : 1.0F;

        List<PlantablesConfig.DropInfo> drops = new ArrayList<>();

        for (Object dropObj : dropsList) {
            int minCount  = defaultMin;
            int maxCount  = defaultMax;
            float chance  = defaultChance;
            String dropId;

            if (dropObj instanceof Map<?, ?> rawMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> dropMap = (Map<String, Object>) rawMap;
                Object itemObj = dropMap.get("item");
                if (itemObj == null) {
                    String info = lineInfo(lineNumbers, plantName);
                    MAIN_LOGGER.warn("{} override '{}'{} has drop without item ID, skipping", plantType, plantName, info);
                    logWarning("{} override '{}'{} has drop without item ID, skipping", plantType, plantName, info);
                    continue;
                }
                dropId = itemObj.toString();
                if (dropMap.get("min_count") instanceof Number n) minCount = n.intValue();
                if (dropMap.get("max_count") instanceof Number n) maxCount = n.intValue();
                if (dropMap.get("chance")    instanceof Number n) chance   = n.floatValue();
            } else {
                dropId = dropObj.toString();
            }

            if (RegistryHelper.getItem(dropId) == null) {
                String info = lineInfo(lineNumbers, plantName);
                MAIN_LOGGER.warn("{} override '{}'{} references non-existent drop item: {}, skipping this drop", plantType, plantName, info, dropId);
                logWarning("{} override '{}'{} references non-existent drop item: {}, skipping this drop", plantType, plantName, info, dropId);
                continue;
            }

            drops.add(new PlantablesConfig.DropInfo(dropId, minCount, maxCount, chance));
        }

        return drops;
    }

    private static int processSoilEntries(
            Map<String, Map<String, Object>> soilEntries,
            Map<String, PlantablesConfig.SoilInfo> cropSoils,
            Map<String, PlantablesConfig.SoilInfo> treeSoils) {

        int count = 0;

        for (Map.Entry<String, Map<String, Object>> entry : soilEntries.entrySet()) {
            String soilName = entry.getKey();
            Map<String, Object> soilConfig = entry.getValue();

            try {
                Object blockObj = soilConfig.get("block");
                if (blockObj == null) {
                    String info = lineInfo(soilLineNumbers, soilName);
                    MAIN_LOGGER.warn("Soil override '{}'{} is missing a block ID, skipping", soilName, info);
                    logWarning("Soil override '{}'{} is missing a block ID, skipping", soilName, info);
                    continue;
                }

                String soilId = blockObj.toString();
                if (RegistryHelper.getBlock(soilId) == null) {
                    String info = lineInfo(soilLineNumbers, soilName);
                    MAIN_LOGGER.warn("Soil override '{}'{} uses non-existent block: {}, skipping", soilName, info, soilId);
                    logWarning("Soil override '{}'{} uses non-existent block: {}, skipping", soilName, info, soilId);
                    continue;
                }

                float growthModifier = soilConfig.get("growth_modifier") instanceof Number n ? n.floatValue() : 1.0F;
                cropSoils.put(soilId, new PlantablesConfig.SoilInfo(growthModifier));
                treeSoils.put(soilId, new PlantablesConfig.SoilInfo(growthModifier));
                count++;
                MAIN_LOGGER.info("Added soil override for '{}' with block {}", soilName, soilId);

            } catch (Exception e) {
                String info = lineInfo(soilLineNumbers, soilName);
                MAIN_LOGGER.error("Error processing soil override '{}'{}: {}", soilName, info, e.getMessage());
                logError("Error processing soil override '{}'{}: {}", soilName, info, e.getMessage());
            }
        }

        return count;
    }

    private static int processFertilizerEntries(
            Map<String, Map<String, Object>> fertilizerEntries,
            Map<String, PlantablesConfig.FertilizerInfo> fertilizers) {

        int count = 0;

        for (Map.Entry<String, Map<String, Object>> entry : fertilizerEntries.entrySet()) {
            String fertilizerName = entry.getKey();
            Map<String, Object> fertilizerConfig = entry.getValue();

            try {
                Object itemObj = fertilizerConfig.get("item");
                if (itemObj == null) {
                    MAIN_LOGGER.warn("Fertilizer override '{}' is missing item ID", fertilizerName);
                    continue;
                }

                String itemId = itemObj.toString();
                if (RegistryHelper.getItem(itemId) == null) {
                    MAIN_LOGGER.warn("Fertilizer override '{}' uses non-existent item: {}", fertilizerName, itemId);
                    continue;
                }

                float speed = fertilizerConfig.get("speed_multiplier") instanceof Number n ? n.floatValue() : 1.2F;
                float yield = fertilizerConfig.get("yield_multiplier") instanceof Number n ? n.floatValue() : 1.2F;
                fertilizers.put(itemId, new PlantablesConfig.FertilizerInfo(speed, yield));
                count++;
                MAIN_LOGGER.info("Added fertilizer override for '{}' with item {}", fertilizerName, itemId);

            } catch (Exception e) {
                MAIN_LOGGER.error("Error processing fertilizer override '{}': {}", fertilizerName, e.getMessage());
            }
        }

        return count;
    }

    private static void createDefaultOverrideFile(Path configDir, Path overridePath) {
        try {
            Files.createDirectories(configDir);
            Files.writeString(overridePath, createBasicTemplate());
            MAIN_LOGGER.info("Created default override.toml file with examples at {}", overridePath);
        } catch (IOException e) {
            MAIN_LOGGER.error("Failed to create default override.toml file: {}", e.getMessage());
            if (HAS_LOGGED_ERRORS) {
                ERROR_LOGGER.error("Failed to create default override.toml file: {}", e.getMessage());
            }
        }
    }

    public static void resetErrorFlag() {
        HAS_LOGGED_ERRORS = false;
        ERROR_LOGGER = null;
        ERROR_LOG_PATH = null;
    }

    private static String createBasicTemplate() {
        return """
                # Plantables Override Configuration
                # This file allows you to add custom crops, trees, and soils without modifying the core configuration.
                # Any entries here will override existing configurations for the same items/blocks.

                # How to use:
                # 1. Add [crops.your_crop_name] sections for new crops or to override existing ones
                # 2. Add [trees.your_tree_name] sections for new trees or to override existing ones
                # 3. Add [soils.your_soil_name] sections for new soils or to override existing ones
                # 4. Save the file and restart your game

                # IMPORTANT: Make sure to verify the exact item and block IDs from your mods
                # Incorrect IDs will be skipped with a warning message in the log
                # The mod uses resource location format (e.g., "minecraft:dirt" not just "dirt")
                # The easiest way to check IDs is with F3+H enabled (shows tooltip IDs) or via JEI/REI

                # NOTES ON OVERRIDES:
                # - If a crop/tree already exists in the core config, its soils list will be EXTENDED,
                #   not replaced. Only the soils you specify here will be added.
                # - If you omit the 'drops' field entirely, the existing drops from the core config
                #   will be preserved. Only specify 'drops' if you want to change them.

                # Example crops:

                # [crops.example_wheat]
                # seed = "minecraft:wheat_seeds"
                # soil = [
                #   "examplemod:custom_soil"
                # ]
                # # Drops are optional — omit to keep the existing drops for this crop.
                # # 1. Simple drops with default settings:
                # drops = [
                #   "examplemod:wheat",
                #   "examplemod:wheat_seeds"
                # ]
                # min_count = 1  # Minimum drop count for all simple drops (default: 1)
                # max_count = 3  # Maximum drop count for all simple drops (default: 1)
                # chance = 0.75  # Drop chance for all simple drops (default: 1.0)

                # # 2. Detailed drops with individual settings (use this for multiple drops with different chances):
                # # [crops.example_wheat_advanced]
                # # seed = "examplemod:wheat_seeds"
                # # soil = ["minecraft:farmland"]
                # # drops = [
                # #   { item = "examplemod:wheat", min_count = 1, max_count = 3, chance = 1.0 },
                # #   { item = "examplemod:wheat_seeds", min_count = 1, max_count = 2, chance = 0.3 }
                # # ]

                # Example trees:

                # [trees.example_oak]
                # sapling = "examplemod:oak_sapling"
                # soil = [
                #   "minecraft:dirt",
                #   "examplemod:rich_soil"
                # ]
                # # Drops are optional — omit to keep the existing drops for this tree.
                # # 1. Simple drops with default settings:
                # drops = [
                #   "examplemod:oak_log",
                #   "examplemod:oak_sapling"
                # ]
                # min_count = 1
                # max_count = 3
                # chance = 0.75

                # # 2. Detailed drops:
                # # [trees.example_oak_advanced]
                # # sapling = "examplemod:oak_sapling"
                # # soil = ["minecraft:dirt"]
                # # drops = [
                # #   { item = "examplemod:oak_log", min_count = 2, max_count = 6, chance = 1.0 },
                # #   { item = "examplemod:oak_sapling", min_count = 1, max_count = 2, chance = 0.5 },
                # #   { item = "minecraft:apple", min_count = 1, max_count = 2, chance = 0.2 }
                # # ]

                # Example soils:

                # [soils.example_rich_soil]
                # block = "examplemod:rich_soil"
                # growth_modifier = 1.5  # Growth speed multiplier (default: 1.0)

                # Example fertilizers:

                # [fertilizers.bone_meal]
                # item = "minecraft:bone_meal"
                # speed_multiplier = 1.2  # 20% faster growth
                # yield_multiplier = 1.2  # 20% more drops

                # [fertilizers.biomass]
                # item = "agritechevolved:biomass"
                # speed_multiplier = 1.2
                # yield_multiplier = 1.2

                # [fertilizers.mystical_fertilizer]
                # item = "mysticalagriculture:mystical_fertilizer"
                # speed_multiplier = 1.2
                # yield_multiplier = 1.2
                """;
    }
}
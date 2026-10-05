package hexca.motet.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class TotemBaseRecipeGenerator {
    private static final List<WoodType> WOOD_TYPES = List.of(
            new WoodType("oak", "totemic:oak", "minecraft:oak_log", "minecraft:stripped_oak_log"),
            new WoodType("spruce", "totemic:spruce", "minecraft:spruce_log", "minecraft:stripped_spruce_log"),
            new WoodType("birch", "totemic:birch", "minecraft:birch_log", "minecraft:stripped_birch_log"),
            new WoodType("jungle", "totemic:jungle", "minecraft:jungle_log", "minecraft:stripped_jungle_log"),
            new WoodType("acacia", "totemic:acacia", "minecraft:acacia_log", "minecraft:stripped_acacia_log"),
            new WoodType("cherry", "totemic:cherry", "minecraft:cherry_log", "minecraft:stripped_cherry_log"),
            new WoodType("dark_oak", "totemic:dark_oak", "minecraft:dark_oak_log", "minecraft:stripped_dark_oak_log"),
            new WoodType("mangrove", "totemic:mangrove", "minecraft:mangrove_log", "minecraft:stripped_mangrove_log"),
            new WoodType("cedar", "totemic:cedar", "totemic:cedar_log", "totemic:stripped_cedar_log")
    );

    private TotemBaseRecipeGenerator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected: <template directory> <output directory>");
        }

        Path templateDirectory = Path.of(args[0]);
        Path outputDirectory = Path.of(args[1]);
        Files.createDirectories(outputDirectory);

        try (Stream<Path> templatePaths = Files.list(templateDirectory)) {
            List<Path> templates = templatePaths
                    .filter(path -> path.getFileName().toString().endsWith("_recipe.template.json"))
                    .sorted()
                    .toList();
            if (templates.isEmpty()) {
                throw new IllegalArgumentException("No *_recipe.template.json files found in " + templateDirectory);
            }

            for (Path templatePath : templates) {
                String template = Files.readString(templatePath);
                requireMarkers(template);
                String recipeName = templatePath.getFileName().toString()
                        .replaceFirst("_recipe\\.template\\.json$", "");

                for (WoodType woodType : WOOD_TYPES) {
                    String recipe = template
                            .replace("__WOOD_TYPE__", woodType.registryName())
                            .replace("__LOG_ITEM__", woodType.logItem())
                            .replace("__STRIPPED_LOG_ITEM__", woodType.strippedLogItem());
                    Files.writeString(outputDirectory.resolve(woodType.name() + "_" + recipeName + ".json"), recipe);
                }
            }
        }
    }

    private static void requireMarkers(String template) {
        for (String marker : List.of("__WOOD_TYPE__", "__LOG_ITEM__", "__STRIPPED_LOG_ITEM__")) {
            if (!template.contains(marker)) {
                throw new IllegalArgumentException("Recipe template is missing marker: " + marker);
            }
        }
    }

    private record WoodType(String name, String registryName, String logItem, String strippedLogItem) {
    }
}

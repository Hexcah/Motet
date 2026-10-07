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
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected: <template directory> <recipe output directory> <JEI manifest path>");
        }

        Path templateDirectory = Path.of(args[0]);
        Path outputDirectory = Path.of(args[1]);
        Path jeiManifestPath = Path.of(args[2]);
        Files.createDirectories(outputDirectory);
        List<String> generatedRecipes = new java.util.ArrayList<>();

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
                    String outputName = woodType.name() + "_" + recipeName + ".json";
                    Files.writeString(outputDirectory.resolve(outputName), recipe);
                    generatedRecipes.add("data/motet/recipe/" + outputName);
                }
            }
        }

        Files.createDirectories(jeiManifestPath.getParent());
        String manifestRecipes = String.join(",\n", generatedRecipes.stream()
                .map(path -> "    \"" + path + "\"")
                .toList());
        Files.writeString(jeiManifestPath, "{\n  \"recipes\": [\n" + manifestRecipes + "\n  ]\n}\n");
    }

    private static void requireMarkers(String template) {
        if (!template.contains("__WOOD_TYPE__")) {
            throw new IllegalArgumentException("Recipe template is missing marker: __WOOD_TYPE__");
        }
    }

    private record WoodType(String name, String registryName, String logItem, String strippedLogItem) {
    }
}

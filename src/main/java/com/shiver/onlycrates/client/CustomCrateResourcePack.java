package com.shiver.onlycrates.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.shiver.onlycrates.OnlyCrates;
import com.shiver.onlycrates.config.ModConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CustomCrateResourcePack implements IResourcePack {

    private static final String LARGE_CRATE_MODEL = "/assets/onlycrates/models/block/block_giant_chest_large.json";
    private static final String DEFAULT_SIDE_TEXTURE = "onlycrates:blocks/block_giant_chest_large";
    private static final String TOP_TEXTURE = "onlycrates:blocks/block_giant_chest_top";

    private static boolean registered;

    private final File texturesFolder;

    private CustomCrateResourcePack(File texturesFolder) {
        this.texturesFolder = texturesFolder;
    }

    public static void register() {
        if (registered || ModConfig.getExtraCrates().isEmpty() || ModConfig.getCustomTexturesFolder() == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        List<IResourcePack> packs = ObfuscationReflectionHelper.getPrivateValue(
                Minecraft.class,
                minecraft,
                "defaultResourcePacks",
                "field_110449_ao"
        );
        packs.add(new CustomCrateResourcePack(ModConfig.getCustomTexturesFolder()));
        registered = true;

        minecraft.refreshResources();
        OnlyCrates.LOGGER.info("Registered configurable crate texture folder: {}", ModConfig.getCustomTexturesFolder());
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        if (!OnlyCrates.MODID.equals(location.getNamespace())) {
            throw new ResourcePackFileNotFoundException(this.texturesFolder, location.toString());
        }

        String path = location.getPath();
        if (isGeneratedBlockstate(path)) {
            return stream(generatedBlockstate(cratePath(path, "blockstates/", ".json")));
        }
        if (isGeneratedModel(path)) {
            return stream(generatedModel(cratePath(path, "models/block/", ".json")));
        }
        if (isCustomTexture(path)) {
            return new FileInputStream(textureFile(path));
        }

        throw new ResourcePackFileNotFoundException(this.texturesFolder, location.toString());
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        if (!OnlyCrates.MODID.equals(location.getNamespace())) {
            return false;
        }

        String path = location.getPath();
        return isGeneratedBlockstate(path) || isGeneratedModel(path) || isCustomTexture(path);
    }

    @Override
    public Set<String> getResourceDomains() {
        return Collections.singleton(OnlyCrates.MODID);
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) {
        return null;
    }

    @Override
    public BufferedImage getPackImage() {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public String getPackName() {
        return "Only Crates Custom Textures";
    }

    private boolean isGeneratedBlockstate(String path) {
        return path.startsWith("blockstates/") && path.endsWith(".json") && isExtraCrate(cratePath(path, "blockstates/", ".json"));
    }

    private boolean isGeneratedModel(String path) {
        return path.startsWith("models/block/") && path.endsWith(".json") && isExtraCrate(cratePath(path, "models/block/", ".json"));
    }

    private boolean isCustomTexture(String path) {
        return path.startsWith("textures/") && path.endsWith(".png") && textureFile(path).isFile();
    }

    private File textureFile(String path) {
        String texturePath = path.substring("textures/".length());
        return new File(this.texturesFolder, texturePath);
    }

    private static boolean isExtraCrate(String cratePath) {
        for (ModConfig.CrateLevel level : ModConfig.getExtraCrates()) {
            if (cratePath.equals(cratePath(level))) {
                return true;
            }
        }
        return false;
    }

    private String generatedBlockstate(String cratePath) {
        String texture = customTextureExists(cratePath) ? textureLocation(cratePath) : DEFAULT_SIDE_TEXTURE;
        return "{\n" +
                "  \"forge_marker\": 1,\n" +
                "  \"defaults\": {\n" +
                "    \"model\": \"" + OnlyCrates.MODID + ":" + cratePath + "\",\n" +
                "    \"textures\": {\n" +
                "      \"particle\": \"" + texture + "\"\n" +
                "    },\n" +
                "    \"transform\": \"forge:default-block\"\n" +
                "  },\n" +
                "  \"variants\": {\n" +
                "    \"normal\": [{}],\n" +
                "    \"inventory\": [{}]\n" +
                "  }\n" +
                "}\n";
    }

    private String generatedModel(String cratePath) throws IOException {
        InputStream input = CustomCrateResourcePack.class.getResourceAsStream(LARGE_CRATE_MODEL);
        if (input == null) {
            throw new IOException("Missing built-in crate model template: " + LARGE_CRATE_MODEL);
        }

        String model;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtils.copy(input, output);
            model = new String(output.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            IOUtils.closeQuietly(input);
        }

        String texture = customTextureExists(cratePath) ? textureLocation(cratePath) : DEFAULT_SIDE_TEXTURE;
        return model
                .replace("\"0\": \"onlycrates:blocks/block_giant_chest_large\"", "\"0\": \"" + texture + "\"")
                .replace("\"1\": \"onlycrates:blocks/block_giant_chest_top\"", "\"1\": \"" + TOP_TEXTURE + "\"");
    }

    private boolean customTextureExists(String cratePath) {
        return new File(this.texturesFolder, cratePath + ".png").isFile();
    }

    private static String textureLocation(String cratePath) {
        return OnlyCrates.MODID + ":" + cratePath;
    }

    private static String cratePath(String path, String prefix, String suffix) {
        return path.substring(prefix.length(), path.length() - suffix.length());
    }

    private static String cratePath(ModConfig.CrateLevel level) {
        String blockId = level.getBlockId();
        return blockId.contains(":") ? blockId.split(":", 2)[1] : blockId;
    }

    private static InputStream stream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}

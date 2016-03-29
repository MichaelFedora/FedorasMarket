package io.github.michaelfedora.fedorasmarket.config;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Michael on 3/22/2016.
 */
public class FmConfig {

    public static final FmConfig instance = new FmConfig();

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode root;

    private int maxItemStacks = 36;
    private boolean cleanOnStartup = false;
    private final HashSet<BlockType> validShopBlockTypes = new HashSet<>();

    private FmConfig() {

        validShopBlockTypes.add(BlockTypes.CHEST);
        validShopBlockTypes.add(BlockTypes.TRAPPED_CHEST); // default values

        Path path = FedorasMarket.getSharedConfigDir().resolve(PluginInfo.DATA_ROOT + ".cfg");
        this.loader = HoconConfigurationLoader.builder().setPath(path).build();
    }

    public static void initialize() {
        load();
        setupValues();
        save();
    }

    public static int getMaxItemStacks() {
        return instance.maxItemStacks;
    }

    public static boolean getCleanOnStartup() {
        return instance.cleanOnStartup;
    }

    public static HashSet<BlockType> getValidShopBlockTypes() {
        return instance.validShopBlockTypes; // clone?
    }

    public static void setMaxItemStacks(int i) {
        instance.maxItemStacks = i;
        getNode("maxItemStacks").setValue(instance.maxItemStacks);
    }

    public static void setCleanOnStartup(boolean b) {
        instance.cleanOnStartup = b;
        getNode("cleanOnStartup").setValue(instance.cleanOnStartup);
    }

    public static void addValidShopBlockType(BlockType blockType) {
        instance.validShopBlockTypes.add(blockType);
        updateValidShopBlockTypes();
    }

    public static void removeValidShopBlockType(BlockType blockType) {
        instance.validShopBlockTypes.remove(blockType);
        updateValidShopBlockTypes();
    }

    private static void updateValidShopBlockTypes() {
        List<String> vsbt_string = new ArrayList<>();
        instance.validShopBlockTypes.forEach((a) -> vsbt_string.add(a.getId()));
        getNode("validShopBlockTypes").setValue(vsbt_string);
    }

    private static void setupValues() {
        if(getNode("maxItemStacks").getValue() == null)
            getNode("maxItemStacks").setValue(instance.maxItemStacks);
        else
            instance.maxItemStacks = getNode("maxItemStacks").getInt();
        getNode("maxItemStacks").setComment("The max number of item stacks (default=36 (4 rows * 9 items))");

        if(getNode("cleanOnStartup").getValue() == null)
            getNode("cleanOnStartup").setValue(instance.cleanOnStartup);
        else
            instance.cleanOnStartup = getNode("maxItemStacks").getBoolean();
        getNode("cleanOnStartup").setComment("Whether or not to clean bad shop references (in the database), on startup");

        if(getNode("validShopBlockTypes").getValue() == null)
            updateValidShopBlockTypes();
        else {
            instance.validShopBlockTypes.clear();
            getNode("validShopBlockTypes").getChildrenList().forEach((a) -> {
                Sponge.getRegistry().getType(BlockType.class, a.getString()).ifPresent(instance.validShopBlockTypes::add);
            });
        }
        getNode("validShopBlockTypes").setComment("The valid shop block types, as string ids; (default values: \"minecraft:chest\", \"minecraft:trapped_chest\")");

    }

    public static void load() {
        try {
            instance.root = instance.loader.load();
        } catch(IOException e) {
            FedorasMarket.getLogger().error("Could not load configuration!", e);
        }
    }

    public static void save() {
        try {
            instance.loader.save(instance.root);
        } catch(IOException e) {
            FedorasMarket.getLogger().error("Could not save configuration!", e);
        }
    }

    public static CommentedConfigurationNode getNode(Object... path) {
        return instance.root.getNode(path);
    }
}

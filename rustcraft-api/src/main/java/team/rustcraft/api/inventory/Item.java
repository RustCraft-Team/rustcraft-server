package team.rustcraft.api.inventory;

/**
 * RustCraft-domain item definition, independent from Minecraft item registries.
 */
public interface Item {
    ItemId id();

    String displayName();

    int maxStackSize();
}

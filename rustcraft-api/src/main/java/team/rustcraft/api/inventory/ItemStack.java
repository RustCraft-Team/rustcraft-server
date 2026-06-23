package team.rustcraft.api.inventory;

/**
 * Stack of RustCraft-domain items in an inventory.
 */
public interface ItemStack {
    ItemStackId id();

    ItemId itemId();

    int amount();
}

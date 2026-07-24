package team.rustcraft.api.item;

/** Searchable domain tag assigned to item definitions. */
public record ItemTag(String value) {
    public static final ItemTag RESOURCE = new ItemTag("RESOURCE");
    public static final ItemTag COMPONENT = new ItemTag("COMPONENT");
    public static final ItemTag WEAPON = new ItemTag("WEAPON");
    public static final ItemTag MELEE = new ItemTag("MELEE");
    public static final ItemTag RANGED = new ItemTag("RANGED");
    public static final ItemTag AMMO = new ItemTag("AMMO");
    public static final ItemTag MEDICAL = new ItemTag("MEDICAL");
    public static final ItemTag FOOD = new ItemTag("FOOD");
    public static final ItemTag ARMOR = new ItemTag("ARMOR");
    public static final ItemTag BUILDING = new ItemTag("BUILDING");
    public static final ItemTag DOOR = new ItemTag("DOOR");
    public static final ItemTag LOCK = new ItemTag("LOCK");
    public static final ItemTag CONTAINER = new ItemTag("CONTAINER");
    public static final ItemTag DEPLOYABLE = new ItemTag("DEPLOYABLE");
    public static final ItemTag CRAFTING = new ItemTag("CRAFTING");
    public static final ItemTag WORKBENCH = new ItemTag("WORKBENCH");
    public static final ItemTag SCRAP_ITEM = new ItemTag("SCRAP_ITEM");
    public static final ItemTag FUEL = new ItemTag("FUEL");
    public static final ItemTag VEHICLE = new ItemTag("VEHICLE");
    public static final ItemTag RAID = new ItemTag("RAID");
    public static final ItemTag TOOL = new ItemTag("TOOL");
    public ItemTag {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item tag must not be blank");
        }
    }
}

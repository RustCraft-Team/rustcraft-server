package team.rustcraft.api.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Catalog of RustCraft domain item definitions inspired by Rust items.
 *
 * <p>This catalog deliberately registers only platform-neutral domain metadata. It does not create
 * Minecraft/Fabric items, blocks, recipes, screens, entities, or gameplay mechanics.</p>
 */
public final class RustItemDefinitions {
    public static final List<ItemDefinition> DEFINITIONS = createDefinitions();
    public static final int DEFINITION_COUNT = DEFINITIONS.size();

    private RustItemDefinitions() {
    }

    public static void registerAll(ItemRegistry registry) {
        DEFINITIONS.forEach(registry::register);
    }

    public static InMemoryItemRegistry createRegistry(team.rustcraft.api.event.EventBus eventBus) {
        InMemoryItemRegistry registry = new InMemoryItemRegistry(eventBus);
        registerAll(registry);
        return registry;
    }

    private static List<ItemDefinition> createDefinitions() {
        List<ItemDefinition> items = new ArrayList<>();
        resources(items);
        components(items);
        medical(items);
        food(items);
        ammunition(items);
        weapons(items);
        armor(items);
        tools(items);
        building(items);
        doorsAndLocks(items);
        containers(items);
        deployablesAndMisc(items);
        transport(items);
        return List.copyOf(items);
    }

    private static void resources(List<ItemDefinition> items) {
        r(items, "wood", "Wood", 1000, ItemTag.RESOURCE);
        r(items, "stones", "Stone", 1000, ItemTag.RESOURCE);
        r(items, "metal_fragments", "Metal Fragments", 1000, ItemTag.RESOURCE);
        r(items, "high_quality_metal", "High Quality Metal", 100, ItemTag.RESOURCE);
        r(items, "sulfur", "Sulfur", 1000, ItemTag.RESOURCE);
        r(items, "charcoal", "Charcoal", 1000, ItemTag.RESOURCE);
        r(items, "cloth", "Cloth", 1000, ItemTag.RESOURCE);
        r(items, "leather", "Leather", 1000, ItemTag.RESOURCE);
        r(items, "animal_fat", "Animal Fat", 1000, ItemTag.RESOURCE);
        r(items, "scrap", "Scrap", 1000, ItemTag.RESOURCE, ItemTag.SCRAP_ITEM);
        r(items, "crude_oil", "Crude Oil", 500, ItemTag.RESOURCE, ItemTag.FUEL);
        r(items, "low_grade_fuel", "Low Grade Fuel", 500, ItemTag.RESOURCE, ItemTag.FUEL);
        r(items, "metal_ore", "Metal Ore", 1000, ItemTag.RESOURCE);
        r(items, "sulfur_ore", "Sulfur Ore", 1000, ItemTag.RESOURCE);
        r(items, "high_quality_metal_ore", "High Quality Metal Ore", 100, ItemTag.RESOURCE);
        r(items, "gun_powder", "Gun Powder", 1000, ItemTag.RESOURCE, ItemTag.RAID);
        r(items, "explosives", "Explosives", 100, ItemTag.RESOURCE, ItemTag.RAID);
    }

    private static void components(List<ItemDefinition> items) {
        String[] names = {"rifle_body|Rifle Body", "smg_body|SMG Body", "semi_automatic_body|Semi Automatic Body", "metal_pipe|Metal Pipe", "metal_spring|Metal Spring", "road_signs|Road Signs", "sheet_metal|Sheet Metal", "metal_blade|Metal Blade", "propane_tank|Empty Propane Tank", "gears|Gears", "sewing_kit|Sewing Kit", "rope|Rope", "tarp|Tarp", "tech_trash|Tech Trash", "targeting_computer|Targeting Computer", "cctv_camera|CCTV Camera", "fuse|Fuse", "bleach|Bleach", "duct_tape|Duct Tape", "glue|Glue", "sticks|Sticks"};
        for (String name : names) c(items, name, ItemTag.SCRAP_ITEM);
    }

    private static void medical(List<ItemDefinition> items) {
        m(items, "bandage", "Bandage", 3, true);
        m(items, "medical_syringe", "Medical Syringe", 2, true);
        m(items, "large_medkit", "Large Medkit", 1, true);
        m(items, "anti_radiation_pills", "Anti-Radiation Pills", 10, false);
    }

    private static void food(List<ItemDefinition> items) {
        String[] names = {"apple|Apple", "black_raspberries|Black Raspberries", "blueberries|Blueberries", "can_of_beans|Can of Beans", "can_of_tuna|Can of Tuna", "chocolate_bar|Chocolate Bar", "granola_bar|Granola Bar", "mushroom|Mushroom", "pickle|Pickle", "small_water_bottle|Small Water Bottle", "water_jug|Water Jug", "water|Water", "raw_chicken_breast|Raw Chicken Breast", "cooked_chicken|Cooked Chicken", "burnt_chicken|Burnt Chicken", "spoiled_chicken|Spoiled Chicken", "raw_deer_meat|Raw Deer Meat", "cooked_deer_meat|Cooked Deer Meat", "raw_horse_meat|Raw Horse Meat", "cooked_horse_meat|Cooked Horse Meat", "raw_pork|Raw Pork", "cooked_pork|Cooked Pork", "raw_wolf_meat|Raw Wolf Meat", "cooked_wolf_meat|Cooked Wolf Meat", "raw_bear_meat|Raw Bear Meat", "cooked_bear_meat|Cooked Bear Meat", "human_meat_raw|Raw Human Meat", "human_meat_cooked|Cooked Human Meat", "human_meat_burnt|Burnt Human Meat", "human_meat_spoiled|Spoiled Human Meat", "pumpkin|Pumpkin", "corn|Corn", "potato|Potato"};
        for (String name : names) f(items, name);
    }

    private static void ammunition(List<ItemDefinition> items) {
        String[] names = {"pistol_bullet|Pistol Bullet", "incendiary_pistol_bullet|Incendiary Pistol Bullet", "hv_pistol_ammo|HV Pistol Ammo", "5_56_rifle_ammo|5.56 Rifle Ammo", "explosive_5_56_rifle_ammo|Explosive 5.56 Rifle Ammo", "incendiary_5_56_rifle_ammo|Incendiary 5.56 Rifle Ammo", "hv_5_56_rifle_ammo|HV 5.56 Rifle Ammo", "handmade_shell|Handmade Shell", "12_gauge_buckshot|12 Gauge Buckshot", "12_gauge_slug|12 Gauge Slug", "12_gauge_incendiary_shell|12 Gauge Incendiary Shell", "nailgun_nails|Nailgun Nails", "wooden_arrow|Wooden Arrow", "bone_arrow|Bone Arrow", "fire_arrow|Fire Arrow", "high_velocity_arrow|High Velocity Arrow", "rocket|Rocket", "high_velocity_rocket|High Velocity Rocket", "incendiary_rocket|Incendiary Rocket", "smoke_rocket_wip|Smoke Rocket WIP", "40mm_grenade_round|40mm HE Grenade", "40mm_shotgun_round|40mm Shotgun Round", "40mm_smoke_grenade|40mm Smoke Grenade", "sam_ammo|SAM Ammo"};
        for (String name : names) a(items, name);
    }

    private static void weapons(List<ItemDefinition> items) {
        String[] melee = {"rock|Rock", "stone_spear|Stone Spear", "wooden_spear|Wooden Spear", "bone_club|Bone Club", "bone_knife|Bone Knife", "combat_knife|Combat Knife", "longsword|Longsword", "machete|Machete", "mace|Mace", "paddle|Paddle", "salvaged_cleaver|Salvaged Cleaver", "salvaged_sword|Salvaged Sword", "pitchfork|Pitchfork", "butcher_knife|Butcher Knife", "salvaged_hammer|Salvaged Hammer"};
        for (String name : melee) w(items, name, ItemTag.MELEE);
        String[] ranged = {"hunting_bow|Hunting Bow", "crossbow|Crossbow", "compound_bow|Compound Bow", "nailgun|Nailgun", "eoka_pistol|Eoka Pistol", "revolver|Revolver", "python_revolver|Python Revolver", "semi_automatic_pistol|Semi-Automatic Pistol", "m92_pistol|M92 Pistol", "spas_12_shotgun|Spas-12 Shotgun", "waterpipe_shotgun|Waterpipe Shotgun", "double_barrel_shotgun|Double Barrel Shotgun", "pump_shotgun|Pump Shotgun", "custom_smg|Custom SMG", "thompson|Thompson", "mp5a4|MP5A4", "semi_automatic_rifle|Semi-Automatic Rifle", "assault_rifle|Assault Rifle", "lr_300_assault_rifle|LR-300 Assault Rifle", "m39_rifle|M39 Rifle", "bolt_action_rifle|Bolt Action Rifle", "l96_rifle|L96 Rifle", "m249|M249", "rocket_launcher|Rocket Launcher", "grenade_launcher|Multiple Grenade Launcher"};
        for (String name : ranged) w(items, name, ItemTag.RANGED);
        String[] raid = {"beancan_grenade|Beancan Grenade", "f1_grenade|F1 Grenade", "satchel_charge|Satchel Charge", "timed_explosive_charge|Timed Explosive Charge", "survey_charge|Survey Charge", "flame_thrower|Flame Thrower"};
        for (String name : raid) w(items, name, ItemTag.RAID);
    }

    private static void armor(List<ItemDefinition> items) {
        String[] names = {"wood_armor_helmet|Wood Armor Helmet", "wood_chestplate|Wood Chestplate", "wood_armor_pants|Wood Armor Pants", "burlap_headwrap|Burlap Headwrap", "burlap_shirt|Burlap Shirt", "burlap_trousers|Burlap Trousers", "burlap_shoes|Burlap Shoes", "burlap_gloves|Burlap Gloves", "hide_poncho|Hide Poncho", "hide_vest|Hide Vest", "hide_pants|Hide Pants", "hide_boots|Hide Boots", "hide_skirt|Hide Skirt", "hide_halterneck|Hide Halterneck", "hide_gloves|Hide Gloves", "bone_helmet|Bone Helmet", "bone_armor|Bone Armor", "wolf_headdress|Wolf Headdress", "riot_helmet|Riot Helmet", "bucket_helmet|Bucket Helmet", "coffee_can_helmet|Coffee Can Helmet", "metal_facemask|Metal Facemask", "metal_chest_plate|Metal Chest Plate", "roadsign_jacket|Roadsign Jacket", "roadsign_kilt|Roadsign Kilt", "roadsign_gloves|Roadsign Gloves", "leather_gloves|Leather Gloves", "tactical_gloves|Tactical Gloves", "hazmat_suit|Hazmat Suit", "scientist_suit|Scientist Suit", "heavy_scientist_suit|Heavy Scientist Suit", "heavy_plate_helmet|Heavy Plate Helmet", "heavy_plate_jacket|Heavy Plate Jacket", "heavy_plate_pants|Heavy Plate Pants", "diving_mask|Diving Mask", "diving_tank|Diving Tank", "diving_fins|Diving Fins", "wetsuit|Wetsuit", "snow_jacket|Snow Jacket", "jacket|Jacket", "hoodie|Hoodie", "shirt|Shirt", "tank_top|Tank Top", "pants|Pants", "shorts|Shorts", "boots|Boots", "frog_boots|Frog Boots"};
        for (String name : names) ar(items, name);
    }

    private static void tools(List<ItemDefinition> items) {
        String[] names = {"stone_hatchet|Stone Hatchet", "hatchet|Hatchet", "stone_pickaxe|Stone Pickaxe", "pickaxe|Pickaxe", "salvaged_icepick|Salvaged Icepick", "hammer|Hammer", "building_plan|Building Plan", "toolgun|Tool Gun", "chainsaw|Chainsaw", "jackhammer|Jackhammer", "handmade_fishing_rod|Handmade Fishing Rod", "binoculars|Binoculars", "camera|Camera", "rf_transmitter|RF Transmitter", "rf_pager|RF Pager", "geiger_counter|Geiger Counter", "supply_signal|Supply Signal"};
        for (String name : names) t(items, name);
    }

    private static void building(List<ItemDefinition> items) {
        String[] names = {"foundation|Foundation", "triangle_foundation|Triangle Foundation", "floor|Floor", "triangle_floor|Triangle Floor", "floor_frame|Floor Frame", "floor_grill|Floor Grill", "ladder_hatch|Ladder Hatch", "ceiling|Ceiling", "stairs|Stairs", "spiral_stairs|Spiral Stairs", "u_shaped_stairs|U-Shaped Stairs", "wall|Wall", "half_wall|Half Wall", "low_wall|Low Wall", "doorway|Doorway", "window|Window", "wall_frame|Wall Frame", "shop_front_frame|Shop Front Frame", "roof|Roof", "roof_triangle|Triangle Roof", "ramp|Ramp", "foundation_steps|Foundation Steps", "high_external_stone_wall|High External Stone Wall", "high_external_wooden_wall|High External Wooden Wall", "high_external_stone_gate|High External Stone Gate", "high_external_wooden_gate|High External Wooden Gate", "wooden_ladder|Wooden Ladder", "watch_tower|Watch Tower"};
        for (String name : names) b(items, name);
    }

    private static void doorsAndLocks(List<ItemDefinition> items) {
        for (String name : new String[]{"wooden_door|Wooden Door", "sheet_metal_door|Sheet Metal Door", "armored_door|Armored Door", "garage_door|Garage Door", "ladder_hatch_door|Ladder Hatch", "floor_grill_door|Floor Grill", "shop_front|Shop Front", "metal_shop_front|Metal Shop Front", "prison_cell_gate|Prison Cell Gate", "prison_cell_wall|Prison Cell Wall", "chainlink_fence|Chainlink Fence", "chainlink_fence_gate|Chainlink Fence Gate"}) d(items, name);
        l(items, "key_lock|Key Lock");
        l(items, "code_lock|Code Lock");
    }

    private static void containers(List<ItemDefinition> items) {
        for (String name : new String[]{"small_stash|Small Stash", "small_wooden_box|Small Wooden Box", "large_wooden_box|Large Wooden Box", "drop_box|Drop Box", "mailbox|Mailbox", "locker|Locker", "fridge|Fridge", "vending_storage|Vending Storage", "coffin|Coffin", "backpack|Backpack"}) co(items, name);
    }

    private static void deployablesAndMisc(List<ItemDefinition> items) {
        for (String name : new String[]{"sleeping_bag|Sleeping Bag", "bed|Bed", "furnace|Furnace", "large_furnace|Large Furnace", "workbench_level_1|Workbench Level 1", "workbench_level_2|Workbench Level 2", "workbench_level_3|Workbench Level 3", "research_table|Research Table", "repair_bench|Repair Bench", "recycler|Recycler", "vending_machine|Vending Machine", "tool_cupboard|Tool Cupboard", "small_oil_refinery|Small Oil Refinery", "gun_trap|Gun Trap", "auto_turret|Auto Turret", "flame_turret|Flame Turret", "land_mine|Land Mine", "bear_trap|Snap Trap", "water_catcher_small|Small Water Catcher", "water_catcher_large|Large Water Catcher", "camp_fire|Camp Fire", "barbeque|Barbeque", "lantern|Lantern", "ceiling_light|Ceiling Light", "search_light|Search Light", "chair|Chair", "table|Table", "rug|Rug", "rug_bear_skin|Rug Bear Skin", "sign_small|Small Sign", "sign_large|Large Sign", "picture_frame|Picture Frame", "storage_barrel|Storage Barrel", "composter|Composter"}) dep(items, name);
    }

    private static void transport(List<ItemDefinition> items) {
        v(items, "rowboat", "Boat");
        v(items, "rhib", "RHIB");
        v(items, "horse", "Horse");
        v(items, "minicopter", "Minicopter");
    }

    private static void r(List<ItemDefinition> items, String internal, String display, int stack, ItemTag... tags) { add(items, internal, display, ItemCategory.RESOURCE, stack, false, null, true, true, true, false, ItemRarity.COMMON, tags); }
    private static void c(List<ItemDefinition> items, String pair, ItemTag... tags) { addPair(items, pair, ItemCategory.COMPONENT, 20, false, null, true, true, false, false, ItemRarity.UNCOMMON, concat(new ItemTag[]{ItemTag.COMPONENT}, tags)); }
    private static void m(List<ItemDefinition> items, String internal, String display, int stack, boolean craft) { add(items, internal, display, ItemCategory.MEDICAL, stack, false, null, true, true, craft, false, ItemRarity.UNCOMMON, ItemTag.MEDICAL); }
    private static void f(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.FOOD, 10, false, null, true, false, false, false, ItemRarity.COMMON, ItemTag.FOOD); }
    private static void a(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.AMMUNITION, 64, false, null, true, true, true, false, ItemRarity.UNCOMMON, ItemTag.AMMO, ItemTag.RAID); }
    private static void w(List<ItemDefinition> items, String pair, ItemTag tag) { addPair(items, pair, ItemCategory.WEAPON, 1, true, 100, true, true, true, true, ItemRarity.RARE, ItemTag.WEAPON, tag); }
    private static void ar(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.ARMOR, 1, true, 100, true, true, true, true, ItemRarity.UNCOMMON, ItemTag.ARMOR); }
    private static void t(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.TOOL, 1, true, 100, true, true, true, true, ItemRarity.COMMON, ItemTag.TOOL); }
    private static void b(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.BUILDING, 10, false, null, false, false, true, false, ItemRarity.COMMON, ItemTag.BUILDING); }
    private static void d(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.DOOR, 1, true, 100, true, true, true, true, ItemRarity.UNCOMMON, ItemTag.DOOR, ItemTag.DEPLOYABLE); }
    private static void l(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.LOCK, 1, false, null, true, true, true, false, ItemRarity.COMMON, ItemTag.LOCK, ItemTag.DEPLOYABLE); }
    private static void co(List<ItemDefinition> items, String pair) { addPair(items, pair, ItemCategory.CONTAINER, 1, false, null, true, true, true, false, ItemRarity.COMMON, ItemTag.CONTAINER, ItemTag.DEPLOYABLE); }
    private static void dep(List<ItemDefinition> items, String pair) { ItemTag[] tags = pair.contains("workbench") ? new ItemTag[]{ItemTag.DEPLOYABLE, ItemTag.CRAFTING, ItemTag.WORKBENCH} : new ItemTag[]{ItemTag.DEPLOYABLE}; addPair(items, pair, ItemCategory.DEPLOYABLE, 1, true, 100, true, true, true, true, ItemRarity.COMMON, tags); }
    private static void v(List<ItemDefinition> items, String internal, String display) { add(items, internal, display, ItemCategory.VEHICLE, 1, true, 250, true, false, false, true, ItemRarity.RARE, ItemTag.VEHICLE); }

    private static void addPair(List<ItemDefinition> items, String pair, ItemCategory category, int stack, boolean durable, Integer durability, boolean tradable, boolean researchable, boolean craftable, boolean repairable, ItemRarity rarity, ItemTag... tags) {
        String[] parts = pair.split("\\|", 2);
        add(items, parts[0], parts[1], category, stack, durable, durability, tradable, researchable, craftable, repairable, rarity, tags);
    }

    private static void add(List<ItemDefinition> items, String internal, String display, ItemCategory category, int stack, boolean durable, Integer durability, boolean tradable, boolean researchable, boolean craftable, boolean repairable, ItemRarity rarity, ItemTag... tags) {
        items.add(new ItemDefinition(new ItemDefinitionId("rustcraft:" + internal), internal, display, category, stack, durable, durability, tradable, researchable, craftable, repairable, rarity, Set.of(tags), Set.of()));
    }

    private static ItemTag[] concat(ItemTag[] left, ItemTag[] right) {
        ItemTag[] result = new ItemTag[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }
}

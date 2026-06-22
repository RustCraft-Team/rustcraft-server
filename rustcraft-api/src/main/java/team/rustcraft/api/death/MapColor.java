package team.rustcraft.api.death;

/**
 * RGB color used to display a respawn point on a map without depending on Minecraft classes.
 *
 * @param red red channel from 0 to 255
 * @param green green channel from 0 to 255
 * @param blue blue channel from 0 to 255
 */
public record MapColor(int red, int green, int blue) {
    /**
     * Creates an RGB map color after validating each channel.
     */
    public MapColor {
        validateChannel("red", red);
        validateChannel("green", green);
        validateChannel("blue", blue);
    }

    private static void validateChannel(String name, int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " channel must be between 0 and 255");
        }
    }
}

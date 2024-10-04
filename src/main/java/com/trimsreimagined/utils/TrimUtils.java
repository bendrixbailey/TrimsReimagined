package com.trimsreimagined.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TrimUtils {

    private static final ArrayList<String> trimNameList = new ArrayList<>(
            Arrays.asList(
                    "sentry",
                    "vex",
                    "wild",
                    "coast",
                    "dune",
                    "wayfinder",
                    "raiser",
                    "shaper",
                    "host",
                    "ward",
                    "silence",
                    "tide",
                    "snout",
                    "rib",
                    "eye",
                    "spire",
                    "bolt",
                    "flow"
            )

    );


    public static ArrayList<String> getListOfVanillaTrims(){
        return trimNameList;
    }


    public enum ArmorSlot{
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    public static final ArrayList<String> wildAllowedBiomes = new ArrayList<>(
            Arrays.asList(
                    "plains",
                    "meadow",
                    "cherry_grove",
                    "windswept_hills",
                    "windswept_forest",
                    "forest",
                    "flower_forest",
                    "taiga",
                    "old_growth_pine_taiga",
                    "old_growth_spruce_taiga",
                    "birch_forest",
                    "old_growth_birch_forest",
                    "dark_forest",
                    "jungle",
                    "sparse_jungle",
                    "bamboo_jungle",
                    "swamp",
                    "mangrove_swamp",
                    "sunflower_plains"
            )
    );

    public static final ArrayList<String> duneAllowedBiomes = new ArrayList<>(
            Arrays.asList(
                    "desert",
                    "savanna",
                    "savanna_plateau",
                    "windswept_savanna",
                    "badlands",
                    "wooded_badlands",
                    "eroded_badlands"
            )
    );

}

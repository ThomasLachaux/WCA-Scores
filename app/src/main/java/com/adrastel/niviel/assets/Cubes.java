package com.adrastel.niviel.assets;

import com.adrastel.niviel.R;

public class Cubes {

    public static int getImage(String event) {

        switch (event.toLowerCase()) {
            case "2x2x2 cube":
                return R.drawable.cube_2x2;

            case "4x4x4 cube":
                return R.drawable.cube_4x4;

            case "5x5x5 cube":
                return R.drawable.cube_5x5;

            case "6x6x6 cube":
                return R.drawable.cube_6x6;

            case "7x7x7 cube":
                return R.drawable.cube_7x7;

            case "3x3x3 blindfolded":
                return R.drawable.cube_3x3_bf;

            case "3x3x3 one-handed":
                return R.drawable.cube_3x3_oh;

            case "megaminx":
                return R.drawable.megaminx;

            case "pyraminx":
                return R.drawable.piraminx;

            case "square-1":
                return R.drawable.square_1;

            case "rubik's clock":
                return R.drawable.cube_clock;

            case "skewb":
                return R.drawable.skewb;

            case "4x4x4 blindfolded":
                return R.drawable.cube_4x4_bf;

            case "5x5x5 blindfolded":
                return R.drawable.cube_5x5_bf;

            case "3x3x3 multi-blind":
                return R.drawable.cube_3x3_mbf;

            case "3x3x3 with feet":
                return R.drawable.cube_3x3_wf;

            case "3x3x3 fewest moves":
                return R.drawable.cube_3x3_fm;

            default:
                return R.drawable.cube_3x3;

        }
    }

    public static int getCubeId(String event) {

        String[] cubes = {"Rubik's cube", "4x4x4 Cube", "5x5x5 Cube", "2x2x2 Cube", "3x3x3 blindfolded",
                "Rubik's Cube: Blindfolded", "3x3 one-handed", "Rubik's Cube: One-handed",
                "3x3x3 fewest moves", "Rubik's Cube: Fewest moves",
                "Megaminx", "Pyraminx", "Square-1", "Rubik's Clock", "Skewb", "6x6x6 Cube", "7x7x7 Cube",
                "Rubik's Magic", "Master Magic"
        };

        for (int i = 0; i < cubes.length; i++) {

            if (event.equalsIgnoreCase(cubes[i]))
                return i;

        }

        return cubes.length;
    }

    public static String getCubeId(int position) {

        switch (position) {
            case 1:
                return "444";

            case 2:
                return "555";

            case 3:
                return "222";

            case 4:
                return "333bf";

            case 5:
                return "333oh";

            case 6:
                return "333fm";

            case 7:
                return "333ft";

            case 8:
                return "minx";

            case 9:
                return "pyram";

            case 10:
                return "sq1";

            case 11:
                return "clock";

            case 12:
                return "skewb";

            case 13:
                return "666";

            case 14:
                return "777";

            case 15:
                return "444bf";

            case 16:
                return "555bf";

            case 17:
                return "333mbf";

            default:
                return "333";
        }
    }
}

package com.wrgy.customportalmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForcedConvert {
    // Coordinate regex pattern
    private static final String regex = "(~?\\-?\\d+(?:\\.\\d{1,2})?|~)";
    private static final Pattern pattern = Pattern.compile(regex);

    public static String forceTransformCommand(String command, int[] blockPositions, String highlightedText, int highlightedStartPos) {
        // Extract the part of the command before and after the highlighted text
        String beforeHighlight = command.substring(0, highlightedStartPos);
        String afterHighlight = command.substring(highlightedStartPos + highlightedText.length());

        // Create a matcher to find all the coordinates in the highlighted text
        Matcher matcher = pattern.matcher(highlightedText);
        StringBuffer transformedCoords = new StringBuffer();

        int coordCount = 0;  // Track the number of coordinates processed
        int lastMatchEnd = 0;  // Track where the last match ended to preserve spaces

        // Process each matched coordinate
        while (matcher.find()) {
            String coordinate = matcher.group();
            boolean isRelative = coordinate.startsWith("~");
            double coordValue = 0.0;

            // If the coordinate is relative (~), handle it, otherwise parse the number
            if (isRelative && coordinate.length() > 1) {
                coordValue = Double.parseDouble(coordinate.substring(1)); // Remove the "~" and parse
            } else if (!isRelative) {
                coordValue = Double.parseDouble(coordinate);  // Absolute coordinate
            }

            // Append any spaces between the last match and the current match
            transformedCoords.append(highlightedText.substring(lastMatchEnd, matcher.start()));

            // Calculate the new coordinate
            String newCoord = calculateCoordinate(isRelative, coordValue, coordCount, blockPositions.length, blockPositions);

            // Append the transformed coordinate
            transformedCoords.append(newCoord);

            // Increment the counter to track coordinate axis (x, y, z)
            coordCount++;
            lastMatchEnd = matcher.end();  // Update the last match end to current match end
        }

        // Append any trailing spaces after the last matched coordinate
        transformedCoords.append(highlightedText.substring(lastMatchEnd));

        // Rebuild the final command with transformed coordinates
        String finalCommand = beforeHighlight + transformedCoords.toString() + afterHighlight;

        // Clean up phase: Convert "~0" to "~" and remove ".00" from whole numbers
        finalCommand = cleanupCommand(finalCommand);

        return finalCommand;
    }

    // Coordinate transformation logic
    public static String calculateCoordinate(boolean isRelative, double coordinateValue, int count, int numberOfCoordinates, int[] blockPositions) {
        double blockPosition = blockPositions[count % 3];  // Handle cyclic use of blockPosition for x, y, z
        double result;

        if (isRelative) {
            // Convert relative coordinate to absolute and remove the '~'
            result = coordinateValue + blockPosition;
            return String.format("%.2f", result);  // No tilde, just the absolute value
        } else {
            // Convert absolute coordinate to relative
            result = coordinateValue - blockPosition;
            // If the result is a whole number, remove decimals (for coordinates like `~10`, not `~10.00`)
            if (result == Math.floor(result)) {
                return String.format("~%.0f", result);
            } else {
                return String.format("~%.2f", result);  // Format with 2 decimals
            }
        }
    }

    // Clean up phase to handle "~0" and remove ".00"
    public static String cleanupCommand(String command) {
        // Replace "~0" with "~"
        command = command.replaceAll("~0(\\.00)?", "~");
        // Remove ".00" from any whole numbers
        command = command.replaceAll("\\.00\\b", "");
        return command;
    }
}

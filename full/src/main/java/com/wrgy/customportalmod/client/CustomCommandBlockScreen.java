package com.wrgy.customportalmod.client;

import com.wrgy.customportalmod.CustomPortalMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

@Mod.EventBusSubscriber(modid = CustomPortalMod.MOD_ID, value = Dist.CLIENT)
public class CustomCommandBlockScreen {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CustomCommandBlockScreen.class);
    }
    //ORIGINAL FUNCTIONING
    private static double[] calculationBlockPos;
    private static double[] blockPositions;
    private static boolean hadDash;
    private static int highlightedStartPosOffset = 0;
    private static String[] decimalCommands = {"spreadplayers","center","summon","spawn", "playsound", "positioned", "r", "particle"};
    private static final Map<String, Integer> COMMANDS_MAP = new HashMap<>();
    static {
        COMMANDS_MAP.put("spreadplayers", 2);
        COMMANDS_MAP.put("center", 2);
        COMMANDS_MAP.put("forceload query",2);
        COMMANDS_MAP.put("forceload add",4);
        COMMANDS_MAP.put("forceload remove",4);
        COMMANDS_MAP.put("placefeature", 3);
        COMMANDS_MAP.put("summon", 3);
        COMMANDS_MAP.put("block", 3);
        COMMANDS_MAP.put("loot", 3);
        COMMANDS_MAP.put("playsound", 3);
        COMMANDS_MAP.put("setblock", 3);
        COMMANDS_MAP.put("spawn", 3);
        COMMANDS_MAP.put("spawnpoint", 3);
        COMMANDS_MAP.put("setworldspawn", 3);
        COMMANDS_MAP.put("particle", 3);
        COMMANDS_MAP.put("positioned",3);
        COMMANDS_MAP.put("fill", 6);
        COMMANDS_MAP.put("r", 6);
        COMMANDS_MAP.put("clone", 9);
    }

    private static void arrayDebug (String header, String[] array){
        out.print(header+"\n");
        for(int i=0;i< array.length;i++){
            out.print(array[i] + "\n");
        }
        out.print("\n");
    }
    private static void stringDebug (String header, String string){
        out.print(header + "\n");
        out.print(string + "\n");
        out.print("\n");
    }

    public static String transformCommand(String command, int[] blockPosition, String highlightedText, int highlightedStartPos, boolean forceAbsolute) {
        hadDash = false;
        String[] processedCommands;
        calculationBlockPos = new double[blockPosition.length]; // convert block pos to double
        for (int i = 0; i < blockPosition.length; i++) {
            calculationBlockPos[i] = (double) blockPosition[i];
        }
        String preProcessedCommand = preProcessCommands(command, highlightedText, highlightedStartPos); // replace stray ~'s with 0's from the command
        stringDebug("PreProcess: ", preProcessedCommand);
        String[] separatedCommands = commandSeparator(preProcessedCommand); // separate commands into an array
        arrayDebug("Separated: ", separatedCommands);
        String[] verifiedCommands = commandsVerifier(separatedCommands); // verify and fix potential separation issues
        arrayDebug("Verified: ", verifiedCommands);
        // Check if highlighted text is empty and process accordingly
        if (!highlightedText.isEmpty()) {
            processedCommands = processHighlighted(verifiedCommands, highlightedText, highlightedStartPos);
            arrayDebug("Highlighted Processed Commands: ",processedCommands);
        } else {
            processedCommands = processCommands(verifiedCommands, forceAbsolute); // modified to include forceAbsolute
            arrayDebug("Processed Commands: ",processedCommands);
        }

        String[] postProcessedCommands = commandsPostProcessing(processedCommands); // remove decimals from int pos commands and turn ~0's to ~
        arrayDebug("Post Processed: ",postProcessedCommands);
        String combinedCommand = combineStrings(postProcessedCommands); // combine string array into one string
        stringDebug("Combined command: ",combinedCommand);
        String transformedCommand = finalCheckStage(combinedCommand);
        stringDebug("Final returned command: ",transformedCommand);
        return transformedCommand;
    }



    private static String finalCheckStage(String combinedComand)
    {
        return combinedComand.replace(" ~0(?!\\.\\d)", " ~");
    }

    private static String[] processCommands(String[] verifiedCommands, boolean forceAbsolute) {
        List<String> processedCommands = new ArrayList<>();
        double[] absoluteCoordinatesForPositioned = new double[3];

        for (String command : verifiedCommands) {
            String regex = "(?<!\\S)~?-?\\d*\\.?\\d+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(command);

            StringBuffer processedString = new StringBuffer();
            int lastEnd = 0;
            int coordinatesConverted = 0;

            // Get the required number of coordinates from the COMMANDS_MAP
            int numCoordinatesRequired = COMMANDS_MAP.getOrDefault(command.split("\\s+")[0], Integer.MAX_VALUE);

            // Reset matcher for processing
            matcher.reset();
            lastEnd = 0;
            coordinatesConverted = 0;

            // Process up to the number of required coordinates
            while (matcher.find() && coordinatesConverted < numCoordinatesRequired) {
                processedString.append(command.substring(lastEnd, matcher.start()));
                String numberStr = matcher.group();
                boolean isRelative = numberStr.startsWith("~");
                double number = isRelative ? (numberStr.equals("~") ? 0 : Double.parseDouble(numberStr.substring(1))) : Double.parseDouble(numberStr);

                // If forceAbsolute is true, convert only relative coordinates, else default logic
                String modifiedNumberStr;
                if (forceAbsolute && isRelative) {
                    modifiedNumberStr = calculateCoordinate(isRelative, number, coordinatesConverted, numCoordinatesRequired);
                } else if (!forceAbsolute) {
                    modifiedNumberStr = calculateCoordinate(isRelative, number, coordinatesConverted, numCoordinatesRequired);
                } else {
                    modifiedNumberStr = numberStr; // keep absolute unchanged if forceAbsolute is true
                }

                if (command.startsWith("positioned")) {
                    double absoluteCoordinate = isRelative ? number + blockPositions[coordinatesConverted % 3] : number;
                    absoluteCoordinatesForPositioned[coordinatesConverted] = absoluteCoordinate;
                }

                processedString.append(modifiedNumberStr);
                lastEnd = matcher.end();
                coordinatesConverted++;
            }
            processedString.append(command.substring(lastEnd));
            processedCommands.add(processedString.toString());

            if (command.startsWith("positioned")) {
                calculationBlockPos = absoluteCoordinatesForPositioned.clone();
            }
        }
        return processedCommands.toArray(new String[0]);
    }



    public static String preProcessCommands(String command, String highlightedText, int highlightedStartPos) {
        hadDash = command.startsWith("/");
        /*if (hadDash)
        {
            command = command.substring(1);
            highlightedStartPosOffset--;
        }*/
        // Pattern to match '~' that is not followed by a digit or '-' and not part of a number
        Pattern pattern = Pattern.compile("~(?!\\d|[-\\d])");
        Matcher matcher = pattern.matcher(command);

        // If highlightedText is empty, replace all occurrences of '~' with '~0'
        if (highlightedText.isEmpty()) {
            return matcher.replaceAll("~0");
        } else {
            // Initialize a StringBuilder to build the modified string
            StringBuilder modifiedCommand = new StringBuilder();

            // Track the current position in the original command
            int currentPos = 0;

            // Iterate over each match of the pattern
            while (matcher.find()) {
                // Append the portion of the string before the match
                modifiedCommand.append(command, currentPos, matcher.start());

                // Replace '~' with '~0'
                modifiedCommand.append("~0");

                // If the match occurs before highlightedStartPos, adjust highlightedStartPos and offset
                if (matcher.start() < highlightedStartPos) {
                    highlightedStartPosOffset++;
                    highlightedStartPos++;
                }

                // Update the current position to the character after the match
                currentPos = matcher.end();
            }

            // Append the remaining part of the command after the last match
            modifiedCommand.append(command.substring(currentPos));

            // Return the modified command
            return modifiedCommand.toString();
        }
    }

    private static String[] commandSeparator(String command) { //separates commands based on keywords

        List<String> separatedCommands = new ArrayList<>();
        String commandPattern = String.join("|", COMMANDS_MAP.keySet());
        Pattern pattern = Pattern.compile("(?<=\\s)\\b(" + commandPattern + ")\\b");
        Matcher matcher = pattern.matcher(command);

        int lastMatchEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            if (lastMatchEnd < start) {
                String commandSegment = command.substring(lastMatchEnd, start).trim();
                if (!commandSegment.isEmpty()) {
                    separatedCommands.add(commandSegment);
                }
            }
            lastMatchEnd = start;
        }

        if (lastMatchEnd < command.length()) {
            separatedCommands.add(command.substring(lastMatchEnd));
        }
        // Convert List to String[]
        return separatedCommands.toArray(new String[0]);
    }

    private static String[] commandsVerifier(String[] separatedCommands) {
        List<String> resultList = new ArrayList<>();
        String regex = "~?-?\\d*\\.?\\d+"; // Regex to identify coordinates
        Pattern coordinatePattern = Pattern.compile(regex);

        int i = 0;
        while (i < separatedCommands.length) {
            String currentCommand = separatedCommands[i].trim();
            Matcher matcher = coordinatePattern.matcher(currentCommand);

            // If the current command contains at least one coordinate, add it to the result list
            if (matcher.find()) {
                resultList.add(currentCommand);
            } else {
                // If the current command does not contain a coordinate, start merging with subsequent commands
                StringBuilder mergedCommand = new StringBuilder(currentCommand);
                while (i + 1 < separatedCommands.length) {
                    i++;
                    mergedCommand.append(" ").append(separatedCommands[i].trim());

                    matcher = coordinatePattern.matcher(mergedCommand.toString());

                    // Break the loop if the merged command now contains a coordinate
                    if (matcher.find()) {
                        break;
                    }
                }
                resultList.add(mergedCommand.toString().trim());
            }
            i++; // Move to the next command
        }

        return resultList.toArray(new String[0]);
    }

    public static String[] processHighlighted(String[] verifiedCommands, String highlightedText, int highlightedStartPos) {
        // Combine the verified commands into a single string
        out.print("" + "\n");
        String command = String.join(" ", verifiedCommands);
        // Regular expression to match coordinates (~, ~1, -1, 10, 12.5, etc.)
        out.print("" + "\n");
        String regex = "(~?\\-?\\d+(?:\\.\\d{1,2})?|~)";
        Pattern pattern = Pattern.compile(regex);
        out.print("\n\n\n\nSTART\n\n");

        // Step 1: Count the coordinates before the highlighted text
        out.print("Step 1: Count the coordinates before the highlighted text" + "\n");
        int coordinatesBeforeHighlighted = 0;
        Matcher matcher = pattern.matcher(command.substring(0, highlightedStartPos));
        while (matcher.find()) {
            coordinatesBeforeHighlighted++;
        }
        out.print("Coordinates before highlighted: " + coordinatesBeforeHighlighted + "\n");

        // Step 2: Count the coordinates within the highlighted text
        out.print("Step 2: Count the coordinates within the highlighted text" + "\n");
        int coordinatesInHighlighted = 0;
        matcher = pattern.matcher(highlightedText);
        while (matcher.find()) {
            coordinatesInHighlighted++;
        }
        out.print("Coordinates in highlighted: " + coordinatesInHighlighted + "\n");

        // Step 3: Find the positions for the part before and after the highlighted section
        out.print("Step 3: Find the positions for the part before and after the highlighted section" + "\n");
        matcher = pattern.matcher(command);
        int afterStartPosition = -1;
        out.print("Next closest coordinate after the highlighted section initial: " + afterStartPosition + "\n");
        int totalCoordinates = 0;
        while (matcher.find()) {
            totalCoordinates++;
            if (totalCoordinates == (coordinatesBeforeHighlighted + coordinatesInHighlighted)) {
                afterStartPosition = matcher.end();
                break;
            }
        }
        // If no coordinate is found after the highlighted section, use the command length as the end position
        if (afterStartPosition == -1) {
            afterStartPosition = command.length();
        }
        out.print("Next closest coordinate after the highlighted section final: " + afterStartPosition + "\n");

        // Step 4: Extract the text before, within, and after the highlighted section
        out.print("Step 4: Extract the text before, within, and after the highlighted section" + "\n");
        String commandBeforeHighlighted = command.substring(0, highlightedStartPos + highlightedStartPosOffset).trim(); // Adjusting the substring boundary here
        String commandAfterHighlighted = command.substring(afterStartPosition).trim();
        out.print("Highlighted start pos: " + highlightedStartPos + "\n");
        out.print("Highlighted start pos offset: " + highlightedStartPosOffset + "\n");
        out.print("Command before highlighted: " + commandBeforeHighlighted + "\n");
        out.print("Command after highlighted: " + commandAfterHighlighted + "\n");

        // Step 5: Process the entire command (using your processing logic)
        out.print("Step 5: Process the entire command (using your processing logic)" + "\n");
        String[] processedCommands = processCommands(verifiedCommands,false);
        arrayDebug("Processed entire command: ",processedCommands);
        String processedCommand = combineStrings(processedCommands);
        stringDebug("Combined processed entire command: ", processedCommand);

        // Step 6: Extract the equivalent processed highlighted text
        out.print("Step 6: Extract the equivalent processed highlighted text" + "\n");
        matcher = pattern.matcher(processedCommand);

        String processedHighlightedText = "";
        totalCoordinates = 0;
        int startProcessed = -1;
        int endProcessed = -1;
        out.print("startProcessed initial: " + startProcessed + "\n");
        out.print("endProcessed initial: " + endProcessed + "\n");
        while (matcher.find()) {
            totalCoordinates++;
            if (totalCoordinates == coordinatesBeforeHighlighted + 1) {
                startProcessed = matcher.start();
                out.print("new startProcessed pos: " + startProcessed + "\n");
            }
            if (totalCoordinates == (coordinatesBeforeHighlighted + coordinatesInHighlighted)) {
                endProcessed = matcher.end();
                out.print("new endProcessed pos: " + endProcessed + "\n");
                break;
            }
        }
        out.print("startProcessed final: " + startProcessed + "\n");
        out.print("endProcessed final: " + endProcessed + "\n");

        // If no processed coordinates were found, default to the original highlighted text
        if (startProcessed != -1 && endProcessed != -1) {
            // Extract from the processed command
            processedHighlightedText = processedCommand.substring(startProcessed, endProcessed).trim();
            out.print("processedHighlightedText substring v: " + processedHighlightedText + "\n");
        } else {
            processedHighlightedText = highlightedText.trim();
            out.print("processedHighlightedText nosubstring v: " + processedHighlightedText + "\n");
        }

        // New addition: If the highlighted text doesn't start with a coordinate, extract the initial word before the first coordinate
        matcher = pattern.matcher(highlightedText);
        String initialWord = "";
        if (matcher.find() && matcher.start() > 0) {
            // Extract the substring before the first coordinate
            initialWord = highlightedText.substring(0, matcher.start()).trim();
            out.print("Initial word before coordinate: " + initialWord + "\n");
        }

        // Prepend the initial word to the processed highlighted text, if not empty
        if (!initialWord.isEmpty()) {
            processedHighlightedText = initialWord + " " + processedHighlightedText;
            out.print("Final processedHighlightedText with initial word: " + processedHighlightedText + "\n");
        }

        // Step 7: Combine the parts together while ensuring words are preserved
        out.print("Step 7: Combine the parts together while ensuring words are preserved" + "\n");
        String[] transformedCommand = new String[] {commandBeforeHighlighted.trim(), processedHighlightedText.trim(), commandAfterHighlighted.trim()};
        out.print("\nComponents: \n");
        stringDebug("commandBeforeHighlighted.trim(): ", commandBeforeHighlighted.trim());
        stringDebug("processedHighlightedText.trim(): ", processedHighlightedText.trim());
        stringDebug("commandAfterHighlighted.trim(): ", commandAfterHighlighted.trim());
        arrayDebug("Transformed command to return: ", transformedCommand);
        out.print("END \n \n \n \n");

        return transformedCommand;
    }

    public static String calculateCoordinate(boolean isRelative, double coordinateValue, int count, int numberOfCoordinates) {
        blockPositions = new double[]{getXDouble(), numberOfCoordinates == 2 || numberOfCoordinates == 4 ? getZDouble() : getYDouble(), getZDouble()};
        double blockPosition = blockPositions[count % 3];
        double result;

        if (isRelative) {
            result = coordinateValue + blockPosition;
            return String.format("%.2f", result);
        } else {
            result = coordinateValue - blockPosition;
            return String.format("~%.2f", result);
        }
    }

    private static String combineStrings(String[] stringsToCombine){
        String combinedCommand = String.join(" ", stringsToCombine);
        if (hadDash)
        {
            combinedCommand = "/" + combinedCommand;
        }

        // Check if the combined string starts with "//" at position 0
        if (combinedCommand.startsWith("//")) {
            combinedCommand = combinedCommand.replaceFirst("//", "/");
        }

        // Check if the combined string starts with "//" at position 0
        if (combinedCommand.startsWith("/ /")) {
            combinedCommand = combinedCommand.replaceFirst("/ /", "/");
        }

        // Check if the combined string starts with "//" at position 0
        if (combinedCommand.startsWith(" ")) {
            combinedCommand = combinedCommand.replaceFirst(" ", "");
        }

        return combinedCommand;
    }

    private static String[] commandsPostProcessing(String[] verifiedCommands) {
        // List to hold the processed commands
        List<String> result = new ArrayList<>();

        // Pattern to match decimal numbers
        Pattern decimalPattern = Pattern.compile("(?<=\\s)~?-?\\d*\\.\\d+");

        for (String command : verifiedCommands) {
            // Split the command into words
            String[] words = command.split("\\s+");
            if (words.length > 0) {
                String baseCommand = words[0];  // Get the first word (command name)
                boolean isDecimalCommand = Arrays.asList(decimalCommands).contains(baseCommand);

                // Get the required number of coordinates for this command (default is 0)
                int requiredCoordinates = COMMANDS_MAP.getOrDefault(baseCommand, 0);

                if (!isDecimalCommand && requiredCoordinates > 0) {
                    // Transform only the required number of coordinates
                    Matcher matcher = decimalPattern.matcher(command);
                    StringBuffer sb = new StringBuffer();

                    int lastEnd = 0;
                    int coordinateCount = 0;
                    while (matcher.find()) {
                        // Append the part before the match
                        sb.append(command, lastEnd, matcher.start());

                        // Transform the coordinate if we're still within the required number
                        String decimalStr = matcher.group();
                        String transformedCoordinate = decimalStr.contains(".") && coordinateCount < requiredCoordinates
                                ? decimalStr.replaceAll("\\.\\d+$", "")  // Replace decimals if it's a coordinate
                                : decimalStr;  // Leave it unchanged if it's beyond the required coordinates

                        sb.append(transformedCoordinate);
                        lastEnd = matcher.end();
                        coordinateCount++;

                        // Stop transforming once we've reached the required number of coordinates
                        if (coordinateCount >= requiredCoordinates) {
                            break;
                        }
                    }

                    // Append the rest of the command after the last coordinate match
                    sb.append(command.substring(lastEnd));

                    // Add the transformed command to the result list
                    result.add(sb.toString());
                } else {
                    // If the command is in decimalCommands or doesn't need transformation, add it as-is
                    result.add(command);
                }
            } else {
                // If the command is empty, just add it to the result list
                result.add(command);
            }
        }

        // Here we log the commands and also do the .00 and ~0 replacements
        for (int i = 0; i < result.size(); i++) {
            String processedCommand = result.get(i).replace(".00", "");
            processedCommand = processedCommand.replaceAll(" ~0(?!\\.\\d)", " ~");
            // Replace the current command in the result list
            result.set(i, processedCommand);
        }

        return result.toArray(new String[0]);
    }

    public static double getXDouble() {
        return calculationBlockPos[0];
    }

    public static double getYDouble() {
        return calculationBlockPos[1];
    }

    public static double getZDouble() {
        return calculationBlockPos[2];
    }
}
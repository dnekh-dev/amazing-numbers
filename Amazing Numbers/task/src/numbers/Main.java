package numbers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Constants for error messages and welcome message
    protected static final String ERROR_INVALID_NUMBER = "The first parameter should be a natural number or zero.\n";
    protected static final String ERROR_INVALID_COUNT = "The second parameter should be a natural number.";
    protected static final String WELCOME = "Welcome to Amazing Numbers!";
    protected static boolean isZero = false;

    // Enum to represent the properties of numbers
    enum Property {
        BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, JUMPING, EVEN, ODD, HAPPY, SAD;

        // Test if a number has the current property
        public boolean test(long number) {
            switch (this) {
                case BUZZ:
                    return number % 7 == 0 || number % 10 == 7;
                case DUCK:
                    return containsZero(number);
                case PALINDROMIC:
                    return isPalindromic(number);
                case GAPFUL:
                    return isGapful(number);
                case SPY:
                    return isSpy(number);
                case HAPPY:
                    return isHappy(number);
                case SAD:
                    return !isHappy(number);
                case EVEN:
                    return number % 2 == 0;
                case ODD:
                    return number % 2 != 0;
                case SQUARE:
                    return isSquare(number);
                case SUNNY:
                    return isSquare(number + 1);
                case JUMPING:
                    return isJumping(number);
                default:
                    return false;
            }
        }

        // Check if the current property is mutually exclusive with another property
        public boolean isMutuallyExclusiveWith(Property other) {
            switch (this) {
                case EVEN:
                    return other == ODD;
                case ODD:
                    return other == EVEN;
                case DUCK:
                    return other == SPY;
                case SPY:
                    return other == DUCK;
                case SUNNY:
                    return other == SQUARE;
                case SQUARE:
                    return other == SUNNY;
                case HAPPY:
                    return other == SAD;
                case SAD:
                    return other == HAPPY;
                default:
                    return false;
            }
        }

        // Helper methods to check specific properties
        private boolean isHappy(long num) {
            long slow = num;
            long fast = num;
            do {
                slow = sumOfSquaresOfDigits(slow);
                fast = sumOfSquaresOfDigits(sumOfSquaresOfDigits(fast));
            } while (slow != fast);
            return slow == 1;
        }

        private long sumOfSquaresOfDigits(long num) {
            long sum = 0;
            while (num > 0) {
                long digit = num % 10;
                sum += digit * digit;
                num /= 10;
            }
            return sum;
        }

        private boolean containsZero(long number) {
            while (number > 0) {
                if (number % 10 == 0) return true;
                number /= 10;
            }
            return false;
        }

        private boolean isPalindromic(long number) {
            String str = String.valueOf(number);
            return str.equals(new StringBuilder(str).reverse().toString());
        }

        private boolean isGapful(long number) {
            String numStr = String.valueOf(number);
            if (numStr.length() < 3) return false;
            long divisor = Long.parseLong(numStr.charAt(0) + "" + numStr.charAt(numStr.length() - 1));
            return number % divisor == 0;
        }

        private boolean isSpy(long number) {
            long sum = 0;
            long product = 1;
            while (number > 0) {
                long digit = number % 10;
                sum += digit;
                product *= digit;
                number /= 10;
            }
            return sum == product;
        }

        private boolean isSquare(long number) {
            double sqrt = Math.sqrt(number);
            return sqrt == Math.floor(sqrt);
        }

        private boolean isJumping(long number) {
            while (number > 0) {
                long digit1 = number % 10;
                number /= 10;
                if (number == 0) break;
                long digit2 = number % 10;
                if (Math.abs(digit1 - digit2) != 1) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(WELCOME);
        System.out.println();
        System.out.println(printMenu());

        // Main loop to handle user input
        mainLoop:
        while (!isZero) {
            System.out.print("Enter a request: ");
            String line = sc.nextLine().trim();

            System.out.println();

            // Handle empty input
            if (line.isEmpty()) {
                System.out.println(printMenu());
                continue;
            }

            // Handle exit condition
            if (line.equals("0")) {
                isZero = true;
                continue;
            }

            String[] parts = line.split("\\s+");

            // Validate input
            if (isNotNumeric(parts[0])) {
                System.out.println(ERROR_INVALID_NUMBER);
                continue;
            }

            if (parts.length > 1 && isNotNumeric(parts[1])) {
                System.out.println(ERROR_INVALID_COUNT);
                continue;
            }

            if (parts.length > 1 && Long.parseLong(parts[1]) < 0) {
                System.out.println(ERROR_INVALID_COUNT);
                continue;
            }

            // Lists to store requested and excluded properties
            List<Property> requestedProperties = new ArrayList<>();
            List<Property> excludedProperties = new ArrayList<>();

            // Parse properties from input
            List<String> invalidProperties = new ArrayList<>();
            for (int i = 2; i < parts.length; i++) {
                String prop = parts[i].toUpperCase();
                if (prop.startsWith("-")) {
                    prop = prop.substring(1);
                    if (isValidProperty(prop)) {
                        excludedProperties.add(Property.valueOf(prop));
                    } else {
                        invalidProperties.add(parts[i]);
                    }
                } else {
                    if (isValidProperty(prop)) {
                        requestedProperties.add(Property.valueOf(prop));
                    } else {
                        invalidProperties.add(parts[i]);
                    }
                }
            }

            // Handle mutually exclusive properties
            if (excludedProperties.contains(Property.ODD) && excludedProperties.contains(Property.EVEN)) {
                System.out.println("The request contains mutually exclusive properties: [-ODD, -EVEN]");
                System.out.println("There are no numbers with these properties.\n");
                continue;
            }

            // Handle invalid properties
            if (!invalidProperties.isEmpty()) {
                if (invalidProperties.size() == 1) {
                    System.out.println("The property [" + invalidProperties.get(0) + "] is wrong.");
                } else {
                    System.out.println("The properties " + invalidProperties + " are wrong.");
                }
                System.out.println("Available properties: " + availableProperties() + "\n");
                continue;
            }

            // Check for mutually exclusive properties in the request
            for (Property prop1 : requestedProperties) {
                for (Property prop2 : requestedProperties) {
                    if (prop1 != prop2 && prop1.isMutuallyExclusiveWith(prop2)) {
                        System.out.println("The request contains mutually exclusive properties: [" +
                                prop1 + ", " + prop2 + "]");
                        System.out.println("There are no numbers with these properties.\n");
                        continue mainLoop;
                    }
                }
            }

            // Check if a property is both requested and excluded
            for (Property prop : requestedProperties) {
                if (excludedProperties.contains(prop)) {
                    System.out.println("The request contains mutually exclusive properties: [" +
                            prop + ", -" + prop + "]");
                    System.out.println("There are no numbers with these properties.\n");
                    continue mainLoop;
                }
            }

            // Handle the EVEN and ODD properties
            if (requestedProperties.contains(Property.EVEN) && excludedProperties.contains(Property.ODD) ||
                    requestedProperties.contains(Property.ODD) && excludedProperties.contains(Property.EVEN)) {
                System.out.println("The request contains mutually exclusive properties: [EVEN, -ODD] or [ODD, -EVEN]");
                System.out.println("There are no numbers with these properties.\n");
                continue;
            }

            // Process the user's request
            if (parts.length == 1) {
                long number = Long.parseLong(parts[0]);
                if (number <= 0) {
                    System.out.println(ERROR_INVALID_NUMBER);
                    continue;
                }
                System.out.println(printSummary(number));
            } else if (parts.length == 2) {
                long start = Long.parseLong(parts[0]);
                long count = Long.parseLong(parts[1]);
                printResultString(start, count, requestedProperties, excludedProperties);
            } else if (parts.length >= 3) {
                long start = Long.parseLong(parts[0]);
                long count = Long.parseLong(parts[1]);
                printResultString(start, count, requestedProperties, excludedProperties);
            } else {
                System.out.println(ERROR_INVALID_NUMBER);
            }
        }

        System.out.println("Goodbye!");
    }

    // Print the properties of a range of numbers
    public static void printResultString(long start, long count, List<Property> properties, List<Property> excludedProperties) {
        long printedNumbers = 0;
        for (long i = start; printedNumbers < count; i++) {
            boolean matchesAllProperties = true;
            for (Property prop : properties) {
                matchesAllProperties &= prop.test(i);
            }
            for (Property prop : excludedProperties) {
                matchesAllProperties &= !prop.test(i);
            }
            if (matchesAllProperties) {
                printProperties(i); // Pass the requested properties here
                printedNumbers++;
            }
        }
        System.out.println();
    }

    // Print the properties of a single number
    public static void printProperties(long number) {
        NumberFormat nf = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%15s is ", nf.format(number)));
        for (Property prop : Property.values()) {
            if (prop.test(number)) {
                sb.append(prop.name().toLowerCase()).append(", ");
            }
        }
        System.out.println(sb.substring(0, sb.length() - 2));
    }

    // Check if a string represents a valid property
    public static boolean isValidProperty(String prop) {
        for (Property p : Property.values()) {
            if (p.name().equals(prop)) {
                return true;
            }
        }
        return false;
    }

    // Check if a string is not numeric
    public static boolean isNotNumeric(String str) {
        try {
            Long.parseLong(str);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    // Return a string representation of all available properties
    public static String availableProperties() {
        StringBuilder sb = new StringBuilder("[");
        for (Property prop : Property.values()) {
            sb.append(prop.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2) + "]";
    }

    // Print a summary of a number's properties
    public static String printSummary(long num) {
        NumberFormat nf = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("Properties of ").append(nf.format(num)).append("\n");
        for (Property prop : Property.values()) {
            sb.append(String.format("%12s: ", prop.name().toLowerCase())).append(prop.test(num)).append("\n");
        }
        return sb.toString();
    }

    // Print the menu for the user
    public static String printMenu() {
        return """
                Supported requests:
                - enter a natural number to know its properties;
                - enter two natural numbers to obtain the properties of the list:
                  * the first parameter represents a starting number;
                  * the second parameter shows how many consecutive numbers are to be processed;
                - two natural numbers and properties to search for;
                - a property preceded by minus must not be present in numbers;
                - separate the parameters with one space;
                - enter 0 to exit.
                """;
    }
}

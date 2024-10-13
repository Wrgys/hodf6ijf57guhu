import static java.lang.System.out;

public class Main {
    private static int autoOption = 1;  // Initial state


    public static void main(String[] args) {

        for (int i = 0; i < 4; i++) {
            int currentAutoState = returnCurrentAutoState();
            if(currentAutoState == 1) {
                out.print("Current state: OFF\n");
            }
            else if (currentAutoState == 2){
                out.print("Current state: REL\n");
            }
            else{
                out.print("Current state: ABS\n");
            }
        }
    }

    private static int returnCurrentAutoState() {
        switch (autoOption) {
            case 1:
                autoOption = 2;
                return 1;
            case 2:
                autoOption = 3;
                return 2;
            case 3:
                autoOption = 1;
                return 3;
        }
        return 1;
    }
}
import javafx.scene.control.TextArea;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ConsoleToGUI {

    public static void redirectSystemOut(TextArea outputArea) {

        PrintStream guiStream;
        try {
            guiStream = new PrintStream(System.out, true, "UTF-8") {
                @Override
                public void println(String s) {
                    super.println(s);
                    javafx.application.Platform.runLater(() ->
                            outputArea.appendText(s + "\n")
                    );
                }

                @Override
                public void print(String s) {
                    super.print(s);
                    javafx.application.Platform.runLater(() ->
                            outputArea.appendText(s)
                    );
                }
            };

            System.setOut(guiStream);
            System.setErr(guiStream);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

package solkris.ru.aste;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import solkris.ru.aste.desc.FontStyle;
import solkris.ru.aste.desc.Keyword;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*String text = "public class Main {\n" +
                "\tpublic void main() {\n" +
                "\t\tint a = 1;\n" +
                "\t\tint b = 1.9;\n" +
                "\n" +
                "\t\tString c = \"abc\";\n"+
                "\t}\n" +
                "a\n" +
                "}";*/
        String text = "public class Sample {\n" +
                "\tpublic Sample() {\n" +
                "\t\tint a = 1;\n" +
                "\t\tint b = 1.9;\n" +
                "\t\tString c = \"abc\";\n"+
                "\t}\n" +
                "\tpublic void method(int a) {\n" +
                "\t\tint sum = 1 + a;\n" +
                "\t\tString b = \"hello\";\n" +
                "\t\tSystem.out.println(b);\n" +
                "\t}\n" +
                "}";

        String textinhtml = "<font color=\"#FF8C00\" size=\"4\" face=\"Arial, Helvetica, sans-serif\"><u><i><b>class</font><br>" +
                "<font color=\"#00FF00\" size=\"4\" face=\"Courier New, Courier, monospace\"><u><i><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;class</font>";

        //Create an array of descriptions of keywords
        List<Keyword> keywords = new ArrayList<Keyword>();
        keywords.add(new Keyword("abstract", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("assert", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("boolean", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("break", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("byte", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("case", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("catch", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("char", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("class", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("const", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("continue", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("default", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("do", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("double", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("else", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("enum", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("extends", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("final", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("finally", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("float", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("for", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("if", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("implements", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("import", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("instanceof", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("int", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("interface", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("long", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("native", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("new", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("package", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("private", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("protected", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("public", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("return", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("try", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("static", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("strictfp", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("super", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("switch", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("synchronized", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("this", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("throw", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("throws", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("transient", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("try", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("void", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("volatile", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("while", new FontStyle(1, "FF8C00", "Arial", false, false, false)));
        keywords.add(new Keyword("\"", new FontStyle(1, "00FF00", "Arial", false, false, false)));
        keywords.add(new Keyword("'", new FontStyle(1, "00FF00", "Arial", false, false, false)));

        //Styling standard tokens
        FontStyle numbersStyle = new FontStyle(1, "0000FF", "Arial", false, true, false);
        FontStyle constStyle = new FontStyle(1, "FF0000", "Arial", false, false, false);
        FontStyle textStyle = new FontStyle(1, "000000", "Arial", false, true, false);

        //Assign by styles
        SyntaxEditText syntaxEditText = (SyntaxEditText) findViewById(R.id.set1);
        syntaxEditText.setKeywordList(keywords);
        syntaxEditText.setNumbersStyle(numbersStyle);
        syntaxEditText.setConstantsStyle(constStyle);
        syntaxEditText.setTextStyle(textStyle);
        syntaxEditText.setText(text);


        //syntaxEditText.getEditableText().replace(3, 15, Html.fromHtml("<font color=\"#FF00FF\" size=\"4\" face=\"Arial, Helvetica, sans-serif\"><u><i><b>ХУЙ</font>"));

        int a = 0;
        a = 1 - 1;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
package solkris.ru.aste;

import android.graphics.Color;
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

        String text = "public class Main {\n" +
                "   public void main() {\n" +
                "       int a = 1;\n" +
                "       int b = 1.9;\n" +
                "       String c = \"abc\";\n"+
                "   }\n" +
                "}";
        String textinhtml = "<font color=\"#FF8C00\" size=\"4\" face=\"Arial, Helvetica, sans-serif\"><u><i><b>class</font><br>" +
                "<font color=\"#00FF00\" size=\"4\" face=\"Courier New, Courier, monospace\"><u><i><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;class</font>";
        List<Keyword> keywords = new ArrayList<Keyword>();
        keywords.add(new Keyword("class", new FontStyle(4, Color.parseColor("#FF8C00"), "Arial, Helvetica, sans-serif", false, false, false)));
        keywords.add(new Keyword("public", new FontStyle(4, Color.parseColor("#FF8C00"), "Arial, Helvetica, sans-serif", false, false, false)));
        keywords.add(new Keyword("int", new FontStyle(4, Color.parseColor("#FF8C00"), "Arial, Helvetica, sans-serif", false, false, false)));
        keywords.add(new Keyword("String", new FontStyle(4, Color.parseColor("#FF8C00"), "Arial, Helvetica, sans-serif", false, false, false)));
        keywords.add(new Keyword("\"", new FontStyle(4, Color.parseColor("#00FF00"), "Arial, Helvetica, sans-serif", false, false, false)));
        FontStyle numbersStyle = new FontStyle(4, Color.parseColor("#0000FF"), "Arial, Helvetica, sans-serif", false, true, false);
        FontStyle constStyle = new FontStyle(4, Color.parseColor("#FF0000"), "Arial, Helvetica, sans-serif", false, false, false);
        FontStyle textStyle = new FontStyle(4, Color.parseColor("#FFFFFF"), "Arial, Helvetica, sans-serif", false, true, false);

        SyntaxEditText syntaxEditText = (SyntaxEditText) findViewById(R.id.set1);
        syntaxEditText.setKeywordList(keywords);
        syntaxEditText.setNumbersStyle(numbersStyle);
        syntaxEditText.setConstantsStyle(constStyle);
        syntaxEditText.setTextStyle(textStyle);
        syntaxEditText.setText(text);

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

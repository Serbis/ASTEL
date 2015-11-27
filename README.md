<div><b>Introduction</b></div>
<div>Text field for Android with support for customizable syntax highlighting and autocompletion secretions code. Distributed under license LGPL v3 as a pluggable external library. Stated possibilities text editor, green marked realized the problem:</div>
<ul>
	<li><font color="#006400">Highlighting arbitrary syntax</font></li>
	<li><font color="#006400">Flexible adjustment of colors</font></li>
	<li>Highlighting arbitrary domains (errors, warnings)</li>
	<li>Autocomplete complementary characters</li>
	<li>Customizable line numbering</li>
	<li>Autocompletion code</li>
	<li>Supports context-sensitive language grammars</li>
</ul>
<div>The editor does not have a clearly defined syntax is and works with any kind of context-free grammars of language, such as Java, C / C ++, Python, PHP, and the like. In the future development of the lexical analyzer for context-sensitive language constructs. How it works? You load the library widgets and disposes it as a normal EditText, and then assign a style set for the keywords, constants, numbers, and pure text. After this, any text entered will be conducted through the lexical analyzer in the case of keywords be painted in the color you specify. For more detailed instructions on how to quickly set up and principles of the library, refer to the wiki.</div>

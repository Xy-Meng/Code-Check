import java.util.ArrayList;



import java.util.List;

/**
 *
 *
//    ___  _____  ____  ____  ___  _   _  ____  ___  _  _
//  / __)(  _  )(  _ \( ___)/ __)( )_( )( ___)/ __)( )/ )
// ( (__  )(_)(  )(_) ))__)( (__  ) _ (  )__)( (__  )  (
// \___)(_____)(____/(____)\___)(_) (_)(____)\___)(_)\_)

 Benjamin Ledford, Christopher Francis-Christie

 Version 1.0

 * CodeCheck API is a lightweight class automatically included with any Java project built using CodeCheck plugin
 * It runs alongside Java programs compiled using the CodeCheck plugin and runs with little overhead.
 * If a possible security exploit is detected, the exploit is caught and handled with a thrown Exception.
 *
 * CodeCheck API is the most important class and is input validator for CodeCheck
 * It is analogous to ASIDE.validator in the ASIDE project.
 *
 *
 *
 *
 * In ASIDE for Eclipse, after the user selects the guidelines for safe code ASIDE then generates better code
 * using a call to the ASIDE.validator.getValidInput()
 *
 *
 * Similarly, we should call the CodeCheck.validator API when we generate the secure code.
 * The CodeCheck.validator API call is generated as part of the securely generated code.
 *
 *
 * FAQ:
 * Q: Class CodeCheck is not visible to my current file. Why?
 * A: This Java file is automatically generated in every source folder.
 * Try changing your package statement so that this file matches the package of your classes.
 * So that you can see this class.
 *
 * Q: I've modified this file but I need the original file back. What do I do?
 * A: Delete this file. Then run a new inspection. It will create a new version of this file fresh.
 *
 * Q: How do I add new validation rules?
 * A: You don't need to modify this file. There is an area in the Settings for CodeCheck
 * You can input all of the data for the new input validation rule, as well as a regexp.
 * **************** If you find that that is not enough, you may modify this file. :)
 *
 *
 *
 * Jargon/Lexicon:
 *
 * 1. Validation Type: A type of input to be validated, includes Filenames, SQLQueries, etc
 * 2. Validation Rule: A rule to apply to a validated type, usually in the form of a regular expression
 *
 */
public class CodeCheck
{

    /*

    See config.xml to see the list of currently handled Validation Types

    */
    private ArrayList<String> _types;
    private ArrayList<String> _rules;



    public class validator {


        public static validator()
        {
            _types = new ArrayList<String>();
            //... TODO: Read config.xml here for all possible ValidationTypes with a for loop

            _rules = new ArrayList<String>();
            //... TODO: Read config.xml here for all possible ValidationRules with a for loop

        }

        public static void displayTypes()
        {

        }

        public static String getValidInputString(String b)
        {

        }
    }
}

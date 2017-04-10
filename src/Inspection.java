package com.intellij.codeInspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.impl.ModuleRootManagerImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileSystemItemUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * @author max
 */
public class Inspection extends BaseJavaLocalInspectionTool {
    private static final Logger LOG = Logger.getInstance("#com.intellij.codeInspection.Inspection");

    private final LocalQuickFix myQuickFix = new MyQuickFix();
    private final LocalQuickFix callFix = new CallFix();

    public static JFrame validateInputbe = new JFrame("Validate Input"); // The Input Validation Window for a Binary Expression
    public static JFrame validateInputae = new JFrame("Validate Input"); // The Input Validation Window for a Binary Expression
    public static JFrame validateInputme = new JFrame("Validate Input"); // The Input Validation Window for a Assignment Expression
    public static boolean notDone = true;

    @SuppressWarnings({"WeakerAccess"})
    @NonNls
    public String CHECKED_CLASSES = "java.lang.String;java.util.Date";
    @NonNls

    public PsiType[] types; //Just a variable used within the functions

    //IMPORTANT! This is the string that reports the actual error.
    private static final String DESCRIPTION_TEMPLATE =
            "CodeCheck(tm) has flagged the statement: " +
                    InspectionsBundle.message("inspection.comparing.references.problem.descriptor").substring(22, 31)
            + ". \n Input data (String) on the right hand side of the expression has not been validated";
    private static final String BINARY_EXPRESSION =
            "Unsafe Boolean Expression: CodeCheck* has flagged the boolean expression operand: '" +
                    InspectionsBundle.message("inspection.comparing.references.problem.descriptor").substring(22, 31) + "'";

    private static final String METHOD_CALL_EXPRESSION =
            "Unsafe Call: CodeCheck* has flagged the call: " +
                    InspectionsBundle.message("inspection.comparing.references.problem.descriptor").substring(22, 31) + ". Return value of call is of unvalidated type.";

    private static final String METHOD_CALL_ARGUMENT =
            "Unsafe Argument Call: CodeCheck* has flagged the call argument: '" +
                    InspectionsBundle.message("inspection.comparing.references.problem.descriptor").substring(22, 31) + "'";


    private static final String ASSIGNMENT_EXPRESSION =
            "Unsafe Assignment Expression: CodeCheck* has flagged the expression: " +
                    InspectionsBundle.message("inspection.comparing.references.problem.descriptor").substring(22, 31)
                    + ". \n Right operand of expression is of unvalidated type.";


    @NotNull
    public String getDisplayName() {

        return "CodeCheck(tm) has found an issue with possible unvalidated input (String) during the";

    }



    @NotNull
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String getShortName() {
        return "CodeCheck";
    }
    public Routines r = new Routines();

    private boolean isCheckedType(PsiType type) {
        if (!(type instanceof PsiClassType)) return false;


        StringTokenizer tokenizer = new StringTokenizer(CHECKED_CLASSES, ";");
        while (tokenizer.hasMoreTokens()) {
            String className = tokenizer.nextToken();
            if (type.equalsToText(className)) return true;
        }


        return false;
    }

    @NotNull
    @Override
    /*
    Override some methods here
    https://github.com/JetBrains/intellij-community/blob/master/java/java-psi-api/src/com/intellij/psi/JavaElementVisitor.java
     */
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitReferenceExpression(PsiReferenceExpression psiReferenceExpression) {
            }


            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);

                PsiExpression rOperand = expression.getRExpression();
                PsiType rType = rOperand.getType();

                if (isCheckedType(rType)) {
                    holder.registerProblem(rOperand,
                            ASSIGNMENT_EXPRESSION, myQuickFix);
                }

            }

            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                IElementType opSign = expression.getOperationTokenType();
                //Handles the operators used, can be ==, !=, =, +=, or +
                if (opSign == JavaTokenType.EQEQ || opSign == JavaTokenType.NE )
                {  //  || opSign == JavaTokenType.PLUS) {
                    PsiExpression lOperand = expression.getLOperand();
                    PsiExpression rOperand = expression.getROperand();
                    if (rOperand == null || isNullLiteral(lOperand) || isNullLiteral(rOperand)) return;

                    PsiType lType = lOperand.getType();
                    PsiType rType = rOperand.getType();

                    if (isCheckedType(lType)) {
                        holder.registerProblem(lOperand,
                                BINARY_EXPRESSION + ". \n Type '" + lType.getPresentableText() + "' of left operand is unvalidated.", myQuickFix);
                    }

                    if (isCheckedType(rType)) {
                        holder.registerProblem(rOperand,
                                BINARY_EXPRESSION + ". \n Type '" + rType.getPresentableText() + "' of right operand is unvalidated.", myQuickFix);
                    }
                }
            }

            /*
            Overridden. Ensures that inputs fed into a call expression are safe.
             */
            @Override
            public void visitCallExpression(PsiCallExpression expression) {
                super.visitCallExpression(expression);
                //Get the types of all of the arguments, if one matches our checked classes, then we FLAG the CODE!

                    PsiExpressionList list = expression.getArgumentList();
                    PsiExpression[] type = list.getExpressions();

/*
                    if (isCheckedType(expression.getType())) //Check the call
                        holder.registerProblem(expression,
                                METHOD_CALL_EXPRESSION , myQuickFix);
*/

                for (int i = 0; i < type.length; i++) { //Check the call arguments
                        if (isCheckedType(type[i].getType()))
                            holder.registerProblem(type[i],
                                    METHOD_CALL_ARGUMENT + ". Argument '" + type[i].getText() + "' is of unvalidated type '" + type[i].getType().getPresentableText() + "'", callFix);
                    }

                }
            };

    }

    private static boolean isNullLiteral(PsiExpression expr) {
        return expr instanceof PsiLiteralExpression && "null".equals(expr.getText());
    }

    /*
    A class for the JPanels that handle the Input Validation
     */
    private static class JPanelWindow implements Runnable {


        @Override
        public void run() {

            JDialog validateInputce = new JDialog(new Frame(), ""); //("Validate Call Input"); // The Input Validation Window for a Call Expression
            System.out.println("Started thread");

            validateInputce.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    notDone = false;
                    System.out.println("Closed Window");
                    validateInputce.setVisible(false);
                }

                public void windowLostFocus(WindowEvent e) {
                    //validateInputce.setFocusableWindowState(true);
                }
            });

            validateInputce.setSize(400, 600);    // Set size of frame
            validateInputce.setVisible(true);

            validateInputce.repaint();
            validateInputce.revalidate();


        }
    }


    /*
    A class for call fixes
     */
    private static class CallFixThread implements Runnable {

        Project project;
        ProblemDescriptor descriptor;

        @Override
        public void run() {
        if (!notDone) {

        }
        }
    }
    /*
    A class of fixes for unsafe calls.
     */
    private static class CallFix implements LocalQuickFix {

        @NotNull
        public String getName() {
            // The test (see the TestThisPlugin class) uses this string to identify the quick fix action.
            return "Validate Call Argument..";//InspectionsBundle.message("inspection.comparing.references.use.quickfix");
        }

        void addCodeCheckAPI(@NotNull Project project)
        {

            ModuleManager manager = ModuleManager.getInstance(project);
            Module[] modules = manager.getModules();
            for (Module module : modules) {
                ModuleRootManager root = ModuleRootManager.getInstance(module);
                //Search through all of the Source Directories
                for (VirtualFile file : root.getSourceRoots()) {
                    //Create a copy of the CodeCheck API within each directory

                    File f = new File(file.getPath().replace("/", "\\") + "\\CodeCheck.java");

                    Routines r = new Routines();
                    if (!f.exists()) {
                        String s  = r.LoadResource("CodeCheck.java");

                        try {

                            PrintWriter writer = new PrintWriter(file.getPath().replace("/", "\\") + "\\CodeCheck.java");
                            writer.write(s);

                            writer.close();
                        } catch (Exception e) {
                        }
                        ;
                    }
                    System.out.println("CC* Created CodeCheck API: " + file.getPath().replace("/", "\\") + "\\CodeCheck.java");


                    File f2 = new File(file.getPath().replace("/", "\\") + "\\config.xml");
                 }
            }
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            //ensure that CodeCheck.java exists

            //Show the validation pane for Binary Expression


            addCodeCheckAPI(project);

            //Create a second thread for the CodeCheck fix
            CallFixThread cf = new CallFixThread();
            try {

                //get the argument
                PsiExpression arg = (PsiExpression) descriptor.getPsiElement();
                //get the current instance of IntelliJ and create a "factory" to generate better code
                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();


                //create a safer call to CodeCheck.validator
                PsiMethodCallExpression safeCall =
                        (PsiMethodCallExpression) factory.createExpressionFromText(
                                "CodeCheck.validator.getValidInputString(a)", null);
                //replace our code with our safer call
                safeCall.getArgumentList().getExpressions()[0].replace(arg);
                arg.replace(safeCall);

            } catch (IncorrectOperationException e) {
                LOG.error(e);

            }








        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }
    }
    /*
    This member returns the string for the form of "fix" needed
     */
    private static class MyQuickFix implements LocalQuickFix {
        @NotNull
        public String getName() {
            // The test (see the TestThisPlugin class) uses this string to identify the quick fix action.
            return "Validate Input..";//InspectionsBundle.message("inspection.comparing.references.use.quickfix");

        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

            //Show the validation pane for Binary Expression
            validateInputbe.setSize(400,300);	// Set size of frame
            validateInputbe.setVisible(true);

            try {
                PsiBinaryExpression binaryExpression = (PsiBinaryExpression) descriptor.getPsiElement();
                IElementType opSign = binaryExpression.getOperationTokenType();
                PsiExpression lExpr = binaryExpression.getLOperand();
                PsiExpression rExpr = binaryExpression.getROperand();
                if (rExpr == null)
                    return;



                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiMethodCallExpression equalsCall =
                        (PsiMethodCallExpression) factory.createExpressionFromText("a.equals(b)", null);

                equalsCall.getMethodExpression().getQualifierExpression().replace(lExpr);
                equalsCall.getArgumentList().getExpressions()[0].replace(rExpr);

                PsiExpression result = (PsiExpression) binaryExpression.replace(equalsCall);

                if (opSign == JavaTokenType.NE) {
                    PsiPrefixExpression negation = (PsiPrefixExpression) factory.createExpressionFromText("!a", null);
                    negation.getOperand().replace(result);
                    result.replace(negation);
                }
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }
    }

    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JTextField checkedClasses = new JTextField(CHECKED_CLASSES);
        checkedClasses.getDocument().addDocumentListener(new DocumentAdapter() {
            public void textChanged(DocumentEvent event) {
                CHECKED_CLASSES = checkedClasses.getText();
            }
        });

        panel.add(checkedClasses);
        return panel;
    }

    public boolean isEnabledByDefault() {
        return true;
    }
}
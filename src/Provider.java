package com.intellij.codeInspection;
import com.intellij.*;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;

/**
 * @author max
 */
public class Provider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{Inspection.class};
    }

}

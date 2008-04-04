/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

public class ClassNamingConventions extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (Character.isLowerCase(node.getImage().charAt(0))) {
            addViolation(data, node);
        }
        return data;
    }
}

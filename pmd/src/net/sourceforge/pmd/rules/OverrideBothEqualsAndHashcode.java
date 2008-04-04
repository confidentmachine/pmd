package net.sourceforge.pmd.rules;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

public class OverrideBothEqualsAndHashcode extends AbstractJavaRule {

    private boolean implementsComparable = false;

    private boolean containsEquals = false;

    private boolean containsHashCode = false;
    
    private Node nodeFound = null;

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        super.visit(node, data);
        if (!implementsComparable && (containsEquals ^ containsHashCode)) {
            if(nodeFound == null){
                nodeFound = node;
            }
            addViolation(data, nodeFound);
        }
        implementsComparable = containsEquals = containsHashCode = false;
        nodeFound = null;
        return data;
    }

    public Object visit(ASTImplementsList node, Object data) {
        for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
            if (node.jjtGetChild(ix).getClass().equals(ASTClassOrInterfaceType.class)) {
                ASTClassOrInterfaceType cit = (ASTClassOrInterfaceType)node.jjtGetChild(ix);
                Class clazz = cit.getType();
                if (clazz != null || node.jjtGetChild(ix).hasImageEqualTo("Comparable")) {
                    implementsComparable = true;
                    return data;
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (implementsComparable) {
            return data;
        }

        int iFormalParams = 0;
        String paramName = null;
        for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
            Node sn = node.jjtGetChild(ix);
            if (sn.getClass().equals(ASTFormalParameters.class)) {
                List<ASTFormalParameter> allParams = ((ASTFormalParameters) sn).findChildrenOfType(ASTFormalParameter.class);
                for (ASTFormalParameter formalParam: allParams) {
                    iFormalParams++;
                    ASTClassOrInterfaceType param = formalParam.getFirstChildOfType(ASTClassOrInterfaceType.class);
                    if (param != null) {
                        paramName = param.getImage();
                    }
                }
            }
        }

        if (iFormalParams == 0 && node.hasImageEqualTo("hashCode")) {
            containsHashCode = true;
            nodeFound = node;
        } else if (iFormalParams == 1 && node.hasImageEqualTo("equals") && ("Object".equals(paramName) || "java.lang.Object".equals(paramName))) {
            containsEquals = true;
            nodeFound = node;
        }
        return super.visit(node, data);
    }

}

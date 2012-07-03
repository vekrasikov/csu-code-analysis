package com.example.atb.nodes.handlers;

import com.example.atb.core.TreeWalker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement;

public class JCLabeledStatementHandler extends JCTreeHandler {

    public JCLabeledStatementHandler(TreeWalker walker) {
        super(walker);
    }

    @Override
    protected void execute(JCTree node) {
        JCLabeledStatement lStmt = JCLabeledStatement.class.cast(node);
        walker.handle(lStmt.label, "nodename.label");
        walker.handle(lStmt.body, "nodename.body");
    }

}

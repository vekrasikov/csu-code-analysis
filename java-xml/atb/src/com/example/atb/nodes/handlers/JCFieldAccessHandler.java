package com.example.atb.nodes.handlers;

import com.example.atb.core.TreeWalker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;

public class JCFieldAccessHandler extends JCTreeHandler {

    public JCFieldAccessHandler(TreeWalker walker) {
        super(walker);
    }

    @Override
    protected void execute(JCTree node) {
        JCFieldAccess fa = JCFieldAccess.class.cast(node);
        walker.handle(fa.name, "nodename.name");
        walker.handle(fa.selected, "nodename.selected");
        walker.handle(fa.sym, "nodename.symbol");
    }

}

package com.example.atb.nodes.handlers;

import com.example.atb.core.TreeWalker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;

public class JCDoWhileLoopHandler extends JCTreeHandler {

    public JCDoWhileLoopHandler(TreeWalker walker) {
        super(walker);
    }

    @Override
    protected void execute(JCTree node) {
        JCDoWhileLoop loop = JCDoWhileLoop.class.cast(node);
        walker.handle(loop.body, "nodename.body");
        walker.handle(loop.cond, "nodename.condition");
    }

}

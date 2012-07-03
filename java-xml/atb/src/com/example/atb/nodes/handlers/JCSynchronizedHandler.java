package com.example.atb.nodes.handlers;

import com.example.atb.core.TreeWalker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCSynchronized;

public class JCSynchronizedHandler extends JCTreeHandler {

    public JCSynchronizedHandler(TreeWalker walker) {
        super(walker);
    }

    @Override
    protected void execute(JCTree node) {
        JCSynchronized sinc = JCSynchronized.class.cast(node);
        walker.handle(sinc.lock, "nodename.lock_object");
        walker.handle(sinc.body, "nodename.body");
    }

}

package com.example.atb.main;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.TypeKind;

import com.example.atb.core.BypassException;
import com.example.atb.core.ContentAssistant;
import com.example.atb.core.Messages;
import com.example.atb.core.TraversalHandler;
import com.example.atb.core.TreeWalker;
import com.example.atb.core.TraversalHandler.Position;
import com.example.atb.nodes.handlers.JCAnnotationHandler;
import com.example.atb.nodes.handlers.JCArrayAccessHandler;
import com.example.atb.nodes.handlers.JCArrayTypeTreeHandler;
import com.example.atb.nodes.handlers.JCAssertHandler;
import com.example.atb.nodes.handlers.JCAssignHandler;
import com.example.atb.nodes.handlers.JCAssignOpHandler;
import com.example.atb.nodes.handlers.JCBinaryHandler;
import com.example.atb.nodes.handlers.JCBlockHandler;
import com.example.atb.nodes.handlers.JCBreakHandler;
import com.example.atb.nodes.handlers.JCCaseHandler;
import com.example.atb.nodes.handlers.JCCatchHandler;
import com.example.atb.nodes.handlers.JCClassDeclHandler;
import com.example.atb.nodes.handlers.JCCompilationUnitHandler;
import com.example.atb.nodes.handlers.JCConditionalHandler;
import com.example.atb.nodes.handlers.JCContinueHandler;
import com.example.atb.nodes.handlers.JCDoWhileLoopHandler;
import com.example.atb.nodes.handlers.JCEnhancedForLoopHandler;
import com.example.atb.nodes.handlers.JCErroneousHandler;
import com.example.atb.nodes.handlers.JCExpressionStatementHandler;
import com.example.atb.nodes.handlers.JCFieldAccessHandler;
import com.example.atb.nodes.handlers.JCForLoopHandler;
import com.example.atb.nodes.handlers.JCIdentHandler;
import com.example.atb.nodes.handlers.JCIfHandler;
import com.example.atb.nodes.handlers.JCImportHandler;
import com.example.atb.nodes.handlers.JCInstanceOfHandler;
import com.example.atb.nodes.handlers.JCLabeledStatementHandler;
import com.example.atb.nodes.handlers.JCLiteralHandler;
import com.example.atb.nodes.handlers.JCMethodDeclHandler;
import com.example.atb.nodes.handlers.JCMethodInvocationHandler;
import com.example.atb.nodes.handlers.JCModifiersHandler;
import com.example.atb.nodes.handlers.JCNewArrayHandler;
import com.example.atb.nodes.handlers.JCNewClassHandler;
import com.example.atb.nodes.handlers.JCParensHandler;
import com.example.atb.nodes.handlers.JCPrimitiveTypeTreeHandler;
import com.example.atb.nodes.handlers.JCReturnHandler;
import com.example.atb.nodes.handlers.JCSkipHandler;
import com.example.atb.nodes.handlers.JCSwitchHandler;
import com.example.atb.nodes.handlers.JCSynchronizedHandler;
import com.example.atb.nodes.handlers.JCThrowHandler;
import com.example.atb.nodes.handlers.JCTreeHandler;
import com.example.atb.nodes.handlers.JCTryHandler;
import com.example.atb.nodes.handlers.JCTypeApplyHandler;
import com.example.atb.nodes.handlers.JCTypeCastHandler;
import com.example.atb.nodes.handlers.JCTypeParameterHandler;
import com.example.atb.nodes.handlers.JCUnaryHandler;
import com.example.atb.nodes.handlers.JCVariableDeclHandler;
import com.example.atb.nodes.handlers.JCWhileLoopHandler;
import com.example.atb.nodes.handlers.JCWildcardHandler;
import com.example.atb.nodes.handlers.LetExprHandler;
import com.example.atb.nodes.handlers.TypeBoundKindHandler;
import com.example.atb.symbols.handlers.SymbolHandler;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCContinue;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCErroneous;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCSkip;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCSynchronized;
import com.sun.tools.javac.tree.JCTree.JCThrow;
import com.sun.tools.javac.tree.JCTree.JCTry;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCWildcard;
import com.sun.tools.javac.tree.JCTree.LetExpr;
import com.sun.tools.javac.tree.JCTree.TypeBoundKind;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Position.LineMap;

public class TreeWalkerImpl implements TreeWalker {

	private final LineMap lineMap;
	private final JCTree root;

	private Collection<TraversalHandler> handlers = new HashSet<TraversalHandler>();

	public TreeWalkerImpl(JCTree root, LineMap lineMap) {
		if (lineMap == null) {
			throw new NullPointerException("Line map is null");
		}
		if (root == null) {
			throw new NullPointerException("Root is null");
		}
		this.root = root;
		this.lineMap = lineMap;
	}

	public void executeBypass() {
		handle(root, root.getClass().getCanonicalName());
	}

	public void addBypassHandler(TraversalHandler handler) {
		handlers.add(handler);
	}

	public void removeBypassHandler(TraversalHandler handler) {
		handlers.remove(handler);
	}

	public final void handle(Symbol symbol, String innerName) {
		handleSymbol(symbol, getNodeName(innerName));
	}

	public final void handle(Type type, String innerName) {
		onType(type, getNodeName(innerName));
	}

	public final void handle(Name nameElement, String innerName) {
		onName(nameElement, getNodeName(innerName));
	}

	public final void handle(JCTree node, String innerName) {
		onHandleNode(node, getNodeName(innerName));
	}

	public final void handle(List<? extends JCTree> nodesList, String innerName) {
		onHandleList(nodesList, getNodeName(innerName));
	}

	public final void handleFlags(long flags) {
		onFlags(flags);
	}

	public final void handlePrimitiveType(TypeKind typeKind) {
		onPrimitiveType(typeKind);
	}

	private void handleSymbol(Symbol symbol, String innerName) {
		if (symbol == null) {

		} else {
			onSymbolStart(symbol, innerName);
			try {
				SymbolHandler<? extends Symbol> handler = getHandler(symbol);
				handler.perform();
			} catch (BypassException e) {
				onErrorOcured(e);
			}
			onSymbolEnd(symbol, innerName);
		}
	}

	protected void onHandleNode(JCTree node, String name) {
		if (node == null) {
			onNullNode(name);
		} else {
			Position position = PositionImpl.createPosition(lineMap, node
					.getStartPosition());
			onStartNode(node, name, position);
			if (JCLiteral.class.isInstance(node)) {
				onLiteral(JCLiteral.class.cast(node));
			} else if (JCSkip.class.isInstance(node)) {
				onEmptyElement();
			} else if (TypeBoundKind.class.isInstance(node)) {
				onBoundKind(TypeBoundKind.class.cast(node).kind);
			} else {
				try {
					JCTreeHandler handler = getHandler(node);
					handler.perform(node);
				} catch (BypassException e) {
					onErrorOcured(e);
				}
			}
			onEndNode(node, name, position);
		}
	}

	protected void onHandleList(List<? extends JCTree> nodesList, String name) {
		if (nodesList == null || nodesList.size() == 0) {
			onNullNodesList(name);
		} else {
			onStartNodesList(nodesList, name);
			int i = 0;
			for (JCTree node : nodesList) {
				onHandleNode(node, "I" + i++);
			}
			onEndNodesList(nodesList, name);
		}
	}

	protected void onType(Type type, String name) {
		for (TraversalHandler handler : handlers) {
			handler.onType(type, name);
		}
	}

	protected void onName(Name nameElement, String name) {
		for (TraversalHandler handler : handlers) {
			handler.onName(nameElement, name);
		}
	}

	protected void onSymbolStart(Symbol symbol, String name) {
		for (TraversalHandler handler : handlers) {
			handler.onSymbolStart(symbol, name);
		}
	}

	protected void onSymbolEnd(Symbol symbol, String name) {
		for (TraversalHandler handler : handlers) {
			handler.onSymbolEnd(symbol, name);
		}
	}

	protected void onNullSymbol(String name) {
		for (TraversalHandler handler : handlers) {
			handler.onNullSymbol(name);
		}
	}

	protected void onStartNode(JCTree node, String name, Position position) {
		for (TraversalHandler handler : handlers) {
			handler.onStartNode(node, name, position);
		}
	}

	protected void onEndNode(JCTree node, String name, Position position) {
		for (TraversalHandler handler : handlers) {
			handler.onEndNode(node, name, position);
		}
	}

	protected void onNullNode(String name) {
		for (TraversalHandler handler : handlers) {
			handler.onNullNode(name);
		}
	}

	protected void onStartNodesList(List<? extends JCTree> nodesList,
			String name) {
		for (TraversalHandler handler : handlers) {
			handler.onStartNodesList(nodesList, name);
		}
	}

	protected void onEndNodesList(List<? extends JCTree> nodesList, String name) {
		for (TraversalHandler handler : handlers) {
			handler.onEndNodesList(nodesList, name);
		}
	}

	protected void onNullNodesList(String name) {
		for (TraversalHandler handler : handlers) {
			handler.onNullNodesList(name);
		}
	}

	protected void onLiteral(JCLiteral literal) {
		for (TraversalHandler handler : handlers) {
			handler.onLiteral(literal.value, literal.getKind());
		}
	}

	protected void onEmptyElement() {
		for (TraversalHandler handler : handlers) {
			handler.onEmptyStatement();
		}
	}

	protected void onFlags(long flags) {
		for (TraversalHandler handler : handlers) {
			handler.onFlags(flags);
		}
	}

	protected void onPrimitiveType(TypeKind primitiveType) {
		for (TraversalHandler handler : handlers) {
			handler.onPrimitiveType(primitiveType);
		}
	}

	protected void onBoundKind(BoundKind boundKind) {
		for (TraversalHandler handler : handlers) {
			handler.onBoundKind(boundKind);
		}
	}

	protected void onErrorOcured(Exception e) {
		for (TraversalHandler handler : handlers) {
			handler.onErrorOcured(e);
		}
	}

	private String getNodeName(String innerName) {
		String name = ContentAssistant.getNodeName(innerName);
		if (name == null) {
			name = innerName;
		}
		return name;
	}

	@SuppressWarnings("unchecked")
	private <K extends Symbol> SymbolHandler<K> getHandler(K symbol)
			throws BypassException {
		Class<? extends SymbolHandler<?>> symbolHandlerClass = symbolsHandlers
				.get(symbol.getClass());
		if (symbolHandlerClass == null) {
			throw new BypassException(Messages.MESSAGE_ATB102E, symbol
					.getClass().getCanonicalName());
		}
		try {
			Constructor<? extends SymbolHandler<?>> constructor = symbolHandlerClass
					.getConstructor(TreeWalker.class, symbol.getClass());
			if (constructor == null) {
				throw new BypassException(Messages.MESSAGE_ATB103E, symbol
						.getClass().getCanonicalName());
			}
			return (SymbolHandler<K>) constructor.newInstance(this, symbol);
		} catch (Exception e) {
			throw new BypassException(Messages.MESSAGE_ATB103E, e, symbol
					.getClass().getCanonicalName());
		}
	}

	private static final Map<Class<? extends Symbol>, Class<? extends SymbolHandler<?>>> symbolsHandlers = new HashMap<Class<? extends Symbol>, Class<? extends SymbolHandler<?>>>();

	static {

	}

	private JCTreeHandler getHandler(JCTree node) throws BypassException {
		Class<? extends JCTreeHandler> handlerClass = nodeHandlers.get(node
				.getClass());
		if (handlerClass == null) {
			throw new BypassException(Messages.MESSAGE_ATB100E, node.getClass()
					.getCanonicalName());
		}
		try {
			Constructor<? extends JCTreeHandler> constructor = handlerClass
					.getConstructor(TreeWalker.class);
			if (constructor == null) {
				throw new BypassException(Messages.MESSAGE_ATB101E, node
						.getClass().getCanonicalName());
			}
			return constructor.newInstance(this);
		} catch (Exception e) {
			throw new BypassException(Messages.MESSAGE_ATB101E, e, node
					.getClass().getCanonicalName());
		}
	}

	private static final Map<Class<? extends JCTree>, Class<? extends JCTreeHandler>> nodeHandlers = new HashMap<Class<? extends JCTree>, Class<? extends JCTreeHandler>>();

	static {
		nodeHandlers.put(JCAnnotation.class, JCAnnotationHandler.class);
		nodeHandlers.put(JCArrayAccess.class, JCArrayAccessHandler.class);
		nodeHandlers.put(JCArrayTypeTree.class, JCArrayTypeTreeHandler.class);
		nodeHandlers.put(JCAssert.class, JCAssertHandler.class);
		nodeHandlers.put(JCAssign.class, JCAssignHandler.class);
		nodeHandlers.put(JCAssignOp.class, JCAssignOpHandler.class);
		nodeHandlers.put(JCBinary.class, JCBinaryHandler.class);
		nodeHandlers.put(JCBlock.class, JCBlockHandler.class);
		nodeHandlers.put(JCBreak.class, JCBreakHandler.class);
		nodeHandlers.put(JCCase.class, JCCaseHandler.class);
		nodeHandlers.put(JCCatch.class, JCCatchHandler.class);
		nodeHandlers.put(JCClassDecl.class, JCClassDeclHandler.class);
		nodeHandlers.put(JCCompilationUnit.class,
				JCCompilationUnitHandler.class);
		nodeHandlers.put(JCConditional.class, JCConditionalHandler.class);
		nodeHandlers.put(JCContinue.class, JCContinueHandler.class);
		nodeHandlers.put(JCDoWhileLoop.class, JCDoWhileLoopHandler.class);
		nodeHandlers.put(JCEnhancedForLoop.class,
				JCEnhancedForLoopHandler.class);
		nodeHandlers.put(JCErroneous.class, JCErroneousHandler.class);
		nodeHandlers.put(JCExpressionStatement.class,
				JCExpressionStatementHandler.class);
		nodeHandlers.put(JCFieldAccess.class, JCFieldAccessHandler.class);
		nodeHandlers.put(JCForLoop.class, JCForLoopHandler.class);
		nodeHandlers.put(JCIdent.class, JCIdentHandler.class);
		nodeHandlers.put(JCIf.class, JCIfHandler.class);
		nodeHandlers.put(JCImport.class, JCImportHandler.class);
		nodeHandlers.put(JCInstanceOf.class, JCInstanceOfHandler.class);
		nodeHandlers.put(JCLabeledStatement.class,
				JCLabeledStatementHandler.class);
		nodeHandlers.put(JCLiteral.class, JCLiteralHandler.class);
		nodeHandlers.put(JCMethodDecl.class, JCMethodDeclHandler.class);
		nodeHandlers.put(JCMethodInvocation.class,
				JCMethodInvocationHandler.class);
		nodeHandlers.put(JCModifiers.class, JCModifiersHandler.class);
		nodeHandlers.put(JCNewArray.class, JCNewArrayHandler.class);
		nodeHandlers.put(JCNewClass.class, JCNewClassHandler.class);
		nodeHandlers.put(JCParens.class, JCParensHandler.class);
		nodeHandlers.put(JCPrimitiveTypeTree.class,
				JCPrimitiveTypeTreeHandler.class);
		nodeHandlers.put(JCReturn.class, JCReturnHandler.class);
		nodeHandlers.put(JCSkip.class, JCSkipHandler.class);
		nodeHandlers.put(JCSwitch.class, JCSwitchHandler.class);
		nodeHandlers.put(JCSynchronized.class, JCSynchronizedHandler.class);
		nodeHandlers.put(JCThrow.class, JCThrowHandler.class);
		nodeHandlers.put(JCTry.class, JCTryHandler.class);
		nodeHandlers.put(JCTypeApply.class, JCTypeApplyHandler.class);
		nodeHandlers.put(JCTypeCast.class, JCTypeCastHandler.class);
		nodeHandlers.put(JCTypeParameter.class, JCTypeParameterHandler.class);
		nodeHandlers.put(JCUnary.class, JCUnaryHandler.class);
		nodeHandlers.put(JCVariableDecl.class, JCVariableDeclHandler.class);
		nodeHandlers.put(JCWhileLoop.class, JCWhileLoopHandler.class);
		nodeHandlers.put(JCWildcard.class, JCWildcardHandler.class);
		nodeHandlers.put(LetExpr.class, LetExprHandler.class);
		nodeHandlers.put(TypeBoundKind.class, TypeBoundKindHandler.class);

	}

	private static class PositionImpl implements Position {

		private final int line;
		private final int column;

		private PositionImpl(int line, int column) {
			this.line = line;
			this.column = column;
		}

		@Override
		public int getColumnNumber() {
			return column;
		}

		@Override
		public int getLineNumber() {
			return line;
		}

		public static Position createPosition(LineMap lineMap, int position) {
			if (position < 0) {
				return new PositionImpl(-1, -1);
			}
			return new PositionImpl(lineMap.getLineNumber(position), lineMap
					.getColumnNumber(position));
		}

	}

}
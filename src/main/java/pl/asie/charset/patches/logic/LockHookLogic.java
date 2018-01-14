/*
 * Copyright (c) 2017, 2018 Adrian Siekierka
 *
 * This file is part of CharsetPatches.
 *
 * CharsetPatches is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CharsetPatches is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CharsetPatches.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.asie.charset.patches.logic;

import com.google.common.io.ByteStreams;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;
import pl.asie.charset.patches.CharsetPatchTransformer;

import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

public final class LockHookLogic {
	private static final Map<String, byte[]> byteCache = new HashMap<>();

	private static ClassNode node;
	private static Map<String, MethodNode> nodeMethods;

	private LockHookLogic() {

	}

	private static ClassNode load(String c) {
		node = new ClassNode();

		if (!byteCache.containsKey(c)) {
			InputStream stream = LockHookLogic.class.getClassLoader().getResourceAsStream(c);
			try {
				byteCache.put(c, ByteStreams.toByteArray(stream));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		ClassReader reader = new ClassReader(byteCache.get(c));
		reader.accept(node, ClassReader.SKIP_DEBUG);
		return node;
	}

	private static void initialize() {
		node = load("pl/asie/charset/patches/LockHooks.class");
		nodeMethods = new HashMap<>();
		for (MethodNode methodNode : node.methods) {
			nodeMethods.put(methodNode.name+methodNode.desc, methodNode);
		}
	}

	public static MethodNode inject(@Nullable MethodNode method, MethodNode srcMethod, ClassNode target, String srcClassName, String baseClassName) {
		ListIterator<AbstractInsnNode> it;

		String suffix = "_postCharset";

		System.out.println("Injecting " + srcMethod.name + " hook into " + target.name);
		if (method != null) {
			method.name += suffix;
			it = method.instructions.iterator();
			while (it.hasNext()) {
				AbstractInsnNode node = it.next();
				if (node instanceof MethodInsnNode) {
					MethodInsnNode methodInsnNode = (MethodInsnNode) node;
					if (methodInsnNode.name.equals(srcMethod.name) && methodInsnNode.desc.equals(srcMethod.desc)) {
						if (CharsetPatchTransformer.isExtends(methodInsnNode.owner.replace('/', '.'), baseClassName.replace('/', '.'))) {
							methodInsnNode.name += suffix;
						}
					}
				}
			}
		}

		MethodNode newMethod = new MethodNode(
				srcMethod.access,
				srcMethod.name,
				srcMethod.desc,
				srcMethod.signature,
				new String[0]
		);
		newMethod.maxLocals = srcMethod.maxLocals;
		newMethod.maxStack = srcMethod.maxStack;
		newMethod.visibleAnnotations = srcMethod.visibleAnnotations;
		newMethod.visibleParameterAnnotations = srcMethod.visibleParameterAnnotations;
		newMethod.visibleTypeAnnotations = srcMethod.visibleTypeAnnotations;
		newMethod.visibleLocalVariableAnnotations = srcMethod.visibleLocalVariableAnnotations;
		newMethod.invisibleAnnotations = srcMethod.visibleAnnotations;
		newMethod.invisibleParameterAnnotations = srcMethod.visibleParameterAnnotations;
		newMethod.invisibleTypeAnnotations = srcMethod.visibleTypeAnnotations;
		newMethod.invisibleLocalVariableAnnotations = srcMethod.visibleLocalVariableAnnotations;
		it = srcMethod.instructions.iterator();
		while (it.hasNext()) {
			AbstractInsnNode node = it.next();
			if (node instanceof FieldInsnNode) {
				FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
				if ("pl/asie/charset/patches/LockHooks".equals(fieldInsnNode.owner)) {
					node = new FieldInsnNode(
							fieldInsnNode.getOpcode(),
							baseClassName,
							fieldInsnNode.name,
							fieldInsnNode.desc
					);
				}
			} else if (node instanceof MethodInsnNode) {
				MethodInsnNode methodInsnNode = (MethodInsnNode) node;
				if (srcClassName.equals(methodInsnNode.owner)) {
					int opc = methodInsnNode.getOpcode();
					node = new MethodInsnNode(
							opc == Opcodes.INVOKEVIRTUAL && methodInsnNode.name.endsWith("_postCharset") ? Opcodes.INVOKESPECIAL : opc,
							target.name,
							methodInsnNode.name,
							/* methodInsnNode.name.endsWith("_postCharset") ? methodInsnNode.name.replaceAll("_postCharset", suffix) : methodInsnNode.name, */
							methodInsnNode.desc,
							methodInsnNode.itf
					);
				}
			} else if (node instanceof FrameNode) {
				FrameNode frameNode1 = (FrameNode) node;
				Object[] newLocal = new Object[0];
				Object[] newStack = newLocal;

				if (frameNode1.local != null) {
					newLocal = new Object[frameNode1.local.size()];
					for (int i = 0; i < newLocal.length; i++) {
						Object o = frameNode1.local.get(i);
						if (o != null && o.equals(srcClassName)) {
							o = target.name;
						}
						newLocal[i] = o;
					}
				}

				if (frameNode1.stack != null) {
					newStack = new Object[frameNode1.stack.size()];
					for (int i = 0; i < newStack.length; i++) {
						Object o = frameNode1.stack.get(i);
						if (o != null && o.equals(srcClassName)) {
							o = target.name;
						}
						newStack[i] = o;
					}
				}

				node = new FrameNode(
						frameNode1.type,
						newLocal.length,
						newLocal,
						newStack.length,
						newStack
				);
			}
			newMethod.instructions.add(node);
		}
		return newMethod;
	}

	public static void patch(ClassNode target, String type, boolean isBase) {
		initialize();

		String baseClassName = "net/minecraft/tileentity/TileEntity";

		/* if (isBase) {
			for (FieldNode fieldNode : node.fields) {
				System.out.println("Added " + fieldNode.name + " to " + target.name);
				target.fields.add(fieldNode);
			}
		} */

		if (CharsetPatchTransformer.isImplements(target.name.replace('/', '.'), "net.minecraft.inventory.IInventory")
			&& !CharsetPatchTransformer.isImplements(target.name.replace('/', '.'), "net.minecraft.inventory.ISidedInventory")) {
			ClassNode invNode = load("pl/asie/charset/patches/DummySidedInventory.class");
			for (MethodNode method : invNode.methods) {
				if (!method.name.contains("<") && !method.name.contains("$")) {
					target.methods.add(inject(null, method, target, "pl/asie/charset/patches/DummySidedInventory", baseClassName));
				}
			}

			target.interfaces.add("net/minecraft/inventory/ISidedInventory");
			System.out.println("Adding dummy ISidedInventory to " + target.name);
		}

		List<MethodNode> newMethods = new ArrayList<>();

		for (MethodNode method : target.methods) {
			MethodNode srcMethod = nodeMethods.get(method.name + method.desc);
			if (srcMethod != null && !method.name.contains("<") && !method.name.contains("$")) {
				boolean hasHook = false;
				if (srcMethod.visibleAnnotations != null) {
					for (AnnotationNode node : srcMethod.visibleAnnotations) {
						if ("Lpl/asie/charset/patches/Hook;".equals(node.desc)) {
							hasHook = true;
							break;
						}
					}
				}
				if (hasHook) {
					newMethods.add(inject(method, srcMethod, target, "pl/asie/charset/patches/LockHooks", baseClassName));
				}
			}
		}

		target.methods.addAll(newMethods);
		/* try {
			target.accept(new TraceClassVisitor(new PrintWriter(new FileOutputStream("./" + target.name.replaceAll("[^a-zA-Z]", "_")))));
		} catch (Exception e) {
			e.printStackTrace();
		} */
	}
}

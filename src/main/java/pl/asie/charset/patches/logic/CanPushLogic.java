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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.*;

public class CanPushLogic {
	private CanPushLogic() {

	}

	public static class MyClassVisitor extends ClassVisitor {
		public MyClassVisitor(int api, ClassVisitor cv) {
			super(api, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
		                                 String signature, String[] exceptions) {
			if ("func_185646_a".equals(name) || "canPush".equals(name)) {
				System.out.println("[CanPushLogic] Patching " + name + "!");

				return new CanPushLogic.MyMethodVisitor(api, "func_185646_a".equals(name), super.visitMethod(access, name, desc, signature, exceptions));
			} else {
				return super.visitMethod(access, name, desc, signature, exceptions);
			}
		}
	}

	public static class MyMethodVisitor extends MethodVisitor {
		private final boolean isSrg;
		private boolean first = true;

		public MyMethodVisitor(int api, boolean srg, MethodVisitor mv) {
			super(api, mv);
			this.isSrg = srg;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
		                            String desc, boolean itf) {
			if (first) {

				first = false;
				Label l0 = new Label();
				super.visitLabel(l0);
				super.visitLineNumber(4000, l0);
				super.visitVarInsn(ALOAD, 0);
				super.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", isSrg ? "fync_177230_c" : "getBlock", "()Lnet/minecraft/block/Block;", true);
				super.visitTypeInsn(INSTANCEOF, "pl/asie/charset/lib/block/IPatchCanPushListener");
				Label l1 = new Label();
				super.visitJumpInsn(IFEQ, l1);
				Label l2 = new Label();
				super.visitLabel(l2);
				super.visitLineNumber(4001, l2);
				super.visitVarInsn(ALOAD, 0);
				super.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", isSrg ? "fync_177230_c" : "getBlock", "()Lnet/minecraft/block/Block;", true);
				super.visitTypeInsn(CHECKCAST, "pl/asie/charset/lib/block/IPatchCanPushListener");
				super.visitVarInsn(ALOAD, 0);
				super.visitVarInsn(ALOAD, 1);
				super.visitVarInsn(ALOAD, 2);
				super.visitVarInsn(ALOAD, 3);
				super.visitVarInsn(ILOAD, 4);
				super.visitVarInsn(ALOAD, 5);
				super.visitMethodInsn(INVOKEINTERFACE, "pl/asie/charset/lib/block/IPatchCanPushListener", "charsetCanPushByPiston", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z", true);
				super.visitInsn(IRETURN);
				super.visitLabel(l1);
				super.visitLineNumber(4002, l1);
				super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			}

			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
	}
}

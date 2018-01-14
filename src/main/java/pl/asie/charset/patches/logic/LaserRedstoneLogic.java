package pl.asie.charset.patches.logic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class LaserRedstoneLogic {
	private LaserRedstoneLogic() {

	}

	public static class MyClassVisitor extends ClassVisitor {
		public MyClassVisitor(int api, ClassVisitor cv) {
			super(api, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
		                                 String signature, String[] exceptions) {
			return new MyMethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
		}
	}

	public static class MyMethodVisitor extends MethodVisitor {
		public MyMethodVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
		                            String desc, boolean itf) {
			if (Opcodes.INVOKEVIRTUAL == opcode && "net/minecraft/world/World".equals(owner)
					&& ("getRedstonePower".equals(name) || "func_175651_c".equals(name))) {
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "pl/asie/charset/patchwork/LaserRedstoneHook", "getRedstonePower",
						"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)I", false);
			} else {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}
}

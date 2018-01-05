package pl.asie.charset.patches;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import pl.asie.charset.patchwork.CharsetPatchwork;

public class CharsetPatchTransformer implements IClassTransformer {
	public static class RedstoneClassVistor extends ClassVisitor {
		public RedstoneClassVistor(int api, ClassVisitor cv) {
			super(api, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
		                                 String signature, String[] exceptions) {
			return new RedstoneMethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
		}
	}

	public static class RedstoneMethodVisitor extends MethodVisitor {
		public RedstoneMethodVisitor(int api, MethodVisitor mv) {
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

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return basicClass;

		ClassReader reader = new ClassReader(basicClass);
		ClassWriter writer = new ClassWriter(Opcodes.ASM5);
		ClassVisitor target = writer;
		if (CharsetPatchwork.LASER_REDSTONE) {
			target = new RedstoneClassVistor(Opcodes.ASM5, writer);
		}
		if (target == writer) {
			return basicClass;
		}
		reader.accept(target, 0);
		return writer.toByteArray();
	}
}

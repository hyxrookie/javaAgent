package com.demo.agent;
 
import org.objectweb.asm.*;
 
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

public class MyClassFileTransformer implements ClassFileTransformer {
    private static final Logger LOGGER = Logger.getLogger(MyClassFileTransformer.class.getName());
    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) {
 
        if (className.equals("org/springframework/web/servlet/DispatcherServlet")) {
            LOGGER.info("====> Transforming DispatcherServlet");
 
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
 
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM7, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
 
                    // Inject interceptor logic before doDispatch method in DispatcherServlet
                    if("doDispatch".equals(name)) {
                        return new MethodVisitor(Opcodes.ASM7, mv) {
                            @Override
                            public void visitCode() {
                                try {
                                    // 加载doDispatch方法的第一个参数
                                    // 如果varIndex是0，代表org/springframework/web/servlet/DispatcherServlet那个类的this
                                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                                    mv.visitVarInsn(Opcodes.ALOAD, 2);
                                    // 执行MyInterceptor.beforeRequest 方法
                                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(MyInterceptor.class), "beforeRequest",
                                            Type.getMethodDescriptor(MyInterceptor.class.getMethod("beforeRequest", Object.class, Object.class)), false);
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                }
                                super.visitCode();
                            }
                        };
                    }
                    return mv;
                }
            };
 
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
 
        return classfileBuffer;
    }
}
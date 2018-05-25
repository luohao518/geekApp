package com.alibaba.fastjson.serializer;

import static com.alibaba.fastjson.util.ASMUtils.getDesc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.asm.ClassWriter;
import com.alibaba.fastjson.asm.Label;
import com.alibaba.fastjson.asm.MethodVisitor;
import com.alibaba.fastjson.util.ASMClassLoader;
import com.alibaba.fastjson.util.ASMUtils;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.asm.FieldVisitor;
import com.alibaba.fastjson.asm.Opcodes;

public class ASMSerializerFactory implements Opcodes {

    private ASMClassLoader classLoader = new ASMClassLoader();

    private final AtomicLong seed = new AtomicLong();

    public String getGenClassName(Class<?> clazz) {
        return "Serializer_" + seed.incrementAndGet();
    }

    public boolean isExternalClass(Class<?> clazz) {
        return classLoader.isExternalClass(clazz);
    }

    static class Context {

        private final String className;
        private final int beanSerializeFeatures;

        public Context(String className, int beanSerializeFeatures){
            this.className = className;
            this.beanSerializeFeatures = beanSerializeFeatures;
        }

        private int                  variantIndex = 9;

        private Map<String, Integer> variants     = new HashMap<String, Integer>();

        public int serializer() {
            return 1;
        }

        public String getClassName() {
            return className;
        }

        public int obj() {
            return 2;
        }

        public int paramFieldName() {
            return 3;
        }

        public int paramFieldType() {
            return 4;
        }
        
        public int features() {
            return 5;
        }

        public int fieldName() {
            return 6;
        }

        public int original() {
            return 7;
        }

        public int processValue() {
            return 8;
        }

        public int getVariantCount() {
            return variantIndex;
        }

        public int var(String name) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex++);
            }
            i = variants.get(name);
            return i.intValue();
        }

        public int var(String name, int increment) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex);
                variantIndex += increment;
            }
            i = variants.get(name);
            return i.intValue();
        }
    }

    public ObjectSerializer createJavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) throws Exception {
        if (clazz.isPrimitive()) {
            throw new JSONException("unsupportd class " + clazz.getName());
        }

        List<FieldInfo> getters = TypeUtils.computeGetters(clazz, aliasMap, false);
        
        for (FieldInfo getter : getters) {
            if (!ASMUtils.checkName(getter.getMember().getName())) {
                return null;
            }
        }

        String className = getGenClassName(clazz);
        int beanSerializeFeatures = TypeUtils.getSerializeFeatures(clazz);

        ClassWriter cw = new ClassWriter();
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, "com/alibaba/fastjson/serializer/ASMJavaBeanSerializer",
                 new String[] { "com/alibaba/fastjson/serializer/ObjectSerializer" });

        for (FieldInfo fieldInfo : getters) {
            {
                FieldVisitor fw = cw.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName() + "_asm_fieldPrefix",
                                                "Ljava/lang/reflect/Type;");
                fw.visitEnd();
            }

            FieldVisitor fw = cw.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName() + "_asm_fieldType",
                                            "Ljava/lang/reflect/Type;");
            fw.visitEnd();
        }

        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(clazz)));
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/serializer/ASMJavaBeanSerializer", "<init>", "(Ljava/lang/Class;)V");

        // mw.visitFieldInsn(PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");

        for (FieldInfo fieldInfo : getters) {
            mw.visitVarInsn(Opcodes.ALOAD, 0);

            mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldInfo.getDeclaringClass())));

            if (fieldInfo.getMethod() != null) {
                mw.visitLdcInsn(fieldInfo.getMethod().getName());
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/util/ASMUtils", "getMethodType",
                                   "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Type;");

            } else {
                mw.visitLdcInsn(fieldInfo.getField().getName());
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/util/ASMUtils", "getFieldType",
                                   "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Type;");
            }

            mw.visitFieldInsn(Opcodes.PUTFIELD, className, fieldInfo.getName() + "_asm_fieldType", "Ljava/lang/reflect/Type;");
        }

        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();

        {
            Context context = new Context(className, beanSerializeFeatures);

            mw = cw.visitMethod(Opcodes.ACC_PUBLIC,
                                "write",
                                "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V",
                                null, new String[] { "java/io/IOException" });

            mw.visitVarInsn(Opcodes.ALOAD, context.serializer()); // serializer
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter",
                               "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("out"));

            JSONType jsonType = clazz.getAnnotation(JSONType.class);

            if (jsonType == null || jsonType.alphabetic()) {
                Label _else = new Label();

                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "SortField",
                                  "Lcom/alibaba/fastjson/serializer/SerializerFeature;");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled",
                                   "(Lcom/alibaba/fastjson/serializer/SerializerFeature;)Z");

                mw.visitJumpInsn(Opcodes.IFEQ, _else);
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitVarInsn(Opcodes.ALOAD, 1);
                mw.visitVarInsn(Opcodes.ALOAD, 2);
                mw.visitVarInsn(Opcodes.ALOAD, 3);
                mw.visitVarInsn(Opcodes.ALOAD, 4);
                mw.visitVarInsn(Opcodes.ILOAD, 5);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "write1",
                                   "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V");
                mw.visitInsn(Opcodes.RETURN);

                mw.visitLabel(_else);
            }

            mw.visitVarInsn(Opcodes.ALOAD, context.obj()); // obj
            mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(clazz)); // serializer
            mw.visitVarInsn(Opcodes.ASTORE, context.var("entity")); // obj
            generateWriteMethod(clazz, mw, getters, context);
            mw.visitInsn(Opcodes.RETURN);
            mw.visitMaxs(6, context.getVariantCount() + 1);
            mw.visitEnd();
        }

        List<FieldInfo> sortedGetters = TypeUtils.computeGetters(clazz, aliasMap, true);
        {

            // sortField support
            Context context = new Context(className, beanSerializeFeatures);

            mw = cw.visitMethod(Opcodes.ACC_PUBLIC,
                                "write1",
                                "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V",
                                null, new String[] { "java/io/IOException" });

            mw.visitVarInsn(Opcodes.ALOAD, context.serializer()); // serializer
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter",
                               "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("out"));

            mw.visitVarInsn(Opcodes.ALOAD, context.obj()); // obj
            mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(clazz)); // serializer
            mw.visitVarInsn(Opcodes.ASTORE, context.var("entity")); // obj

            generateWriteMethod(clazz, mw, sortedGetters, context);

            mw.visitInsn(Opcodes.RETURN);
            mw.visitMaxs(6, context.getVariantCount() + 1);
            mw.visitEnd();
        }

        // writeAsArray
        {
            Context context = new Context(className, beanSerializeFeatures);

            mw = cw.visitMethod(Opcodes.ACC_PUBLIC,
                                "writeAsArray",
                                "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;)V",
                                null, new String[] { "java/io/IOException" });

            mw.visitVarInsn(Opcodes.ALOAD, context.serializer()); // serializer
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter",
                               "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("out"));

            mw.visitVarInsn(Opcodes.ALOAD, context.obj()); // obj
            mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(clazz)); // serializer
            mw.visitVarInsn(Opcodes.ASTORE, context.var("entity")); // obj
            generateWriteAsArray(clazz, mw, sortedGetters, context);
            mw.visitInsn(Opcodes.RETURN);
            mw.visitMaxs(6, context.getVariantCount() + 1);
            mw.visitEnd();
        }

        byte[] code = cw.toByteArray();
        //
        // org.apache.commons.io.IOUtils.write(code, new java.io.FileOutputStream(
        // "/usr/alibaba/workspace-3.7/fastjson-asm/target/classes/"
        // + className + ".class"));

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);
        Object instance = exampleClass.newInstance();

        return (ObjectSerializer) instance;
    }

    private void generateWriteAsArray(Class<?> clazz, MethodVisitor mw, List<FieldInfo> getters, Context context)
                                                                                                                 throws Exception {

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.BIPUSH, '[');
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");

        int size = getters.size();

        if (size == 0) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
            mw.visitVarInsn(Opcodes.BIPUSH, ']');
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");
            return;
        }

        for (int i = 0; i < size; ++i) {
            final char seperator = (i == size - 1) ? ']' : ',';

            FieldInfo property = getters.get(i);
            Class<?> propertyClass = property.getFieldClass();

            mw.visitLdcInsn(property.getName());
            mw.visitVarInsn(Opcodes.ASTORE, context.fieldName());

            if (propertyClass == byte.class //
                || propertyClass == short.class //
                || propertyClass == int.class) {

                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeIntAndChar", "(IC)V");

            } else if (propertyClass == long.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeLongAndChar", "(JC)V");

            } else if (propertyClass == float.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFloatAndChar", "(FC)V");

            } else if (propertyClass == double.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeDoubleAndChar", "(DC)V");

            } else if (propertyClass == boolean.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeBooleanAndChar", "(ZC)V");
            } else if (propertyClass == char.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeCharacterAndChar", "(CC)V");

            } else if (propertyClass == String.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeString",
                                   "(Ljava/lang/String;C)V");
            } else if (propertyClass.isEnum()) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                _get(mw, context, property);
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeEnum", "(Ljava/lang/Enum;C)V");
            } else {
                String format = property.getFormat();

                mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
                _get(mw, context, property);
                if (format != null) {
                    mw.visitLdcInsn(format);
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFormat",
                                       "(Ljava/lang/Object;Ljava/lang/String;)V");
                } else {
                    mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
                    if (property.getFieldType() instanceof Class<?> //
                        && ((Class<?>) property.getFieldType()).isPrimitive()) {
                        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                           "(Ljava/lang/Object;Ljava/lang/Object;)V");
                    } else {
                        mw.visitVarInsn(Opcodes.ALOAD, 0);
                        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), property.getName() + "_asm_fieldType",
                                          "Ljava/lang/reflect/Type;");
                        mw.visitLdcInsn(property.getSerialzeFeatures());

                        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                           "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V");
                    }
                }

                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");
            }
        }
    }

    private void generateWriteMethod(Class<?> clazz, MethodVisitor mw, List<FieldInfo> getters, Context context)
                                                                                                                throws Exception {
        Label end = new Label();

        int size = getters.size();

        {
            // 格式化输出不走asm 优化
            Label endFormat_ = new Label();
            Label notNull_ = new Label();
            mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "PrettyFormat",
                              "Lcom/alibaba/fastjson/serializer/SerializerFeature;");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled",
                               "(Lcom/alibaba/fastjson/serializer/SerializerFeature;)Z");
            mw.visitJumpInsn(Opcodes.IFEQ, endFormat_);

            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), "nature", "Lcom/alibaba/fastjson/serializer/JavaBeanSerializer;");
            mw.visitJumpInsn(Opcodes.IFNONNULL, notNull_);

            // /////
            mw.visitLabel(notNull_);

            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), "nature", "Lcom/alibaba/fastjson/serializer/JavaBeanSerializer;");
            mw.visitVarInsn(Opcodes.ALOAD, 1);
            mw.visitVarInsn(Opcodes.ALOAD, 2);
            mw.visitVarInsn(Opcodes.ALOAD, 3);
            mw.visitVarInsn(Opcodes.ALOAD, 4);
            mw.visitVarInsn(Opcodes.ILOAD, 5);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JavaBeanSerializer", "write",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V");
            mw.visitInsn(Opcodes.RETURN);

            mw.visitLabel(endFormat_);
        }

        {
            // if (serializer.containsReference(object)) {

            Label endRef_ = new Label();
            Label notNull_ = new Label();

            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), "nature", "Lcom/alibaba/fastjson/serializer/JavaBeanSerializer;");
            mw.visitJumpInsn(Opcodes.IFNONNULL, notNull_);

            // /////
            mw.visitLabel(notNull_);
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), "nature", "Lcom/alibaba/fastjson/serializer/JavaBeanSerializer;");
            mw.visitVarInsn(Opcodes.ALOAD, 1);
            mw.visitVarInsn(Opcodes.ALOAD, 2);
            mw.visitVarInsn(Opcodes.ILOAD, 5);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JavaBeanSerializer", "writeReference",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;I)Z");
            
            mw.visitJumpInsn(Opcodes.IFEQ, endRef_);

            mw.visitInsn(Opcodes.RETURN);

            mw.visitLabel(endRef_);
        }

        {
            Label endWriteAsArray_ = new Label();

            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), "nature", "Lcom/alibaba/fastjson/serializer/JavaBeanSerializer;");
            mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JavaBeanSerializer", "isWriteAsArray",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;)Z");
            mw.visitJumpInsn(Opcodes.IFEQ, endWriteAsArray_);

            // /////
            mw.visitVarInsn(Opcodes.ALOAD, 0); // this
            mw.visitVarInsn(Opcodes.ALOAD, 1); // serializer
            mw.visitVarInsn(Opcodes.ALOAD, 2); // obj
            mw.visitVarInsn(Opcodes.ALOAD, 3); // fieldObj
            mw.visitVarInsn(Opcodes.ALOAD, 4); // fieldType
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, context.getClassName(), "writeAsArray",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;)V");

            mw.visitInsn(Opcodes.RETURN);

            mw.visitLabel(endWriteAsArray_);
        }

        {
            mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getContext",
                               "()Lcom/alibaba/fastjson/serializer/SerialContext;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("parent"));

            mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
            mw.visitVarInsn(Opcodes.ALOAD, context.var("parent"));
            mw.visitVarInsn(Opcodes.ALOAD, context.obj());
            mw.visitVarInsn(Opcodes.ALOAD, context.paramFieldName());
            mw.visitLdcInsn(context.beanSerializeFeatures);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "setContext",
                               "(Lcom/alibaba/fastjson/serializer/SerialContext;Ljava/lang/Object;Ljava/lang/Object;I)V");
        }

        // SEPERATO
        {
            Label end_ = new Label();
            Label else_ = new Label();
            Label writeClass_ = new Label();

            // mw.visitVarInsn(ALOAD, context.var("out"));
            // mw.visitFieldInsn(GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "WriteClassName",
            // "L" + "com/alibaba/fastjson/serializer/SerializerFeature" + ";");
            // mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled",
            // "(" + "L" + "com/alibaba/fastjson/serializer/SerializerFeature" + ";" + ")Z");
            // mw.visitJumpInsn(IFEQ, else_);

            mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
            mw.visitVarInsn(Opcodes.ALOAD, context.paramFieldType());
            mw.visitVarInsn(Opcodes.ALOAD, context.obj());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "isWriteClassName",
                               "(Ljava/lang/reflect/Type;Ljava/lang/Object;)Z");
            mw.visitJumpInsn(Opcodes.IFEQ, else_);

            // mw.visitVarInsn(ALOAD, context.paramFieldType());
            // mw.visitJumpInsn(IFNULL, writeClass_);

            // IFNULL
            mw.visitVarInsn(Opcodes.ALOAD, context.paramFieldType());
            mw.visitVarInsn(Opcodes.ALOAD, context.obj());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
            mw.visitJumpInsn(Opcodes.IF_ACMPEQ, else_);

            mw.visitLabel(writeClass_);
            mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
            mw.visitLdcInsn("{\"" + JSON.DEFAULT_TYPE_KEY + "\":\"" + clazz.getName() + "\"");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(Ljava/lang/String;)V");
            mw.visitVarInsn(Opcodes.BIPUSH, ',');
            mw.visitJumpInsn(Opcodes.GOTO, end_);

            mw.visitLabel(else_);
            mw.visitVarInsn(Opcodes.BIPUSH, '{');

            mw.visitLabel(end_);
        }

        mw.visitVarInsn(Opcodes.ISTORE, context.var("seperator"));

        _before(mw, context);

        for (int i = 0; i < size; ++i) {
            FieldInfo property = getters.get(i);
            Class<?> propertyClass = property.getFieldClass();

            mw.visitLdcInsn(property.getName());
            mw.visitVarInsn(Opcodes.ASTORE, context.fieldName());

            if (propertyClass == byte.class) {
                _byte(clazz, mw, property, context);
            } else if (propertyClass == short.class) {
                _short(clazz, mw, property, context);
            } else if (propertyClass == int.class) {
                _int(clazz, mw, property, context);
            } else if (propertyClass == long.class) {
                _long(clazz, mw, property, context);
            } else if (propertyClass == float.class) {
                _float(clazz, mw, property, context);
            } else if (propertyClass == double.class) {
                _double(clazz, mw, property, context);
            } else if (propertyClass == boolean.class) {
                _boolean(clazz, mw, property, context);
            } else if (propertyClass == char.class) {
                _char(clazz, mw, property, context);
            } else if (propertyClass == String.class) {
                _string(clazz, mw, property, context);
            } else if (propertyClass == BigDecimal.class) {
                _decimal(clazz, mw, property, context);
            } else if (List.class.isAssignableFrom(propertyClass)) {
                _list(clazz, mw, property, context);
                // _object(clazz, mw, property, context);
            } else if (propertyClass.isEnum()) {
                _enum(clazz, mw, property, context);
            } else {
                _object(clazz, mw, property, context);
            }
        }

        _after(mw, context);
        
        Label _else = new Label();
        Label _end_if = new Label();

        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitIntInsn(Opcodes.BIPUSH, '{');
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, _else);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.BIPUSH, '{');
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");

        mw.visitLabel(_else);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.BIPUSH, '}');
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");

        mw.visitLabel(_end_if);
        mw.visitLabel(end);

        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.var("parent"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "setContext",
                           "(Lcom/alibaba/fastjson/serializer/SerialContext;)V");

    }

    private void _object(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ASTORE, context.var("object"));

        _filters(mw, property, context, _end);

        _writeObject(mw, property, context, _end);

        mw.visitLabel(_end);
    }

    private void _enum(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        boolean writeEnumUsingToString = false;
        JSONField annotation = property.getAnnotation(JSONField.class);
        if (annotation != null) {
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteEnumUsingToString) {
                    writeEnumUsingToString = true;
                }
            }
        }

        Label _not_null = new Label();
        Label _end_if = new Label();
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Enum"); // cast
        mw.visitVarInsn(Opcodes.ASTORE, context.var("enum"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("enum"));
        mw.visitJumpInsn(Opcodes.IFNONNULL, _not_null);
        _if_write_null(mw, property, context);
        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(_not_null);
        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ALOAD, context.var("enum"));

        if (writeEnumUsingToString) {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                               "(CLjava/lang/String;Ljava/lang/String;)V");
        } else {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                               "(CLjava/lang/String;Ljava/lang/Enum;)V");
        }

        _seperator(mw, context);

        mw.visitLabel(_end_if);

        mw.visitLabel(_end);
    }

    private void _long(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.LSTORE, context.var("long", 2));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.LLOAD, context.var("long", 2));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;J)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _float(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.FSTORE, context.var("float"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.FLOAD, context.var("float"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;F)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _double(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.DSTORE, context.var("double", 2));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.DLOAD, context.var("double", 2));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;D)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _char(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("char"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("char"));

        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;C)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _boolean(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("boolean"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("boolean"));

        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;Z)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _get(MethodVisitor mw, Context context, FieldInfo property) {
        Method method = property.getMethod();
        if (method != null) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("entity"));
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(method.getDeclaringClass()), method.getName(), ASMUtils.getDesc(method));
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("entity"));
            mw.visitFieldInsn(Opcodes.GETFIELD, ASMUtils.getType(property.getDeclaringClass()), property.getField().getName(),
                              ASMUtils.getDesc(property.getFieldClass()));
        }
    }

    private void _byte(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("byte"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("byte"));

        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;I)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _short(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("short"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("short"));

        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;I)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _int(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("int"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));

        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue", "(CLjava/lang/String;I)V");

        _seperator(mw, context);

        mw.visitLabel(_end);
    }

    private void _decimal(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ASTORE, context.var("decimal"));

        _filters(mw, property, context, _end);

        Label _if = new Label();
        Label _else = new Label();
        Label _end_if = new Label();

        mw.visitLabel(_if);

        // if (decimalValue == null) {
        mw.visitVarInsn(Opcodes.ALOAD, context.var("decimal"));
        mw.visitJumpInsn(Opcodes.IFNONNULL, _else);
        _if_write_null(mw, property, context);
        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(_else); // else { out.writeFieldValue(seperator, fieldName, fieldValue)

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ALOAD, context.var("decimal"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                           "(CLjava/lang/String;Ljava/math/BigDecimal;)V");

        _seperator(mw, context);
        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(_end_if);

        mw.visitLabel(_end);
    }

    private void _string(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Label _end = new Label();

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitVarInsn(Opcodes.ASTORE, context.var("string"));

        _filters(mw, property, context, _end);

        Label _else = new Label();
        Label _end_if = new Label();

        // if (value == null) {
        mw.visitVarInsn(Opcodes.ALOAD, context.var("string"));
        mw.visitJumpInsn(Opcodes.IFNONNULL, _else);

        _if_write_null(mw, property, context);

        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(_else); // else { out.writeFieldValue(seperator, fieldName, fieldValue)
        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitVarInsn(Opcodes.ALOAD, context.var("string"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                           "(CLjava/lang/String;Ljava/lang/String;)V");

        _seperator(mw, context);

        mw.visitLabel(_end_if);

        mw.visitLabel(_end);
    }

    private void _list(Class<?> clazz, MethodVisitor mw, FieldInfo property, Context context) {
        Type propertyType = property.getFieldType();

        Type elementType;
        if (propertyType instanceof Class) {
            elementType = Object.class;
        } else {
            elementType = ((ParameterizedType) propertyType).getActualTypeArguments()[0];
        }

        Class<?> elementClass = null;
        if (elementType instanceof Class<?>) {
            elementClass = (Class<?>) elementType;
        }

        Label _end = new Label();

        Label _if = new Label();
        Label _else = new Label();
        Label _end_if = new Label();

        mw.visitLabel(_if);

        _nameApply(mw, property, context, _end);
        _get(mw, context, property);
        mw.visitTypeInsn(Opcodes.CHECKCAST, "java/util/List"); // cast
        mw.visitVarInsn(Opcodes.ASTORE, context.var("list"));

        _filters(mw, property, context, _end);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
        mw.visitJumpInsn(Opcodes.IFNONNULL, _else);
        _if_write_null(mw, property, context);
        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(_else); // else {

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldName", "(Ljava/lang/String;)V");

        //
        mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I");
        mw.visitVarInsn(Opcodes.ISTORE, context.var("int"));

        Label _if_3 = new Label();
        Label _else_3 = new Label();
        Label _end_if_3 = new Label();

        mw.visitLabel(_if_3);

        mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
        mw.visitInsn(Opcodes.ICONST_0);
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, _else_3);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitLdcInsn("[]");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(Ljava/lang/String;)V");

        mw.visitJumpInsn(Opcodes.GOTO, _end_if_3);

        mw.visitLabel(_else_3);

        {
            mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
            mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
            mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "setContext",
                               "(Ljava/lang/Object;Ljava/lang/Object;)V");
        }

        {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
            mw.visitVarInsn(Opcodes.BIPUSH, '[');
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");

            // list_serializer = null
            mw.visitInsn(Opcodes.ACONST_NULL);
            mw.visitTypeInsn(Opcodes.CHECKCAST, "com/alibaba/fastjson/serializer/ObjectSerializer"); // cast to string
            mw.visitVarInsn(Opcodes.ASTORE, context.var("list_ser"));

            Label _for = new Label();
            Label _end_for = new Label();

            mw.visitInsn(Opcodes.ICONST_0);
            mw.visitVarInsn(Opcodes.ISTORE, context.var("i"));

            // for (; i < list.size() -1; ++i) {
            mw.visitLabel(_for);
            mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));

            mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
            mw.visitInsn(Opcodes.ICONST_1);
            mw.visitInsn(ISUB);

            mw.visitJumpInsn(Opcodes.IF_ICMPGE, _end_for); // i < list.size - 1

            if (elementType == String.class) {
                // out.write((String)list.get(i));
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
                mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));
                mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;");
                mw.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String"); // cast to string
                mw.visitVarInsn(Opcodes.BIPUSH, ',');
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeString",
                                   "(Ljava/lang/String;C)V");
            } else {
                mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
                mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
                mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));
                mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;");
                mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");

                if (elementClass != null && Modifier.isPublic(elementClass.getModifiers())) {
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc((Class<?>) elementType)));
                    mw.visitLdcInsn(property.getSerialzeFeatures());
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                       "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V");
                } else {
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                       "(Ljava/lang/Object;Ljava/lang/Object;)V");
                }

                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitVarInsn(Opcodes.BIPUSH, ',');
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");
            }

            mw.visitIincInsn(context.var("i"), 1);
            mw.visitJumpInsn(Opcodes.GOTO, _for);

            mw.visitLabel(_end_for);

            if (elementType == String.class) {
                // out.write((String)list.get(size - 1));
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
                mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
                mw.visitInsn(Opcodes.ICONST_1);
                mw.visitInsn(ISUB);
                mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;");
                mw.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String"); // cast to string
                mw.visitVarInsn(Opcodes.BIPUSH, ']');
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeString",
                                   "(Ljava/lang/String;C)V");
            } else {
                mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
                mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
                mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));
                mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;");
                mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");

                if (elementClass != null && Modifier.isPublic(elementClass.getModifiers())) {
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc((Class<?>) elementType)));
                    mw.visitLdcInsn(property.getSerialzeFeatures());
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                       "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V");
                } else {
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                       "(Ljava/lang/Object;Ljava/lang/Object;)V");
                }

                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitVarInsn(Opcodes.BIPUSH, ']');
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");
            }
        }

        {
            mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "popContext", "()V");
        }

        mw.visitLabel(_end_if_3);

        _seperator(mw, context);

        mw.visitLabel(_end_if);

        mw.visitLabel(_end);
    }

    private void _filters(MethodVisitor mw, FieldInfo property, Context context, Label _end) {
        if (property.getField() != null) {
            if (Modifier.isTransient(property.getField().getModifiers())) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "SkipTransientField",
                                  "Lcom/alibaba/fastjson/serializer/SerializerFeature;");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled",
                                   "(Lcom/alibaba/fastjson/serializer/SerializerFeature;)Z");

                // if true
                mw.visitJumpInsn(Opcodes.IFNE, _end);
            }
        }
        
        _notWriteDefault(mw, property, context, _end);
        
        _apply(mw, property, context);
        mw.visitJumpInsn(Opcodes.IFEQ, _end);

        _processKey(mw, property, context);

        Label _else_processKey = new Label();
        _processValue(mw, property, context);

        mw.visitVarInsn(Opcodes.ALOAD, context.original());
        mw.visitVarInsn(Opcodes.ALOAD, context.processValue());
        mw.visitJumpInsn(Opcodes.IF_ACMPEQ, _else_processKey);
        _writeObject(mw, property, context, _end);
        mw.visitJumpInsn(Opcodes.GOTO, _end);

        mw.visitLabel(_else_processKey);
    }

    private void _nameApply(MethodVisitor mw, FieldInfo property, Context context, Label _end) {
        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.obj());
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "applyName",
                           "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;)Z");
        mw.visitJumpInsn(Opcodes.IFEQ, _end);
    }

    private void _writeObject(MethodVisitor mw, FieldInfo fieldInfo, Context context, Label _end) {
        String format = fieldInfo.getFormat();

        Label _not_null = new Label();

        mw.visitVarInsn(Opcodes.ALOAD, context.processValue());
        mw.visitJumpInsn(Opcodes.IFNONNULL, _not_null); // if (obj == null)
        _if_write_null(mw, fieldInfo, context);
        mw.visitJumpInsn(Opcodes.GOTO, _end);

        mw.visitLabel(_not_null);
        // writeFieldNullNumber
        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "write", "(C)V");

        // out.writeFieldName("fieldName")
        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldName", "(Ljava/lang/String;)V");

        // serializer.write(obj)
        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.processValue());
        if (format != null) {
            mw.visitLdcInsn(format);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFormat",
                               "(Ljava/lang/Object;Ljava/lang/String;)V");
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());
            if (fieldInfo.getFieldType() instanceof Class<?> //
                && ((Class<?>) fieldInfo.getFieldType()).isPrimitive()) {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                   "(Ljava/lang/Object;Ljava/lang/Object;)V");
            } else {
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_fieldType",
                                  "Ljava/lang/reflect/Type;");
                mw.visitLdcInsn(fieldInfo.getSerialzeFeatures());

                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "writeWithFieldName",
                                   "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;I)V");
            }
        }

        _seperator(mw, context);
    }

    private void _before(MethodVisitor mw, Context context) {
        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.obj());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "writeBefore",
                           "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;C)C");
        mw.visitVarInsn(Opcodes.ISTORE, context.var("seperator"));
    }

    private void _after(MethodVisitor mw, Context context) {
        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.obj());
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "writeAfter",
                           "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;C)C");
        mw.visitVarInsn(Opcodes.ISTORE, context.var("seperator"));
    }
    
    private void _notWriteDefault(MethodVisitor mw, FieldInfo property, Context context, Label _end) {
        Label elseLabel = new Label();
        
        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "NotWriteDefaultValue",
                          "Lcom/alibaba/fastjson/serializer/SerializerFeature;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled",
                           "(Lcom/alibaba/fastjson/serializer/SerializerFeature;)Z");
        mw.visitJumpInsn(Opcodes.IFEQ, elseLabel);
        
        Class<?> propertyClass = property.getFieldClass();
        if (propertyClass == boolean.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("boolean"));
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        } else if (propertyClass == byte.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("byte"));
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        } else if (propertyClass == short.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("short"));
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        } else if (propertyClass == int.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        } else if (propertyClass == long.class) {
            mw.visitVarInsn(Opcodes.LLOAD, context.var("long"));
            mw.visitInsn(Opcodes.LCONST_0);
            mw.visitInsn(Opcodes.LCMP);
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        } else if (propertyClass == float.class) {
            mw.visitVarInsn(Opcodes.FLOAD, context.var("float"));
            mw.visitInsn(Opcodes.FCONST_0);
            mw.visitInsn(Opcodes.FCMPL);
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        } else if (propertyClass == double.class) {
            mw.visitVarInsn(Opcodes.DLOAD, context.var("double"));
            mw.visitInsn(Opcodes.DCONST_0);
            mw.visitInsn(Opcodes.DCMPL);
            mw.visitJumpInsn(Opcodes.IFEQ, _end);
        }
        
        mw.visitLabel(elseLabel);
    }

    private void _apply(MethodVisitor mw, FieldInfo property, Context context) {
        Class<?> propertyClass = property.getFieldClass();

        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.obj());
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());

        if (propertyClass == byte.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("byte"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;B)Z");
        } else if (propertyClass == short.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("short"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;S)Z");
        } else if (propertyClass == int.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;I)Z");
        } else if (propertyClass == char.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("char"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;C)Z");
        } else if (propertyClass == long.class) {
            mw.visitVarInsn(Opcodes.LLOAD, context.var("long", 2));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;J)Z");
        } else if (propertyClass == float.class) {
            mw.visitVarInsn(Opcodes.FLOAD, context.var("float"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;F)Z");
        } else if (propertyClass == double.class) {
            mw.visitVarInsn(Opcodes.DLOAD, context.var("double", 2));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;D)Z");
        } else if (propertyClass == boolean.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("boolean"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;B)Z");
        } else if (propertyClass == BigDecimal.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("decimal"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z");
        } else if (propertyClass == String.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("string"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z");
        } else if (propertyClass.isEnum()) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("enum"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z");
        } else if (List.class.isAssignableFrom(propertyClass)) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z");
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("object"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "apply",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Z");
        }
    }

    private void _processValue(MethodVisitor mw, FieldInfo property, Context context) {
        Class<?> propertyClass = property.getFieldClass();

        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.obj());
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());

        if (propertyClass == byte.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("byte"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
        } else if (propertyClass == short.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("short"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
        } else if (propertyClass == int.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        } else if (propertyClass == char.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("char"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
        } else if (propertyClass == long.class) {
            mw.visitVarInsn(Opcodes.LLOAD, context.var("long", 2));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
        } else if (propertyClass == float.class) {
            mw.visitVarInsn(Opcodes.FLOAD, context.var("float"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
        } else if (propertyClass == double.class) {
            mw.visitVarInsn(Opcodes.DLOAD, context.var("double", 2));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
        } else if (propertyClass == boolean.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("boolean"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
        } else if (propertyClass == BigDecimal.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("decimal"));
        } else if (propertyClass == String.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("string"));
        } else if (propertyClass.isEnum()) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("enum"));
        } else if (List.class.isAssignableFrom(propertyClass)) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("object"));
        }

        mw.visitVarInsn(Opcodes.ASTORE, context.original());
        mw.visitVarInsn(Opcodes.ALOAD, context.original());

        mw.visitMethodInsn(Opcodes.INVOKESTATIC,
                           "com/alibaba/fastjson/serializer/FilterUtils",
                           "processValue",
                           "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");

        mw.visitVarInsn(Opcodes.ASTORE, context.processValue());
    }

    private void _processKey(MethodVisitor mw, FieldInfo property, Context context) {
        Class<?> propertyClass = property.getFieldClass();

        mw.visitVarInsn(Opcodes.ALOAD, context.serializer());
        mw.visitVarInsn(Opcodes.ALOAD, context.obj());
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());

        if (propertyClass == byte.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("byte"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;B)Ljava/lang/String;");
        } else if (propertyClass == short.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("short"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;S)Ljava/lang/String;");
        } else if (propertyClass == int.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("int"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;I)Ljava/lang/String;");
        } else if (propertyClass == char.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("char"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;C)Ljava/lang/String;");
        } else if (propertyClass == long.class) {
            mw.visitVarInsn(Opcodes.LLOAD, context.var("long", 2));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;J)Ljava/lang/String;");
        } else if (propertyClass == float.class) {
            mw.visitVarInsn(Opcodes.FLOAD, context.var("float"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;F)Ljava/lang/String;");
        } else if (propertyClass == double.class) {
            mw.visitVarInsn(Opcodes.DLOAD, context.var("double", 2));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;D)Ljava/lang/String;");
        } else if (propertyClass == boolean.class) {
            mw.visitVarInsn(Opcodes.ILOAD, context.var("boolean"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/serializer/FilterUtils", "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Z)Ljava/lang/String;");
        } else if (propertyClass == BigDecimal.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("decimal"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,
                               "com/alibaba/fastjson/serializer/FilterUtils",
                               "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
        } else if (propertyClass == String.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("string"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,
                               "com/alibaba/fastjson/serializer/FilterUtils",
                               "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
        } else if (propertyClass.isEnum()) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("enum"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,
                               "com/alibaba/fastjson/serializer/FilterUtils",
                               "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
        } else if (List.class.isAssignableFrom(propertyClass)) {

            mw.visitVarInsn(Opcodes.ALOAD, context.var("list"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,
                               "com/alibaba/fastjson/serializer/FilterUtils",
                               "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("object"));
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,
                               "com/alibaba/fastjson/serializer/FilterUtils",
                               "processKey",
                               "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
        }

        mw.visitVarInsn(Opcodes.ASTORE, context.fieldName());
    }

    private void _if_write_null(MethodVisitor mw, FieldInfo fieldInfo, Context context) {
        Class<?> propertyClass = fieldInfo.getFieldClass();

        Label _if = new Label();
        Label _else = new Label();
        Label _write_null = new Label();
        Label _end_if = new Label();

        mw.visitLabel(_if);

        // out.isEnabled(Serializer.WriteMapNullValue)
        boolean writeNull = false;
        boolean writeNullNumberAsZero = false;
        boolean writeNullStringAsEmpty = false;
        boolean writeNullBooleanAsFalse = false;
        boolean writeNullListAsEmpty = false;
        JSONField annotation = fieldInfo.getAnnotation(JSONField.class);
        if (annotation != null) {
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteMapNullValue) {
                    writeNull = true;
                } else if (feature == SerializerFeature.WriteNullNumberAsZero) {
                    writeNullNumberAsZero = true;
                } else if (feature == SerializerFeature.WriteNullStringAsEmpty) {
                    writeNullStringAsEmpty = true;
                } else if (feature == SerializerFeature.WriteNullBooleanAsFalse) {
                    writeNullBooleanAsFalse = true;
                } else if (feature == SerializerFeature.WriteNullListAsEmpty) {
                    writeNullListAsEmpty = true;
                }
            }
        }

        if (!writeNull) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "WriteMapNullValue",
                              "Lcom/alibaba/fastjson/serializer/SerializerFeature;");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled",
                               "(Lcom/alibaba/fastjson/serializer/SerializerFeature;)Z");
            mw.visitJumpInsn(Opcodes.IFEQ, _else);
        }

        mw.visitLabel(_write_null);
        // out.writeFieldNull(seperator, 'fieldName')
        mw.visitVarInsn(Opcodes.ALOAD, context.var("out"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("seperator"));
        mw.visitVarInsn(Opcodes.ALOAD, context.fieldName());

        if (propertyClass == String.class || propertyClass == Character.class) {
            if (writeNullStringAsEmpty) {
                mw.visitLdcInsn("");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                                   "(CLjava/lang/String;Ljava/lang/String;)V");
            } else {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldNullString",
                                   "(CLjava/lang/String;)V");
            }
        } else if (Number.class.isAssignableFrom(propertyClass)) {
            if (writeNullNumberAsZero) {
                mw.visitInsn(Opcodes.ICONST_0);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                                   "(CLjava/lang/String;I)V");
            } else {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldNullNumber",
                                   "(CLjava/lang/String;)V");
            }
        } else if (propertyClass == Boolean.class) {
            if (writeNullBooleanAsFalse) {
                mw.visitInsn(Opcodes.ICONST_0);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldValue",
                                   "(CLjava/lang/String;Z)V");
            } else {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldNullBoolean",
                                   "(CLjava/lang/String;)V");
            }
        } else if (Collection.class.isAssignableFrom(propertyClass) || propertyClass.isArray()) {
            if (writeNullListAsEmpty) {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldEmptyList",
                                   "(CLjava/lang/String;)V");
            } else {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldNullList",
                                   "(CLjava/lang/String;)V");
            }
        } else {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "writeFieldNull",
                               "(CLjava/lang/String;)V");
        }

        // seperator = ',';
        _seperator(mw, context);

        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(_else);

        mw.visitLabel(_end_if);
    }

    private void _seperator(MethodVisitor mw, Context context) {
        mw.visitVarInsn(Opcodes.BIPUSH, ',');
        mw.visitVarInsn(Opcodes.ISTORE, context.var("seperator"));
    }
}
